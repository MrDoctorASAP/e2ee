# e2ee
e2ee messenger

# Запуск

Перед запуском установить сертификат безопастности [https/RootCA.crt](https/RootCA.crt) в браузер.

Запустить серверную часть:

```
.\backend\api\mvnw.cmd spring-boot:run
```

Запустить клиентскую часть:

```
.\frontend\server\mvnw.cmd spring-boot:run
```

Открыть в браузере [https://localhost:3000](https://localhost:3000/)
