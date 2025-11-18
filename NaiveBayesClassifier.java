import java.io.*;
import java.util.*;

public class NaiveBayesClassifier {

    // example weights
    private double[] weights = {1.5, 2.0, 1.0, 1.5, 1.0};

    // Party -> count of that party
    private Map<String, Integer> partyCounts = new HashMap<>();

    // Feature counts: questionIndex -> party -> answer -> count
    private Map<Integer, Map<String, Map<String, Integer>>> featureCounts = new HashMap<>();

    private int totalSamples = 0;
    private int numQuestions = 0;

    public NaiveBayesClassifier(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    // Train the model from CSV file
    public void train(String csvFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String line = br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            // last column is the party
            String party = parts[parts.length - 1];

            // Count party frequency
            partyCounts.put(party, partyCounts.getOrDefault(party, 0) + 1);
            totalSamples++;

            // For each question answer
            for (int q = 0; q < numQuestions; q++) {
                String answer = parts[q];

                featureCounts
                        .computeIfAbsent(q, k -> new HashMap<>())
                        .computeIfAbsent(party, k -> new HashMap<>())
                        .put(answer, featureCounts.get(q).get(party).getOrDefault(answer, 0) + 1);
            }
        }

        br.close();
    }

    // Predict party based on partial answers (A/B/C/D or null for unanswered)
    public String predict(List<String> answers) {
        double bestScore = Double.NEGATIVE_INFINITY;
        String bestParty = null;

        for (String party : partyCounts.keySet()) {

            double score = Math.log(partyCounts.get(party) / (double) totalSamples);

            for (int q = 0; q < answers.size(); q++) {
                String answer = answers.get(q);
                if (answer == null) continue; // skip unanswered

                int count = 0;
                if (featureCounts.containsKey(q) &&
                        featureCounts.get(q).containsKey(party) &&
                        featureCounts.get(q).get(party).containsKey(answer)) {

                    count = featureCounts.get(q).get(party).get(answer);
                }

                // Laplace smoothing
                double probability = (count + 1.0) / (partyCounts.get(party) + 4.0);

                score += weights[q] * Math.log(probability);
            }

            if (score > bestScore) {
                bestScore = score;
                bestParty = party;
            }
        }

        return bestParty;
    }

    // Evaluate model accuracy
    public double evaluate(String csvFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String line = br.readLine(); // skip header

        int correct = 0;
        int total = 0;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            // all answers except the last one
            List<String> answers = new ArrayList<>();
            for (int i = 0; i < parts.length - 1; i++) {
                answers.add(parts[i]);
            }

            String actual = parts[parts.length - 1];
            String predicted = predict(answers);

            if (actual.equals(predicted)) {
                correct++;
            }

            total++;
        }

        br.close();
        return total == 0 ? 0 : (correct / (double) total);
    }
}
