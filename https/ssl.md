
Create root certificate
```

openssl req -x509 -nodes -new -sha256 -days 1024 -newkey rsa:2048 -keyout RootCA.key -out RootCA.pem -subj "/C=RU/CN=E2EE"
openssl x509 -outform pem -in RootCA.pem -out RootCA.crt

```

Create localhost certificate

```
openssl req -new -nodes -newkey rsa:2048 -keyout e2ee.key -out e2ee.csr -subj "/C=RU/ST=RU/L=RU/O=E2EE/CN=E2EE"
openssl x509 -req -sha256 -days 1024 -in e2ee.csr -CA RootCA.pem -CAkey RootCA.key -CAcreateserial -extfile domains.ext -out e2ee.crt
```

Create keystore

```
openssl pkcs12 -export -out e2ee.p12 -inkey e2ee.key -in e2ee.crt
```

