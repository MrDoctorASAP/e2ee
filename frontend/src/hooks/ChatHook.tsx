import {useState} from "react";
import ImmutableMap from "../model/ImmutableMap";
import {IBatchChat, IShortMessage, IUser} from "../api/types";


export function useChatList() {
  const [chats, setChats] = useState(new ImmutableMap<number, IBatchChat>())
  return {
    setChats: (chats: IBatchChat[]) => {
      setChats(new ImmutableMap(new Map(chats.map(chat => [chat.details.chatId, chat]))))
    },
    getChats: () => Array.from(chats.map.values()),
    getChat: (chatId: number) => chats.get(chatId),
    has: (chatId: number) => chats.has(chatId),
    addChat: (chat: IBatchChat) => {
      if (!chats.has(chat.details.chatId)) {
        setChats(chats => chats.set(chat.details.chatId, chat))
      }
    },
    setLastMessage: (chatId: number, sender: IUser, message: IShortMessage) => {
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
    modifyUnseen: (chatId: number, mapper: (n: number) => number) => {
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
    cleanUnseen: (chatId: number) => {
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
    incrementUnseen: (chatId: number) => {
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
  const [messagesCache, setMessagesCache] = useState(new ImmutableMap<number, IShortMessage[]|null>())
  const [membersCache, setMembersCache] = useState(new ImmutableMap<number, Map<number, IUser>>())
  return {
    getChat: (chatId: number) => {
      const messages = messagesCache.get(chatId)
      if (messages) {
        return {
          messages: messages,
          members: membersCache.get(chatId)
        }
      }
      return messages
    },
    isLoading: (chatId: number) => messagesCache.get(chatId) === null,
    isLoaded: (chatId: number) => messagesCache.get(chatId),
    markChatLoading: (chatId: number) => setMessagesCache(messagesCache => messagesCache.set(chatId, null)),
    setChat: (chatId: number, messages: IShortMessage[], members: Map<number, IUser>) => {
      setMessagesCache(messagesCache => messagesCache.set(chatId, messages))
      setMembersCache(membersCache => membersCache.set(chatId, members))
    },
    addMessageToChat: (chatId: number, message: IShortMessage) => {
      setMessagesCache(messagesCache => messagesCache.modifyIfPresent(chatId,
          messages => messages ? [...messages, message] : messages))
    }
  }
}
