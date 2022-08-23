SERVICE_NAME=AliDip2BK
#PATH_TO_ALI=/home/cil/TEST_GIT/AliDip2BK
PATH_TO_ALI=`pwd`
BASE=$PATH_TO_ALI
DATE=`date +%d-%m-%y`
OPT=-Dorg.slf4j.simpleLogger.defaultLogLevel=error
CLASS=$BASE/lib64/dip-jni.nar:$BASE/lib/*:$BASE/AliDip2BK.jar:.:
PID_PATH_NAME=$PATH_TO_ALI/AliDip2BK.pid
LOG_FILE=$PATH_TO_ALI/LOG_${DATE}.txt

echo "Starting $SERVICE_NAME ..."
 export LD_LIBRARY_PATH=$BASE/lib64

     if [ ! -f $PID_PATH_NAME ]; then
            nohup java $OPT -classpath $CLASS alice.dip.AliDip2BK  >> $LOG_FILE 2>&1  &
            PID=$!
            echo $PID
            echo $PID  > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
