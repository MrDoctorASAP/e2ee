

export default class ChatCache {

  // model -> Map(chatId, {messages, members})
  // messages -> [message]
  // members -> Map(memberId, member)

  constructor(cache) {
    this.cache = cache ?? new Map()
  }

  getChat(chatId) {
    return this.cache.get(chatId)
  }

  isLoading(chatId) {
    return this.getChat(chatId) === null
  }

  isLoaded(chatId) {
    return this.getChat(chatId)
  }

  markChatLoading(chatId) {
    return new ChatCache(
      new Map([...this.cache].concat([[chatId, null]]))
    )
  }

  setChat(chatId, messages, members) {
    return new ChatCache(
      new Map([...this.cache].concat([[chatId, {messages, members}]]))
    )
  }

  addMessageToChat(chatId, message) {
    const chat = this.cache.has(chatId)
    if (!chat) return this
    return new ChatCache(
      new Map([...this.cache].concat([[chatId, {
        ...chat,
        messages: [...chat.messages, message]
      }]]))
    )
  }

}

