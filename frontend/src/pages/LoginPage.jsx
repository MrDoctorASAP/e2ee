import { useEffect, useState } from "react";
import { loadAuth, storeAuth } from "../model/SecureChatStorage";
import LoadingPage from "./LoadingPage";
import {login, register} from "../api/ChatApi";
import { Button, Card, Form, NavLink, Tab, Tabs } from "react-bootstrap";
import CardHeader from "react-bootstrap/esm/CardHeader";

function LoginPage({ auth, setAuth, ...props }) {

  // Состояние загрузки данных
  const [loading, setLoading] = useState(true)


  const [username, setUsername] = useState('')
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [password, setPassword] = useState('')

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
      // Сохраняем данные о авторизации в локальное хранилище
      storeAuth(auth)
    }
    // Данные загруженны
    setLoading(false)
  }

  const onRegister = async () => {
    // Устанавливаем флаг загрузки данных
    setLoading(true)

    // Убираем данные пользвателя из полей ввода
    setUsername('')
    setFirstName('')
    setLastName('')
    setPassword('')

    // Посылаем запрос на сервер для авторизации
    const auth = await register({credentials: {username, password}, firstName, lastName, email: ''})
    // Если данные успешно получены
    if (auth) {
      // Устанвливаем в состояние
      setAuth(auth)
      // Сохраняем данные о авторизации в локальное хранилище
      storeAuth(auth)
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
    <Tabs defaultActiveKey="login">
      <Tab eventKey="login" title="Login">
      <Card style={{borderTop: "none", borderTopLeftRadius: "0", borderTopRightRadius: "0"}}>
      <Card.Body>
      <h2 className="login-header">E2EE Messanger</h2>
    <Form className="login-form" onSubmit={e => {
      e.preventDefault()
      onLogin().catch(console.log).finally(() => setLoading(false))
      }}>
      <Form.Group className="mb-3">
        <Form.Control type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
      </Form.Group>
      <Form.Group className="mb-3">
        <Form.Control type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
      </Form.Group>
      <Form.Group className="mb-3 login-button-group">
        <Button variant="primary" type="submit" className="w-100">Login</Button>
      </Form.Group>
    </Form>
      </Card.Body>
    </Card>
      </Tab>
      <Tab eventKey="register" title="Register">
      <Card style={{borderTop: "none", borderTopLeftRadius: "0", borderTopRightRadius: "0"}}>
      <Card.Body>
      <h2 className="login-header">E2EE Messanger</h2>
    <Form className="login-form"onSubmit={e => {
      e.preventDefault()
      onRegister().catch(console.log).finally(() => setLoading(false))
      }}>
      <Form.Group className="mb-3">
        <Form.Control type="text" placeholder="First name" value={firstName} onChange={e => setFirstName(e.target.value)} />
      </Form.Group>
      <Form.Group className="mb-3">
        <Form.Control type="text" placeholder="Last Name" value={lastName} onChange={e => setLastName(e.target.value)} />
      </Form.Group>
      <Form.Group className="mb-3">
        <Form.Control type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
      </Form.Group>
      <Form.Group className="mb-3">
        <Form.Control type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
      </Form.Group>
      <Form.Group className="mb-3 login-button-group">
        <Button variant="primary" type="submit" className="w-100">Register</Button>
      </Form.Group>
    </Form>
      </Card.Body>
    </Card>
      </Tab>
    </Tabs>
    
  </div>
}

export default LoginPage;
