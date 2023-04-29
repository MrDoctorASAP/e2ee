
interface IFingerPrintProps {
  fingerprint: Uint8Array
}

const hexDigits = [...'0123456789ABCDEF']

function FingerPrint({fingerprint}: IFingerPrintProps) {
  // 256
  const rows = Array.from(Array(4).keys()).map(i => {
    let row = ''
    for (let j = i*64; j < (i+1)*64; j+=2) {
      row += hexDigits[fingerprint[j]] + hexDigits[fingerprint[j+1]]
      row += j % 4 === 0 ? '  ' : ' '
    }
    return <p>{row}</p>
  })
  return <div>
    {rows}
  </div>
}

export default FingerPrint
