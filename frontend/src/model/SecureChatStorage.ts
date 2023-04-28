import { IAuth } from "../api/types"
import { exportPrivateKey, exportPublicKey, exportSecretKey, importPrivateKey, importPublicKey, importSecretKey } from "./Encryption"

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

export async function loadPrivayeKey(secureChatId: string) {
  const item = localStorage.getItem('PRIVATE_'+secureChatId)
  if (!item) return null
  return await importPrivateKey(item)
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


