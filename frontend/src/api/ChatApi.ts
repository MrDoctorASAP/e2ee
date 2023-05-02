import {
  IAcceptedSecureChat,
  IAuth,
  IBatchChat,
  IBatchMessages,
  IPersonalChat,
  IRecipientKey,
  ISecureChat,
  ISecureChatId,
  ISecureChatInvite, ISecureChatMessage,
  ISecureChatMessageToSend,
  IUser,
  IUserCredentials
} from "./types";

const apiHost = 'http://localhost:8080'

class ApiRequestInit {

  method
  headers
  body

  constructor(method: string, auth?: IAuth, body?: any) {
    const authHeaders = auth ? {'authorization': 'Bearer ' + auth.token} : undefined
    const contentHeaders = body ? {'Content-Type': 'Application/json'} : undefined
    this.headers = {...authHeaders, ...contentHeaders}
    this.body = body ? JSON.stringify(body) : undefined
    this.method = method
  }
}

export async function request(method: string,
                              path: string,
                              auth?: IAuth,
                              body?: any): Promise<any> {
  const url = apiHost + path;
  return fetch(url, new ApiRequestInit(method, auth, body))
    .then(async resp => {
      if (resp.ok) {
        const text = await resp.text()
        if (text) {
          return JSON.parse(text)
        }
        return undefined
      } else {
        console.log('Code ' + resp.status + ' ' + resp.statusText + ' on GET ' + url)
        console.log('Response: ' + await resp.text())
        return null
      }
    }).catch(console.log)
}

export async function get(path: string,
                          auth?: IAuth): Promise<any> {
  return request('GET', path, auth, null)
}

export async function post(
  path: string,
  body?: any,
  auth?: IAuth): Promise<any> {
  return request('POST', path, auth, body)
}

export async function login(userCredentials: IUserCredentials): Promise<IAuth|null> {
  return await post('/api/v1/auth/login', userCredentials)
}

export async function getChats(auth: IAuth): Promise<IBatchChat[]|null> {
  return get('/api/v1/batch/chats', auth)
}

export async function getMessages(auth: IAuth, chatId: number): Promise<IBatchMessages|null> {
  return get('/api/v1/batch/chat/' + chatId, auth)
}

export async function sendMessage(auth: IAuth, chatId: number, message: string): Promise<void> {
  return post('/api/v1/message/send', {chatId, message}, auth)
}

export async function seen(auth: IAuth, chatId: number): Promise<void> {
  return get('/api/v1/unseen/seen?chatId=' + chatId, auth)
}

export function avatarUrl(userId: number): string {
  return apiHost + '/api/v1/avatar/user/' + userId
}

export async function search(query: string) : Promise<IUser[]> {
  return (await get('/api/v1/user/search?query='+query)) ?? []
}

export async function createPersonalChat(auth: IAuth, personalChat: IPersonalChat) {
  return post('/api/v1/chat/create/personal', personalChat, auth)
}

export async function createSecureChat(auth: IAuth, secureChat: ISecureChat) : Promise<ISecureChatId> {
  return post('/api/v1/secure/chat/create', secureChat, auth)
}

export async function invites(auth: IAuth): Promise<ISecureChatInvite[]> {
  return (await get('/api/v1/secure/chat/invites', auth)) ?? []
}

export async function accept(auth: IAuth, accept: IAcceptedSecureChat): Promise<void> {
  await post('/api/v1/secure/chat/accept', accept, auth)
}

export async function exchange(auth: IAuth) : Promise<IRecipientKey[]> {
  return (await get('/api/v1/secure/chat/exchange', auth)) ?? []
}

export async function complete(auth: IAuth, id: ISecureChatId) : Promise<void> {
  await post('/api/v1/secure/chat/complete', id, auth)
}

export async function profile(userId: number) : Promise<IUser> {
  return (await post('/api/v1/user/users', [userId]))[0]
}

export async function sendSecureMessage(auth: IAuth, message: ISecureChatMessageToSend) {
  await post('/api/v1/secure/chat/send', message, auth)
}

export async function getSecureChatsMessages(auth: IAuth, chatIds: string[]) : Promise<ISecureChatMessage[]> {
  if (!chatIds) return []
  return await post('/api/v1/secure/chat/messages', chatIds, auth)
}

export async function seenSecureChatMessages(auth: IAuth, messageIds: number[]) {
  if (!messageIds) return
  await post('/api/v1/secure/chat/seen', messageIds, auth)
}

