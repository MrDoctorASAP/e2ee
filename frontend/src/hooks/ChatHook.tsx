import React, {useState} from "react";
import ImmutableMap from "../model/ImmutableMap";
import {ChatId, IBatchChat, IShortMessage, IUser} from "../api/types";

export class ChatListModel {

  chatsState: ImmutableMap<ChatId, IBatchChat>
  setChatsState: React.Dispatch<React.SetStateAction<ImmutableMap<ChatId, IBatchChat>>>

  constructor(chatsState: ImmutableMap<ChatId, IBatchChat>,
              setChatsState: React.Dispatch<React.SetStateAction<ImmutableMap<ChatId, IBatchChat>>>) {
    this.chatsState = chatsState
    this.setChatsState = setChatsState
  }

  registerChats(chats: IBatchChat[]) {
    const mapping: (c: IBatchChat) => [ChatId, IBatchChat] = chat => [chat.details.chatId, chat]
    this.setChatsState(existedChats => existedChats.setAll(chats.map(mapping)))
  }

  registerChat(chat: IBatchChat) {
    return this.registerChats([chat])
  }

  getChats() {
    return Array.from(this.chatsState.values())
  }

  getChat(chatId: ChatId) {
    return this.chatsState.get(chatId)
  }

  hasChat(chatId: ChatId) {
    return this.chatsState.has(chatId)
  }

  setLastMessage(chatId: ChatId, sender: IUser, message: IShortMessage) {
    this.setChatsState(chats => chats.modifyIfPresent(chatId, chat => {
      return {
        ...chat,
        last: {
          sender,
          message
        }
      }
    }))
  }

  modifyUnseen(chatId: ChatId, mapper: (n: number) => number) {
    this.setChatsState(chats => chats.modifyIfPresent(chatId, chat => {
      return {
        ...chat,
        details: {
          ...chat.details,
          unseen: mapper(chat.details.unseen)
        }
      }
    }))
  }

  incrementUnseen(chatId: ChatId) {
    this.modifyUnseen(chatId, unseen => unseen + 1)
  }

  cleanUnseen(chatId: ChatId) {
    this.modifyUnseen(chatId, unseen => 0)
  }

  getChatName(chatId: ChatId) {
    const currentChat = this.getChat(chatId)
    if (currentChat) {
      if (currentChat.secure) {
        return currentChat.personal?.recipient.firstName + ' ' + currentChat.personal?.recipient.lastName
      } else if (currentChat.details.personal) {
        return ' ' + currentChat.personal?.recipient.firstName + ' ' + currentChat.personal?.recipient.lastName
      }
      return currentChat.group?.chatName
    }
    return ''
  }

  getPersonalChats(): IBatchChat[] {
    return this.getChats()
      .filter(chat => chat.personal && !chat.secure);
  }

}

export class ChatCache {

  messagesCacheState: ImmutableMap<ChatId, IShortMessage[] | null>
  setMessagesCacheState: React.Dispatch<React.SetStateAction<ImmutableMap<ChatId, IShortMessage[] | null>>>
  membersCacheState: ImmutableMap<ChatId, Map<number, IUser>>
  setMembersCacheState: React.Dispatch<React.SetStateAction<ImmutableMap<ChatId, Map<number, IUser>>>>

  constructor(messagesCacheState: ImmutableMap<ChatId, IShortMessage[] | null>,
              setMessagesCacheState: React.Dispatch<React.SetStateAction<ImmutableMap<ChatId, IShortMessage[] | null>>>,
              membersCacheState: ImmutableMap<ChatId, Map<number, IUser>>,
              setMembersCacheState: React.Dispatch<React.SetStateAction<ImmutableMap<ChatId, Map<number, IUser>>>>) {
    this.messagesCacheState = messagesCacheState
    this.setMessagesCacheState = setMessagesCacheState
    this.membersCacheState = membersCacheState
    this.setMembersCacheState = setMembersCacheState
  }

  getChat(chatId: ChatId) {
    const messages = this.messagesCacheState.get(chatId)
    if (messages) {
      return {
        messages: messages,
        members: this.membersCacheState.get(chatId)
      }
    }
    return messages
  }

  isLoading(chatId: ChatId) {
    return this.messagesCacheState.get(chatId) === null
  }

  isLoaded(chatId: ChatId) {
    return this.messagesCacheState.get(chatId)
  }

  markChatLoading(chatId: ChatId) {
    this.setMessagesCacheState(messagesCache => messagesCache.set(chatId, null))
  }

  setChat(chatId: ChatId, messages: IShortMessage[], members: Map<number, IUser>) {
    this.setMessagesCacheState(messagesCache => messagesCache.set(chatId, messages))
    this.setMembersCacheState(membersCache => membersCache.set(chatId, members))
  }

  addMessageToChat(chatId: ChatId, message: IShortMessage) {
    this.setMessagesCacheState(messagesCache => messagesCache.modifyIfPresent(chatId,
      messages => messages ? [...messages, message] : messages))
  }

}

export function useChatList() {
  const [chats, setChats] = useState(new ImmutableMap<number | string, IBatchChat>())
  return new ChatListModel(chats, setChats)
}

export function useChatCache() {
  const [messagesCache, setMessagesCache] = useState(new ImmutableMap<number | string, IShortMessage[] | null>())
  const [membersCache, setMembersCache] = useState(new ImmutableMap<number | string, Map<number, IUser>>())
  return new ChatCache(messagesCache, setMessagesCache, membersCache, setMembersCache)
}
