#!/bin/sh

bin=`dirname "$0"`
KAFKA_ES_HOME=`cd "$bin/.."; pwd -P`
if [ "$1" != "" ]; then
	KAFKA_ES_HOME=$1
fi
PID_FILE=$KAFKA_ES_HOME/pids/kafka_es.pid

PID=`cat $PID_FILE`
if [ -f "$PID_FILE" ]; then 
	`cat "$PID_FILE" | xargs kill -9`
	echo "kill the old server successfully"

fi 



