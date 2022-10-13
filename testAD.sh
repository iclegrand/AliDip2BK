line=$(ps aux | grep AliDip2BK | grep -v grep)
if [ -z "$line" ]
then
    echo "AliDip2BK NOT running"
     mail -s "AliDip2BK NOT RUNNING" iosif.legrand@cern.ch < /dev/null

else
  echo "AliDip2BK RUNNING"
#  echo $line
fi
