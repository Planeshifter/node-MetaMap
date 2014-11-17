#!/bin/sh

BASEDIR=$(dirname $0)

CP=$BASEDIR/lib/httpclient-4.1.1.jar:$BASEDIR/lib/httpclient-cache-4.1.1.jar
CP=$CP:$BASEDIR/lib/httpcore-4.1.jar:$BASEDIR/lib/httpcore-nio-4.1.jar
CP=$CP:$BASEDIR/lib/httpmime-4.1.1.jar
CP=$CP:$BASEDIR/lib/commons-logging-1.1.1.jar
CP=$CP:$BASEDIR/lib/skrAPI.jar

echo Compiling skrAPI sources
echo javac -cp $CP -d $BASEDIR/classes `find sources -name \*.java`
javac -cp $CP -d $BASEDIR/classes `find sources -name \*.java`

echo Building jar file
echo jar cf $BASEDIR/lib/skrAPI.jar -C classes .
jar cf $BASEDIR/lib/skrAPI.jar -C classes .

echo Compiling examples
echo javac -cp $CP -d $BASEDIR/classes `find examples -name \*.java`
javac -cp $CP -d $BASEDIR/classes `find examples -name \*.java`

exit 0
