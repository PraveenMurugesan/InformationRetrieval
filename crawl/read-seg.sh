#!/bin/bash

NUTCH_FOLDER=/Users/KiranKumar/Desktop/apache-nutch-1.12-AWS
CRAWLED_FOLDER=$NUTCH_FOLDER/crawl/crawled_pages
SEGMENTS_FOLDER=$CRAWLED_FOLDER/segments
SOLR_URL=http://127.0.0.1:8983/solr/

cd $SEGMENTS_FOLDER 

for f in *; do
    if [[ -d $f ]]; then
        echo "Segment Folder $f"
#	$NUTCH_FOLDER/bin/nutch solrindex $SOLR_URL $CRAWLED_FOLDER/crawldb -linkdb $CRAWLED_FOLDER/linkdb $SEGMENTS_FOLDER/$f
	$NUTCH_FOLDER/bin/nutch readseg -dump $SEGMENTS_FOLDER/$f $NUTCH_FOLDER/crawl/dumps
    fi
done
