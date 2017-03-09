# WikipediaAnchorExtractor

* Download the latest Wikipedia dump. For example:
   `wget http://download.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2`

* Use the [WikiExtractor.py](https://github.com/attardi/wikiextractor) script from [Giuseppe Attardi](http://www.di.unipi.it/~attardi/) to extract Wikipedia ` python2.7 WikiExtractor.py -o extracted -ls enwiki-latest-pages-articles.xml.bz2`

* Set path to extracted Wikipedia articles in `run.sh` and run the following:
`sh run.sh`
