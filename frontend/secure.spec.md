

```js
function create_secure_chat(receiverId) {
    const [public, private] = generate_rsa_keys()
    const chatId = fetch('/secure/create', {receiverId, public})
    store_private(chatId, private)
    strore_public(chatId, public)
}

fetch('/secure/accept').then(accept_secure_chat)
// websockets

function accept_secure_chat({public, chatId, senderId}) {
    const key = generate_aes_key()
    const enc_key = enc_rsa(key, public)
    fetch('/secure/exchange/load', {chatId, enc_key})
    store_key(chatId, key)
}

fetch('/secure/exchange/accept').then(key_exchange)
// websockets

function key_exchange({chatId, enc_key}) {
    const private = load_private(chatId)
    const key = enc_rsa(enc_key, private)
    store_key(chatId, key)
    delete_private(chatId)
}

    
```




    

