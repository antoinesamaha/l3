c:\java\jdk1.5.0\bin\keytool -genkey -alias alias-name -keystore keystore-name
c:\java\jdk1.5.0\bin\jarsigner -keystore keystore-name -storepass 01barmaja -keypass 01barmaja c:\exe\app\devtask\build\jar\devtask.jar alias-name
c:\java\jdk1.5.0\bin\jarsigner -keystore keystore-name -storepass 01barmaja -keypass 01barmaja c:\exe\app\devtask\build\jar\foc.jar alias-name