package com.zakmicallef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Main {

    public static ArrayList<String> training = new ArrayList<>();
    public static ArrayList<String> test = new ArrayList<>();

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

        cloneArrays();

        laPlaceSmoothing();

        unkVersion();
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

        try {
            FileInput.writeNgramsToFile(unigrams, "SampleModelOutput/vanilla/unigram.txt");
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
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

        try {
            FileInput.writeNgramsToFile(bigrams, "SampleModelOutput/vanilla/bigram.txt");
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
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

        try {
            FileInput.writeNgramsToFile(trigrams, "SampleModelOutput/vanilla/trigram.txt");
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
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

        try {
            FileInput.writeNgramsToFile(unigramsLP, "SampleModelOutput/laplace/LPunigrams.txt");
            FileInput.writeNgramsToFile(bigramsLP, "SampleModelOutput/laplace/LPbigrams.txt");
            FileInput.writeNgramsToFile(trigramsLP, "SampleModelOutput/laplace/LPtrigrams.txt");
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private static void cloneArrays() {
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
    }

    private static void unkVersion() {
        System.out.println("UNKVersion " + java.time.LocalTime.now());
        String unkToken = "<UNK>";
        unigramsUNK = new ArrayList<Ngram>(unigrams);
        bigramsUNK = new ArrayList<Ngram>(bigrams);
        trigramsUNK = new ArrayList<Ngram>(trigrams);


        for (Ngram ngram : unigramsUNK) {
            if (ngram.count == 1) {
                ngram.n_gram = new String[]{unkToken};
            }
        }

        for (Ngram ngram : bigramsUNK) {
            if (ngram.count == 1) {
                ngram.n_gram = new String[]{unkToken};
            }
        }

        for (Ngram ngram : trigramsUNK) {
            if (ngram.count == 1) {
                ngram.n_gram = new String[]{unkToken};
            }
        }

        try {
            FileInput.writeNgramsToFile(unigramsUNK, "SampleModelOutput/unk/UNKunigrams.txt");
            FileInput.writeNgramsToFile(bigramsUNK, "SampleModelOutput/unk/UNKbigrams.txt");
            FileInput.writeNgramsToFile(trigramsUNK, "SampleModelOutput/unk/UNKtrigrams.txt");
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    public static void smoothUnigram() {
        int totalCount = 0;
        int vocabSize = unigrams.size();

        for (int i = 0; i < unigrams.size(); i++) {
            totalCount += unigrams.get(i).count;
        }


        for (int i = 0; i < unigrams.size(); i++) {
            unigrams.get(i).smoothProbability = (double) (unigrams.get(i).count + 1) / (totalCount + vocabSize);
        }
    }

    public static void smoothBigram() {
        int vocabSize = unigrams.size();

        for (int i = 0; i < bigrams.size(); i++) {
            int count_word = 0;
            for (int j = 0; j < unigrams.size(); j++) {
                if (bigrams.get(i).n_gram[0].equals(unigrams.get(j).n_gram[0])) {
                    count_word = unigrams.get(j).count;
                }
            }
            bigrams.get(i).smoothProbability = (double) (bigrams.get(i).count + 1) / (count_word + vocabSize);
        }
    }

    public static void smoothTrigram() {

    }
}
