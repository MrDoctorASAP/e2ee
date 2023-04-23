import { useEffect, useState } from "react"
import LoadingPage from "./LoadingPage"
import SockJsClient from 'react-stomp';
import { getChats, getMessages, seen, sendMessage } from "../ref/components/api";
import Chats from "../components/Chats";
import Chat from "../components/Chat";
import '../ref/components/Styles.css'
import { MessageInput } from "@minchat/react-chat-ui";

function seenChatUpdater(chatId) {
  return chat => {
    if (chat.details.chatId != chatId) {
      return chat
    }
    return {
      ...chat,
      details: {
        ...chat.details,
        unseen: 0
      }
    }
  }
}

function chatOnMessageReceiveUpdater(currectChatId, chatId, message, sender) {
  return chat => {
    if (chat.details.chatId != chatId) {
      return chat
    }
    return {
      ...chat,
      details: {
        ...chat.details,
        unseen: currectChatId != chatId ? chat.details.unseen + 1 : 0
      },
      last: {
        sender,
        message
      }
    }
  }
}

function ChatPage({ auth, ...props }) {

  const [socketLoading, setSocketLoading] = useState(true)
  const [chatsLoading, setChatsLoading] = useState(true)

  const [chats, setChats] = useState([])
  const [messages, setMessages] = useState(new Map())
  const [chatMembers, setChatMembers] = useState(new Map())
  const [currentChatId, setCurrentChatId] = useState(null)

  useEffect(() => {
    if (!auth) return
    if (socketLoading) return
    getChats(auth)
      .then(chats => {
        console.log(chats)
        setChats(chats)
        setChatsLoading(false)
      })
  }, [auth, socketLoading])

  useEffect(() => {

    if (!auth) return
    if (!currentChatId) return

    if (!messages.has(currentChatId)) {
      messages.set(currentChatId, null)
      getMessages(auth, currentChatId)
        .then((chat) => {
          setChatMembers(new Map([...chatMembers].concat([[currentChatId,
            new Map(chat.members.map(member => [member.userId, member]))
          ]])))
          setMessages(new Map([...messages].concat([[currentChatId, chat.messages]])))
        })
    }
  }, [auth, currentChatId])

  const onMessageReceive = (messageEvent) => {
    // TODO: Fix back
    const message = messageEvent.message
    const sender = messageEvent.sender
    const shortMessage = {
      messageId: message.id,
      senderId: message.userId,
      text: message.message,
      date: message.date
    }
    setChats(chats.map(chatOnMessageReceiveUpdater(currentChatId, message.chatId, shortMessage, sender)))
    if (messages.has(message.chatId)) {
      setMessages(new Map([...messages].concat([[message.chatId, 
        [...messages.get(message.chatId), shortMessage]
      ]])))
    }
  }

  const onSocketMessage = (body, dest) => {
    console.log(body)
    console.log(dest)
    if (dest == '/topic/message') {
      onMessageReceive(body)
    }
  }

  const onSocketConnect = e => {
    setSocketLoading(false)
  }

  const onChatClick = (chatId) => {
    setCurrentChatId(chatId)
    setChats(chats.map(seenChatUpdater(chatId)))
    // seen(auth, chatId)
  }

  const onSendMessage = (message) => {
    if (currentChatId) {
      sendMessage(auth, currentChatId, message)
    }
  }

  const socket = <SockJsClient
    url='http://localhost:8080/ws'
    topics={['/topic/message']}
    onMessage={onSocketMessage}
    onConnect={onSocketConnect}
    debug={true}
  />

  if (socketLoading || chatsLoading) {
    return <>
      {socket}
      <LoadingPage />
    </>
  }

  const currentChat = currentChatId ?
    chats.filter(chat => chat.details.chatId == currentChatId)[0] : null

  const currentChatName = currentChat ?
    (currentChat.details.personal ?
      currentChat.personal.recipient.firstName + ' ' + currentChat.personal.recipient.lastName :
      currentChat.group.chatName)
    : ''

  const chat = messages.get(currentChatId) !== null ?
    <Chat userId={auth.userId} messages={messages.get(currentChatId)}
      chatMembers={chatMembers.get(currentChatId)} onSend={console.log} />
    : <LoadingPage />

  const messageInput = currentChat ? <MessageInput onSendMessage={onSendMessage} /> : undefined

  return <div>
    {socket}
    <div className='middle'>
      <div className='middle-offset'>
        <div className='chats-list'>
          <Chats chats={chats} onChatClick={onChatClick} />
        </div>
        <div className='chat-header'>
          {currentChatName}
        </div>
        <div className='current-chat'>
          {chat}
        </div>
        <div className='input-field-container'>
          {messageInput}
        </div>
      </div>
    </div>

  </div>
}

export default ChatPage
