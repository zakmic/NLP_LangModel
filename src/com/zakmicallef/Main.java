package com.zakmicallef;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static ArrayList<String> training = new ArrayList<>();
    public static ArrayList<String> test = new ArrayList<>();

    public static ArrayList<Ngram> unigrams = new ArrayList<>();
    public static ArrayList<Ngram> bigrams = new ArrayList<>();
    public static ArrayList<Ngram> trigrams = new ArrayList<>();

    public static int index = 0;

    public static void main(String[] args) {

        FileInput.listFiles("corpus");

        calcUnigram();
        calcBigram();


    }

    private static void calcBigram() {
        for (int i = 0; i < training.size(); i++) {
            if (!(training.get(i+1).charAt(0) == '.')){

            }

        }
    }

    private static void calcUnigram() {
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
}
