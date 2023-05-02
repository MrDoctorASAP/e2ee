# Implementation note


# Авторизация

### Регистрация пользователя

```
Реализованно только на сервере
```

При входе пользователь указывает свои данные:
* Уникальный логин пользователя
* Пароль
* Email (необязательно)

Клиентское приложение посылает запрос на сервер: (путь `/api/v1/auth/register`)
В ответ получает jwt токен (Подробнее описанно далее)

### Вход пользователя

При входе пользователь указывает свои данные:
* Уникальный логин пользователя
* Пароль

Клиентское приложение посылает запрос на сервер: 

[ChatApi.ts:66](https://github.com/MrDoctorASAP/e2ee/blob/main/frontend/src/api/ChatApi.ts#L66)
```ts
export async function login(userCredentials: IUserCredentials): Promise<IAuth|null> {
  return await post('/api/v1/auth/login', userCredentials)
}
```

В ответе сервера содержится следующая информация:
```json
{
  "userId": 1,
  "username": "admin",
  "token": "..."
}
```

* `userId` - идентификатор пользователя
* `username` - логин пользователя
* `token` - специальный jwt токен

После успешной авторизации эта информация сохраняется в локальное хранилище и переиспользуется:

[SecureChatStorage.ts:8](https://github.com/MrDoctorASAP/e2ee/blob/main/frontend/src/model/SecureChatStorage.ts#L8)

```ts
export function storeAuth(auth: IAuth) {
  if (!auth) return
  localStorage.setItem('AUTH', JSON.stringify(auth))
}
```

При следующем входе на страницу она будет загруженна:

[SecureChatStorage.ts:14](https://github.com/MrDoctorASAP/e2ee/blob/main/frontend/src/model/SecureChatStorage.ts#L14)

```ts
export function loadAuth() {
  const auth = localStorage.getItem('AUTH')
  if (!auth) return null
  return JSON.parse(auth)
}
```

### Авторизованные запросы

При запросах к серверу требующих авторизации в запрос включается `token`.
По этому токену сервер сможет идентифицировать пользователя и проверить подлинность запроса

Токен включается в заголовок запроса в следующем формате:

```
authorization: Bearer [token]
```

Пример кода:
[ChatApi.ts:25](https://github.com/MrDoctorASAP/e2ee/blob/main/frontend/src/api/ChatApi.ts#L25)
```js
fetch(url, {
  headers: {
    'authorization': 'Bearer ' + token
  }
})

```

Страница логина - [LoginPage.jsx](https://github.com/MrDoctorASAP/e2ee/blob/main/frontend/src/ref/components/LoginPage.jsx)

### Jwt token
Jwt token - стандарт авторизации
https://ru.wikipedia.org/wiki/JSON_Web_Token


