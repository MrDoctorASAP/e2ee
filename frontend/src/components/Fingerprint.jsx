
// export interface IFingerprintProps {
//   fingerprint: Uint8Array // Size: 64 bytes
// }



function Fingerprint({fingerprint}) {

  if (!fingerprint) return <></>
  
  if (fingerprint.length === 0) {
    return <p>Waiting for key exchange</p>
  }

  const view = []
  for (let i = 0; i < 8; i++) {
    const row = []
    for (let j = 0; j < 8; j++) {
      let hex = fingerprint[i*8+j].toString(16)
      if (hex.length === 1) hex = '0' + hex
      let background = "rgb(" + (fingerprint[i*8+j]/2+64) + ", " + (fingerprint[i*8+j]/2+64) + ", 255)"
      row.push(<span
        key={j} 
        className="fingerprint-hex" 
        style={{backgroundColor: background}}>
          {hex.toUpperCase()}
          </span>)
    }
    view.push(<p className="fingerprint-row" key={i}>{row}</p>)
  }

  return <div>
    <p className="fingerprint-title">Secure key fingerprint</p>
    <div className="fingerprint-inner-box">
      {view}
    </div>
    <p></p>
    <p className="fingerprint-text">
      This image and text were derived from the encryption key for this secure chat.</p>
    <p className="fingerprint-text">
      If they look the same on interlocutor side, end-to-end ecryption is guaranteed.</p>
  </div>
}

export default Fingerprint
