import { useState } from "react";
import { Button, Input } from "react-chat-elements";
import { sendMessage } from "./api";


function MessageInput({ currectChatId, auth, ...props }) {
  const [message, setMessage] = useState("")
  
  const onSendMessage = (e) => {
    console.log('currectChatId = ' + currectChatId)
    console.log(message)
    if (currectChatId) {
      // setMessage('')
      sendMessage(auth, currectChatId, message)
    }
  }
  return (
    <Input
    value={message}
    onChange={e => setMessage(e.target.value)}
      placeholder='Type here...'
      multiline={true}
      rightButtons={
        <Button color='white' backgroundColor='black' text='Send' onClick={onSendMessage} />
      }
    />
  );
}

export default MessageInput;