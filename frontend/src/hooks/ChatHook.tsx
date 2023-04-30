import {useState} from "react";
import ImmutableMap from "../model/ImmutableMap";
import {IBatchChat, IShortMessage, IUser} from "../api/types";


export function useChatList() {
  const [chats, setChats] = useState(new ImmutableMap<number|string, IBatchChat>())
  return {
    registerChats: (chats: IBatchChat[]) => {
      const mapping: (c: IBatchChat) => [number|string, IBatchChat] = chat => [chat.details.chatId, chat]
      setChats(existedChats => existedChats.setAll(chats.map(mapping)))
    },
    getChats: () => Array.from(chats.map.values()),
    getChat: (chatId: number|string) => chats.get(chatId),
    has: (chatId: number|string) => chats.has(chatId),
    addChat: (chat: IBatchChat) => {
      if (!chats.has(chat.details.chatId)) {
        setChats(chats => chats.set(chat.details.chatId, chat))
      }
    },
    setLastMessage: (chatId: number|string, sender: IUser, message: IShortMessage) => {
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
    modifyUnseen: (chatId: number|string, mapper: (n: number) => number) => {
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
    cleanUnseen: (chatId: number|string) => {
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
    incrementUnseen: (chatId: number|string) => {
      setChats(chats => chats.modifyIfPresent(chatId, chat => {
        return {
          ...chat,
          details: {
            ...chat.details,
            unseen: chat.details.unseen + 1
          }
        }
      }))
    },
  }
}

export function useChatCache() {
  const [messagesCache, setMessagesCache] = useState(new ImmutableMap<number|string, IShortMessage[]|null>())
  const [membersCache, setMembersCache] = useState(new ImmutableMap<number|string, Map<number, IUser>>())
  return {
    getChat: (chatId: number|string) => {
      const messages = messagesCache.get(chatId)
      if (messages) {
        return {
          messages: messages,
          members: membersCache.get(chatId)
        }
      }
      return messages
    },
    isLoading: (chatId: number|string) => messagesCache.get(chatId) === null,
    isLoaded: (chatId: number|string) => messagesCache.get(chatId),
    markChatLoading: (chatId: number|string) => setMessagesCache(messagesCache => messagesCache.set(chatId, null)),
    setChat: (chatId: number|string, messages: IShortMessage[], members: Map<number, IUser>) => {
      setMessagesCache(messagesCache => messagesCache.set(chatId, messages))
      setMembersCache(membersCache => membersCache.set(chatId, members))
    },
    addMessageToChat: (chatId: number|string, message: IShortMessage) => {
      setMessagesCache(messagesCache => messagesCache.modifyIfPresent(chatId,
          messages => messages ? [...messages, message] : messages))
    }
  }
}
