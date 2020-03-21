package com.zakmicallef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.zakmicallef.Model.*;
import static com.zakmicallef.Probabilities.*;

public class Main {

    private static String word_i;

    public static void main(String[] args) {
//        FileInput.listFiles("sample");
//
//        BuildModel();

        loadModel();


        Scanner kb = new Scanner(System.in);
        String option = "0";

        do {
            System.out.println("\n1. Generate rest of sentence" + "\n2. Calculate Probability" + "\n 3. Exit");
            option = kb.nextLine();
            switch (Integer.parseInt(option)) {
                case 1: {

                    System.out.println("Choose 1.Vanilla 2.Laplace 3.UNK Version");
                    String flavourStr = kb.nextLine();
                    int flavour = Integer.parseInt(flavourStr);
                    System.out.println("Choose 1.Unigram 2.Bigram 3.Trigram 4.Linear Interpolation");
                    String typeStr = kb.nextLine();
                    int type = Integer.parseInt(typeStr);

                    System.out.println("Enter Text: ");
                    String input = kb.nextLine();

                    String[] textAr = input.toLowerCase().split(" ");
                    ArrayList<String> text = new ArrayList<String>(Arrays.asList(textAr));

                    System.out.println("How many words to generate: ");
                    String count = kb.nextLine();


                    for (int i = 0; i < Integer.parseInt(count); i++) {
                        String nextWord = generateText(text, flavour, type);
                        text.add(nextWord);
                    }

                    for (String word : text) {
                        System.out.println(word);
                    }

                    break;
                }
                case 2: {

                    System.out.println("Choose 1.Vanilla 2.Laplace 3.UNK Version");
                    String flavourStr = kb.nextLine();
                    int flavour = Integer.parseInt(flavourStr);

                    System.out.println("Choose 1.Unigram 2.Bigram 3.Trigram 4.Linear Interpolation");
                    String typeStr = kb.nextLine();
                    int type = Integer.parseInt(typeStr);

                    System.out.println("Please enter the text you would like the program to detect the probability.");
                    String input = kb.nextLine();

                    String[] textAr = input.toLowerCase().split(" ");
                    ArrayList<String> text = new ArrayList<String>(Arrays.asList(textAr));

                    System.out.println(probability(text, flavour, type));
                    break;
                }
                case 3: {
                    System.exit(0);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + Integer.parseInt(option));
            }
        } while (Integer.parseInt(option) != 3);
        System.out.println("Program is terminating");
    }

    public static ArrayList<Ngram> uni = new ArrayList<>();
    public static ArrayList<Ngram> bi = new ArrayList<>();
    public static ArrayList<Ngram> tri = new ArrayList<>();

    private static void BuildModel() {
        JSONCreator.printSingleJSONString(training, "JSON/training.json");
        JSONCreator.printSingleJSONString(test, "JSON/test.json");

        calcUnigram();
        calcBigram();
        calcTrigram();

        unigramProbability();
        bigramProbability();
        trigramProbability();

        printNgram(unigrams, "SampleModelOutput/vanilla/unigram.txt");
        printNgram(bigrams, "SampleModelOutput/vanilla/bigram.txt");
        printNgram(trigrams, "SampleModelOutput/vanilla/trigram.txt");

        JSONCreator.printJSON(unigrams, bigrams, trigrams, "JSON/vanilla/unigram.json", "JSON/vanilla/bigram.json", "JSON/vanilla/trigram.json");

        cloneArrays();

        laPlaceSmoothing();

        laplaceUnigramProbability();
        laplaceBigramProbability();
        laplaceTrigramProbability();

        printNgram(unigramsLP, "SampleModelOutput/laplace/LPunigrams.txt");
        printNgram(bigramsLP, "SampleModelOutput/laplace/LPbigrams.txt");
        printNgram(trigramsLP, "SampleModelOutput/laplace/LPtrigrams.txt");

        JSONCreator.printJSON(unigramsLP, bigramsLP, trigramsLP, "JSON/laplace/unigramLP.json", "JSON/laplace/bigramLP.json", "JSON/laplace/trigramLP.json");

        unkVersion();

        printNgram(unigramsUNK, "SampleModelOutput/unk/UNKunigrams.txt");
        printNgram(bigramsUNK, "SampleModelOutput/unk/UNKbigrams.txt");
        printNgram(trigramsUNK, "SampleModelOutput/unk/UNKtrigrams.txt");

        JSONCreator.printJSON(unigramsUNK, bigramsUNK, trigramsUNK, "JSON/unk/unigramUNK.json", "JSON/unk/bigramUNK.json", "JSON/unk/trigramUNK.json");
        JSONCreator.printSingleJSONString(trainingUNK, "JSON/trainingUNK.json");

        linearInterpolation("vanilla");
    }

    private static double probability(ArrayList<String> sentence, int flavour, int type) {


        setNgram(flavour);


        double probability = 0;


        if (flavour == 2) {
            if (type == 1) {
                for (int i = 1; i < sentence.size(); i++) {
                    probability += CalcLaplaceUnigramProbability(sentence.get(i));
                }
            } else if (type == 2) {
                for (int i = 1; i < sentence.size(); i++) {
                    probability += CalcLaplaceBigramProbability(sentence.get(i - 1), sentence.get(i));
                }
            } else if (type == 3) {
                for (int i = 2; i < sentence.size(); i++) {
                    probability += CalcLaplaceTrigramProbability(sentence.get(i - 2), sentence.get(i - 1), sentence.get(i));
                }
            }

        } else {
            if (type == 1) {
                for (int i = 1; i < sentence.size(); i++) {
                    probability += CalcUnigramProbability(sentence.get(i));
                }
            } else if (type == 2) {
                for (int i = 1; i < sentence.size(); i++) {
                    probability += CalcBigramProbability(sentence.get(i - 1), sentence.get(i));
                }
            } else if (type == 3) {
                for (int i = 2; i < sentence.size(); i++) {
                    probability += CalcTrigramProbability(sentence.get(i - 2), sentence.get(i - 1), sentence.get(i));
                }
            }
        }

        return probability;
    }


    private static String generateText(ArrayList<String> text, int flavour, int type) {
        String word2, word1; //word1 is word n-1
        String word = null;

        setNgram(flavour);

        if (type == 4) {
            tri = linearInterpolation;
        }


        if (text.size() == 1) {   // If the user has only entered one word
            word1 = text.get(0);

            ArrayList<Ngram> bigramMatches = new ArrayList<>();
            bigramMatches = findBigramMatches(word1);

            // If no matching bi are found, back off to unigram model
            if (bigramMatches.size() == 0 || type == 1) {
                System.out.println("Using the Unigram model");
                word = uni.get(probabilisticChoice(uni)).n_gram[0];
            } else {
                System.out.println("Using the Bigram model");
                word = bigramMatches.get(probabilisticChoice(bigramMatches)).n_gram[1];
            }
        } else {                        // If the user has entered 2 or more words
            word2 = text.get(text.size() - 2);
            word1 = text.get(text.size() - 1);

            // finding all tri which start with the last 2 words of the entered sentence
            ArrayList<Ngram> trigramMatches = new ArrayList<>();
            trigramMatches = findTrigramMatches(word2, word1);


            ArrayList<Ngram> bigramMatches = new ArrayList<>();
            if (type != 1) {
                // if no matching tri are found (back off)
                if (trigramMatches.size() == 0 || type != 3) {
                    bigramMatches = findBigramMatches(word1);
                } else {
                    System.out.println("Using the Trigram model");
                    word = trigramMatches.get(probabilisticChoice(trigramMatches)).n_gram[2];
                }
            }

            // if no matching tri or bi are found, back off to unigram model
            if (trigramMatches.size() == 0 && bigramMatches.size() == 0 || type == 1) {
                System.out.println("Using the Unigram model");
                word = uni.get(probabilisticChoice(uni)).n_gram[0];
            } else if (trigramMatches.size() == 0 && bigramMatches.size() != 0) {
                System.out.println("Using the Bigram model");
                word = bigramMatches.get(probabilisticChoice(bigramMatches)).n_gram[1];
            }
        }

        return word;
    }

    // Returns an ArrayList of tri, for which words_im2 and word_im1 are the last words in the input sentence

    private static ArrayList<Ngram> findTrigramMatches(String word_im2, String word_im1) {
        ArrayList<Ngram> trigramMatches = new ArrayList<>();

        for (Ngram trigram : tri) {
            if (trigram.n_gram[0].equals(word_im2) && trigram.n_gram[1].equals(word_im1)) {
                trigramMatches.add(trigram);
            }
        }

        return trigramMatches;
    }
    // Returns an ArrayList of bi, for which word_im1 is the last word in the input sentence

    private static ArrayList<Ngram> findBigramMatches(String word_im1) {
        ArrayList<Ngram> bigramMatches = new ArrayList<>();

        for (Ngram bigram : bi) {
            if (bigram.n_gram[0].equals(word_im1)) {
                bigramMatches.add(bigram);
            }
        }

        return bigramMatches;
    }

    private static void setNgram(int flavour) {
        if (flavour == 1) {
            //Vanilla
            uni = unigrams;
            bi = bigrams;
            tri = trigrams;
        } else if (flavour == 2) {
            //LaPlace
            uni = unigramsLP;
            bi = bigramsLP;
            tri = trigramsLP;
        } else if (flavour == 3) {
            //UNK
            uni = unigramsUNK;
            bi = bigramsUNK;
            tri = trigramsUNK;
        }
    }


    private static void loadModel() {

        training = JSONCreator.loadStringFile("JSON/training.json");
        trainingUNK = JSONCreator.loadStringFile("JSON/trainingUNK.json");
        test = JSONCreator.loadStringFile("JSON/test.json");

        unigrams = JSONCreator.loadNgramFile("JSON/vanilla/unigram.json", true);
        bigrams = JSONCreator.loadNgramFile("JSON/vanilla/bigram.json", false);
        trigrams = JSONCreator.loadNgramFile("JSON/vanilla/trigram.json", false);

        unigramsLP = JSONCreator.loadNgramFile("JSON/laplace/unigramLP.json", true);
        bigramsLP = JSONCreator.loadNgramFile("JSON/laplace/bigramLP.json", false);
        trigramsLP = JSONCreator.loadNgramFile("JSON/laplace/trigramLP.json", false);

        unigramsUNK = JSONCreator.loadNgramFile("JSON/unk/unigramUNK.json", true);
        bigramsUNK = JSONCreator.loadNgramFile("JSON/unk/bigramUNK.json", false);
        trigramsUNK = JSONCreator.loadNgramFile("JSON/unk/trigramUNK.json", false);

        linearInterpolation = JSONCreator.loadNgramFile("JSON/linearInterpolation.json", false);
        System.out.println("Successfully Loaded Language Model");
    }


}
