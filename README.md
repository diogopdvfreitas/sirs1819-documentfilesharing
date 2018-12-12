# SIRS
Project for Network and Computer Security - 2018/2019

Development Enviroment:\
Java 11.0.1(LTS)\
IntelliJ IDEA 2018.2.6 (fyi)\
Maven 3.6.0


## Certificate 
* Run script with: bash gencer.sh all  (type yes, to trust certificates into keystore)
* Copy keystore .p12 to resources folders
* Import certificate to $JDK_HOME/jre/lib/security/cacerts by doing:\
$ keytool -importcert -file serverCertificate.crt -alias server -keystore $JDK_HOME/jre/lib/security/cacerts\
$ keytool -importcert -file caCertificate.crt -alias ca -keystore $JDK_HOME/jre/lib/security/cacerts

##Build & Run
mvn clean install\
mvn spring-boot:run -Dspring-boot.run.arguments=--primary,--server.port=30001\
mvn spring-boot:run -Dspring-boot.run.arguments=--secondary,--server.port=30002\
mvn exec:java
