cls

Set PATH_BACKUP=%PATH%
Set PATH=C:\Program Files\Java\jdk1.6.0_21\bin;%PATH%

Set CLASSPATH_BACKUP=%CLASSPATH%
Set CLASSPATH=.;C:\eclipseworkspace\_resources\jar\ant\ant-contrib.jar;

C:\eclipseworkspace\_resources\apache-ant-1.7.1\bin\ant -buildfile build.xml -Ddrive_workspace=c -Ddrive_java=c -Dobfuscate=false -Declipseworkspace=eclipsews/eclipseworkspace_hsg

Set CLASSPATH=%CLASSPATH_BACKUP%
Set PATH=%PATH_BACKUP%