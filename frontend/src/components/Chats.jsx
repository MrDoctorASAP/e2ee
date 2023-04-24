import { ChatList, MessageBox } from "react-chat-elements"
import "../styles/components/TextMessage.css"


function Chats({ chats, onChatClick, ...props }) {
  const dataSource = chats.map(chat => {
    const title = chat.details.personal ?
      chat.personal.recipient.firstName + ' ' + chat.personal.recipient.lastName :
      chat.group.chatName
    const subtitle = chat.last ?
      chat.last.sender.firstName + ': ' + chat.last.message.text :
      undefined
    const avatar = chat.details.personal ? 
    'http://localhost:8080/api/v1/avatar/user/'+chat.personal.recipient.userId:
    'http://localhost:8080/api/v1/avatar/chat/'+chat.details.chatId
    return {
      id: chat.details.chatId,
      avatar: avatar,
      alt: '',
      title: title,
      subtitle: subtitle,
      date: chat.last ? new Date(chat.last.message.date) : undefined,
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