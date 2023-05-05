import {IAuth, ILocalSecureChat, IShortMessage, IUser} from "../api/types"
import { calculateFingerprint, exportPrivateKey, exportPublicKey, exportSecretKey, fromBase64, importPrivateKey, importPublicKey, importSecretKey, toBase64 } from "./Encryption"

export function clear() {
  localStorage.clear()
}

export function storeAuth(auth: IAuth) {
  if (!auth) return
  localStorage.setItem('AUTH', JSON.stringify(auth))
}

export function loadAuth() {
  const auth = localStorage.getItem('AUTH')
  if (!auth) return null
  return JSON.parse(auth)
}

export async function storePublicKey(secureChatId: string, key: CryptoKey) {
  const item = await exportPublicKey(key)
  localStorage.setItem('PUBLIC_'+secureChatId, item)
}

export async function loadPublicKey(secureChatId: string) {
  const item = localStorage.getItem('PUBLIC_'+secureChatId)
  if (!item) return null
  return await importPublicKey(item)
}

export async function storePrivateKey(secureChatId: string, key: CryptoKey) {
  const item = await exportPrivateKey(key)
  localStorage.setItem('PRIVATE_'+secureChatId, item)
}

export async function loadPrivateKey(secureChatId: string) {
  const item = localStorage.getItem('PRIVATE_'+secureChatId)
  if (!item) return null
  return await importPrivateKey(item)
}

export async function clearTemporaryKeys(secureChatId: string) {
  localStorage.removeItem('PRIVATE_'+secureChatId)
  localStorage.removeItem('PUBLIC_'+secureChatId)
}

export async function storeSecretKey(secureChatId: string, key: CryptoKey) {
  const item = await exportSecretKey(key)
  localStorage.setItem('SECRET_'+secureChatId, item)
}

export async function loadSecretKey(secureChatId: string) {
  const item = localStorage.getItem('SECRET_'+secureChatId)
  if (!item) return null
  return await importSecretKey(item)
}

export function hasSecret(secureChatId: string|number) {
  return localStorage.getItem('SECRET_'+secureChatId)
}

export async function storeFingerprint(secureChatId: string, secretKey: CryptoKey, publicKey: CryptoKey) {
  const fingerprint = await calculateFingerprint(secretKey, publicKey)
  const hex = toBase64(fingerprint)
  localStorage.setItem('FINGERPRINT_'+secureChatId, hex)
}

export function loadFingerprint(secureChatId: string) {
  const hex = localStorage.getItem('FINGERPRINT_'+secureChatId) ?? ''
  return fromBase64(hex)
}

export function storeSecureChat(secureChatId: string, user: IUser) {
  const chat = {secureChatId, user}
  const chats = loadSecureChats()
  localStorage.setItem('SECRET_CHATS', JSON.stringify(chats.concat([chat])))
  return chat
}

export function loadSecureChats() : ILocalSecureChat[] {
  const chatsJson = localStorage.getItem('SECRET_CHATS')
  return chatsJson ? JSON.parse(chatsJson) : []
}

export function addSecureChatMessages(secureChatId: string, messages: IShortMessage[]) {
  const chatMessages = getSecureChatMessages(secureChatId)
  localStorage.setItem('SECRET_MESSAGES_'+secureChatId, JSON.stringify(chatMessages.concat(messages)))
}

export function getSecureChatMessages(secureChatId: string) : IShortMessage[] {
  const messagesJson = localStorage.getItem('SECRET_MESSAGES_'+secureChatId)
  return messagesJson ? JSON.parse(messagesJson) : []
}
