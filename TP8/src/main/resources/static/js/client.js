import {multiply, randomMatrix} from "./matrix.js";

window.onload = function () {
    const button = document.getElementById('multiply');

    button.addEventListener('click', async (event) => {
        document.getElementById('time').innerHTML = "";
        const size = parseInt(document.getElementById('size').value);
        const matrixA = randomMatrix(size);
        const matrixB = randomMatrix(size);
        const time = await multiply('multiply-client', size, matrixA, matrixB);
        document.getElementById('time').innerHTML = "Time: " + time;
    });
};
