# log-analysis [![Build Status](https://travis-ci.org/zouzias/spark-hello-world.svg?branch=master)](https://travis-ci.org/zouzias/spark-hello-world)
This project aims to analyze the logs of HTTP requests made to the NASA Kennedy Space Center server

#Application Narrative
1. Data from the logs of HTTP requests made to NASA Kennedy Space Center is read into a spark dataframe
2. parse the dataframe for host endpoint and timestamp
3. cleanse the dataframe
4. parsing the timestamp to format which spark understands
5. get top n hosts per day
6. get top n urls per day

# Data 
**Download the dataset:**
* July: ftp://ita.ee.lbl.gov/traces/NASA_access_log_Jul95.gz


**Description of the dataset:** The logs are in ASCII files with one line per request with the following columns:

* Host making the request. A hostname when possible, otherwise the internet address if the name cannot be identified;
* Timestamp in the format "DAY / MONTH / YEAR: HH: MM: SS TIMEZONE";
* Requisition (in quotes);
* HTTP return code;
* Total bytes returned.

## Running the project

Pre requisite local setup
- SBT - 1.3.4
- Spark - 3.0.1 with hadoop 2.7
- Scala version - 2.12.8

Adjust SPARK_HOME path in spark-log-parser/submit-spark-log-parser.sh and run the script

## Running the project on docker

* Build the docker image with spark and scala env
```docker-compose build spark-scala-env```
* Build the image with the app jar
```docker-compose build app-spark-scala```
* Bring the spark env container
```docker-compose up -d --scale spark-worker=2 spark-worker```
* Submit the job 
```docker-compose up -d app-submit-job```

##Information extracted from the dataset
   
####1. top 5 hosts per day
    +---+--------------------+----+
    |day|                host|rank|
    +---+--------------------+----+
    |193|   indy.gradient.com|   1|
    |193|   bill.ksc.nasa.gov|   2|
    |193|marina.cea.berkel...|   3|
    |193|piweba3y.prodigy.com|   4|
    |193|piweba4y.prodigy.com|   5|
    |183|piweba3y.prodigy.com|   1|
    |183|  alyssa.prodigy.com|   2|
    |183|piweba1y.prodigy.com|   3|
    |183|disarray.demon.co.uk|   4|
    |183|www-d4.proxy.aol.com|   5|
####2. top 5 urls per day
    +---+--------------------+----+
    |day|            endpoint|rank|
    +---+--------------------+----+
    |193|/images/NASA-logo...|   1|
    |193|  /htbin/cdt_main.pl|   2|
    |193|/images/KSC-logos...|   3|
    |193|/shuttle/countdow...|   4|
    |193|/images/MOSAIC-lo...|   5|
    |183|/images/NASA-logo...|   1|
    |183|/images/KSC-logos...|   2|
    |183|/shuttle/countdow...|   3|
    |183| /shuttle/countdown/|   4|
    