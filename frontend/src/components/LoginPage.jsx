import { useState } from "react";
import { login } from "./api";

function LoginPage({ setAuth, setLoading, ...props }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
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
      .then(setAuth)
      .then(e => setLoading(false))
      .catch(err => {
        console.log(err)
        setAuth(null)
        setLoading(false)
      });
    setUsername('')
    setPassword('')
  }
  return <div>
    <input value={username} onChange={e => setUsername(e.target.value)} placeholder="username" />
    <input value={password} onChange={e => setPassword(e.target.value)} type={'password'} placeholder="password" />
    <button onClick={e => onLogin()}>Log in</button>
  </div>
}

export default LoginPage;