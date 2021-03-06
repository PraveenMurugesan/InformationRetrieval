/**
 * Created by ram on 4/21/17.
 */

var BING="bing";
var GOOGLE="google";
var PAGE_SELECTED=GOOGLE;
var CUSTOM_PAGE_DEFAULT_MODEL="score";


$(function () {

    var modelText=$("#model").text();
    var model=$("#model").val();
    if(model == undefined || model == "" || model == null) {
        $("#model").text("Score");
        $("#model").val(CUSTOM_PAGE_DEFAULT_MODEL);
    }

    $("#customMenu").on('click', 'a', function () {

        var model=this.name;
        var modelText=this.text;
        if(model != undefined && model != "") {
            $("#model").text(modelText);
            $("#model").val(model);
            loadCustomSearchPage(model);
        }
    });

});

function loadResults() {

    if(PAGE_SELECTED == BING) {
        loadBingPage();
    }
    else {
        loadGooglePage();
    }

    loadCustomSearchPage($("#model").val());

}

function loadCustomSearchPage(model) {

    if(model == undefined) {
        model=CUSTOM_PAGE_DEFAULT_MODEL;
    }
    var query=document.getElementById("search").value;
    var customSearch = document.getElementById('customSearch');
    if(query.length > 0 ) {
        customSearch.src = "http://localhost:8080/tennis/result.html?q=" + query+"&model="+model+"&start=0&rows=10";
    }
}

function loadGooglePage() {

    PAGE_SELECTED=GOOGLE;
    var query=document.getElementById("search").value;
    var existingSearch = document.getElementById('existingSearch');
    if(query.length > 0 ) {
        existingSearch.src = "https://www.google.com/#q=" + query;
    }
}

function loadBingPage() {

    PAGE_SELECTED=BING;
    var query=document.getElementById("search").value;
    var existingSearch = document.getElementById('existingSearch');
    if(query.length > 0 ) {
        existingSearch.src = "https://www.bing.com/search?q=" + query;
    }
}

function resetCustomSearch() {

    var customSearch = document.getElementById('customSearch');
    customSearch.src="";
}

function resetExistingSearch() {

    var existingSearch = document.getElementById('existingSearch');
    existingSearch.src="";
}

$(document).keypress(function(e) {

    if(e.which == 13) {
        var query=document.getElementById("search").value;
        if(query.length > 0) {
            loadResults()
        }
        else {
            resetExistingSearch();
            resetCustomSearch();
        }
    }
});
