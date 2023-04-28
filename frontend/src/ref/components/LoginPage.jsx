import { useEffect, useState } from "react";
import { loadAuth, storeAuth } from "../../model/SecureChatStorage";
import { login } from "./api";

function LoginPage({ setAuth, setLoading, ...props }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)

  useEffect(() => {
    setLoading(true)
    const auth = loadAuth()
    if (auth) {
      setAuth(auth)
    }
    setLoading(false)
  }, [])

  const onLogin = () => {
    setLoading(true)
    fetch('http://localhost:8080/api/v1/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'Application/json',
      },
      body: JSON.stringify({ username, password })
    })
      .then(resp => resp.json())
      .then(auth => {
        setAuth(auth)
        if (rememberMe) {
          storeAuth(auth)
        }
      })
      .then(e => setLoading(false))
      .catch(err => {
        console.log(err)
        setAuth(null)
        setLoading(false)
      });
    setUsername('')
    setPassword('')
  }
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