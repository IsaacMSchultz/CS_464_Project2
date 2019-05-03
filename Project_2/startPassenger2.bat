@echo off
cd bin
java -Dfile.encoding=Cp1252 -classpath "E:\Git\CS_464\Project 2\Project_2\bin;E:\Programs\RTI-Connext\rti_connext_dds-5.3.1\lib\java\nddsjava.jar" PassengerLauncher Express2 3 2
pause