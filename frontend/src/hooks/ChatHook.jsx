import {useState} from "react";
import ImmutableMap from "../model/ImmutableMap";

// chats = new Map(chatId, {chat, messages, members})

// TODO: replace setState(newValue) on setState(oldValue => newValue)
export function useChatList() {
  const [chats, setChats] = useState(new ImmutableMap())
  return {
    setChats: chats => {
      setChats(new ImmutableMap(new Map(chats.map(chat => [chat.details.chatId, chat]))))
    },
    getChats: () => Array.from(chats.map.values()),
    getChat: chatId => chats.get(chatId),
    has: (chatId) => chats.has(chatId),
    setLastMessage: (chatId, sender, message) => {
      setChats(chats => chats.modifyIfPresent(chatId, chat => {
        return {
          ...chat,
          last: {
            sender,
            message
          }
        }
      }))
    },
    modifyUnseen: (chatId, mapper) => {
      setChats(chats => chats.modifyIfPresent(chatId, chat => {
        return {
          ...chat,
          details: {
            ...chat.details,
            unseen: mapper(chat.details.unseen)
          }
        }
      }))
    },
    cleanUnseen: (chatId) => {
      setChats(chats => chats.modifyIfPresent(chatId, chat => {
        return {
          ...chat,
          details: {
            ...chat.details,
            unseen: 0
          }
        }
      }))
    },
    incrementUnseen: (chatId) => {
      setChats(chats => chats.modifyIfPresent(chatId, chat => {
        return {
          ...chat,
          details: {
            ...chat.details,
            unseen: chat.details.unseen + 1
          }
        }
      }))
    }
  }
}

export function useChatCache() {
  const [messagesCache, setMessagesCache] = useState(new ImmutableMap())
  const [membersCache, setMembersCache] = useState(new ImmutableMap())
  return {
    getChat: (chatId) => {
      const messages = messagesCache.get(chatId)
      if (messages) {
        return {
          messages: messages,
          members: membersCache.get(chatId)
        }
      }
      return messages
    },
    isLoading: chatId => messagesCache.get(chatId) === null,
    isLoaded: chatId => messagesCache.get(chatId),
    markChatLoading: chatId => setMessagesCache(messagesCache.set(chatId, null)),
    setChat: (chatId, messages, members) => {
      setMessagesCache(messagesCache.set(chatId, messages))
      setMembersCache(membersCache.set(chatId, members))
    },
    addMessageToChat: (chatId, message) => {
      setMessagesCache(messagesCache.modifyIfPresent(chatId, messages => [...messages, message]))
    }
  }
}
