
export interface IEncryptedMessage {
  message: string,
  iv: string
}

export function toBase64(buffer: ArrayBuffer) {
  return btoa(String.fromCharCode(...new Uint8Array(buffer)))
}

export function fromBase64(base64: string) {
  return Uint8Array.from(atob(base64), c => c.charCodeAt(0))
}

/**
 * Создаёт пару открытый/закрытый ключ для создания общего симмитричного ключа
 * по протоколу Диффи — Хеллмана на эллиптических кривых.
 * Каждый вызов метода возвращает уникальную пару.
 * 
 * ECDH (Elliptic Curve Diffie-Hellman) — это алгоритм согласования ключей.
 * Это позволяет двум людям, у каждого из которых есть пара открытого и закрытого ключей ECDH,
 * сгенерировать общий секрет: то есть секрет, которым они — и никто другой — не делятся.
 * Затем они могут использовать этот общий секрет в качестве симметричного ключа для защиты своей связи.
 */
export async function generateKeyPair() : Promise<CryptoKeyPair> {
  return window.crypto.subtle.generateKey(
    {
      name: "ECDH", // Протокол Диффи — Хеллмана на эллиптических кривых
      namedCurve: "P-384", // Вид кривой для генерации ключей
    },
    true, // Ключ может быть экспортирован
    ["deriveKey"] // Назначение ключа - deriveKey (создание общего симметричного ключа)
  );
}

/**
 * Вычисляет общий симметричный ключ, одинаковый для собеседников,
 * для шифрования по алгоритму AES-GCM.
 * @param privateKey Закрытый ключ пользователя
 * @param publicKey Открытый ключ собеседника
 * @returns Общий симметричный ключ 
 */
export async function deriveSecretKey(privateKey: CryptoKey,
                                      publicKey: CryptoKey): Promise<CryptoKey> {
  return window.crypto.subtle.deriveKey(
    {
      name: "ECDH",
      public: publicKey,
    },
    privateKey,
    {
      name: "AES-GCM",
      length: 256,
    },
    true,
    ["encrypt", "decrypt"]
  );
}

export async function exportPublicKey(key: CryptoKey): Promise<string> {
  const raw = await window.crypto.subtle.exportKey('raw', key)
  return toBase64(raw)
}

export async function importPublicKey(raw: string): Promise<CryptoKey> {
  const data = fromBase64(raw)
  return await window.crypto.subtle.importKey('raw', data,
    {
      name: "ECDH",
      namedCurve: "P-384",
    },
    true,
    []
  )
}

export async function exportPrivateKey(key: CryptoKey): Promise<string> {
  const jwk = await window.crypto.subtle.exportKey('jwk', key)
  return JSON.stringify(jwk)
}

export async function importPrivateKey(raw: string): Promise<CryptoKey> {
  const jwk = JSON.parse(raw)
  return await window.crypto.subtle.importKey('jwk', jwk,
    {
      name: "ECDH",
      namedCurve: "P-384",
    },
    true,
    ["deriveKey"]
  )
}

export async function exportSecretKey(key: CryptoKey): Promise<string> {
  const jwk = await window.crypto.subtle.exportKey('jwk', key)
  return JSON.stringify(jwk)
}

export async function importSecretKey(raw: string): Promise<CryptoKey> {
  const jwk = JSON.parse(raw)
  return await window.crypto.subtle.importKey('jwk', jwk,
    {
      name: "AES-GCM",
      length: 256,
    },
    true,
    ["encrypt", "decrypt"]
  )
}

export async function encrypt(key: CryptoKey, message: string): Promise<IEncryptedMessage> {
  const iv = window.crypto.getRandomValues(new Int8Array(12))
  const encoder = new TextEncoder()
  const encodedText = encoder.encode(message);
  const encrypted = await window.crypto.subtle.encrypt(
    { name: "AES-GCM", iv: iv },
    key,
    encodedText
  );
  return {message: toBase64(encrypted), iv: toBase64(iv)}
}

export async function decrypt(key: CryptoKey, message: IEncryptedMessage): Promise<string> {
  const decoder = new TextDecoder()
  const decrypted = await window.crypto.subtle.decrypt(
    { name: "AES-GCM", iv: fromBase64(message.iv) }, key, fromBase64(message.message)
  );
  return decoder.decode(decrypted)
}

export async function sha256(data: Uint8Array) : Promise<Uint8Array> {
  return new Uint8Array(await window.crypto.subtle.digest('SHA-256', data))
}

export async function calculateFingerprint(secretKey: CryptoKey, publicKey: CryptoKey) {
  const secretRaw = new Uint8Array(await window.crypto.subtle.exportKey('raw', secretKey))
  const publicRaw = new Uint8Array(await window.crypto.subtle.exportKey('raw', publicKey))
  const secretHash = await sha256(secretRaw)
  const publicHash = await sha256(publicRaw)
  return new Uint8Array([...secretHash.subarray(0, 127), ...publicHash.subarray(128)])
}
