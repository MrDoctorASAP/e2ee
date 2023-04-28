import {MessageBox} from "react-chat-elements"
import LoadingPage from "../pages/LoadingPage";

function Chat({userId, chat, ...props}) {
  if (chat === undefined) {
    return <></>
  }
  if (chat === null) {
    return <LoadingPage/>
  }
  return chat.messages.map(message => {
    const pos = message.senderId !== userId ? 'left' : 'right'
    const sender = chat.members.get(message.senderId)
    return <MessageBox
      key={message.messageId}
      position={pos}
      avatar={'http://localhost:8080/api/v1/avatar/user/' + message.senderId}
      type={'text'}
      title={sender.firstName}
      text={message.text}
      notch={false}
    />
  })
}

export default Chat