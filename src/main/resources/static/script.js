let RANDOM_QUOTE_API_URL = 'https://api.quotable.io/random'
let quoteDisplayElement = document.getElementById('quoteDisplay')
let quoteInputElement = document.getElementById('quoteInput')
let timerElement = document.getElementById('timer')
let timerStart = false;

let numOfInputs = 0;
let numOfCharacters;

quoteInputElement.addEventListener('keydown', (event) => {
    if (event.code == "Backspace" && numOfInputs <= 0) {
        --numOfInputs;
    }
})

quoteInputElement.addEventListener('input', () => {
    let arrayQuote = quoteDisplayElement.querySelectorAll('span')
    let arrayValue = quoteInputElement.value.split('')
    let correct = true;
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
        numOfCharacters = index + 1;

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
    console.log(numOfInputs);

    if (correct) {
        clearInterval(intervalId);
        timerStart = false;
        alert("Finish the quote in: " + timerElement.innerText + " seconds");
        // alert("# of characters: " + numOfCharacters);
        // alert("# of inputs: " + numOfInputs);
        // alert("# of mistakes: " + (numOfInputs-numOfCharacters));
        document.getElementById('timer_input').value = timerElement.innerText;
        document.getElementsByName('submitTime')[0].submit();
        renderNewQuote();
    }
})

function getRandomQuote() {
    return fetch(RANDOM_QUOTE_API_URL).then(response => response.json()).then(data => data.content).catch(error => console.error('Error:', error));
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
    document.getElementById('timer_input').value = '';
}


let startTime
let intervalId
function startTimer() {
    // timerElement.innerText = 0;
    startTime = new Date();
    intervalId = setInterval(() => {
        timer.innerText = getTimerTime()
    }, 1000);
}

function getTimerTime() {
    return Math.floor((new Date() - startTime) / 1000)
}

renderNewQuote()
