export async function multiply(url, size, matrixA, matrixB) {
    const response = await fetch(`http://localhost:8080/${url}`, {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify({
            size: size,
            matrixA: matrixA,
            matrixB: matrixB,
        })
    })

    const json = await response.json();
    const time = json.time;
    return time;
}

export function randomMatrix(size) {
    return Array.from({length: size},
        () => Array.from({length: size}, () => Math.floor(Math.random() * 1000)));
} 
