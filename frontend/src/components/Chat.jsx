import { MessageBox } from "react-chat-elements"

function Chat({ userId, messages, chatMembers, ...props }) {
  if (!messages) return <div></div>
  const messageBoxes = messages.map(message => {
    const pos = message.senderId != userId ? 'left' : 'right'
    const sender = chatMembers.get(message.senderId)
    return <MessageBox
    key={message.messageId}
      position={pos}
      avatar={'http://localhost:8080/api/v1/user/avatar?userId=' + message.senderId}
      type={'text'}
      title={sender.firstName}
      text={message.text}
    />
  })
  return messageBoxes
}

export default Chat