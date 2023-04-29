
export interface IAuth {
  userId: number,
  username: string,
  token: string
}

export interface IUserCredentials {
  username: string,
  password: string
}

export interface IChatDetails {
  chatId: number,
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

export interface IBatchChat {
  details: IChatDetails,
  group?: IGroupChatDetails,
  personal?: IPersonalChatDetails,
  last?: ILastMessage
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

export interface ILocalSecureMessage {
  senderId: number,
  message: string
}
