package com.zakmicallef;

import java.util.ArrayList;
import java.util.Arrays;

import static com.zakmicallef.Main.*;

public class Probabilities {

    public static double CalcUnigramProbability(String word) {
        word = word.replaceAll(" ","");
        int vocabSize = uni.size();
        double probability = (double) (CalcUnigramCount(word)) / vocabSize;
        if (Double.isNaN(probability)) {
            return 0;
        }
        return probability;
    }


    public static double CalcBigramProbability(String wordn1, String word) {
        word = word.replaceAll(" ","");
        wordn1 = wordn1.replaceAll(" ","");

        int count = CalcUnigramCount(wordn1);
        int bigramCount = CalcBigramCount(new String[]{wordn1, word});
        double probability = (double) (bigramCount) / count;
        if (Double.isNaN(probability)) {
            return 0;
        }
        return probability;
    }

    public static double CalcTrigramProbability(String wordn2, String wordn1, String word) {
        word = word.replaceAll(" ","");
        wordn1 = wordn1.replaceAll(" ","");
        wordn2 = wordn2.replaceAll(" ","");
        int count = CalcBigramCount(new String[]{wordn2, wordn1});
        double probability = (double) (CalcTrigramCount(new String[]{wordn2, wordn1, word})) / count;
        if (Double.isNaN(probability)) {
            return 0;
        }
        return probability;
    }

    public static double CalcLaplaceUnigramProbability(String word) {
        int totalCount = 0;
        int vocabSize = uni.size();

        for (Ngram ngram : uni) {
            totalCount += ngram.count;
        }


        double probability = (double) (CalcUnigramCount(word) + 1) / (totalCount + vocabSize);
        if (Double.isNaN(probability)) {
            return 0;
        }
        return probability;
    }

    public static double CalcLaplaceBigramProbability(String wordn1, String word) {
        int vocabSize = uni.size();

        int count_word = 0;
        for (Ngram value : uni) {
            if (wordn1.equals(value.n_gram[0])) {
                count_word = value.count;
            }
        }

        double probability = (double) (CalcBigramCount(new String[]{wordn1, word}) + 1) / (count_word + vocabSize);
        if (Double.isNaN(probability)) {
            return 0;
        }
        return probability;
    }

    public static double CalcLaplaceTrigramProbability(String wordn2, String wordn1, String word) {
        int vocabSize = uni.size();

        // Finding the count of word_n-2,word_n-1
        int countWords = 0;
        for (Ngram value : bi) {
            if (Arrays.equals(new String[]{wordn2, wordn1}, value.n_gram)) {
                countWords = value.count;
            }
        }
        double probability = (double) (CalcTrigramCount(new String[]{wordn2, wordn1, word}) + 1) / (countWords + vocabSize);
        if (Double.isNaN(probability)) {
            return 0;
        }
        return probability;
    }

    private static int CalcUnigramCount(String word) {
        for (Ngram unigram : uni) {
            if (word.equals(unigram.n_gram[0].replaceAll(" ", ""))) {
                return unigram.count;
            }
        }
        return 0;
    }

    private static int CalcBigramCount(String[] strings) {
        for (Ngram bigram : bi) {
            bigram.n_gram[0] = bigram.n_gram[0].replaceAll(" ", "");
            bigram.n_gram[1] = bigram.n_gram[0].replaceAll(" ", "");
            if (Arrays.equals(strings, bigram.n_gram)) {
                return bigram.count;
            }
        }
        return 0;
    }

    public static int CalcTrigramCount(String[] strings) {
        for (Ngram trigram : tri) {
            trigram.n_gram[0] = trigram.n_gram[0].replaceAll(" ", "");
            trigram.n_gram[1] = trigram.n_gram[1].replaceAll(" ", "");
            trigram.n_gram[2] = trigram.n_gram[2].replaceAll(" ", "");
            if (Arrays.equals(strings, trigram.n_gram)) {
                return trigram.count;
            }
        }
        return 0;
    }

    // Method which chooses an ngram based on its probability
    public static int rouletteWheel(ArrayList<Ngram> ngram) {
        // finding the total probability of all the elements in the given ngram
        double totalProbability = 0.0;
        for (Ngram item : ngram) {
            totalProbability += item.probability;
        }

        // divide all the probabilities by the Total Probability
        for (Ngram value : ngram) {
            value.probability /= totalProbability;
        }

        // choosing an ngram based on its probability
        double roulette = Math.random();
        double cumulativeProbability = 0.0;
        int i;
        for (i = 0; i < ngram.size(); i++) {
            cumulativeProbability += ngram.get(i).probability;
            if (cumulativeProbability > roulette) {
                break;
            }
        }
        return i;
    }
}
