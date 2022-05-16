#start the Alice Dip to Bookkeeping program
BASE=`pwd`
export LD_LIBRARY_PATH=$BASE/lib64
java  -Dorg.slf4j.simpleLogger.defaultLogLevel=error -classpath $BASE/lib64/dip-jni.nar:$BASE/lib/*:$BASE/AliDip2BK.jar:.: alice.dip.AliDip2BK
