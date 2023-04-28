import 'react-chat-elements/dist/main.css'
import ChatApp from "./ChatApp";
import { useEffect, useState } from "react";
import E2EEDemo from './E2EEDemo';
import UserList from './components/UserList';
import UserChoice from './components/UserChoice';

function App() {
    return <ChatApp/>
    // return <E2EEDemo />
}


export default App;

