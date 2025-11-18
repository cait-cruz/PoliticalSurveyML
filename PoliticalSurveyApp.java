import java.io.*;
import java.util.*;

public class PoliticalSurveyApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Build the list of questions
        List<Question> questions = buildQuestions();

        NaiveBayesClassifier model = new NaiveBayesClassifier(questions.size());

        try {
            model.train("data.csv");
            System.out.println("\nModel trained on previous survey data.\n");
        } catch (Exception e) {
            System.out.println("No training data yet — model will learn as users take the survey.");
        }

        // --- Evaluate model performance if training data exists ---
        try {
            double accuracy = model.evaluate("data.csv");
            System.out.println("Current model accuracy on past data: " + (accuracy * 100) + "%\n");
        } catch (Exception e) {
            System.out.println("Model cannot be evaluated yet — not enough data.\n");
        }

        System.out.println("Welcome to the Political Survey!");
        System.out.println("Please answer the following questions by typing A, B, C, or D.\n");

        // 2. Collect answers
        List<String> answers = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("Question " + (i + 1) + ": " + q.text);
            System.out.println("A. " + q.options[0]);
            System.out.println("B. " + q.options[1]);
            System.out.println("C. " + q.options[2]);
            System.out.println("D. " + q.options[3]);
            System.out.print("Your answer (A/B/C/D): ");

            String input = scanner.nextLine().trim().toUpperCase();
            while (!input.matches("[ABCD]")) {
                System.out.print("Please enter A, B, C, or D: ");
                input = scanner.nextLine().trim().toUpperCase();
            }

            answers.add(input);

            // Make a partial prediction after each answer
            List<String> partialAnswers = new ArrayList<>(answers);
            // Fill unanswered with null
            while (partialAnswers.size() < questions.size()) {
                partialAnswers.add(null);
            }

            String guess = model.predict(partialAnswers);
            System.out.println("Current model guess (so far): " + guess + "\n");

            System.out.println();
        }

        // 3. Ask for true political affiliation (label)
        System.out.println("Final question: Which political party do you most identify with?");
        System.out.println("A. Democrat");
        System.out.println("B. Republican");
        System.out.println("C. Libertarian");
        System.out.println("D. Green");
        System.out.print("Your party (A/B/C/D): ");

        String partyInput = scanner.nextLine().trim().toUpperCase();
        while (!partyInput.matches("[ABCD]")) {
            System.out.print("Please enter A, B, C, or D: ");
            partyInput = scanner.nextLine().trim().toUpperCase();
        }

        String party = mapPartyLetterToName(partyInput);

        // 4. Save to CSV
        try {
            saveResponseToCsv(answers, party, "data.csv");
            System.out.println("\nThank you! Your responses have been recorded.");
        } catch (IOException e) {
            System.out.println("Error saving your response: " + e.getMessage());
        }

        scanner.close();
    }

    // ---------- Helper classes and methods ----------

    // Simple Question class used inside this file
    static class Question {
        String text;
        String[] options; // length 4: A, B, C, D

        Question(String text, String[] options) {
            this.text = text;
            this.options = options;
        }
    }

    private static List<Question> buildQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question(
                "What should be the government's role in supporting low-income communities?",
                new String[]{
                        "Expand federal assistance and social support programs.",
                        "Encourage private charities and local organizations to take the lead.",
                        "Prioritize skill-building programs and workforce development.",
                        "Limit government involvement and allow the market to address needs."
                }
        ));

        questions.add(new Question(
                "How do you believe healthcare should be structured in the U.S.?",
                new String[]{
                        "A government-run system that provides healthcare for everyone.",
                        "Primarily private insurance with very little government regulation.",
                        "A blended system where public and private options both exist.",
                        "A fully free-market healthcare system with no federal oversight."
                }
        ));

        questions.add(new Question(
                "How should taxes be managed?",
                new String[]{
                        "Increase taxes on higher earners to support public programs.",
                        "Lower taxes overall, especially for businesses and investors.",
                        "Close loopholes and simplify the entire tax system.",
                        "Reduce or eliminate income taxes and shrink government budgets."
                }
        ));

        questions.add(new Question(
                "What approach should the government take on environmental issues?",
                new String[]{
                        "Implement strong rules and fund renewable energy projects.",
                        "Promote innovation but avoid heavy federal restrictions.",
                        "Find a middle ground between environmental action and economic growth.",
                        "Keep government involvement minimal and let market forces decide."
                }
        ));

        questions.add(new Question(
                "What is the best way to improve the education system?",
                new String[]{
                        "Increase funding and investment in public schools.",
                        "Give families more freedom through school choice programs.",
                        "Emphasize career training and vocational pathways.",
                        "Reduce federal control and pass decisions to local communities."
                }
        ));

        return questions;
    }

    private static String mapPartyLetterToName(String letter) {
        switch (letter) {
            case "A":
                return "Democrat";
            case "B":
                return "Republican";
            case "C":
                return "Libertarian";
            case "D":
                return "Green";
            default:
                return "Unknown";
        }
    }

    private static void saveResponseToCsv(List<String> answers, String party, String fileName) throws IOException {
        File file = new File(fileName);
        boolean fileExists = file.exists();

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // If file doesn't exist yet, write header
            if (!fileExists) {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < answers.size(); i++) {
                    header.append("Q").append(i + 1).append(",");
                }
                header.append("Party");
                bw.write(header.toString());
                bw.newLine();
            }

            // Write the actual data row
            StringBuilder row = new StringBuilder();
            for (String ans : answers) {
                row.append(ans).append(",");
            }
            row.append(party);
            bw.write(row.toString());
            bw.newLine();
        }
    }
}
