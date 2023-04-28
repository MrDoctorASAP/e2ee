import {useState} from "react";
import LoadingPage from "./pages/LoadingPage";
import ChatPage from "./pages/ChatPage";
import LoginPage from "./ref/components/LoginPage";

function ChatApp() {
    const [auth, setAuth] = useState(null);
    const [loading, setLoading] = useState(false)
    if (loading) {
        return <LoadingPage/>
    }
    if (auth && auth.token) {
        return <ChatPage auth={auth} setAuth={setAuth}/>
    }
    return <LoginPage setAuth={setAuth} setLoading={setLoading}/>
}

export default ChatApp
