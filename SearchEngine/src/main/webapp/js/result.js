/**
 * Created by ram on 4/22/17.
 */

var API_CLUSTER = "cluster?";
var API_RELEVANCE_MODEL_SCORE = "query?order=score";
var API_RELEVANCE_MODEL_PAGERANK = "query?order=rankScore";
var API_RELEVANCE_MODEL_HITS = "query?order=hitScore";
var API_QUERY_EXPANSION_ROCCHIO = "query?expand=Rocchio";
var API_QUERY_EXPANSION_ASSOCIATION = "query?expand=AssociationCluster";
var API_QUERY_EXPANSION_METRIC = "query?expand=MetricCluster";
var TEST_API="http://localhost:8080/tennis/test.json";
var BASE_URL = "http://localhost:8080/tennis/";

//models
var ROCCHIO="rocchio";
var METRIC_CLUSTER="metric_cluster";
var ASSOCIATION_CLUSTER="association_cluster";
var PAGERANK="pagerank";
var HITS="hits";

var REQUEST_TIME=(new Date()).getTime();
var RESPONSE_TIME=0;

$(function () {

    var query = getParameterByName("q");
    var model = getParameterByName("model");
    var start= getParameterByName("start");
    var rows= getParameterByName("rows");

    var OFFSET_URL=getOffsetURL(model);
    //result=getSearchResults(TEST_API, query, 0, 10);
    getSearchResults(BASE_URL+OFFSET_URL, query, start, rows);

});

function getNextPage(linkElement) {

    REQUEST_TIME=(new Date()).getTime();
    //linkElement.preventDefault();
    var start=linkElement.name;
    var query = getParameterByName("q");
    var model = getParameterByName("model");
    var OFFSET_URL=getOffsetURL(model);
    //result=getSearchResults(TEST_API, query, start, 10);
    getSearchResults(BASE_URL+OFFSET_URL, query, start, 10);
}

function callClusterAPI(params) {

    var query = getParameterByName("q");
    console.log(BASE_URL+API_CLUSTER+params);
    getSearchResults(BASE_URL+API_CLUSTER+params, query, 0, 10);
}

function getOffsetURL(model) {

    var OFFSET_URL="";

    switch (model) {
        case PAGERANK:
            OFFSET_URL = API_RELEVANCE_MODEL_PAGERANK;
            break;
        case HITS:
            OFFSET_URL = API_RELEVANCE_MODEL_HITS;
            break;
        case ROCCHIO:
            OFFSET_URL = API_QUERY_EXPANSION_ROCCHIO;
            break;
        case METRIC_CLUSTER:
            OFFSET_URL = API_QUERY_EXPANSION_METRIC;
            break;
        case ASSOCIATION_CLUSTER:
            OFFSET_URL = API_QUERY_EXPANSION_ASSOCIATION;
            break;
        default:
            OFFSET_URL = API_RELEVANCE_MODEL_SCORE;
            break;
    }

    return OFFSET_URL;
}

function generateLabels(query) {

    var tokens = query.split(" ");
    console.log(tokens);
    var htmlData = "<div class='row'><ul id='labels'>";
    for( var index in tokens) {
        htmlData += "<li><h4><span class='label label-info'>"+tokens[index]+"</span></h4></li>";
    }

    htmlData += "</ul></div>";
    return htmlData;
}

function getContent(content) {

    var text="";
    if(content != null) {
        text = content.substr(0,170);
    }
    return text;
}

function generateResult(json) {

    RESPONSE_TIME=(new Date()).getTime();
    var htmlData = "";
    htmlData += "<div id='results'>";
    htmlData += "<div class='stats'>About "+json.matches+" results ("+((RESPONSE_TIME - REQUEST_TIME)/1000)+" seconds)</div>";

    if(json.expandedQuery != undefined || json.expandedQuery != null) {
        htmlData += generateLabels(json.expandedQuery);
    }

    var documents = json.documents;
    for(i=0; i<documents.length; i++) {
        htmlData += "<div class='entry'>";
        htmlData += "<div><a class='title' href="+documents[i].url+">"+documents[i].title+"</a></div>";
        //htmlData += "<div class='url'>"+documents[i].url+"</div>";
        htmlData += "<div class='url'>"+documents[i].url+"</div>";
        //htmlData += "<div><a class='dropdown-toggle' data-toggle='dropdown' class='url'>"+documents[i].url+"<span class='caret'></span></a><ul class='dropdown-menu'><li><a href='#' name='similar'>similar</a></li><li><a href='#' name='moresimilar'>More similar</a></li></ul></div>";
        htmlData += "<div class='snippet'>"+ getContent(documents[i].content)+"...</div>";
        htmlData += "<div class='clusterLinks'><ul>" +
            "<li><a href='#' class='clusterlink' name=kClusterId="+documents[i].kClusterId+" onclick='callClusterAPI(this.name)' >similar</a></li>" +
            "<li><a href='#' name=kClusterId="+documents[i].kClusterId+"&aggClusterId1="+documents[i].aggClusterId1+" onclick='callClusterAPI(this.name)' class='clusterlink'>similar (complete linkage)</a></li>" +
            "<li><a href='#' name=kClusterId="+documents[i].kClusterId+"&aggClusterId2="+documents[i].aggClusterId2+" onclick='callClusterAPI(this.name)' class='clusterlink'>similar (avg linkage)</a></li>" +
            "</ul></div>";
        /*
        htmlData += "<div class='clusterLinks'><ul>" +
            "<li><a name=kClusterId=168 onclick='callClusterAPI(this.name)' class='clusterlink'>similar</a></li>" +
            "<li><a name=kClusterId=168&aggClusterId1="+documents[i].aggClusterId1+" onclick='callClusterAPI(this.name)' class='clusterlink'>more similar (complete link)</a></li>" +
            "<li><a name=kClusterId=168&aggClusterId2="+documents[i].aggClusterId2+" onclick='callClusterAPI(this.name)' class='clusterlink'>more similar (avg link)</a></li>" +
            "</ul></div>";
        htmlData += "</div>"; */
    }
    htmlData += "</div>";

    return htmlData;
}


function generateFooter(start, count) {

    var htmlData = "<div class='footer'><ul class='pager'>";

    var prevLink="";
    var nextLink="";

    //disable Prev link if current start is 0
    if(parseInt(start,10) == 0) {
        //prevLink="<li><a name="+start+" class='previous' style='pointer-events: none; cursor:default; float: left'> Prev </a></li>";
    }
    else {
        prevLink="<li><a name="+(parseInt(start, 10)-10)+"  href='#' class='previous' onclick='getNextPage(this)' style='float: left'> Prev</a></li>";
    }

    if(count < 10) {
        //nextLink="<li><a name="+(start+10)+" class='next'  style='pointer-events: none; cursor:default; float: right'> Next </a></li>";
    }
    else {
        nextLink="<li><a name="+(parseInt(start, 10)+10)+" href='#' class='next' onclick='getNextPage(this)' style='float: right;'> Next</a></li>";
    }

    htmlData += prevLink+nextLink;
    htmlData += "</ul></div>";

    return htmlData;
}

function getOptions(query, start, count) {

    return "&query="+query+"&start="+start+"&rows="+count;
}

function getSearchResults(url, query, start, count) {

    var queryString = url + getOptions(query, start, count);
    var encodedQueryString=encodeURI(queryString);
    console.log(encodedQueryString);

    $.ajax({
        async: true,
        type: "GET",
        url: encodedQueryString,
        dataType: "json",
        success: function (data) {
            generateResultPage(start, data);
        },
        error: function(d) {
            console.log("Http Request Failed");
            document.getElementById("customSearch").innerHTML = "<html><p>Your search did not match any documents.</p> Suggestions: <ul><li>Make sure all words are spelled correctly.</li><li>Try different keywords.</li></ul></html>";
        }
    });
}

function getParameterByName(name) {

    var url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function generateResultPage(start, result) {

    if(result != null && result != undefined && result.documents.length > 0) {
        
        //console.log(result);
        var resultEntries = generateResult(result);
        var footer = generateFooter(start, 10);
        document.getElementById("customSearch").innerHTML = resultEntries + footer;
    }
    else {

        document.getElementById("customSearch").innerHTML = "<html><p>Your search did not match any documents.</p> Suggestions: <ul><li>Make sure all words are spelled correctly.</li><li>Try different keywords.</li></ul></html>";
    }

}
