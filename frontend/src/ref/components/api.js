
async function get(auth, url, noBody) {
  const bearer = 'Bearer ' + auth.token
  return await fetch(url, { headers: { 'authorization': bearer } })
    .then(resp => {
      if (resp.ok) {
        if (noBody) {
          return null
        }  
        return resp.json()
      } else {
        console.log('Code ' + resp.status + ' ' + resp.statusText + ' on GET ' + url)
        return null
      }
    }).catch(console.log)
}

async function post(auth, body, url, noBody) {
  const bearer = 'Bearer ' + auth.token
  return await fetch(url, {
    method: 'POST',
    headers: {
      'authorization': bearer,
      'Content-Type': 'Application/json'
    },
    body: JSON.stringify(body)
  }).then(resp => {
    if (resp.ok) {
      if (noBody) {
        return null
      }  
      return resp.json()
    } else {
      console.log('Code ' + resp.status + ' ' + resp.statusText + ' on POST ' + url)
      return null
    }
  }).catch(console.log)
}

export async function login(username, password) {
  return await fetch('http://localhost:8080/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'Application/json'
    },
    body: JSON.stringify({ username, password })
  }).then(resp => {
    if (resp.ok) {
      return resp.json()
    } else {
      console.log('Code ' + resp.status + ' ' + resp.statusText + ' on POST login')
      return null
    }
  }).catch(console.log)
}

export async function getChats(auth) {
  return await get(auth, 'http://localhost:8080/api/v1/batch/chats')
}

export async function getMessages(auth, chatId) {
  return await get(auth, 'http://localhost:8080/api/v1/batch/chat/' + chatId)
}

export async function sendMessage(auth, chatId, message) {
  return await post(auth, { chatId, message },
    'http://localhost:8080/api/v1/message/send')
}

export function seen(auth, chatId) {
  get(auth, 'http://localhost:8080/api/v1/unseen/seen?chatId=' + chatId, true)
    .catch(console.log)
}
