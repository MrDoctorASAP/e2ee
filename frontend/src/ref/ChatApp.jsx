import E2EE from '@chatereum/react-e2ee';
import 'react-chat-elements/dist/main.css'
import './components/Styles.css'
import ChatPage from './components/ChatPage';
import LoginPage from './components/LoginPage';
import { useEffect, useState, useRef } from "react";
import TestComp from './components/ChatContext';
import { getMessages } from './components/api';
import SockJsClient from 'react-stomp';

function ChatApp() {

  return <></>

  return <>
    <SockJsClient url='http://localhost:8080/gs-guide-websocket' topics={['/topic/message']}
          onMessage={(msg) => { console.log(msg); }} debug={true}/>
  </>

  const [auth, setAuth] = useState(null)
  const [isLoading, setLoading] = useState(true)

  const appInit = () => {
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('user')
    const userId = localStorage.getItem('userId')
    if (token) {
      setAuth({ token, username, userId })
    }
  }

  useEffect(() => {
    appInit()
    setLoading(false)
  }, [])

  useEffect(() => {
    if (auth && auth.token) {
      localStorage.setItem('token', auth.token)
      localStorage.setItem('user', auth.username)
      localStorage.setItem('userId', auth.userId)
    } else {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('userId')
    }
  }, [auth])

  if (isLoading) {
    return <p>Loading...</p>
  }

  if (auth && auth.token) {
    return (
      <div>
        <ChatPage auth={auth} />
      </div>
    );
  }
  return <LoginPage setLoading={setLoading} setAuth={setAuth} />
}

export default ChatApp;
