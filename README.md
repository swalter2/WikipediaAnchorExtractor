# WikipediaAnchorExtractor

* Download the latest Wikipedia dump. For example:
   `wget wget http://download.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2`

* Use the [WikiExtractor.py](http://medialab.di.unipi.it/wiki/Wikipedia_Extractor) script by Medialab to extract Wikipedia ` python2.7 WikiExtractor.py -o extracted enwiki-latest-pages-articles.xml.bz2`

* Set path to extracted Wikipedia articles in `run.sh` and run the following:
`sh run.sh`