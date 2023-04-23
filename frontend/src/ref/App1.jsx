import ChatApp from "./ChatApp";
import { MainContainer } from "@minchat/react-chat-ui";
import { useEffect, useState } from "react";
import { MapModel } from "./components/model";
import Chat from "./components2/Chat";
import { getChats, getMessages, sendMessage } from "./components/api";
import LoginPage from "./components/LoginPage";
import SockJsClient from 'react-stomp';

function App1() {

  const [auth, setAuth] = useState(null)
  const [loading, setLoading] = useState(false)
  const [chats, setChats] = useState([])
  const [messages, setMessages] = useState(new Map())
  const [chatMembers, setChatMembers] = useState(new Map())
  const [chatId, setChatId] = useState(undefined)
  const [chatMapping, setChatMapping] = useState([])
  const [socketLoading, setSocketLoading] = useState(true)
  
  useEffect(() => {
    if (!auth) return
    getChats(auth).then(chats => {
      console.log(chats)
      setChatMapping(chats.map(chat => chat.details.chatId))
      const processed = chats.map(chat => {
        var base = {
          id: chat.details.chatId,
        }
        if (chat.last) {
          base = {
            lastMessage: {
              user: {
                name: chat.last.sender.username,
                id: chat.last.sender.userId
              },
              seen: false,
              text: chat.last.message.text
            },
            ...base
          }
        }
        if (chat.details.personal) {
          base = {
            title: chat.personal.recipient.username,
            ...base
          }
        } else {
          base = {
            title: chat.group.chatName,
            ...base
          }
        }
        return base
      })
      setChats(processed)
      console.log(processed)
      setLoading(false)
    })
  }, [auth])

  useEffect(() => {
    if (!auth) return
    if (!messages.has(chatId)) {
      messages.set(chatId, null)
      getMessages(auth, chatId)
        .then(chat => {
          const users = new Map(chat.members.map(member => [member.userId, member]))
          const chatMessages = chat.messages.map(message => {
            const sender = users.get(message.senderId)
            return {
              user: {
                name: sender.firstName,
                id: sender.userId
              },
              text: message.text
            }
          })
          setChatMembers(new Map([...chatMembers].concat([[chatId, users]])))
          setMessages(new Map([...messages].concat([[chatId, chatMessages]])))
          console.log('loaded')
        })
    }
  }, [chatId])

  const onChatSelect = (index) => {
    setChatId(chatMapping[index])
  }

  const reciveMessage = (messageEvent) => {
    console.log(messageEvent)
    const message = messageEvent.message
    const sender = messageEvent.sender
    console.log(message)
    setChats(chats.map(chat => {
      if (chat.id != message.chatId) {
        return chat
      }
      return {
        ...chat,
        lastMessage: {
          user: {
            name: sender.username,
            id: sender.userId
          },
          seen: false,
          text: message.message
        }
      }
    }))
    if (messages.has(message.chatId)) {
      const users = chatMembers.get(message.chatId)
      const newMessages = [...messages.get(message.chatId), {
        user: {
          name: users.get(message.userId).username,
          id: users.get(message.userId).userId
        },
        text: message.message
      }]
      setMessages(new Map([...messages].concat([[message.chatId, newMessages]])))
    }
  }

  const onSendMessage = message => {
    if (chatId) {
      sendMessage(auth, chatId, message)
    }
  }

  if (loading) {
    return <Chat
      loading={true}
    />
  }

  if (auth && auth.token) {
    
    const socket = <SockJsClient url='http://localhost:8080/ws' topics={['/topic/message']}
      onMessage={(msg) => { reciveMessage(msg) }} onConnect={e => setSocketLoading(false)} debug={true} />

    if (socketLoading) {
      return <>
        {socket}
        <Chat
          loading={true}
        />
      </>
    }

    return <>
      {socket}
      <Chat
        auth={auth}
        chats={chats}
        chatId={chatId}
        messages={messages}
        setChatId={onChatSelect}
        chatName=""
        sendMessage={onSendMessage}
        loading={loading}
      />
    </>
  }

  return <LoginPage setAuth={setAuth} setLoading={setLoading} />

}

export default App1;
