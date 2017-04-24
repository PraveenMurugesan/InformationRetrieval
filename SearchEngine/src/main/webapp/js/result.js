/**
 * Created by ram on 4/22/17.
 */

var API_RELEVANCE_MODEL_SCORE = "query?order=SCORE";
var API_RELEVANCE_MODEL_PAGERANK = "query?order=RANK_SCORE";
var API_RELEVANCE_MODEL_HITS = "query?order=HIT_SCORE";
var API_QUERY_EXPANSION_ROCCHIO = "expand?scheme=Rocchio";
var API_QUERY_EXPANSION_ASSOCIATION = "expand?scheme=AssociationCluster";
var API_QUERY_EXPANSION_METRICS = "expand?scheme=MetricCluster";
var TEST_API="http://localhost:8080/tennis/test.json";
var BASE_URL = "http://localhost:8080/tennis/";

//models
var ROCCHIO="rocchio";
var METRICS_CLUSTER="metrics_cluster";
var ASSOCIATION_CLUSTER="association_cluster";
var PAGERANK="pagerank";
var HITS="hits";

var REQUEST_TIME=(new Date()).getTime();
var RESPONSE_TIME=0;

$(function () {

    var query = getParameterByName("q");
    var model = getParameterByName("model");

    var OFFSET_URL=getOffsetURL(model);
    //result=getSearchResults(TEST_API, query, 0, 10);
    getSearchResults(BASE_URL+OFFSET_URL, query, 0, 10);


    $(".next").on('click', 'a', function (e) {

        console.log("working");
        e.preventDefault();
        var query = getParameterByName("q");
        var start=this.name;
        var model = getParameterByName("model");
        var OFFSET_URL=getOffsetURL(model);
        //result=getSearchResults(TEST_API, query, start, 10);
        getSearchResults(BASE_URL+OFFSET_URL, query, start, 10);

    });


});


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
        case METRICS_CLUSTER:
            OFFSET_URL = API_QUERY_EXPANSION_METRICS;
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
    var htmlData = "<ul id='labels'>";
    for( var index in tokens) {
        htmlData += "<li><h5><span class='label label-info'>"+tokens[index]+"</span></h5></li>";
    }

    htmlData += "</ul>";
    return htmlData;
}

function generateResult(json) {

    RESPONSE_TIME=(new Date()).getTime();
    var htmlData = "";
    htmlData += "<div class='container' id='results'>";
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
        htmlData += "<div class='snippet'>"+documents[i].content.substr(0,170)+"...</div>";
        htmlData += "</div>";
    }
    htmlData += "</div>";

    return htmlData;
}


function generateFooter(start, count) {

    var htmlData = "<div class='footer'><ul class='pager'>";
    var prevLink="";
    var nextLink="";
    //disable Prev link if current start is 0
    if(start == 0) {
        prevLink="<li><a name="+start+" class='previous' style='pointer-events: none; cursor:default; float: left'> Prev </a></li>";
    }
    else {
        prevLink="<li><a name="+start+"  class='previous' style='float: left'> Prev</a></li>";
    }

    if(count < 10) {
        nextLink="<li><a name="+(start+10)+" class='next'  style='pointer-events: none; cursor:default; float: right'> Next </a></li>";
    }
    else {
        nextLink="<li><a name="+(start+10)+" class='next' style='float: right;'> Next</a></li>";
    }

    htmlData += prevLink+nextLink;
    htmlData += "</ul></div>";

    return htmlData;
}

function getOptions(query, start, count) {

    return "&query="+query+"&start="+start+"&rows="+count;
}

function getSearchResults(url, query, start, count) {

    console.log(url+getOptions(query, start, count));
    var queryString=url+getOptions(query, start, count);

    $.ajax({
        async: true,
        type: "GET",
        url: queryString,
        dataType: "json",
        success : function(data) {
            generateResultPage(data, start);
        }
    });

}

function getParameterByName(name) {

    var url = window.location.href;
    console.log(url);
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function generateResultPage(result, start) {

    if(result != null && result != undefined && result.documents.length > 0) {
        console.log(result);
        var resultEntries = generateResult(result);
        var footer = generateFooter(start, 10);
        document.getElementById("customSearch").innerHTML = resultEntries + footer;
    }
    else {

        document.getElementById("customSearch").innerHTML = "<html><p>Your search did not match any documents.</p> Suggestions: <ul><li>Make sure all words are spelled correctly.</li><li>Try different keywords.</li></ul></html>";
    }

}
