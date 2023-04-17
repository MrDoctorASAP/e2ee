import React, { useEffect, useState } from 'react'
import { ChatList } from 'react-chat-elements'
import { getChats } from './api'

function ChatsList({ auth, currectChatId, setCurrectChatId }) {
  const [sources, setSources] = useState([])
  useEffect(() => {
    setInterval(async () => {
      const chats = await getChats(auth)
      console.log('get chats')
      if (!chats) return;
      setSources(chats.map(chat => {
        const base = {
          avatar: 'http://localhost:8080/api/v1/user/avatar?userId=' + chat.chat.id,
          subtitle: chat.sender.firstName + ': ' + chat.lastMessage.message,
          date: new Date(chat.lastMessage.date),
          unread: chat.unseen.count,
          chatId: chat.chat.id
        }
        if (chat.chat.personal) {
          return {
            title: chat.personal.firstName + ' ' + chat.personal.lastName,
            ...base
          }
        } else {
          return {
            title: chat.group.name,
            ...base
          }
        }
      }))
    }, 1000)
  }, [])
  return <ChatList
    className='chat-list'
    dataSource={sources}
    onClick={e => {
      console.log(e)
      setCurrectChatId(e.chatId)}
    } />;
}


export default ChatsList;