import { useEffect, useState } from "react"
import { loadFingerprint } from "../model/SecureChatStorage"
import Fingerprint from "./Fingerprint"

function FingerpringHolder({ secureChatId, show, setShow }) {

  const [fingerprint, setFingerprint] = useState()

  useEffect(() => {
    if (!secureChatId) return
    const value = loadFingerprint(secureChatId)
    if (value) {
      setFingerprint(value)
    }
  }, [secureChatId])

  const onExit = (target) => {
    if (target.className === 'popup') {
      setShow(false)
    }
  }

  if (!show) return <></>

  return <div className="popup" onClick={e => onExit(e.target)}>
    <div className="fingerprint-box">
      <Fingerprint fingerprint={fingerprint} />
    </div>
  </div>

}

export default FingerpringHolder
