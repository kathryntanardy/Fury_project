var btn = document.getElementById('submit')
btn.addEventListener("click", () => {
    var username = document.getElementById('username')
    var password = document.getElementById('password')
    var retypePass = document.getElementById('retypePassword')
    if (username.value == '') {
        alert("Username is empty")
    }
    else if (password.value == '') {
        alert("Password is empty")
    }
    else if (retypePass.value == '') {
        alert("Re-type Password is empty")
    }
    else if (password.value != retypePass.value) {
        alert("Password and Re-type Password do not match")
    }
    else {
        document.getElementById('signup').onsubmit = function (event) {
            return true;
        };
    }
})