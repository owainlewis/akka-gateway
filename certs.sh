#!/usr/bin/env bash

# Create a rootCA key
openssl genrsa -out rootCA.key 2048

openssl req -x509 -new -nodes -key rootCA.key -days 3560 -out rootCA.crt \
		-subj "/C=US/ST=California/L=San Francisco/O=Example/OU=Example/CN=example.com"

openssl genrsa -out server.key 2048

openssl req -new -key server.key -out server.csr \
		-subj "/C=US/ST=California/L=San Francisco/O=Example/OU=Example/CN=example.com"

openssl x509 -req -in server.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out server.crt -days 3560

cat server.crt rootCA.crt > chain.pem

openssl pkcs12 -export -name servercrt -in chain.pem -inkey server.key -out server.p12
