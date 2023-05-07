import { useEffect, useState } from "react";
import { loadAuth, storeAuth } from "../model/SecureChatStorage";
import LoadingPage from "./LoadingPage";
import {login} from "../api/ChatApi";

function LoginPage({ auth, setAuth, ...props }) {

  // Состояние загрузки данных
  const [loading, setLoading] = useState(true)

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)

  // При первой загрузке страницы,
  // проверяем нет ли сохранённых данных о авторизованном пользователе
  useEffect(() => {

    // Загружаем иформацию о авторизванном пользователе из локального хранилища
    const auth = loadAuth()
    // Если таковая имеется, то устанавливаем
    if (auth) {
      setAuth(auth)
    }
    // Данные загруженны
    setLoading(false)
  }, [auth, setAuth])

  // Действия при подтвеждении входа в систему
  const onLogin = async () => {
    // Устанавливаем флаг загрузки данных
    setLoading(true)

    // Убираем данные пользвателя из полей ввода
    setUsername('')
    setPassword('')
    // Посылаем запрос на сервер для авторизации
    const auth = await login({username, password})
    // Если данные успешно получены
    if (auth) {
      // Устанвливаем в состояние
      setAuth(auth)
      if (rememberMe) {
        // Если поставлен флаг rememberMe
        // Сохраняем данные о авторизации в локальное хранилище
        storeAuth(auth)
      }
    }
    // Данные загруженны
    setLoading(false)
  }

  // Если загружаются данные, показываеем страницу загрузки
  if (loading) {
    return <LoadingPage/>
  }

  // В противном случае показывем форму ввода логина/пароля
  return <div className="login-page">
    <p className="login-line"><input className="login-input"
      value={username}
      onChange={e => setUsername(e.target.value)}
      placeholder="username" /></p>
    <p className="login-line">
      <input className="login-input"
        value={password}
        onChange={e => setPassword(e.target.value)}
        type={'password'}
        placeholder="password" />
    </p>
    <p className="login-line">
      <input type={'checkbox'} 
      checked={rememberMe} 
      onChange={e => setRememberMe(e.target.checked)}/> Remember Me</p>
    <p className="login-line"><button className="login-button"
      onClick={e => onLogin()}>Log in</button></p>
  </div>
}

export default LoginPage;