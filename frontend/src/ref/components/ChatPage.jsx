import { useState } from 'react';
import { Input, Button } from 'react-chat-elements'
import Chat from './Chat'
import ChatsList from './ChatsList';
import Header from './Header';
import MessageInput from './MessageInput';

function ChatPage({ auth, ...props }) {
  const [currectChatId, setCurrectChatId] = useState(null)
  return (
    <>
      <div className='header'><Header /></div>
      <div className='middle'>
        <div className='middle-offset'>
          <div className='chats-list'><ChatsList auth={auth}
            currectChatId={currectChatId}
            setCurrectChatId={setCurrectChatId} /></div>
          <div className='chat-header'>Username</div>
          <div className='current-chat'><Chat currectChatId={currectChatId} auth={auth} /></div>
          <div className='input-field'><MessageInput currectChatId={currectChatId} auth={auth} /></div>
        </div>
      </div>
    </>
  );
}

export default ChatPage;
