package com.zakmicallef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static ArrayList<String> test = new ArrayList<>();
    public static ArrayList<String> training = new ArrayList<>();
    public static ArrayList<String> trainingUNK = new ArrayList<>();


    public static ArrayList<Ngram> unigrams = new ArrayList<>();
    public static ArrayList<Ngram> bigrams = new ArrayList<>();
    public static ArrayList<Ngram> trigrams = new ArrayList<>();

    public static ArrayList<Ngram> unigramsLP = new ArrayList<>();
    public static ArrayList<Ngram> bigramsLP = new ArrayList<>();
    public static ArrayList<Ngram> trigramsLP = new ArrayList<>();

    public static ArrayList<Ngram> unigramsUNK = new ArrayList<>();
    public static ArrayList<Ngram> bigramsUNK = new ArrayList<>();
    public static ArrayList<Ngram> trigramsUNK = new ArrayList<>();

    public static int index = 0;

    public static void main(String[] args) {
        FileInput.listFiles("sample");

        calcUnigram();
        calcBigram();
        calcTrigram();

        unigramProbability();
        bigramProbability();
        trigramProbability();

        printNgram(unigrams, "SampleModelOutput/vanilla/unigram.txt");
        printNgram(bigrams, "SampleModelOutput/vanilla/bigram.txt");
        printNgram(trigrams, "SampleModelOutput/vanilla/trigram.txt");

        cloneArrays();

        laPlaceSmoothing();

        laplaceUnigramProbability();
        laplaceBigramProbability();
        laplaceTrigramProbability();

        printNgram(unigramsLP, "SampleModelOutput/laplace/LPunigrams.txt");
        printNgram(bigramsLP, "SampleModelOutput/laplace/LPbigrams.txt");
        printNgram(trigramsLP, "SampleModelOutput/laplace/LPtrigrams.txt");

        unkVersion();

        printNgram(unigramsUNK, "SampleModelOutput/unk/UNKunigrams.txt");
        printNgram(bigramsUNK, "SampleModelOutput/unk/UNKbigrams.txt");
        printNgram(trigramsUNK, "SampleModelOutput/unk/UNKtrigrams.txt");
    }

    private static void printNgram(ArrayList<Ngram> unigrams, String path) {
        try {
            FileInput.writeNgramsToFile(unigrams, path);
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private static void calcUnigram() {
        System.out.println("Calculating Unigram " + java.time.LocalTime.now());

        for (String word : training) {
            if (exists(new String[]{word}, unigrams)) {
                unigrams.get(index).count++;
            } else {
                Ngram unigram = new Ngram(1);
                unigram.n_gram[0] = word;
                unigrams.add(unigram);
            }
        }
    }

    private static void calcBigram() {
        System.out.println("Calculating Bigram " + java.time.LocalTime.now());
        for (int i = 0; i < training.size() - 1; i++) {
            if (valid(training.get(i)) && (valid(training.get(i + 1)))) {
                String word = training.get(i);
                String word2 = training.get(i + 1);

                if (exists(new String[]{word, word2}, bigrams)) {
                    bigrams.get(index).count++;
                } else {
                    Ngram bigram = new Ngram(2);
                    bigram.n_gram[0] = training.get(i);
                    bigram.n_gram[1] = training.get(i + 1);
                    bigrams.add(bigram);
                }
            }
        }


    }

    private static void calcTrigram() {
        System.out.println("Calculating Trigram " + java.time.LocalTime.now());

        for (int i = 0; i < training.size() - 2; i++) {
            if (valid(training.get(i)) && valid(training.get(i + 1)) && valid(training.get(i + 2))) {
                String word = training.get(i);
                String word2 = training.get(i + 1);
                String word3 = training.get(i + 2);

                if (exists(new String[]{word, word2, word3}, trigrams)) {
                    trigrams.get(index).count++;
                } else {
                    Ngram trigram = new Ngram(3);
                    trigram.n_gram[0] = training.get(i);
                    trigram.n_gram[1] = training.get(i + 1);
                    trigram.n_gram[2] = training.get(i + 2);
                    trigrams.add(trigram);
                }
            }
        }
    }

    private static boolean valid(String word) {
        if (word.isBlank() || word.equals("EOS") || word.equals("EOF")) {
            return false;
        }
        return true;
    }

    private static boolean exists(String[] words, ArrayList<Ngram> ngrams) {
        for (int i = 0; i < ngrams.size(); i++) {
            // comparing 2 arrays to check if their size and contents are identical
            if (Arrays.equals(words, ngrams.get(i).n_gram)) {
                index = i;
                return true;
            }
        }
        return false;
    }

    public static void laPlaceSmoothing() {
        System.out.println("LaPlace Smoothing " + java.time.LocalTime.now());


        for (Ngram ngram : unigramsLP) {
            ngram.count++;
        }

        for (Ngram ngram : bigramsLP) {
            ngram.count++;
        }

        for (Ngram ngram : trigramsLP) {
            ngram.count++;
        }

    }

    private static void unkVersion() {
        System.out.println("UNKVersion " + java.time.LocalTime.now());
        String unkToken = "<UNK>";

        for (int i = 0; i < trainingUNK.size() - 3; i++) {
            if (unigramCount(trainingUNK.get(i)) == 1) {
                trainingUNK.set(i, unkToken);
            }
        }

        calcUnigramsUNK();
        calcBigramUNK();
        calcTrigramUNK();
    }


    public static void unigramProbability() {
        System.out.println("Unigram Probability" + java.time.LocalTime.now());

        for (Ngram unigram : unigrams) {
            int count = unigramCount(unigram.n_gram[0]);
            unigram.probability = (double) (unigram.count) / count;
        }
    }


    public static void bigramProbability() {
        System.out.println("Bigram Probability" + java.time.LocalTime.now());

        for (Ngram bigram : bigrams) {
            int count = unigramCount(bigram.n_gram[0]);
            bigram.probability = (double) (bigram.count) / count;
        }
    }

    public static void trigramProbability() {
        System.out.println("Trigram Probability" + java.time.LocalTime.now());

        for (Ngram trigram : trigrams) {
            int count = bigramCount(new String[]{trigram.n_gram[0], trigram.n_gram[1]});
            trigram.probability = (double) (trigram.count) / count;
        }
    }

    private static int unigramCount(String word) {
        for (Ngram unigram : unigrams) {
            if (word.equals(unigram.n_gram[0])) {
                return unigram.count;
            }
        }
        return -1;
    }

    private static int bigramCount(String[] strings) {
        for (Ngram bigram : bigrams) {
            if (Arrays.equals(strings, bigram.n_gram)) {
                return bigram.count;
            }
        }
        return -1;
    }

    public static void laplaceUnigramProbability() {
        System.out.println("Laplace Unigram Probability " + java.time.LocalTime.now());

        int totalCount = 0;
        int vocabSize = unigramsLP.size();

        for (int i = 0; i < unigramsLP.size(); i++) {
            totalCount += unigramsLP.get(i).count;
        }


        for (int i = 0; i < unigramsLP.size(); i++) {
            unigramsLP.get(i).probability = (double) (unigramsLP.get(i).count + 1) / (totalCount + vocabSize);
        }
    }

    public static void laplaceBigramProbability() {
        System.out.println("Laplace Bigram Probability " + java.time.LocalTime.now());

        int vocabSize = unigramsLP.size();

        for (int i = 0; i < bigramsLP.size(); i++) {
            int count_word = 0;
            for (int j = 0; j < unigramsLP.size(); j++) {
                if (bigramsLP.get(i).n_gram[0].equals(unigramsLP.get(j).n_gram[0])) {
                    count_word = unigramsLP.get(j).count;
                }
            }

            bigramsLP.get(i).probability = (double) (bigramsLP.get(i).count + 1) / (count_word + vocabSize);
        }
    }

    public static void laplaceTrigramProbability() {
        System.out.println("Laplace Trigram Probability " + java.time.LocalTime.now());

        int vocabSize = unigramsLP.size();

        for (int i = 0; i < trigramsLP.size(); i++) {
            // Finding the count of word_n-2,word_n-1
            int countWords = 0;
            for (int j = 0; j < bigramsLP.size(); j++) {
                if (Arrays.equals(new String[]{trigramsLP.get(i).n_gram[0], trigramsLP.get(i).n_gram[1]}, bigramsLP.get(j).n_gram)) {
                    countWords = bigramsLP.get(j).count;
                }
            }
            // (count(Wn-2,Wn-1,Wn) + 1) / (count(Wn-2,Wn-1) + V)
            trigramsLP.get(i).probability = (double) (trigramsLP.get(i).count + 1) / (countWords + vocabSize);
        }
    }

    private static void cloneArrays() {
        System.out.println("Cloning Arrays " + java.time.LocalTime.now());

        for (Ngram ngram : unigrams) {
            unigramsLP.add(ngram.clone());
        }

        for (Ngram ngram : bigrams) {
            bigramsLP.add(ngram.clone());
        }

        for (Ngram ngram : trigrams) {
            trigramsLP.add(ngram.clone());
        }

        for (Ngram ngram : unigrams) {
            unigramsUNK.add(ngram.clone());
        }

        for (Ngram ngram : bigrams) {
            bigramsUNK.add(ngram.clone());
        }

        for (Ngram ngram : trigrams) {
            trigramsUNK.add(ngram.clone());
        }

        trainingUNK.addAll(training);
    }

    private static void calcUnigramsUNK() {
        System.out.println("Calculating unigramsUNK " + java.time.LocalTime.now());

        for (String word : trainingUNK) {
            if (exists(new String[]{word}, unigramsUNK)) {
                unigramsUNK.get(index).count++;
            } else {
                Ngram unigram = new Ngram(1);
                unigram.n_gram[0] = word;
                unigramsUNK.add(unigram);
            }
        }
        unigramProbabilityUNK();
    }

    private static void calcBigramUNK() {
        System.out.println("Calculating BigramUNK " + java.time.LocalTime.now());
        for (int i = 0; i < trainingUNK.size() - 1; i++) {
            if (valid(trainingUNK.get(i)) && (valid(trainingUNK.get(i + 1)))) {
                String word = trainingUNK.get(i);
                String word2 = trainingUNK.get(i + 1);

                if (exists(new String[]{word, word2}, bigramsUNK)) {
                    bigramsUNK.get(index).count++;
                } else {
                    Ngram bigram = new Ngram(2);
                    bigram.n_gram[0] = trainingUNK.get(i);
                    bigram.n_gram[1] = trainingUNK.get(i + 1);
                    bigramsUNK.add(bigram);
                }
            }
        }
        bigramProbabilityUNK();
    }

    private static void calcTrigramUNK() {
        System.out.println("Calculating TrigramUNK " + java.time.LocalTime.now());

        for (int i = 0; i < trainingUNK.size() - 2; i++) {
            if (valid(trainingUNK.get(i)) && valid(trainingUNK.get(i + 1)) && valid(trainingUNK.get(i + 2))) {
                String word = trainingUNK.get(i);
                String word2 = trainingUNK.get(i + 1);
                String word3 = trainingUNK.get(i + 2);

                if (exists(new String[]{word, word2, word3}, trigramsUNK)) {
                    trigramsUNK.get(index).count++;
                } else {
                    Ngram trigram = new Ngram(3);
                    trigram.n_gram[0] = trainingUNK.get(i);
                    trigram.n_gram[1] = trainingUNK.get(i + 1);
                    trigram.n_gram[2] = trainingUNK.get(i + 2);
                    trigramsUNK.add(trigram);
                }
            }
        }
        trigramProbabilityUNK();
    }

    public static void unigramProbabilityUNK() {
        System.out.println("Calculating unigramUNK Probability " + java.time.LocalTime.now());
        for (Ngram unigram : unigramsUNK) {
            int count = unigramCount(unigram.n_gram[0]);
            unigram.probability = (double) (unigram.count) / count;
        }
    }


    public static void bigramProbabilityUNK() {
        System.out.println("Calculating BigramUNK Probability " + java.time.LocalTime.now());

        for (Ngram bigram : bigramsUNK) {
            int count = unigramCount(bigram.n_gram[0]);
            bigram.probability = (double) (bigram.count) / count;
        }
    }

    public static void trigramProbabilityUNK() {
        System.out.println("Calculating TrigramUNK Probability " + java.time.LocalTime.now());

        for (Ngram trigram : trigramsUNK) {
            int count = bigramCount(new String[]{trigram.n_gram[0], trigram.n_gram[1]});
            trigram.probability = (double) (trigram.count) / count;
        }
    }
}
