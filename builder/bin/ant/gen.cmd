cls
Set PATH_BACKUP=%PATH%
Set PATH=%PATH%;%1:\java\jdk1.5.0\bin

Set CLASSPATH_BACKUP=%CLASSPATH%
Set CLASSPATH=.;C:\java\antAddOns\commons-net-1.4.1\commons-net-1.4.1.jar;C:\java\antAddOns\jakarta-oro-2.0.8\jakarta-oro-2.0.8.jar;

%1:\java\apache-ant-1.6.2\bin\ant -buildfile build.xml -Ddrive_workspace=%2 -Ddrive_java=%1 %3 %4 %5 %6

Set PATH=%PATH_BACKUP%
Set CLASSPATH=%CLASSPATH_BACKUP%