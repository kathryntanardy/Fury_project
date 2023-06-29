const username = document.getElementById("username");
const password = document.getElementById("password");
const password2 = document.getElementById("repassword");
var btn = document.getElementById("createacc");



console.log("is this working");

btn.addEventListener('click', function (evt) {
    var values = document.querySelector('input[type="password"]');
    console.log(values);
    // if (values[0] != values[1]) {
    //     evt.preventDefault();
    //     var msg1 = document.getElementById('passwordmsg');
    //     var msg2 = document.getElementById('repasswordmsg');
    //     msg1.innerHTML += "Password doesn't match!";
    //     msg2.innerHTML += "Password doesn't match!";
    // }
    console.log("hello")
})

const setError = (element, message) => {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error');
}

const validInputs = () => {
    const usernameValue = username.value.trim();
    const passwordValue = password.value.trim();
    const password2Value = password2.value.trim();

    if(usernameValue === ''){
        setError(username, 'Username is required');
    } else {
        setSuccess(username);
    }

    if(passwordValue === ''){
        setError(password, 'Password is required');
    }
    else{
        setSuccess(password);
    }

    if(password2Value === ''){
        setError(password2, 'Please confirm your password');
    }
    else if(password2Value != passwordValue){
        setError(password2, 'Password does not match with the previous one');
    }
    else{
        setSuccess(password2);
    }
}