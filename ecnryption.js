function deriveSecretKey(privateKey, publicKey) {
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
    false,
    ["encrypt", "decrypt"]
  );
}

async function agreeSharedSecretKey() {
  // Generate 2 ECDH key pairs: one for Alice and one for Bob
  // In more normal usage, they would generate their key pairs
  // separately and exchange public keys securely
  let alicesKeyPair = await window.crypto.subtle.generateKey(
    {
      name: "ECDH",
      namedCurve: "P-384",
    },
    false,
    ["deriveKey"]
  );

  let bobsKeyPair = await window.crypto.subtle.generateKey(
    {
      name: "ECDH",
      namedCurve: "P-384",
    },
    false,
    ["deriveKey"]
  );

  // Alice then generates a secret key using her private key and Bob's public key.
  let alicesSecretKey = await deriveSecretKey(
    alicesKeyPair.privateKey,
    bobsKeyPair.publicKey
  );
	
  console.log(alicesKeyPair.publicKey)
  
  // Bob generates the same secret key using his private key and Alice's public key.
  let bobsSecretKey = await deriveSecretKey(
    bobsKeyPair.privateKey,
    alicesKeyPair.publicKey
  );
	const encoder = new TextEncoder()
  const message = 'Hello, Alice!'
  const encodedText = encoder.encode(message);
  const iv = window.crypto.getRandomValues(new Int8Array(12))
  
   const encrupted =  await window.crypto.subtle.encrypt(
    { name: "AES-GCM", iv: iv },
    bobsSecretKey,
    encodedText
  );
  console.log(encrupted)
  const decoded = await window.crypto.subtle.decrypt({ name: "AES-GCM", iv }, alicesSecretKey, encrupted);
  const decoder = new TextDecoder()
  console.log(decoder.decode(decoded))
  
}

agreeSharedSecretKey()
