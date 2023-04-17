```

create account:
    client:
        input -> { username, password }
        private, public = generate_rsa_keys()
        enc_private = encrypt(message=private, key=password)
        { username, enc_private, private } -> server/signup
    server:
        client -> { username, enc_private, public } -> db

create dialogue:

    client:
        input -> { username, password, recipient }
        { username, recipient } -> server -> { public, recipient.public }
        dialogue_key = generate_aes_key()
        sender_dialogue_key = encrypt_rsa(message=dialogue_key, key=public)
        recipient_dialogue_key = encrypt_rsa(message=dialogue_key, key=recipient.public)
        { username, recipient, sender_dialogue_key, recipient_dialogue_key } -> server
    server:
        client -> { username, recipient, sender_dialogue_key, recipient_dialogue_key } -> db


send message:
    input -> { username, password, dialogue_id, message }
    { dialogue_id } -> server -> { sender_dialogue_key }
    { username } -> server -> { dec_private }
    private = decrypt_aes(dec_private, password)
    dialogue_key = decrypt_rsa(sender_dialogue_key, private)
    enc_message = encrypt_aes(message, dialogue_key)
    { dialogue_id, enc_message } -> server

read message:


```


```

```