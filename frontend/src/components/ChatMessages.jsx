import {MessageBox, SystemMessage} from "react-chat-elements"
import { avatarUrl } from "../api/ChatApi";
import LoadingPage from "../pages/LoadingPage";

function ChatMessages({userId, chat, canWrite, ...props}) {

  // Если чат не выбран, не показывать ничего
  if (chat === undefined) {
    return <></>
  }

  // Если чат выбран, но находится на стадии загрузки, то показать страницу загрузки
  // null - специальное значение, показывющее, что чат загружается
  if (chat === null) {
    return <LoadingPage/>
  }

  // Если это секретный чат, который находится на стадии обмена ключей
  // Показать соответствующее сообщение
  if (!canWrite) {
    return <SystemMessage text={'Waiting for key exchange'}/>
  }

  // Во всех остальных случаях показать сообщения чата
  return chat.messages.map(message => {
    const pos = message.senderId !== userId ? 'left' : 'right'
    const sender = chat.members.get(message.senderId)
    return <MessageBox
      key={message.messageId}
      position={pos}
      avatar={avatarUrl(message.senderId)}
      type={'text'}
      title={sender.firstName}
      text={message.text}
      notch={false}
    />
  })
}

export default ChatMessages