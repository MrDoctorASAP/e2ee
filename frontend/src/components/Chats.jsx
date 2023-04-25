import {ChatList} from "react-chat-elements"
import "../styles/components/TextMessage.css"
import {getMessagesTs} from "../api/chatApi";

function compareChats(chat1, chat2) {
  if (chat1.last && chat2.last) {
    return chat2.last.message.date - chat1.last.message.date
  }
  return chat2.last ? 1 : (chat1.last ? -1 : 0)
}

function Chats({chats, onChatClick, ...props}) {
  const sortedChats = [...chats].sort(compareChats)
  const dataSource = sortedChats.map(chat => {
    const title = chat.details.personal ?
      chat.personal.recipient.firstName + ' ' + chat.personal.recipient.lastName :
      chat.group.chatName
    const subtitle = chat.last ?
      chat.last.sender.firstName + ': ' + chat.last.message.text :
      undefined
    const avatar = chat.details.personal ?
      'http://localhost:8080/api/v1/avatar/user/' + chat.personal.recipient.userId :
      'http://localhost:8080/api/v1/avatar/chat/' + chat.details.chatId
    const date = chat.last ? new Date(chat.last.message.date) : undefined
    return {
      id: chat.details.chatId,
      avatar: avatar,
      alt: title,
      title: title,
      subtitle: subtitle,
      date: date,
      unread: chat.details.unseen
    }
  })
  return <ChatList
    className='chat-list'
    dataSource={dataSource}
    onClick={e => onChatClick(e.id)}
  />
}

export default Chats
