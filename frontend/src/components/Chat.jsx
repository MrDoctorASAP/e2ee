import {MessageBox, SystemMessage} from "react-chat-elements"
import { distinctMessages } from "../api/types";
import LoadingPage from "../pages/LoadingPage";

function Chat({userId, chat, enable, ...props}) {
  if (chat === undefined) {
    return <></>
  }
  if (chat === null) {
    return <LoadingPage/>
  }
  if (!enable) {
    return <SystemMessage text={'Waiting for key exchange'}/>
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