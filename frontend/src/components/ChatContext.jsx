import { useEffect, useState } from "react";
import { Button } from "react-chat-elements";

function TestComp() {
    const [i, setI] = useState(0)
    const [id, setId] = useState([])
    useEffect(() => {
        clearInterval(id)
        const cur_id = setInterval(() => {
            console.log(i)
        }, 100)
        setId(cur_id)
    }, [i])
    return <>
        <h1>{i}</h1>
        <Button onClick={e => setI(i + 1)}>+</Button>
        <Button onClick={e => setI(i - 1)}>-</Button>
    </>
}

export default TestComp;
