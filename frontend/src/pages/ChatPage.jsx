import {useEffect, useRef, useState} from "react"
import LoadingPage from "./LoadingPage"
import SockJsClient from 'react-stomp';
import { getChats, getMessages, seen, sendMessage } from "../ref/components/api";
import Chats from "../components/Chats";
import Chat from "../components/Chat";
import '../ref/components/Styles.css'
import { MessageInput } from "@minchat/react-chat-ui";
import { useChatCache, useChatList } from "../hooks/ChatHook";

function ChatPage({ auth, ...props }) {

  const [socketLoading, setSocketLoading] = useState(true)
  const [chatsLoading, setChatsLoading] = useState(true)

  const chatList = useChatList()
  const chatCache = useChatCache()
  const [currentChatId, setCurrentChatId] = useState(null)

  const chatEndRef = useRef(null)

  useEffect(() => {
    if (!auth) return
    if (socketLoading) return
    getChats(auth)
      .then(chats => {
        chatList.setChats(chats)
        setChatsLoading(false)
      })
  }, [auth, socketLoading])

  useEffect(() => {

    if (!auth) return
    if (!currentChatId) return

    if (!chatCache.isLoading(currentChatId) && !chatCache.isLoaded(currentChatId)) {
      chatCache.markChatLoading(currentChatId)
      getMessages(auth, currentChatId)
        .then((chat) => {
          const messages = chat.messages
          const members = new Map(chat.members.map(member => [member.userId, member]))
          chatCache.setChat(currentChatId, messages, members)
        })
    }
  }, [auth, currentChatId])

  useEffect(() => {
    chatEndRef.current?.scrollIntoView()
  }, [currentChatId])

  const onMessageReceive = (messageEvent) => {
    const message = messageEvent.message
    const sender = messageEvent.sender
    const chatId = message.chatId
    const shortMessage = {
      messageId: message.id,
      senderId: message.userId,
      text: message.message,
      date: message.date
    }
    chatCache.addMessageToChat(chatId, shortMessage)
    chatList.setLastMessage(chatId, sender, shortMessage)
    if (currentChatId !== chatId) {
      chatList.incrementUnseen(chatId)
    } else {
      seen(auth, chatId)
    }
    chatEndRef.current?.scrollIntoView()
  }

  const onSocketMessage = (body, dest) => {
    if (dest === '/topic/message') {
      onMessageReceive(body)
    }
  }

  const onSocketConnect = e => {
    setSocketLoading(false)
  }

  const onChatClick = (chatId) => {
    setCurrentChatId(chatId)
    chatList.cleanUnseen(chatId)
    seen(auth, chatId)
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

  const currentChat = currentChatId ? chatList.getChat(currentChatId) : null

  const currentChatName = currentChat ?
    (currentChat.details.personal ?
      currentChat.personal.recipient.firstName + ' ' + currentChat.personal.recipient.lastName :
      currentChat.group.chatName)
    : ''

  const messageInput = currentChat ?
    <MessageInput onSendMessage={onSendMessage} /> : undefined

  return <div>
    {socket}
    <div className='middle'>
      <div className='middle-offset'>
        <div className='chats-list'>
          <Chats chats={chatList.getChats()} onChatClick={onChatClick} />
        </div>
        <div className='chat-header'>
          {currentChatName}
        </div>
        <div className='current-chat'>
          <Chat
            chat={chatCache.getChat(currentChatId)}
            userId={auth.userId}
          />
          <div ref={chatEndRef}></div>
        </div>
        <div className='input-field-container'>
          {messageInput}
        </div>
      </div>
    </div>
  </div>
}

export default ChatPage
