import ChatMessages from "./ChatMessages";
import {ChatId, IAuth, IBatchChat, IShortMessage, IUser} from "../api/types";
import {ChatCache} from "../hooks/ChatHook";
import {MessageInput} from "@minchat/react-chat-ui";
import {hasSecret} from "../model/SecureChatStorage";

export interface IChatProps {
  chatEndRef: any,
  auth: IAuth,
  onSendMessage: (text: string) => void,
  chat: IBatchChat,
  chatDetails: {messages: IShortMessage[] , members: Map<number,IUser>}
}

function Chat({chat, chatDetails, chatEndRef, onSendMessage, auth}: IChatProps) {
  const canWrite = (chat && (!chat.secure || hasSecret(chat.details.chatId)))
  const messageInput = canWrite ?
    <MessageInput onSendMessage={onSendMessage} /> : undefined
  return <>
    <div className='current-chat'>
      <ChatMessages
        canWrite={canWrite}
        chat={chatDetails}
        userId={auth.userId}
      />
      <div ref={chatEndRef}></div>
    </div>
    <div className='input-field-container'>
      {messageInput}
    </div>
  </>
}

export default Chat
