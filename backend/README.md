
* [Регистрация пользователей](#регистрация-пользователей)
* [Аутентификация](#аутентификация)
* [Web socket](#web-socket)
* [Обмен ключей](#обмен-ключей)
* [Отправка сообщений в секретный чат](#отправка-сообщений-в-секретный-чат)
* [HTTPS](#https)
* [CORS](#cors)
* [Тестирование](#тестирование)

# Содержательная часть

* Анализ предметой области
    * Понятие системы мгновенного обмена сообщениями (мессенждера)
    * Понятие ифнормационной безопасности и в частности безопасности мессенджера
    * Обзор существующих мессенджеров
* Описание формальных требований к приложению
    * Осписание основынх возможностей мессенждера
    * Описание требований к безопастности
* Архитектура веб приложения
    * Клиент-серверная архитектура (+схема)
    * Понятие backend/frontend
* Реализация серверной части веб приложения
    * Что из себя представляет серверная часть веб приложения
    * База данных
        * Выбор базы данных
        * Проектирование базы данных (схемы)
        * Смеха базы данных (sql с пояснением)
    * Выбор технологий разработки серверной части веб приложения
        * Java
        * Java Servlet Api
            * Tomcat
        * Spring
    * Архитектура
    * Основные возможности системы
        * Контроллеры
            * Spring Web
            * Endpoints и назначение каждого
            * Пример кода контроллера
        * Сервисы
            * Обзор кода основных функций
        * Репозитории
            * Spring Data
            * Пример кода репозитория
    * WebSocket
        * Конфигурация
        * Оправка сообщений
    * Безопастность
        * Spring Security
            * Конфигурация Spring Security
        * Аутентификация
            * Создание JWT токена
            * Регистрация пользователя
            * Вход в систему
            * Аутентификация запроса
        * Https
        * Cors
    * Реализация секретных чатов
        * Обмен ключей
        * Оправка и принятие сообщений
* Тестирование
    * Выбор технологий тестирования
    * Unit-тестирование
    * Интеграционное тестирование

# Регистрация пользователей

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

# Аутентификация

Для аунтефикации пользоваетя по jwt токену, клиент включает в запрос 
заголовок Authorization, в котором укзаывает jwt токен

Заголовок имеет вид:

```
Authorization: Bearer [token]
```

По этому заголовку сервер сможет идентифицировать пользователя.

Получение токена из запроса:

```java

// Извлечение токена из запроса пользователя
public String resolveToken(Request request) {
    // Получение заголовка Authorization из запроса
    String bearerToken = request.getHeader("Authorization");
    // Если такой заголовок есть и начинается с ключевого слова Bearer
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        // Обрезаем ключевое слово Bearer
        return bearerToken.substring(7);
    }
    // В противном случае возвращяем null
    return null;
}

// Извлечение логина пользователя по токену
public String getUsernameByToken(String token) {
    return Jwts.parser()
        // Установка ключа подписи
        .setSigningKey(secret)
        // Парсинг токена и получение логина из тела токена
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
}

// Создание обьекта аутентификации из токена
public Authentication getAuthentication(String token) {
    
    // Получение логина из токена
    String username = getUsernameByToken(token);
    
    // Загрузка данных пользователя из базы данных
    UserDetails userDetails = service.loadUserByUsername(username);

    // Создание обьекта аутентификации на основе данных пользователя
    return new UsernamePasswordAuthenticationToken(userDetails);

}

```

При поступлении запроса от клиента, 
этот запрос обязан пройти через цепочку фильтров безопастности. 

Один из таких фильров это фильтр `JwtSecurityFilter`, 
который отвечает за аутентификацию пользователя по jwt токену.

Метод класса отвечающий за аутентификацию пользователя:

```java

// Филтрация зароса пользователя
public void doFilter(Request request, Response response){
    
    // Получаем токен из запроса
    String token = resolveToken(request);
    
    // Если в запросе есть токен
    if (token != null) {
        // Создание обьекта аутентификации из токена
        Authentication authentication = getAuthentication(token);
        // Устанавливаем обьект аутентификации в контекс запроса
        // В дальнейшем мы можем получить этот обьект из контекста для авторизации пользоваетя
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
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

# Обмен ключей

Аннтоация `@Transactional` означает, что все обращения к базе данных в данном методе
будут выполнены в одной транзакции.

```java

// Сервис реализующий методы обмена ключей
public class KeyExchangeService {

    // Серсивы
    private final UserService userService;
    private final SecureChatIdentityService idService;
    private final UserProfileService profileService;

    // Репозитории (для работы с базой данных)
    private final SecureChatMemberRepository memberRepository;
    private final SecureChatInviteRepository inviteRepository;
    private final SecureChatAcceptRepository acceptRepository;

    // Сервис для работы с веб сокетом
    private final MessagingService messagingService;

    // Создание секретного чата
    @Transactional
    public SecureChatId createSecureChat(User user, SecureChat secureChat) {

        // Генерируем идетификатор секретного чата
        SecureChatId secureChatId = idService.generateSecureChatId(user, secureChat);
        
        // Сохраняем учатников чата в базу данных
        memberRepository.saveAll(
                new SecureChatMember(secureChatId, user.getId()),
                new SecureChatMember(secureChatId, secureChat.getRecipientId())
        );

        // Создаём и сохраням приглашение в секретный чат
        SecureChatInvite invite = inviteRepository.save(new SecureChatInvite(
                user.getId(),
                secureChat.getRecipientId(),
                secureChatId,
                secureChat.getPublicKey()
        ));

        // Отправляем приглашение получателю через веб сокет
        messagingService.publish(invite);

        // Возвращяем идентификатор секретного чата отправителю
        return secureChatId;
    }

    // Получение приглашений в сектреный чат для указанного пользователя
    @Transactional
    public List<SecureChatInvite> getInvites(User user) {
        // Получение приглашений из базы данных по идентификатору пользователя
        return inviteRepository.findAllByRecipientId(user.getId());
    }

    // Принятие приглашения в сектреный чат
    @Transactional
    public void accept(User user, AcceptedSecureChat accept) {
        
        // Сохраняем публичный ключ прияного приглашения в базу данных
        acceptRepository.save(accept.getRecipientKey());
        
        // Удаляем приглашение из базы данных
        inviteRepository.deleteBySecureChatId(accept.getSecureChatId());
        
        // Отправляем принятое приглашение через веб сокет
        messagingService.publish(accept);

    }

    // Получение открытых ключей принятых приглашений для завершения обмена ключей
    @Transactional
    public List<RecipientKey> exchange(User user) {
        // Получение открытых ключей из базы данных по идентификатору пользователя
        return acceptRepository.findAllByUserId(user.getId());
    }

    // Завершение обмена ключей
    @Transactional
    public void complete(User user, SecureChatId secureChatId) {
        // Удаление из базы публичного ключа секретного чата по его идентификатору
        acceptRepository.deleteBySecureChatId(secureChatId);
    }
    
}
```

# Отправка сообщений в секретный чат

```java

// Отпарвка сообщения в секретный чат
public void sendMessage(User user, SecureChatMessage message) {
    
    // Сохранение зашифрованного сообщения 
    messageRepository.save(message);

    // Отпавка сообщения через веб сокет
    messagingService.publish(chatMessage);

}

// Получение всех новых сообщений во всех секретных чатах
public List<SecureChatMessage> getMessages(User user, List<SecureChatId> chatIds) {
    // Найти в базе данных все сообщения в секретных чатах по указанному идентификатору пользователя
    return messageRepository.findAllByRecipientIdAndSecureChatIds(user.getId(), chatIds);
}

// Удалить сообщения секретных чатов после получения
@Transactional
public void deleteMessages(User user, List<Long> messageIds) {
    // Удаление сообщений с указанными идентификаторами из базы данных
    messageRepository.deleteAllByIdIn(messageIds);
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
    protocol: tls
    key-store: classpath:e2ee.p12
    key-store-password: 123456
    key-store-type: pkcs12
    key-alias: 1
    key-password: 123456
  port: 8080

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


