import { useEffect, useRef, useState } from "react"
import LoadingPage from "./LoadingPage"
import SockJsClient from 'react-stomp';
import {
  accept,
  complete,
  createPersonalChat,
  createSecureChat,
  exchange,
  getChats,
  getMessages,
  getSecureChatsMessages,
  invites,
  profile,
  seen,
  seenSecureChatMessages,
  sendMessage,
  sendSecureMessage
} from "../api/ChatApi";
import ChatListView from "../components/ChatListView";
import '../ref/components/Styles.css'
import { useChatCache, useChatList } from "../hooks/ChatHook";

import 'bootstrap/dist/css/bootstrap.min.css';
import {
  addSecureChatMessages,
  clear,
  clearTemporaryKeys,
  loadPrivateKey, loadSecretKey,
  loadSecureChats,
  storePrivateKey,
  storePublicKey,
  storeSecretKey,
  storeSecureChat
} from "../model/SecureChatStorage";
import {
  decrypt,
  deriveSecretKey,
  encrypt,
  exportPublicKey,
  generateKeyPair,
  importPublicKey
} from "../model/Encryption";
import { decryptMessages, loadChatMessages } from "../api/types";
import UserActions from "../components/UserActions";
import Chat from "../components/Chat";

function ChatPage({ auth, setAuth, ...props }) {

  const [chatsLoading, setChatsLoading] = useState(true)
  const [user, setUser] = useState()
  const chatList = useChatList()
  const chatCache = useChatCache()
  const [currentChatId, setCurrentChatId] = useState(null)
  const chatEndRef = useRef(null)

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

  const onExchange = async (recipientKey) => {
    const privateKey = await loadPrivateKey(recipientKey.chatId)
    const publicKey = await loadPrivateKey(recipientKey.chatId)
    if (!privateKey || !publicKey) return
    const recipientPublicKey = await importPublicKey(recipientKey.publicKey)
    const secret = await deriveSecretKey(privateKey, recipientPublicKey)
    await storeSecretKey(recipientKey.chatId, secret)
    // await storeFingerprint(recipientKey.chatId, recipientPublicKey, secret)
    await complete(auth, { secureChatId: recipientKey.chatId })
    await clearTemporaryKeys(recipientKey.chatId)
  }

  const onInvite = async (invite) => {
    const senderPublicKey = await importPublicKey(invite.publicKey)
    const keys = await generateKeyPair()
    const secret = await deriveSecretKey(keys.privateKey, senderPublicKey)
    await storeSecretKey(invite.secureChatId, secret)
    // await storeFingerprint(invite.secureChatId, senderPublicKey, secret)
    const secureChat = storeSecureChat(invite.secureChatId, invite.sender)
    chatList.registerChats([{
      details: { chatId: secureChat.secureChatId, personal: true, unseen: 0 },
      personal: { recipient: invite.sender },
      secure: {}
    }])
    const members = new Map([[auth.userId, user], [secureChat.user.userId, secureChat.user]])
    chatCache.setChat(secureChat.secureChatId, [], members)
    await accept(auth, { secureChatId: invite.secureChatId, publicKey: await exportPublicKey(keys.publicKey) })
  }

  const onLoad = async () => {

    const user = await profile(auth.userId)
    setUser(user)

    const chats = await getChats(auth)
    chatList.registerChats(chats)

    const _invites = await invites(auth)
    for (const invite of _invites) {
      await onInvite(invite)
    }

    const exchanges = await exchange(auth)
    for (const key of exchanges) {
      await onExchange(key)
    }

    const secureChats = loadSecureChats()
    const secureChatIds = secureChats.map(secureChat => secureChat.secureChatId)
    const newMessages = await getSecureChatsMessages(auth, secureChatIds)
    const newDecryptedMessages = await decryptMessages(newMessages)

    for (let [key, value] of newDecryptedMessages.entries()) {
      addSecureChatMessages(key, value)
    }

    const chatMessages = loadChatMessages(secureChatIds)

    chatList.registerChats(secureChats.map(secureChat => {
      const unseen = newDecryptedMessages.get(secureChat.secureChatId)?.length ?? 0
      const members = new Map([[auth.userId, user], [secureChat.user.userId, secureChat.user]])
      chatCache.setChat(secureChat.secureChatId, chatMessages.get(secureChat.secureChatId) ?? [], members)
      return {
        details: { chatId: secureChat.secureChatId, personal: true, unseen },
        personal: {
          recipient: secureChat.user
        },
        secure: {}
      }
    }))
    await seenSecureChatMessages(auth, newMessages.map(message => message.id))
  }

  useEffect(() => {
    if (!auth) return
    onLoad().then(() => setChatsLoading(false))
  }, [auth])

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
      seen(auth, chatId).then(() => { })
    }
    chatEndRef.current?.scrollIntoView()
  }

  const onChatCreate = (chatCreationEvent) => {
    if (chatCreationEvent.members.map(m => m.userId).find(id => id === auth.userId)) {
      chatList.registerChat(chatCreationEvent.chat)
    }
  }

  const onSecureChatMessage = async (message) => {
    const secret = await loadSecretKey(message.secureChatId)
    const decryptedMessage = await decrypt(secret, message)
    const messageObj = {
      messageId: message.id,
      text: decryptedMessage,
      date: message.date,
      senderId: message.senderId
    }
    chatCache.addMessageToChat(message.secureChatId, messageObj)
    addSecureChatMessages(message.secureChatId, [messageObj])
    seenSecureChatMessages(auth, [message.id])
  }

  const onSocketMessage = (body, dest) => {
    if (dest === '/topic/message') {
      onMessageReceive(body)
    } else if (dest === '/topic/chat') {
      onChatCreate(body)
    } else if (dest === '/topic/invite') {
      if (auth.userId === body.userId) {
        onInvite(body.event)
      }
    } else if (dest === '/topic/exchange') {
      if (auth.userId === body.userId) {
        onExchange(body.event)
      }
    } else if (dest === '/topic/secureMessage') {
      if (auth.userId === body.userId) {
        onSecureChatMessage(body.event)
      }
    }
  }

  const onChatClick = (chatId) => {
    setCurrentChatId(chatId)
    chatList.cleanUnseen(chatId)
    if (!chatList.getChat(chatId).secure) {
      seen(auth, chatId)
    }
  }

  const onSendMessage = async (message) => {
    if (currentChatId) {
      const chat = chatList.getChat(currentChatId)
      if (chat.secure) {
        const encrypted = await encrypt(await loadSecretKey(currentChatId), message)
        await sendSecureMessage(auth, {
          secureChatId: currentChatId,
          message: encrypted.message,
          iv: encrypted.iv
        })
        const date = new Date().getTime()
        const messageObj = {
          messageId: date,
          text: message,
          date: date,
          senderId: auth.userId
        }
        chatCache.addMessageToChat(currentChatId, messageObj)
        addSecureChatMessages(currentChatId, [messageObj])
      } else {
        await sendMessage(auth, currentChatId, message)
      }
    }
  }

  if (chatsLoading) {
    return <>
      <LoadingPage />
    </>
  }

  const onLogout = () => {
    clear()
    setAuth(null)
  }

  const currentChatName = chatList.getChatName(currentChatId)

  const onCreatePersonalChat = (user) => {
    createPersonalChat(auth, { userId: user.userId })
  }

  const onCreateSecureChat = async (user) => {
    const keys = await generateKeyPair()
    const publicKey = await exportPublicKey(keys.publicKey)
    const chatId = await createSecureChat(auth, { publicKey: publicKey, recipientId: user.userId })
    await storePublicKey(chatId.secureChatId, keys.publicKey)
    await storePrivateKey(chatId.secureChatId, keys.privateKey)
    const secureChat = storeSecureChat(chatId.secureChatId, user)
    chatList.registerChats([{
      details: { chatId: secureChat.secureChatId, personal: true, unseen: 0 },
      personal: { recipient: user },
      secure: {}
    }])
    const members = new Map([[auth.userId, user], [secureChat.user.userId, secureChat.user]])
    chatCache.setChat(secureChat.secureChatId, [], members)
  }

  return <div>
    <SockJsClient
      url='http://localhost:8080/ws'
      topics={[
        '/topic/message', // Сообщения персональных и груповых чатов
        '/topic/chat', // Создание чатов
        '/topic/invite', // Приглашение в секретный чат
        '/topic/exchange', // Обмен ключей секретного чата
        '/topic/secureMessage' // Сообщение в общем чате
      ]}
      onMessage={onSocketMessage}
      debug={true}
    />
    <div className='middle'>
      <div className='middle-offset'>
        <div className='chats-list'>
          <ChatListView chats={chatList.getChats()} onChatClick={onChatClick} />
        </div>
        <div className='chat-header'>
          {currentChatName}
        </div>
        <Chat chatEndRef={chatEndRef}
          chat={chatList.getChat(currentChatId)}
          chatDetails={chatCache.getChat(currentChatId)}
          auth={auth}
          onSendMessage={onSendMessage}
        />
      </div>
      <UserActions chatList={chatList} auth={auth} actions={{
        onLogout, onCreateSecureChat, onCreatePersonalChat
      }} />
    </div>

  </div>
}

export default ChatPage
