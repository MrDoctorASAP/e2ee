import { MessageBox, SystemMessage } from "react-chat-elements"

function Chat({ userId, messages, chatMembers, ...props }) {
  if (!messages) return <div></div>
  const messageBoxes = messages.map(message => {
    const pos = message.senderId != userId ? 'left' : 'right'
    const sender = chatMembers.get(message.senderId)
    return <MessageBox
      key={message.messageId}
      position={pos}
      avatar={'http://localhost:8080/api/v1/avatar/user/' + message.senderId}
      type={'text'}
      title={sender.firstName}
      text={message.text}
      // dateString={new Date(message.date).toTimeString()}
      notch={false}
    // status='read'
    />
  })
  return messageBoxes
}

export default Chat