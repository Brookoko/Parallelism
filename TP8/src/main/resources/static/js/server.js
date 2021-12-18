window.onload = function () {
    const button = document.getElementById('multiply');

    button.addEventListener('click', async (event) => {
        const size = document.getElementById('size').value;

        console.log(JSON.stringify({
            size: parseInt(size)
        }));
        const response = await fetch('http://localhost:8080/multiply-server', {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify({
                size: parseInt(size)
            })
        })

        const json = await response.json();
        const time = json.time;
        document.getElementById('time').innerHTML = "Time: " + time;
    });
};
