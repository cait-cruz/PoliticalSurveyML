# Political Survey Machine Learning Classifier

This project uses a Naive Bayes classifier to predict a user's political party based on their answers to a short survey.  
The program asks multiple ideology-based questions and attempts to guess the user's political affiliation before the survey ends.

## Contents
- PoliticalSurveyApp.java — main program that runs the survey and saves responses
- NaiveBayesClassifier.java — simple Naive Bayes model trained on CSV data
- data.csv — file where survey responses are stored (optional)

## How It Works
- Users answer A/B/C/D multiple-choice questions
- System predicts political leaning using partial answers
- Final answer provides the true label for supervised learning
- Data is saved and used to retrain the model automatically
