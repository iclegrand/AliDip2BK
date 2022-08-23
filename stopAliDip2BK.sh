SERVICE_NAME=AliDip2BK
PATH_TO_ALI=`pwd`
PID_PATH_NAME=$PATH_TO_ALI/AliDip2BK.pid
       if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
