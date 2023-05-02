import {IAuth, IUser} from "../api/types";
import NavDropdown from "react-bootstrap/NavDropdown";
import UserChoice from "./UserChoice";
import React, {useState} from "react";
import {ChatListModel} from "../hooks/ChatHook";

export interface IUserActionsProps {
  chatList: ChatListModel,
  auth: IAuth,
  actions: IUserActions
}

export interface IUserActions {
  onCreatePersonalChat: (user: IUser) => void,
  onCreateGroupChat: (user: IUser) => void,
  onCreateSecureChat: (user: IUser) => void,
  onLogout: () => void
}

function UserActions({auth, chatList, actions}: IUserActionsProps) {

  const [showPersonal, setShowPersonal] = useState(false)
  const [showSecure, setShowSecure] = useState(false)

  return <><div className="user-profile">
    <NavDropdown
      id="nav-dropdown-dark-example"
      title={auth.username}
      menuVariant="light"
    >
      <NavDropdown.Item onClick={e => setShowPersonal(true)}>
        Create personal chat...
      </NavDropdown.Item>
      <NavDropdown.Item>
        Create group chat...
      </NavDropdown.Item>
      <NavDropdown.Item onClick={e => setShowSecure(true)}>
        Create secure chat...
      </NavDropdown.Item>
      <NavDropdown.Divider />
      <NavDropdown.Item onClick={actions.onLogout}>
        Logout
      </NavDropdown.Item>
    </NavDropdown>
  </div>
  <UserChoice
      show={showPersonal}
      setShow={setShowPersonal}
      onUserClick={actions.onCreatePersonalChat}
      exclude={ chatList.getPersonalChats().map(chat => chat.personal?.recipient.userId).concat([auth.userId]) }
    />
    <UserChoice
      show={showSecure}
      setShow={setShowSecure}
      onUserClick={actions.onCreateSecureChat}
      exclude={[auth.userId]}
    />
  </>
}

export default UserActions
