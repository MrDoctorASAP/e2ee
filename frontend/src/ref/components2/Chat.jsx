import { MainContainer } from "@minchat/react-chat-ui";


function Chat({ auth, chats, messages, chatId, setChatId, sendMessage, chatName, loading, ...props }) {
  return <div style={{ height: '100vh' }}> <MainContainer
    inbox={{
      themeColor: "#6ea9d7",
      conversations: chats,
      loading: loading,
      onConversationClick: setChatId,
      selectedConversationId: chatId
    }}
    selectedConversation={{
      themeColor: "#6ea9d7",
      messages: messages ? messages.get(chatId) : [],
      header: chatName,
      currentUserId: auth ? "" + auth.userId : "",
      onSendMessage: sendMessage,
      onBack: () => { },
    }
    }
  />
  </div>
}

export default Chat;
