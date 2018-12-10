#!/usr/bin/env bash
#SUBJ_WIN="//C=PT\ST=LISBON\L=LISBON\O=IST\CN=localhost"
#SUBJ_UNIX="/C=PT/ST=LISBON/L=LISBON/O=IST/CN=SIRS"
#SUBJ=${SUBJ_WIN}
#ROOT_NAME=ca-root
#CA_NAME=ca
#SERVER_NAME=server
#
#
#generate_root_ca() {
#    echo "Generating ca root key"
#    openssl genrsa -out ${ROOT_NAME}.key 2048
#    echo "Creating root ca certificate"
#    openssl req -new -x509 -days 1826 -key ${ROOT_NAME}.key -out ${ROOT_NAME}.crt -subj ${SUBJ}
#}
#
#generate_ca(){
#    echo "Generating ca key"
#    openssl genrsa -out ${CA_NAME}.key 4096
#    echo "Generating ca csr"
#    openssl req -new -key ${CA_NAME}.key -out ${CA_NAME}.csr -subj ${SUBJ}
#    echo "Signing ca with root ca"
#    openssl x509 -req -days 1000 -in ${CA_NAME}.csr -CA ${ROOT_NAME}.crt -CAkey ${ROOT_NAME}.key -CAcreateserial -out ${CA_NAME}.crt
#}
#
#generate_server(){
#    echo "Generating ca key"
#    openssl genrsa -out ${SERVER_NAME}.key 4096
#    echo "Generating server csr"
#    openssl req -new -key ${SERVER_NAME}.key -out ${SERVER_NAME}.csr -subj ${SUBJ}
#    echo "Signing server with root ca"
#    openssl x509 -req -days 1000 -in ${SERVER_NAME}.csr -CA ${ROOT_NAME}.crt -CAkey ${ROOT_NAME}.key -CAcreateserial -out ${SERVER_NAME}.crt
#
#    openssl x509 -text -noout -in ${SERVER_NAME}.crt
#}
#
#import_certificate(){
#    echo "Importing server certificate to keystore"
#    #keytool -import -alias server -file server.crt -keystore keystore.p12 -storepass password
#    keytool -v -storetype pkcs12 -keystore server.jks -importcert -trustcacerts -file server.crt
#}

generate_server(){
    echo "Generating keystore with self signed certificate for server"
    echo "Use \"localhost\" as CN (first and last name)"
    keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore_server.p12 -validity 3650
}

generate_ca(){
    echo "Generating keystore with self signed certificate for ca"
    echo "Use \"localhost\" as CN (first and last name)"
    keytool -genkeypair -alias ca -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore_ca.p12 -validity 3650
}

#To import certificate to jre use: $ keytool -importcert -file serverCertificate.crt -alias server -keystore $JDK_HOME/jre/lib/security/cacerts

generate_certificate_server(){
    echo "Generating client server certificate"
    keytool -export -keystore keystore_server.p12 -alias server -file serverCertificate.crt
}

generate_certificate_ca(){
    echo "Generating client ca certificate"
    keytool -export -keystore keystore_ca.p12 -alias ca -file caCertificate.crt
}

if [[ -z "$1" ]]
  then
    echo "No argument supplied"
    exit
fi

if test $1 = server; then
    generate_server
elif test $1 = ca; then
    generate_ca
elif test $1 = serverCert; then
    generate_certificate_server
elif test $1 = caCert; then
    generate_certificate_ca
elif test $1 = all; then
    generate_server
    generate_ca
    generate_certificate_server
    generate_certificate_ca
else
    echo "Unknown command"
fi
