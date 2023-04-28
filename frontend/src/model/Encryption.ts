
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

export async function generateKeyPair() : Promise<CryptoKeyPair> {
  return window.crypto.subtle.generateKey(
    {
      name: "ECDH",
      namedCurve: "P-384",
    },
    true,
    ["deriveKey"]
  );
}

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
    ["deriveKey"]
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
