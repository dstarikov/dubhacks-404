# [FinanceCam](https://devpost.com/software/financecam)
## AI-powered Instant financial insights about the companies of your choosing, with a single press of a button

## WINNER OF [DubHacks](https://dubhacks.co/) 2018
* Best Use of BlackRock API
* Best Use of Google Cloud Platform

## Inspiration
We were inspired by our inexperience in the world of investments, and wanted to create a tool that would make it easier for the uninitiated (like us) to make sound investment decisions by summarizing the financial information we have available in a short and easy-to-read format.

## What it does
The user has multiple options to choose a company, and all of them are simple and intuitive. The three options are:
* Voice detection
* Logo detection using a camera
* Text detection using a camera One a company has been chosen, the app interfaces with our server in order to process the company name, find a unique Ticker to identify the company if it exists in the database, and return financial information about the company.

## How we built it
We designed it from the top down, gradually turning an idea into reality, with a lot of teamwork and communication. We started by building an android app and connecting it with Firebase to take advantage of machine learning for image identification. We added speech-to-text for accessibility, while simultaneously working on a communication protocol between the app and our server, and the server with external APIs. The data is returned to the user and rendered in an html page for easy scaling.

## What we learned
This was a very multi-faceted project, and we learned a lot about integrating multiple languages and communication protocols in a short amount of time. Every single one of us is now capable of doing something that we had no previous experience with, and we are more confident in our problem-solving and team design decisions.
