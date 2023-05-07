import {useState} from "react";
import ChatPage from "./pages/ChatPage";
import LoginPage from "./pages/LoginPage";
import {IAuth} from "./api/types";

function ChatApp() {

  // Данные о текущем авторизованном пользователе
  // Содержит иформацию:
  //   1. userId - Идентификатор пользователя
  //   2. username - Имя пользователя (его логин)
  //   3. token - Jwt токен авторизации
  const [auth, setAuth] = useState<IAuth|null>(null);

  // Если пользователь авторизован переходим в приложение
  if (auth) {
    return <ChatPage auth={auth} setAuth={setAuth}/>
  }

  // В противном случае переходм на страницу входа
  return <LoginPage auth={auth} setAuth={setAuth}/>
}

export default ChatApp
