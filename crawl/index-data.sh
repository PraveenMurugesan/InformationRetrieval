#!/bin/bash

NUTCH_FOLDER=/Users/KiranKumar/Desktop/apache-nutch-1.12
CRAWLED_FOLDER=$NUTCH_FOLDER/crawl/atp_Roddick
SEGMENTS_FOLDER=$CRAWLED_FOLDER/segments
SOLR_URL=http://52.41.35.204:8983/solr/tennisCollection

cd $SEGMENTS_FOLDER 

for f in *; do
    if [[ -d $f ]]; then
        echo "Indexing Segment Folder $f"
	$NUTCH_FOLDER/bin/nutch solrindex $SOLR_URL $CRAWLED_FOLDER/crawldb -linkdb $CRAWLED_FOLDER/linkdb $SEGMENTS_FOLDER/$f
    fi
done
