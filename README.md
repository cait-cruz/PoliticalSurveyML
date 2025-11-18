# Political Survey Machine Learning Classifier

This project is a simple machine learning program written in Java that predicts a user's political party based on their answers to a short survey. The prediction is made using a Naive Bayes classifier that trains itself each time new survey results are added.

## How It Works

### 1. Survey Questions
The program asks several multiple-choice questions (A, B, C, D). Each question represents different ideological viewpoints.

### 2. Political Party Label
At the end of the survey, the user selects the political party they identify with:
- Democrat  
- Republican  
- Libertarian  
- Green  

This final answer becomes the *label* used for supervised learning.

### 3. CSV Data Storage
All responses are saved into a file called `data.csv`.  
Every time the program runs, it reads this file and retrains the classifier.

### 4. Machine Learning Model
The program uses a Naive Bayes classifier that:
- Counts how often each answer is associated with each political party  
- Applies weights to certain questions  
- Uses Laplace smoothing  
- Predicts the most likely party after each new answer  

### 5. Model Evaluation
The classifier includes an evaluation function that checks accuracy by comparing predictions with actual labels from the CSV file.

## Files Included
- `PoliticalSurveyApp.java` — Main program that runs the survey.
- `NaiveBayesClassifier.java` — Machine learning classifier.
- `data.csv` — Generated automatically when users complete the survey.

## How to Run
1. Open the project in IntelliJ.
2. Run `PoliticalSurveyApp.java`.
3. Answer the survey questions.
4. The program will:
   - Predict your political party as you answer
   - Ask for your real party at the end
   - Save your response to `data.csv`
   - Retrain itself for next time
