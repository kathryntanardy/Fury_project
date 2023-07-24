let RANDOM_QUOTE_API_URL = 'https://api.quotable.io/random'
let quoteDisplayElement = document.getElementById('quoteDisplay')
let quoteInputElement = document.getElementById('quoteInput')
let timerElement = document.getElementById('timer')
let timerStart = false;

let numOfInputs = 0;
let numOfCharacters;

let theLength;
let theFirstLetter;
let numbers;

let firstLetterBox = document.getElementById('firstLetter');
let numbersBox = document.getElementById('numbers');


quoteInputElement.addEventListener('keydown', (event) => {
    if (event.code == "Backspace" && numOfInputs <= 0) {
        --numOfInputs;
    }
})

quoteInputElement.addEventListener('input', () => {
    let arrayQuote = quoteDisplayElement.querySelectorAll('span')
    let arrayValue = quoteInputElement.value.split('')
    let correct = true;
    numOfCharacters = arrayQuote.length

    if (timerStart === false) {
        startTimer();
        timerStart = true;
    }
    if (quoteInputElement.value == '') {
        numOfInputs = 0;
    }

    numOfInputs++;

    arrayQuote.forEach((characterSpan, index) => {
        let character = arrayValue[index];

        if (character == null) {
            characterSpan.classList.remove('correct');
            characterSpan.classList.remove('incorrect');
            correct = false;
        }
        else if (character === characterSpan.innerText) {
            characterSpan.classList.add('correct');
            characterSpan.classList.remove('incorrect');
        }
        else {
            characterSpan.classList.remove('correct');
            characterSpan.classList.add('incorrect');
            correct = false;
        }
    })

    if (correct) {
        clearInterval(intervalId);
        timerStart = false;
        alert("Finish the quote in: " + timerElement.innerText + " seconds");
        let wpm = ((numOfInputs / 5) / (timerElement.innerText / 60)).toFixed(2);
        let accuracy = ((numOfCharacters / numOfInputs) * 100).toFixed(2);
        alert("WPM: " + wpm + " WPM");
        alert("Accuracy: " + accuracy + "%");
        document.getElementById('wpm').value = wpm;
        document.getElementsByName('submitWPM')[0].submit();
        renderNewQuote();
    }
})

function getRandomQuote() {
    return fetch(RANDOM_QUOTE_API_URL).then(response => response.json()).then(data => data.content).catch(error => console.error('Error:', error));
}
function getEasyRandomWords() {
    return fetch('https://random-word-api.vercel.app/api?words=13').then(response => response.text());
}
function getHardRandomWords() {
    return fetch('https://random-word-api.herokuapp.com/word?number=12').then(response => response.text());
}
function getCustomWords() {
    if (theFirstLetter != '' && numbers == '' && theLength == '') {
        return fetch('https://random-word-api.vercel.app/api?words=15&letter=' + theFirstLetter).then(response => response.text());
    }
    else if (theFirstLetter == '' && numbers != '' && theLength == '') {
        return fetch('https://random-word-api.herokuapp.com/word?number=' + numbers).then(response => response.text());
    }
    else if (theFirstLetter == '' && numbers == '' && theLength != '') {
        return fetch('https://random-word-api.herokuapp.com/word?number=15&length=' + theLength).then(response => response.text());
    }
    else if (theFirstLetter != '' && numbers == '' && theLength != '') {
        return fetch('https://random-word-api.vercel.app/api?words=15&length=' + theLength + '&letter=' + theFirstLetter).then(response => response.text());
    }
    else if (theFirstLetter == '' && numbers != '' && theLength != ''){
        return fetch('https://random-word-api.herokuapp.com/word?number=' + numbers + '&length=' + theLength).then(response => response.text());
    }
}

async function renderNewQuote() {
    let quote = await getRandomQuote()
    quoteDisplayElement.innerHTML = '';
    quote.split('').forEach(character => {
        let characterSpan = document.createElement('span');
        characterSpan.innerText = character;
        quoteDisplayElement.appendChild(characterSpan)
    })
    quoteInputElement.value = null;
    timer.innerText = 0;
    document.getElementById('wpm').value = '';
}

async function renderHardRandomWords() {
    let words = await getHardRandomWords();
    quoteDisplayElement.innerHTML = '';
    words.split('').forEach(character => {
        if (character == ',') {
            let characterSpan = document.createElement('span');
            characterSpan.innerText = ' ';
            quoteDisplayElement.appendChild(characterSpan)
        }
        else if (character.charCodeAt() >= 97 && character.charCodeAt() <= 122) {
            let characterSpan = document.createElement('span');
            characterSpan.innerText = character;
            quoteDisplayElement.appendChild(characterSpan)
        }
    })
    quoteInputElement.value = null;
    timer.innerText = 0;
    document.getElementById('wpm').value = '';
}
async function renderEasyRandomWords() {
    let words = await getEasyRandomWords();
    quoteDisplayElement.innerHTML = '';
    words.split('').forEach(character => {
        if (character == ',') {
            let characterSpan = document.createElement('span');
            characterSpan.innerText = ' ';
            quoteDisplayElement.appendChild(characterSpan)
        }
        else if (character.charCodeAt() >= 97 && character.charCodeAt() <= 122) {
            let characterSpan = document.createElement('span');
            characterSpan.innerText = character;
            quoteDisplayElement.appendChild(characterSpan)
        }
    })
    quoteInputElement.value = null;
    timer.innerText = 0;
    document.getElementById('wpm').value = '';
}

async function renderCustomWords() {
    theFirstLetter = document.getElementById('firstLetter').value;
    numbers = document.getElementById('numbers').value;
    theLength = document.getElementById('wordLength').value;
    
    if ((theLength == '' && theFirstLetter == '' && numbers == '') || (numbers != '' && theFirstLetter != '') || (theFirstLetter.toLowerCase().charCodeAt() < 97 || theFirstLetter.toLowerCase().charCodeAt() > 122) ){
        theFirstLetter = ''
        numbers = ''
    } else {
        if (theFirstLetter.toLowerCase().charCodeAt() < 97 || theFirstLetter.toLowerCase().charCodeAt() > 122) {
            theFirstLetter = ''
        }
        if (numbers != '' && numbers < 12) {
            numbers = '12'
            numbersBox.value = numbers;
        }
        else if (numbers != '' && numbers > 25) {
            numbers = '25'
            numbersBox.value = numbers;
        }
        if (theFirstLetter != '' && theLength > 9) {
            theLength = '9'
            document.getElementById('wordLength').value ='9'
        }
        if (theLength != '' && theLength < 3) {
            theLength = '3'
            document.getElementById('wordLength').value ='3'
        } 
        else if (theLength != '' && theLength > 15) {
            theLength = '15'
            document.getElementById('wordLength').value ='15'
        }
        let custom = await getCustomWords();
        quoteDisplayElement.innerHTML = '';
        custom.split('').forEach(character => {
            if (character == ',') {
                let characterSpan = document.createElement('span');
                characterSpan.innerText = ' ';
                quoteDisplayElement.appendChild(characterSpan)
            }
            else if (character.charCodeAt() >= 97 && character.charCodeAt() <= 122) {
                let characterSpan = document.createElement('span');
                characterSpan.innerText = character;
                quoteDisplayElement.appendChild(characterSpan)
            }
        })
        quoteInputElement.value = null;
        timer.innerText = 0;
        document.getElementById('wpm').value = '';
    }
}

let startTime
let intervalId
function startTimer() {
    startTime = new Date();
    intervalId = setInterval(() => {
        timer.innerText = getTimerTime()
    }, 1000);
}

function getTimerTime() {
    return Math.floor((new Date() - startTime) / 1000)
}

renderNewQuote()

firstLetterBox.addEventListener('click', (event)=> { 
    if (firstLetterBox.value.toLowerCase().charCodeAt() < 97 || firstLetterBox.value.toLowerCase().charCodeAt() > 122) {
        firstLetterBox.value = ''
    }
    if (firstLetterBox.value != '' && numbersBox.value != '') {
        firstLetterBox.value = ''
        numbersBox.value = ''
    }
    if (firstLetterBox.value == '' && numbersBox.value == '') {
        document.getElementById('firstLetter').removeAttribute("readonly");
        document.getElementById('numbers').removeAttribute("readonly");
    }
    else if (numbersBox.value != '') {
        document.getElementById('firstLetter').setAttribute("readonly","readonly");
        document.getElementById('numbers').removeAttribute("readonly");
    }
})

numbersBox.addEventListener('click', (event)=> {
    if (firstLetterBox.value.toLowerCase().charCodeAt() < 97 || firstLetterBox.value.toLowerCase().charCodeAt() > 122) {
        firstLetterBox.value = ''
    }
    if (firstLetterBox.value != '' && numbersBox.value != '') {
        firstLetterBox.value = ''
        numbersBox.value = ''
    }
    if (firstLetterBox.value == '' && numbersBox.value == '') {
        document.getElementById('firstLetter').removeAttribute("readonly");
        document.getElementById('numbers').removeAttribute("readonly");
    }
    else if (firstLetterBox.value != '') {
        document.getElementById('numbers').setAttribute("readonly","readonly");
        document.getElementById('firstLetter').removeAttribute("readonly");
    }
})

window.addEventListener('click', (event)=> {
    if (firstLetterBox.value != '' && numbersBox.value != '') {
        firstLetterBox.value = ''
        numbersBox.value = ''
    }
    if (firstLetterBox.value.toLowerCase().charCodeAt() < 97 || firstLetterBox.value.toLowerCase().charCodeAt() > 122) {
        firstLetterBox.value = ''
    }
})
