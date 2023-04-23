import { useState } from "react";
import ChatPage from "./pages/ChatPage";
import LoadingPage from "./pages/LoadingPage";
import LoginPage from "./ref/components/LoginPage";
import 'react-chat-elements/dist/main.css'

function App() {
  const [auth, setAuth] = useState(null);
  const [loading, setLoading] = useState(false)
  if (loading) {
    return <LoadingPage/>
  }
  if (auth && auth.token) {
    return <ChatPage auth={auth}/>
  }
  return <LoginPage setAuth={setAuth} setLoading={setLoading} />
}


export default App;

