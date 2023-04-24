
async function get(auth, url) {
    const bearer = 'Bearer ' + auth.token
    return await fetch(url, { headers: { 'authorization': bearer } })
        .then(resp => {
            if (resp.ok) {
                return resp.json()
            } else {
                console.log('Code ' + resp.status + ' on GET ' + url)
                return null
            }
        }).catch(console.log)
}

async function get_nobody(auth, url) {
    const bearer = 'Bearer ' + auth.token
    return await fetch(url, { headers: { 'authorization': bearer } })
        .then(resp => {
            if (!resp.ok) {
                console.log('Code ' + resp.status + ' on GET ' + url)
            }
            return null
        }).catch(console.log)
}

async function post(auth, body, url) {
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
            return resp.json()
        } else {
            console.log('Code ' + resp.status + ' on POST ' + url)
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
        body: JSON.stringify({username, password})
    }).then(resp => {
        if (resp.ok) {
            return resp.json()
        } else {
            console.log('Code ' + resp.status + ' on POST login')
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
    return await post(auth, {chatId, message},
         'http://localhost:8080/api/v1/message/send')
}

export function seen(auth, chatId) {
    get_nobody(auth, 'http://localhost:8080/api/v1/unseen/seen?chatId='+chatId)
        .catch(console.log)
}

// export async function getMessages(chatId, token) {
//     const apiurl = 'http://localhost:8080'
//     const bearer = 'Bearer ' + token
//     return await fetch(apiurl + '/api/v1/message/messages?chatId=' + chatId, {
//         method: 'GET',
//         headers: {
//             'Content-Type': 'Application/json',
//             'authorization': bearer,
//         }
//     }).then(resp => resp.json())
//         .catch(err => {
//             return null;
//         })
// }

// export async function login(username, password) {
//     const apiurl = 'http://localshot:8080'
//     return await fetch(apiurl + '/api/v1/auth/loginOrRegister', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'Application/json',
//         },
//         body: JSON.stringify({ username, password })
//     })
// }
