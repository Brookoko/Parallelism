import {multiply} from "./matrix.js";

window.onload = function () {
    const button = document.getElementById('multiply');

    button.addEventListener('click', async (event) => {
        document.getElementById('time').innerHTML = "";
        const size = parseInt(document.getElementById('size').value);
        const time = await multiply('multiply-server', size, null, null);
        document.getElementById('time').innerHTML = "Time: " + time;
    });
};
