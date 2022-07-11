SERVICE_NAME=AliDip2BK
PATH_TO_ALI=/home/cil/TEST_GIT/AliDip2BK/
ALIRUN=$PATH_TO_ALI/runAliDip2BK.sh
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

