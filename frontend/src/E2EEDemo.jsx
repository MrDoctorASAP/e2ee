import { useEffect, useState } from "react";
import { decrypt, deriveSecretKey, encrypt, exportPrivateKey, exportPublicKey, exportSecretKey, generateKeyPair } from "./model/Encryption";
import { storePrivateKey, storePublicKey, storeSecretKey } from "./model/SecureChatStorage";

function E2EEDemo() {

  const [message, setMessage] = useState('')
  const [ready, setReady] = useState(false)

  const [aliceKeys, setAliceKeys] = useState()
  const [bobKeys, setBobKeys] = useState()

  const [bobPublic, setBobPublic] = useState('')
  const [alicePublic, setAlicePublic] = useState('')
  const [bobPrivate, setBobPrivate] = useState('')
  const [alicePrivate, setAlicePrivate] = useState('')

  const [aliceSK, setAliceSK] = useState()
  const [bobSK, setBobSK] = useState()

  const [aliceSKText, setAliceSKText] = useState()
  const [bobSKText, setBobSKText] = useState()

  const [enc, setEnc] = useState('')
  const [dec, setDec] = useState('')
  const [iv, setIv] = useState('')

  const createKeys = async () => {

    const alicesKeyPair = await generateKeyPair()
    const bobsKeyPair = await generateKeyPair()
    
    setAliceKeys(alicesKeyPair)
    setBobKeys(bobsKeyPair)

    setAlicePublic(await exportPublicKey(alicesKeyPair.publicKey))
    setAlicePrivate(await exportPrivateKey(alicesKeyPair.privateKey))
    setBobPublic(await exportPublicKey(bobsKeyPair.publicKey))
    setBobPrivate(await exportPrivateKey(bobsKeyPair.privateKey))

    const aliceSK = await deriveSecretKey(alicesKeyPair.privateKey, bobsKeyPair.publicKey)
    const bobSK = await deriveSecretKey(bobsKeyPair.privateKey, alicesKeyPair.publicKey)
    
    setAliceSK(aliceSK)
    setBobSK(bobSK)

    const aliceSKText = await exportSecretKey(aliceSK)
    const bobSKText = await exportSecretKey(bobSK)
    setAliceSKText(aliceSKText)
    setBobSKText(bobSKText)
    
    const chatId = 'a96e6482-e590-11ed-b5ea-0242ac120002'

    storePublicKey(chatId, alicesKeyPair.publicKey)
    storePrivateKey(chatId, alicesKeyPair.privateKey)
    storeSecretKey(chatId, aliceSK)

    setReady(true)
  }

  const sendMessage = async () => {
    const encMessage = await encrypt(aliceSK, message)
    setEnc(encMessage.message)
    setIv(encMessage.iv)
    setDec(await decrypt(bobSK, encMessage))
  }

  useEffect(() => {
    createKeys()
  }, [])

  return <div>
    <div>
      <h1>Alice</h1>
      <p>Public: {alicePublic}</p>
      <p>Private: {alicePrivate}</p>
      <p>Shared: {aliceSKText}</p>
      <p>Message: <input value={message} onChange={e => setMessage(e.target.value)} /></p>
      <p><button onClick={e => sendMessage()}>Send</button></p>
    </div>
    <div>
      <h1>Bob</h1>
      <p>Public: {bobPublic}</p>
      <p>Private: {bobPrivate}</p>
      <p>Shared: {bobSKText}</p>
      <p></p>
      <p>Iv: {iv}</p>
      <p>Enc: {enc}</p>
      <p>Dec: {dec}</p>
    </div>
  </div>
}


export default E2EEDemo;