
Cold load:

    get chats {chatId, personal, info?}
    get last messages { chatId, message }
    get users { userId, perofile }
    get unseen { chatId, unseen }

    get messages { message }
    get users { chatId }

    open chat:
        -> /seen/chatId
        

    events
    -> message [new]
        + add message 
        ? inc unseen
        ? /seen
    


