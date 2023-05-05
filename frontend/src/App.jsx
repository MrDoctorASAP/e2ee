import { useEffect, useState } from 'react';
import 'react-chat-elements/dist/main.css'
import ChatApp from "./ChatApp"
import Fingerprint from './components/Fingerprint';

function App() {

//    const [bytes, setBytes] = useState()

//    useEffect(() => {
//        setBytes(window.crypto.getRandomValues(new Uint8Array(64)))
//    }, [])

    return <ChatApp/>
//    return <Fingerprint fingerprint={bytes} />
}

export default App;
