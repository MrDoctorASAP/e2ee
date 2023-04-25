import {Auth, IAuth} from "./ApiTypes";

export type optional<T> = T | null | undefined

async function get(auth: optional<IAuth> | null, url: string) {
  const bearer = 'Bearer ' + auth.token
  return await fetch(url, {headers: {'authorization': bearer}})
    .then(resp => {
      if (resp.ok) {

      } else {
        console.log('Code ' + resp.status + ' ' + resp.statusText + ' on GET ' + url)
        return null
      }
    }).catch(console.log)
}

