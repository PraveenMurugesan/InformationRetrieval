############################
# Backup
############################
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans
from sklearn.cluster import AgglomerativeClustering

import pickle
import pysolr
from collections import Counter;
from SolrClient import  SolrClient;

def writeToFile(outputfile, items):
    with open(outputfile, 'wb') as fp:
        pickle.dump(items, fp)

def readFromFile(inputfileName):
    with open(inputfileName, 'rb') as fp:
        itemlist = pickle.load(fp)
        return itemlist

def AgglomerativeClusteringCustom(X, numberOfClusters,linkage):
    print("Result of agglomerative clustering")
    model = AgglomerativeClustering(n_clusters=numberOfClusters,   linkage=linkage)
    model.fit(X.toarray())
    return model.labels_

def KMeansClusteringCustom(X,numberOfClusters):
    print("Result of K-Means Clustering")
    model = KMeans(n_clusters=numberOfClusters, init='k-means++', max_iter=100, n_init=1)
    model.fit(X)
    return model.labels_

def readDocumentsFromSolr(numberofrecords):
    contents = []
    solr = SolrClient('http://52.41.35.204:8983/solr')
    res = solr.query('tennisCollection',{
            'q':'*:*', 'rows':numberofrecords
    })
    for doc in res.docs:
        processedDocument = {}
        processedDocument['id'] = '';
        processedDocument['title'] = '';
        processedDocument['content'] = '';

        if 'id' in doc:
            processedDocument['id'] = doc['id'];
        if 'title' in doc:
            processedDocument['title'] = doc['title'];
        if 'content' in doc:
            processedDocument['content'] = doc['content'];

        contents.append(processedDocument)

    return contents


def getDocumentFromSolrById(solr,id):
    res = solr.search('id:\"' + str(id) + '\"')
    for doc in res.docs:
        return doc


def addDocumentsByReferringToSolr(documents):
    solr = pysolr.Solr('http://52.41.35.204:8983/solr/tennisCollection', timeout=10)
    i = 0
    n = len(documents)
    while (i < n):
        temp = documents[i:(i + 1000)]
        newList = []
        for doc in temp:
            retrievedDoc = {}
            if ("id" not in doc) or doc["id"] == '':
                doc["id"] = "default"
                retrievedDoc = doc
                retrievedDoc["id"] = "default"
            else:
                retrievedDoc = getDocumentFromSolrById(solr, doc['id'])
            retrievedDoc['kClusterId'] = doc['kClusterId'] if doc['kClusterId'] != None else kClusterSize + 1
            retrievedDoc['aggClusterId1'] = doc['aggClusterId1'] if doc['aggClusterId1'] != None else aggClusterSize + 1
            retrievedDoc['aggClusterId2'] = doc['aggClusterId2'] if doc['aggClusterId2'] != None else aggClusterSize + 1

            try:
                del retrievedDoc['_version_']
            except KeyError:
                pass

            newList.append(retrievedDoc)
        solr.add(newList)
        solr.commit(True)
        print("Completed writing to solr from " + str(i) + " to " + str(i + 1000))
        i = i + 1000


def prepareForClustering(documents):
    contents = []
    for document in documents:
        title =  str(document['title']).replace(u'\xa0',u'')
        content = str(document['content']).replace(u'\xa0',u'')
        contents.append(title+" "+content)
    return contents

def printDocuments(documents):
    for doc in documents:
        print(doc)


def findTotalNumberOfDocuments():
    solr = pysolr.Solr('http://52.41.35.204:8983/solr/tennisCollection', timeout=10)
    res = solr.search('*:*');
    return (res.raw_response['response']['numFound'])



datasetFile = "jsoncollection"
kMeansClusterFile = "kclusterResult"
aggClusterFile = "aggClusterResult"

totalDocuments = findTotalNumberOfDocuments();
print("Total Number Of Documents: "+ str(totalDocuments))

kClusterSize = 400
aggClusterSize = 50

print("\nStarting to read from Solr")
dataset = readDocumentsFromSolr(totalDocuments)
print("\nWriting the contents read from Solr to file")
writeToFile(datasetFile, dataset);

#################################################################################
#       Reading documents and performing clustering
#################################################################################
print("\n Reading documents from input file")
datasetFromFile = readFromFile(datasetFile)
print("\n Preparing cluster for K-Means clustering")
documentsToBeClustered = prepareForClustering(datasetFromFile)

#################################################################################
#       K Means clustering
#################################################################################

vectorizer = TfidfVectorizer(max_df=0.8, max_features=40000,lowercase=True,use_idf=True,
                                 min_df=0.2, stop_words='english', ngram_range=(1,1))
X = vectorizer.fit_transform(documentsToBeClustered)
labels = KMeansClusteringCustom(X, kClusterSize)

print("\nK Means Clustering completed")
kclusterDistribution = Counter(labels)

i = 0;
for document in datasetFromFile:
    document['kClusterId'] = labels[i];
    i = i+1;

print("\nWriting results of KMeans to file")

writeToFile(kMeansClusterFile, datasetFromFile)
print("\nK Means clustering distribution: "+str(kclusterDistribution))


##############################################################################

################################################################################
#      Agglomerative clustering within each K Means Cluster
################################################################################

print("\nReading results of k-Means Clustering")
kMeansClusteredDocuments = readFromFile(kMeansClusterFile)

print("\nReading K-Means clustering results completed ")
clusters = [[] for i in range(kClusterSize)]

for document in kMeansClusteredDocuments:
    clusters[document['kClusterId']].append(document)

for i in range(kClusterSize):
    print("\nInformaton of Kcluster: "+str(i))
    if len(clusters[i]) >= 2:
        documentsToBeClustered = prepareForClustering(clusters[i])
        vectorizer = TfidfVectorizer(max_features=40000,lowercase=True,use_idf=True, stop_words='english', ngram_range=(1,1))
        X = vectorizer.fit_transform(documentsToBeClustered)
        labelsAverage = AgglomerativeClusteringCustom(X, min(aggClusterSize, len(X.toarray())), "average")
        aggcluster1Distribution = Counter(labelsAverage)
        print(aggcluster1Distribution)

        labelsComplete = AgglomerativeClusteringCustom(X, min(aggClusterSize, len(X.toarray())), "complete")
        aggcluster2Distribution = Counter(labelsComplete)
        print(aggcluster2Distribution)
        j = 0;
        for document in clusters[i]:
            document['aggClusterId1'] = labelsAverage[j];
            document['aggClusterId2'] = labelsComplete[j];
            j = j + 1;
    else:
        clusters[i][0]['aggClusterId1'] = aggClusterSize + 1
        clusters[i][0]['aggClusterId2'] = aggClusterSize + 1


###############################################################################

################################################################################
#      Removing unnecessary fields
################################################################################


for document in kMeansClusteredDocuments:
    try:
        del document['title']
        del document['content']
    except KeyError:
        pass


writeToFile(aggClusterFile, kMeansClusteredDocuments)


#################################################################################

#Writing to Solr
print("Reading files from agg cluster result")
aggClusteredDocuments = readFromFile(aggClusterFile)
print("Reading files from agg cluster result completed")
addDocumentsByReferringToSolr(aggClusteredDocuments)

