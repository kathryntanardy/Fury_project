let RANDOM_QUOTE_API_URL = 'https://api.quotable.io/random'
let quoteDisplayElement = document.getElementById('quoteDisplay')
let quoteInputElement = document.getElementById('quoteInput')
let timerElement = document.getElementById('timer')
let timerStart = false;

let numOfInputs = 0;
let numOfCharacters;

let theLength;
let theFirstLetter;


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
        let wpm = ((numOfInputs/5)/(timerElement.innerText/60)).toFixed(2);
        let accuracy = ((numOfCharacters/numOfInputs)*100).toFixed(2);
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
    return fetch('https://random-word-api.vercel.app/api?words=13').then(response=> response.text());
}
function getHardRandomWords () {
    return fetch('https://random-word-api.herokuapp.com/word?number=12').then(response => response.text());
}
function getCustomWords() {
    if (theFirstLetter == '' && theLength != '') {
        return fetch('https://random-word-api.vercel.app/api?words=15&length='+theLength).then(response => response.text());

    }
    else if (theLength == '' && theFirstLetter != '') {
        return fetch('https://random-word-api.vercel.app/api?words=15&letter='+ theFirstLetter).then(response => response.text());
    }
    else if(theLength != '' && theFirstLetter != '') {
        return fetch('https://random-word-api.vercel.app/api?words=15&length=' + theLength + '&letter=' + theFirstLetter).then(response => response.text());
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
    let quote = await getHardRandomWords();
    quoteDisplayElement.innerHTML = '';
    quote.split('').forEach(character => {
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
    let quote = await getEasyRandomWords();
    quoteDisplayElement.innerHTML = '';
    quote.split('').forEach(character => {
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
    theLength = document.getElementById('wordLength').value;
    theFirstLetter = document.getElementById('firstLetter').value;
    if (theLength == '' && theFirstLetter == '') {
    }
    else {
        if (theLength > 9) {
            theLength = '9'
            document.getElementById('wordLength').value = '9'
        }
        if (theLength < 4) {
            theLength = '4'
            document.getElementById('wordLength').value = '4'
        }

       if ((theFirstLetter.toLowerCase().charCodeAt() >= 97 && theFirstLetter.toLowerCase().charCodeAt() <= 122)){ // Uppercase
            theFirstLetter = theFirstLetter.toLowerCase();
        }
        else {
            theFirstLetter = '';
            document.getElementById('firstLetter').value = '';
        } 
        let quote = await getCustomWords();
        quoteDisplayElement.innerHTML = '';
        quote.split('').forEach(character => {
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
