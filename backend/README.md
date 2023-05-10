

# Features

### Регистрация пользователей

Регистрация пользователя производится HTTP запросом на `/api/v1/auth/register` методом POST с предачей тела `UserRegistration` в формате JSON.

```java
// Тело запроса регистрации
public class UserRegistration {
    // Данные для регистрации
    private UserCredentials credentials;
    // Данные профиля пользователя
    private String firstName;
    private String lastName;
    private String email;
}

// Данные для регистрации/входа
public class UserCredentials {
    private String username; // Логин пользователя
    private String password; // Пароль
}
```

Сервер отправляет ответ с телом `AuthenticationToken` в формате JSON

```java
public class AuthenticationToken {
    private Long userId; // Идентификатор пользователя
    private String username; // Логин
    private String token; // Токен авторизации
}
```

Метод контроллера:

```java
@PostMapping("/api/v1/auth/register")
public AuthenticationToken register(@Valid @RequestBody UserRegistration user) {
    // Создание пользователя
    userService.createUser(user);
    // Авторизация
    return authService.authenticate(user.getCredentials());
}
```

Авторизация пользователя:

```java

public AuthenticationToken authenticate(String username, String password) {
    // Авторизация пользователя, через authenticationManager (класс Spring Security)
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
    );
    // Создание JWT токена авторизации
    String token = provider.createToken(username);
    // Загрузка пользователя из базы данных
    Long userId = userService.loadUserByUsername(username).getId();
    // Формирование ответа сервера
    return new AuthenticationToken(userId, username, token);
}

```

Создание JWT токена авторизации:

```java
// Создание JWT токена авторизации
public String createToken(String username) {
    // Полезные данные - логин пользователя
    Claims claims = Jwts.claims().setSubject(username);
    Date now = new Date();
    Date validity = new Date(now.getTime() + expired);
    return Jwts.builder()
            .setClaims(claims) // Полезные данные
            .setIssuedAt(now) // Дата создания токена
            .setExpiration(validity) // Срок годности
            .signWith(SignatureAlgorithm.HS256, secret) // Алгоритм подписи токена
            .compact();
}
```

# Web socket

Конфигурация:

```java

// Конфигурация веб сокета
public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    // Базовая конфигурация вэб сокета
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Устанавливаем модель обмена сообщениями с клиетом
        // topic - обмен сообщениями по модели "издатель-подписчик"
        registry.enableSimpleBroker("/topic");
    }
    
    // Регистрация точек доступа к веб сокету
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Добавляем точку доступа к вебсокету по пути /ws
        registry.addEndpoint("/ws")
                // Разрешённый origin для доступа к веб сокету 
                .setAllowedOrigins("https://localhost:3000")
                // Указание, что клиент будет работать с веб сокетом, через SockJS
                .withSockJS();
    }

    // Разрешить доступ к веб сокету клиентскому серверу
    protected boolean sameOriginDisabled() {
        return true;
    }

}

```

Работа с веб сокетом осуществялется, через класс `SimpMessagingTemplate` - это специальный класс библиотеки `spring-websocket`, который предоставляет методы для работы с веб сокетом.

Рассмотрим пример отправки сообщения через веб сокет:

```java

// Сервис для работы с веб сокетом
public class MessagingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    // Публикация события отпарвки сообщения
    public void publish(MessageEvent event) {
        // Отправляем обьект события "event" через веб сокет
        // Обьект будет преобразован в JSON формат 
        // и оправлен по протоколу STOMP
        // Это событие получат клиенты подписанные на "/topic/message"
        simpMessagingTemplate.convertAndSend("/topic/message", event);
    }

}

```

# HTTPS

Для создания и подписи сертификата используется **openssl** (Улитита Linux)

Файл **domains.ext** (Создать перед исполнением команд в папке исполнения)

```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
```

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
openssl x509 -req -sha256 -days 365 -in e2ee.csr -CA RootCA.pem -CAkey RootCA.key \
    -CAcreateserial -extfile domains.ext -out e2ee.crt
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

# Тестирование

Написанны unit тесты, которые тестируют отдельные компоненты системы:
* Создание пользователя
* Создание личного чата
* Создание групового чата
* Создание секретного чата
* Обмен ключей для секретного чата
* Отправка сообщения

Пример unit-теста:

```java

// Тестирование создания личного чата
@Test
void createPersonalChat() {

    // Создание тестовых пользователей
    User user1 = testSupport.createTestUser();
    User user2 = testSupport.createTestUser();

    // Вызов тестируемого метода
    Chat chat = chatService.createPersonalChat(user1, with(user2));

    // Проверяем, что созданный чат является личным
    assertThat(chat.isPersonal(), is(true));

    // Проверяем, что созданный чат содержит информацию о личном чате
    assertThat(chat.getPerosnalChatInfo(), is(notNullValue()));

    // Проверяем, что созданные чаты видны пользователям
    // Метод getChat вернёт чат пользователя по идентификатору,
    // getChat так же осуществит проверку прав доступа на просмотр чата
    Chat chatUser1 = chatService.getChat(user1, chat.getId());
    Chat chatUser2 = chatService.getChat(user2, chat.getId());
    assertThat(chat, is(equalTo(chatUser1)));
    assertThat(chat, is(equalTo(chatUser2)));

}

```

Также написанны интеграционные тесты, которые тестируют всю систему целиком:

Пример интеграционного теста:

```java

// Тестирование авторизации пользователя
@Test
void registerTest() {

    // Логин и пароль
    String username = "ExampleUser";
    String password = "password";

    // Тело запроса
    UserCredentials credentials = new UserCredentials(username, password);

    // Отправляем запрос POST
    mvc.perform(post("/api/v1/auth/login")
                    // Указываем тело запроса
                    .content(mapper.writeValueAsString(credentials))
                    // Указываем тип тела запроса: JSON 
                    .contentType(MediaType.APPLICATION_JSON))
            // Проверяем, что статус ответа - 200 OK
            .andExpect(status().isOk())
            // Проверяем, что ответ содержит идентификатор пользователя
            .andExpect(jsonPath("$.userId", is(not(emptyString())))) 
            // Проверяем, что ответ содержит jwt токен авторизации
            .andExpect(jsonPath("$.token", is(not(emptyString())))) 
            // Проверяем, что ответ содержит правильный логин пользователя
            .andExpect(jsonPath("$.username", is(equalTo(username))));
}

```


