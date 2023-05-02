import { ChatList } from "react-chat-elements"
import { avatarUrl } from "../api/ChatApi"

function UserList({ users, onUserClick, exclude }) {

  const datasource = users.filter(user => !exclude.find(n => n === user.userId)).map(user => {
    return {
      user: user,
      id: user.userId,
      avatar: avatarUrl(user.userId),
      alt: user.firstName + ' ' + user.lastName,
      title: user.firstName + ' ' + user.lastName,
      subtitle: '@' + user.username,
    }
  })
  
  return <ChatList
    className='chat-list'
    dataSource={datasource}
    onClick={e => onUserClick(e.user)}
  />
}

export default UserList