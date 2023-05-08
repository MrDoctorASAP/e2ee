

# HTTPS

Для создания и подписи сертификата используется **openssl** (Улитита Linux)

#### Создание сертификата:

```
openssl req -x509 -nodes -new -sha256 -days 365 -newkey rsa:2048 \
    -keyout RootCA.key -out RootCA.pem -subj "/C=RU/CN=E2EE"
```

```
openssl x509 -outform pem -in RootCA.pem -out RootCA.crt
```

```
openssl req -new -nodes -newkey rsa:2048 -keyout e2ee.key -out e2ee.csr \
    -subj "/C=RU/ST=RU/L=RU/O=E2EE/CN=E2EE"
```

```
openssl x509 -req -sha256 -days 1024 -in e2ee.csr -CA RootCA.pem -CAkey RootCA.key \
    -CAcreateserial -extfile domains.ext -out e2ee.crt
```

Файл domains.ext

```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
```


### Создание KeyStore

```
openssl pkcs12 -export -out e2ee.p12 -inkey e2ee.key -in e2ee.crt
...
password: 123456
```

### Конфигурация сервера

```yml

server:
  ssl:
    key-store: classpath:e2ee.p12
    key-store-password: 123456
    key-store-type: pkcs12
    key-alias: 1
    key-password: 123456
  port: 3000

```

# CORS

Заголовки ответа сервера:

```
Access-Control-Allow-Origin: https://localhost:3000
Access-Control-Allow-Methods: *
Access-Control-Allow-Headers: *
Access-Control-Expose-Headers: Access-Control-Allow-Origin, Access-Control-Allow-Credentials
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 10
X-Frame-Options: SAMEORIGIN
```

