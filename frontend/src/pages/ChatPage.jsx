import { useEffect, useRef, useState } from "react"
import LoadingPage from "./LoadingPage"
import SockJsClient from 'react-stomp';
import { createPersonalChat, getChats, getMessages, seen, sendMessage } from "../api/ChatApi";
import Chats from "../components/Chats";
import Chat from "../components/Chat";
import '../ref/components/Styles.css'
import { MessageInput } from "@minchat/react-chat-ui";
import { useChatCache, useChatList } from "../hooks/ChatHook";
import UserChoice from "../components/UserChoice";

import 'bootstrap/dist/css/bootstrap.min.css';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import { clear } from "../model/SecureChatStorage";

function ChatPage({ auth, setAuth, ...props }) {

  const [socketLoading, setSocketLoading] = useState(true)
  const [chatsLoading, setChatsLoading] = useState(true)

  const chatList = useChatList()
  const chatCache = useChatCache()
  const [currentChatId, setCurrentChatId] = useState(null)

  const [showPersonal, setShowPersonal] = useState(false)

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
    // chatEndRef.current?.scrollIntoView()
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
    // chatEndRef.current?.scrollIntoView()
  }

  const onChatCreate = (chatCreationEvent) => {
    if (chatCreationEvent.members.map(m => m.userId).find(id => id === auth.userId)) {
      chatList.addChat(chatCreationEvent.chat)
    }
  }

  const onSocketMessage = (body, dest) => {
    if (dest === '/topic/message') {
      onMessageReceive(body)
    } else if (dest === '/topic/chat') {
      onChatCreate(body)
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
    topics={['/topic/message', '/topic/chat']}
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

  const logout = () => {
    clear()
    setAuth(null)
  }

  const currentChat = currentChatId ? chatList.getChat(currentChatId) : null

  const currentChatName = currentChat ?
    (currentChat.details.personal ?
      currentChat.personal.recipient.firstName + ' ' + currentChat.personal.recipient.lastName :
      currentChat.group.chatName)
    : ''

  const messageInput = currentChat ?
    <MessageInput onSendMessage={onSendMessage} /> : undefined

  const onCreatePersonalChat = (user) => {
    setShowPersonal(false)
    createPersonalChat(auth, { userId: user.userId })
  }

  // {"timestamp":"2023-04-27T16:17:51.337+00:00","status":404,"error":"Not Found","message":"No message available","path":"/api/v1/chat/create/personal"}
  return <div>
    {socket}
    <div className='middle'>
      <div className='middle-offset'>
        {/* <div className="plus alt" onClick={e => setShowPersonal(true)}></div> */}
        <div className="user-profile">
        <NavDropdown
                    id="nav-dropdown-dark-example"
                    title={auth.username}
                    menuVariant="light"
                  >
                    <NavDropdown.Item onClick={ e => setShowPersonal(true) }>
                      Create personal chat...
                      </NavDropdown.Item>
                    <NavDropdown.Item>
                      Create group chat...
                    </NavDropdown.Item>
                    <NavDropdown.Item>
                      Create secure chat...
                    </NavDropdown.Item>
                    <NavDropdown.Divider />
                    <NavDropdown.Item onClick={logout}>
                      Logout
                    </NavDropdown.Item>
                  </NavDropdown>
        </div>
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
    <UserChoice
      show={showPersonal}
      setShow={setShowPersonal}
      onUserClick={onCreatePersonalChat}
      exclude={
        (chatList.getChats()
          ?.filter(chat => chat.details.personal)
          ?.map(chat => chat.personal.recipient.userId) ?? [])
          .concat([auth.userId])
      }
    />
  </div>
}

export default ChatPage
