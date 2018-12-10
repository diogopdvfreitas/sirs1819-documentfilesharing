# SIRS
Project for Network and Computer Security - 2018/2019

Development Enviroment:\
Java 11.0.1(LTS)\
IntelliJ IDEA 2018.2.6 (fyi)\
Maven 3.6.0

## Certificate 
To import certificate to jre use:\
$ keytool -importcert -file serverCertificate.crt -alias server -keystore $JDK_HOME/jre/lib/security/cacerts
$ keytool -importcert -file caCertificate.crt -alias ca -keystore $JDK_HOME/jre/lib/security/cacerts