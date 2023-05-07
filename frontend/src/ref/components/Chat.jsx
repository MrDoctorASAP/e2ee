import { useEffect, useRef, useState } from "react";
import { MessageBox } from "react-chat-elements";
import { getMessages } from "./api";


function Chat({ auth, currectChatId, ...props }) {
  const [messages, setMessages] = useState(new Map())
  const [currectUpdateIntervalId, setCurrectUpdateIntervalId] = useState(null)
  const chatRef = useRef(null)
  useEffect(() => {
    if (chatRef && chatRef.current) {
      chatRef.current.addEventListener('DOMNodeInserted', event => {
        const { currentTarget: target } = event;
        target.scroll({ top: target.scrollHeight, behavior: 'smooth' });
      });
    }
  }, [])
  useEffect(() => {
    console.log('get messages')
    clearInterval(currectUpdateIntervalId)
    if (currectChatId) {
      // setMessages(new Map([...messages, [currectChatId, null]]))
      const intervalId = setInterval(() => {
        getMessages(auth, currectChatId)
        .then(updated_messages => {
          if (!updated_messages) return
          const components = updated_messages.map(message => {
            var pos = 'left'
            if (message.sender.userId == auth.userId) {
              pos = 'right'
            }
            return <MessageBox
              position={pos}
              avatar={'https://localhost:8080/api/v1/user/avatar?userId=' + message.sender.userId}
              type={'text'}
              title={message.sender.firstName + ' ' + message.sender.lastName}
              text={message.message.message}
            />
          })
          setMessages(new Map([...messages].concat(
            [[currectChatId, components]])))
        })
      }, 1000)
      setCurrectUpdateIntervalId(intervalId)
    }
  }, [currectChatId])
  if (!currectChatId) {
    return <></>
  }
  if (messages.has(currectChatId)) {
    return messages.get(currectChatId)
  }
  return <p>Loading</p>;
}


export default Chat;
