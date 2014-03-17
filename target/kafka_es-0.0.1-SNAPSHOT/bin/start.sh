#!/bin/sh

export LANG=en_US.UTF-8

bin=`dirname "$0"`
KAFKA_ES_HOME=`cd "$bin/.."; pwd -P`
KAFKA_ES_CONFIG_DIR=$KAFKA_ES_HOME/conf

KAFKA_ES_LOGS=$KAFKA_ES_HOME/logs
mkdir -p $KAFKA_ES_LOGS

# add all lib and config in classpath 
ELE_LIB=$KAFKA_ES_HOME/lib
CLASSPATH=$KAFKA_ES_CONFIG_DIR
for jar in `ls $ELE_LIB/*.jar`
do
      CLASSPATH="$CLASSPATH:""$jar"
done

JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:$KAFKA_ES_LOGS/gc.log -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$KAFKA_ES_LOGS/gc_dump"
nohup java $JAVA_OPT -classpath $CLASSPATH com.sohu.tv.m.kafka.es.IndexLoader  $1>>/dev/null 2>&1 &

# add pid file
PID_FOLDER=$KAFKA_ES_HOME/pids
mkdir -p $PID_FOLDER
echo $!>$PID_FOLDER/kafka_es.pid
echo "pid file created successfully"
