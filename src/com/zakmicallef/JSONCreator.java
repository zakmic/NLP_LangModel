package com.zakmicallef;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class JSONCreator {

    @SuppressWarnings("unchecked")

    public static void printJSON(ArrayList<Ngram> unigrams, ArrayList<Ngram> bigrams, ArrayList<Ngram> trigrams, String unigramPath, String bigramPath, String trigramPath) {
        JSONArray unigramsArrJson = new JSONArray();
        JSONArray bigramsArrJson = new JSONArray();
        JSONArray trigramsArrJson = new JSONArray();

        JSONObject unigramsJSON = new JSONObject();
        JSONObject bigramsJSON = new JSONObject();
        JSONObject trigramsJSON = new JSONObject();

        for (Ngram ngram : unigrams) {
            JSONObject unigramJSON = new JSONObject();
            JSONObject unigramJSONDetails = new JSONObject();

            unigramJSONDetails.put("words", ngram.n_gram[0]);
            unigramJSONDetails.put("count", ngram.count);
            unigramJSONDetails.put("probability", ngram.probability);

            unigramJSON.put("ngram", unigramJSONDetails);
            unigramsArrJson.add(unigramJSON);
        }

        for (Ngram ngram : bigrams) {
            JSONObject bigramJSONDetails = new JSONObject();
            JSONObject bigramJSON = new JSONObject();


            JSONArray words = new JSONArray();
            words.add(ngram.n_gram[0]);
            words.add(ngram.n_gram[1]);

            bigramJSONDetails.put("words", words);
            bigramJSONDetails.put("count", ngram.count);
            bigramJSONDetails.put("probability", ngram.probability);

            bigramJSON.put("ngram", bigramJSONDetails);
            bigramsArrJson.add(bigramJSON);
        }

        for (Ngram ngram : trigrams) {
            JSONObject trigramJSONDetails = new JSONObject();
            JSONObject trigramJSON = new JSONObject();


            JSONArray words = new JSONArray();
            words.add(ngram.n_gram[0]);
            words.add(ngram.n_gram[1]);
            words.add(ngram.n_gram[2]);


            trigramJSONDetails.put("words", words);
            trigramJSONDetails.put("count", ngram.count);
            trigramJSONDetails.put("probability", ngram.probability);

            trigramJSON.put("ngram", trigramJSONDetails);
            trigramsArrJson.add(trigramJSON);
        }


        try {
            FileInput.writeStringToFile(unigramsArrJson.toJSONString(), unigramPath);
            FileInput.writeStringToFile(bigramsArrJson.toJSONString(), bigramPath);
            FileInput.writeStringToFile(trigramsArrJson.toJSONString(), trigramPath);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @SuppressWarnings("unchecked")
    public static void printSingleJSON(ArrayList<Ngram> trigrams, String path) {

        JSONArray trigramsArrJson = new JSONArray();

        for (Ngram ngram : trigrams) {
            JSONObject trigramJSONDetails = new JSONObject();
            JSONObject trigramJSON = new JSONObject();


            JSONArray words = new JSONArray();
            words.add(ngram.n_gram[0]);
            words.add(ngram.n_gram[1]);
            words.add(ngram.n_gram[2]);


            trigramJSONDetails.put("words", words);
            trigramJSONDetails.put("count", ngram.count);
            trigramJSONDetails.put("probability", ngram.probability);

            trigramJSON.put("ngram", trigramJSONDetails);
            trigramsArrJson.add(trigramJSON);
        }

        try {
            FileInput.writeStringToFile(trigramsArrJson.toJSONString(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public static void printSingleJSONString(ArrayList<String> words, String path) {
        JSONArray allWords = new JSONArray();

        allWords.addAll(words);

        try {
            FileInput.writeStringToFile(allWords.toJSONString(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> loadStringFile(String path) {
        ArrayList<String> strs = new ArrayList<>();

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(path)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray allWords = (JSONArray) obj;

            for (Object wordJSON : allWords) {
                strs.add(wordJSON.toString());
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return strs;
    }

    public static ArrayList<Ngram> loadNgramFile(String path, boolean singleValue) {
        ArrayList<Ngram> ngramList = new ArrayList<>();

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(path)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray ngramListJson = (JSONArray) obj;

            if (singleValue) {
                for (Object object : ngramListJson) {
                    ngramList.add(parseUnigram((JSONObject) object));
                }
            } else {
                for (Object object : ngramListJson) {
                    ngramList.add(parseNgram((JSONObject) object));
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return ngramList;
    }

    private static Ngram parseNgram(JSONObject ngramJson) {
        JSONObject ngramObject = (JSONObject) ngramJson.get("ngram");


        Ngram ngram = new Ngram();
        ngram.count = Math.toIntExact((long) ngramObject.get("count"));
        ngram.probability = (double) ngramObject.get("probability");

        JSONArray words = (JSONArray) ngramObject.get("words");
        String[] strs = new String[words.size()];

        int i = 0;
        for (Object wordJSON : words) {
            strs[i] = wordJSON.toString();
            i++;
        }
        ngram.n_gram = strs;

        return ngram;
    }

    private static Ngram parseUnigram(JSONObject ngramJson) {
        JSONObject ngramObject = (JSONObject) ngramJson.get("ngram");

        Ngram ngram = new Ngram(1);
        ngram.count = Math.toIntExact((long) ngramObject.get("count"));
        ngram.probability = (double) ngramObject.get("probability");
        ngram.n_gram[0] = (String) ngramObject.get("words");

        return ngram;
    }
}

