// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
    const facts =
        ['I live in Texas.', 'I am 20 years old.', 'This is my first internship.',
            'I love cats.', "I was Petroleum Engineering major before I transfered to CS."];

    // Pick a random fact.
    const fact = facts[Math.floor(Math.random() * facts.length)];

    // Add it to the page.
    const factContainer = document.getElementById('fact-container');
    factContainer.innerText = fact;

    // Update texts in button
    const button = document.getElementById('button');
    button.innerText = "See another one";
}

/**
 * Adds a response after user posts a comment
 */
async function showHistory() {
    // get comments
    const maxNum = document.getElementById("maxNum").value;
    const response = await fetch('/data?maxNum=' + maxNum);
    const comments = await response.json();
    
    // list out the comments
    const commentsContainer = document.getElementById('comments-container');
    commentsContainer.innerHTML = '';
    for (var i = 0; i < comments.length; i++){
        commentsContainer.appendChild(createListElement(comments[i][0] + ": " + comments[i][1]));
    }
}

/** Creates an <li> element containing text. */
function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
}

/**
 * Delete all comments
 */
async function deleteComments() {
    await fetch('/delete-data', {method: 'POST'});
    showHistory();
}

/** Show comments input */
async function showCommentsInput() {
    const response = await fetch('/login');
    const showInput = await response.text();
    const inputContainer = document.getElementById('inputContainer');
    inputContainer.innerHTML = showInput;
}

/** Creates a chart and adds it to the page. */
async function drawChart() {
    // get data
    const response = await fetch('/data?maxNum=0');
    const comments = await response.json();

    // get statistics
    const email = ["@google.com", "@gmail.com", "Others"];
    const emailData = [0, 0, 0];
    comments.forEach((comment) => {
        if (comment[0].includes(email[0])) {
            emailData[0]++;
        } else if (comment[0].includes(email[1])) {
            emailData[1]++;
        } else {
            emailData[2]++;
        }
    })
    
    // create chart
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Email');
    data.addColumn('number', 'Count');
    for(var i = 0; i < email.length; i++){
        data.addRow([email[i], emailData[i]]);
    }

    const options = {
        'title': 'Statistics',
        'width':500,
        'height':400,
        'pieHole': 0.4
    };

    const chart = new google.visualization.PieChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
}