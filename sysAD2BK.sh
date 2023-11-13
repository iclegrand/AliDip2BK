#!/bin/bash
BASE=`pwd`
DATE=`date +%d-%m-%y`
LOG_FILE=$BASE/LOG_${DATE}.txt
export LD_LIBRARY_PATH=$BASE/lib64
java  -Dorg.slf4j.simpleLogger.defaultLogLevel=error -classpath $BASE/lib64/dip-jni.nar:$BASE/lib/*:$BASE/AliDip2BK.jar:.: alice.dip.AliDip2BK >>$LOG_FILE 2>&1 &
