# AliDip2BK
Collect selected Info from the CERN DIP system (LHC &amp; ALICE -DCS) and publish  them into the Bookkeeping/InfoLogger systems

A detailed description for this project is provided by Roberto in this document:
https://codimd.web.cern.ch/G0TSXqA1R8iPqWw2w2wuew
 

This program requires java 11 on a 64 bit system
(this is a constrain from the DIP library)

To test the java version run 
java -version 

The run configuration is defined in the AliDip2BK.properties file.

To run the program :

sh AliDip2BK.sh 

to Stop the program use CtrlC or kill PID command 
The program will enter into the shutdown mode and it will 
unsubscribe  to the DIP data providers will wait to process the DipData queue 
and it will close the DB connection 

