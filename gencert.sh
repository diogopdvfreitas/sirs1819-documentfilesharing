#!/usr/bin/env bash
SUBJ_WIN="//C=PT\ST=LISBON\L=LISBON\O=IST\CN=localhost"
SUBJ_UNIX="/C=PT/ST=LISBON/L=LISBON/O=IST/CN=SIRS"
SUBJ=${SUBJ_WIN}
ROOT_NAME=ca-root
CA_NAME=ca
SERVER_NAME=server
STOREPASS="password"


generate_root_ca() {
    echo "Generating ca root key"
    openssl genrsa -out ${ROOT_NAME}.key 2048
    echo "Creating root ca certificate"
    openssl req -new -x509 -days 1826 -key ${ROOT_NAME}.key -out ${ROOT_NAME}.crt -subj ${SUBJ}
}

generate_ca(){
    echo "Generating ca key"
    keytool -storepass ${STOREPASS} -genkey -alias ${CA_NAME} -keyalg RSA -storetype pkcs12 -keystore ${CA_NAME}.p12 -keysize 2048 -dname "CN=localhost, OU=SIRS, O=IST, L=Lisbon, S=Lisbon, C=PT"
    echo "Generating server csr"
    keytool -storepass ${STOREPASS} -certreq -alias ${CA_NAME} -storetype pkcs12 -keystore ${CA_NAME}.p12 -file ${CA_NAME}.csr
    echo "Signing server with root ca"
    openssl x509 -req -days 1000 -in ${CA_NAME}.csr -CA ${ROOT_NAME}.crt -CAkey ${ROOT_NAME}.key -CAcreateserial -out ${CA_NAME}.crt

    keytool -import -trustcacerts -alias ${CA_NAME} -file ${CA_NAME}.crt -storetype pkcs12 -keystore ${CA_NAME}.p12

    openssl x509 -text -noout -in ${CA_NAME}.crt
}

generate_server(){
    echo "Generating server key"
    keytool -storepass ${STOREPASS} -genkey -alias ${SERVER_NAME} -keyalg RSA -storetype pkcs12 -keystore ${SERVER_NAME}.p12 -keysize 2048 -dname "CN=localhost, OU=SIRS, O=IST, L=Lisbon, S=Lisbon, C=PT"
    echo "Generating server csr"
    keytool -storepass ${STOREPASS} -certreq -alias ${SERVER_NAME} -storetype pkcs12 -keystore ${SERVER_NAME}.p12 -file ${SERVER_NAME}.csr
    echo "Signing server with root ca"
    openssl x509 -req -days 1000 -in ${SERVER_NAME}.csr -CA ${ROOT_NAME}.crt -CAkey ${ROOT_NAME}.key -CAcreateserial -out ${SERVER_NAME}.crt

    keytool -import -trustcacerts -alias ${SERVER_NAME} -file ${SERVER_NAME}.crt -storetype pkcs12 -keystore ${SERVER_NAME}.p12

    openssl x509 -text -noout -in ${SERVER_NAME}.crt
}

import_certificate(){
    echo "Importing server certificate to keystore"
    #keytool -import -alias server -file server.crt -keystore keystore.p12 -storepass password
    keytool -import -trustcacerts -alias ${SERVER_NAME} -file ${SERVER_NAME}.crt -keystore ${SERVER_NAME}.jks
}

#To import certificate to jre use: $ keytool -importcert -file serverCertificate.crt -alias server -keystore $JDK_HOME/jre/lib/security/cacerts

generate_certificate_server(){
    echo "Generating client server certificate"
    keytool -storepass ${STOREPASS} -export -storetype pkcs12 -keystore ${SERVER_NAME}.p12 -alias ${SERVER_NAME} -file ${SERVER_NAME}Certificate.crt
}

generate_certificate_ca(){
    echo "Generating client ca certificate"
    keytool -storepass ${STOREPASS} -export -storetype pkcs12 -keystore ${CA_NAME}.p12 -alias ${CA_NAME} -file ${CA_NAME}Certificate.crt
}

if [[ -z "$1" ]]
  then
    echo "No argument supplied"
    exit
fi

if test $1 = root; then
    generate_root_ca
elif test $1 = ca; then
    generate_ca
elif test $1 = server; then
    generate_server
elif test $1 = serverCert; then
    generate_certificate_server
elif test $1 = caCert; then
    generate_certificate_ca
elif test $1 = all; then
    generate_root_ca
    generate_ca
    generate_server
    generate_certificate_server
    generate_certificate_ca
else
    echo "Unknown command"
fi
