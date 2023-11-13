line=$(ps aux | grep AliDip2BK | grep -v grep) 
if [ -z "$line" ]
then
    mail -s "AliDip2BK NOT RUNNING" iosif.legrand@cern.ch  < /dev/null
    echo "- $(date) AliDip2BK NOT running ! Sent email !"

else 
  #true
    echo "* $(date) AliDip2BK RUNNING"
  # echo $line
fi
