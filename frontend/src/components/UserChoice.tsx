import { useEffect, useState } from "react";
import UserList from "./UserList";
import { IUser } from "../api/types";
import { search } from "../api/ChatApi";

interface IUserChoiceProps {

  /** Действие при выборе пользователя */
  onUserClick: (u:IUser) => void,
  
  /** Видимость окна */
  show: boolean,

  /** Установить видимость окна */
  setShow: (b: boolean) => void,

  /** Идентификаторы пользователей, которые будут исключены из списка */
  exclude: unknown[]
}

/**
 * Окно выбора пользователя
*/
function UserChoice({onUserClick, show, setShow, exclude}: IUserChoiceProps) {

  const [query, setQuery] = useState<string>('')
  const [users, setUsers] = useState<IUser[]>([])

  useEffect(() => {
    if (query) {
      search(query).then(setUsers)
    } else {
      setUsers([])
    }
  }, [query])

  const onExit = (target: any) => {
    if (target.className === 'popup') {
      setShow(false)
    }
  }

  if (!show) return <></>
  
  return <div className="popup" onClick={e => onExit(e.target)}>
    <div className="user-choice-box">
      <input placeholder="search" className="user-choice-input" value={query}
        onChange={e => setQuery(e.target.value)} />
      <UserList users={users} onUserClick={onUserClick} exclude={exclude}/>
    </div>
  </div>
}


export default UserChoice;
