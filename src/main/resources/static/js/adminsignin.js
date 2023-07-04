var btn = document.getElementById('submit')
btn.addEventListener("click", () => {
    var username = document.getElementById('username')
    var password = document.getElementById('password')
    if (username.value == '') {
        alert("Username is empty")
    }
    else if (password.value == '') {
        alert("Password is empty")
    }
    else {
        document.getElementById('adminlogin').onsubmit = function (event) {
            return true;
        };
    }
})