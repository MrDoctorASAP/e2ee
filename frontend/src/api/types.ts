import {decrypt} from "../model/Encryption";
import {getSecureChatMessages, loadSecretKey} from "../model/SecureChatStorage";

export type ChatId = string|number

export interface IAuth {
  userId: number,
  username: string,
  token: string
}

export interface IUserCredentials {
  username: string,
  password: string
}

export interface IUserRegistration {
  firstName: string,
  lastName: string,
  email: string,
  username: string,
  password: string
}

export interface IChatDetails {
  chatId: ChatId,
  personal: boolean,
  unseen: number
}

export interface IGroupChatDetails {
  ownerId: number,
  chatName: string
}

export interface IUser {
  userId: number,
  username: string,
  firstName: string,
  lastName: string
}

export interface IPersonalChatDetails {
  recipient: IUser
}

export interface IShortMessage {
  messageId: number,
  senderId: number,
  text: string,
  date: number
}

export interface ILastMessage {
  message: IShortMessage,
  sender: IUser
}

export interface IMessage {
  id: number,
  chatId: number,
  userId: number,
  date: number,
  message: string
}

export interface ISecureChatDetails {

}

export interface IBatchChat {
  details: IChatDetails,
  group?: IGroupChatDetails,
  personal?: IPersonalChatDetails,
  last?: ILastMessage,
  secure?: ISecureChatDetails
}

export interface IBatchMessages {
  members: IUser[],
  messages: IShortMessage[]
}

export interface IMessageEvent {
  message: IMessage,
  sender: IUser
}

export interface IPersonalChat {
  userId: number
}

export interface IChatCreationEvent {
  chat: IBatchChat,
  members: IUser[]
}

export interface ISecureChat {
  publicKey: string,
  recipientId: number
}

export interface ISecureChatId {
  secureChatId: string
}

export interface ISecureChatInvite {
  secureChatId: string,
  publicKey: string,
  sender: IUser
}

export interface IRecipientKey {
  chatId: string,
  publicKey: string
}

export interface IAcceptedSecureChat {
  secureChatId: string,
  publicKey: string
}

export interface ILocalSecureChat {
  secureChatId: string,
  user: IUser
}

export interface ISecureChatMessageToSend {
  secureChatId: string,
  message: string,
  iv: string
}

export interface ISecureChatMessage {
  id: number,
  senderId: number,
  secureChatId: string,
  message: string,
  date: number,
  iv: string
}

export function groupMessages(messages: ISecureChatMessage[]) : Map<string, ISecureChatMessage[]> {
  const mapping: (m: ISecureChatMessage) => [string, ISecureChatMessage[]] = message => {
    return [message.secureChatId, messages.filter(msg => msg.secureChatId === message.secureChatId)]
  }
  return new Map<string, ISecureChatMessage[]>(messages.map(mapping))
}

export async function decryptMessage(message: ISecureChatMessage) : Promise<IShortMessage|undefined> {
  const key = await loadSecretKey(message.secureChatId)
  if (!key) return
  const decrypted = await decrypt(key, message)
  return {
    text: decrypted,
    date: message.date,
    messageId: message.id,
    senderId: message.senderId
  }
}

export async function decryptMessages(messages: ISecureChatMessage[]) : Promise<Map<string, IShortMessage[]>> {
  const map = new Map<string, IShortMessage[]>()
  for (let message of messages) {
    const key = await loadSecretKey(message.secureChatId)
    if (!key) continue
    const decrypted = await decrypt(key, message)
    map.set(message.secureChatId, (map.get(message.secureChatId) ?? []).concat([{
      text: decrypted,
      date: message.date,
      messageId: message.id,
      senderId: message.senderId
    }]))
  }
  return map
}

export function loadChatMessages(secureChatIds: string[]) : Map<string, IShortMessage[]> {
  const map = new Map<string, IShortMessage[]>()
  for (let secureChatId of secureChatIds) {
    map.set(secureChatId, getSecureChatMessages(secureChatId))
  }
  return map
}

export function distinctMessages(messages: IShortMessage[]) : IShortMessage[] {
  const distinct = new Map<number, IShortMessage>()
  for (let message of messages) {
    distinct.set(message.messageId, message)
  }
  return [...distinct.values()]
}

