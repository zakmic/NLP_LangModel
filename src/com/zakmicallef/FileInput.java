package com.zakmicallef;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.zakmicallef.Model.training;
import static com.zakmicallef.Model.test;

public class FileInput {

    static void listFiles(String path) {
        File folder = new File(path);
        int i = 0;
        File[] files = folder.listFiles();

        try {
            for (File file : files) {
                if (file.isFile()) {
                    if (new Random().nextDouble() < 0.75) {
                        training.addAll(parseFile(file));
                    } else {
                        test.addAll(parseFile(file));
                    }

                } else if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath());
                }
            }
        } catch (IOException io) {
            io.getMessage();
            io.printStackTrace();
        }

        try {
            writeToFile(training, "SampleModelOutput/training.txt");
            writeToFile(test, "SampleModelOutput/test.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static ArrayList<String> parseFile(File file) throws IOException {

        System.out.println(file.getAbsolutePath());
        ArrayList<String> allWords = new ArrayList<>();

        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document = null;
        try {
            document = builder.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        }

        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList nList = document.getElementsByTagName("s");


        for (int temp = 1; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                for (int j = 0; j < ((Element) node).getElementsByTagName("w").getLength(); j++) {
                    allWords.add(eElement.getElementsByTagName("w").item(j).getTextContent().toLowerCase());
                }
            }
            allWords.add("EOS");
        }

        allWords.add("EOF");
        allWords.add("EOF");
        allWords.add("EOF");

        return allWords;
    }


    static void writeToFile(ArrayList<String> allWords, String path) throws IOException {
        File OutFile = new File(path);

        if (!OutFile.exists()) {
            OutFile.createNewFile();
        }

        FileWriter fw = new FileWriter(OutFile);
        BufferedWriter bw = new BufferedWriter(fw);

        for (String word : allWords) {
            bw.write(word);
            bw.write("\n");
        }


        System.out.println("Total " + allWords.size());

        try {
            bw.close();
        } catch (Exception ex) {
            System.out.println("Error in closing the BufferedWriter" + ex);
        }

    }

    static void writeStringToFile(String str, String path) throws IOException {
        File OutFile = new File(path);

        if (!OutFile.exists()) {
            OutFile.createNewFile();
        }

        FileWriter fw = new FileWriter(OutFile);
        BufferedWriter bw = new BufferedWriter(fw);


        bw.write(str);


        try {
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println("Error in closing the BufferedWriter" + ex);
        }

    }

    public static void writeNgramsToFile(ArrayList<Ngram> ngrams, String path) throws IOException {
        System.out.println("Printing to: " + path + "\t" + java.time.LocalTime.now());

        File OutFile = new File(path);

        if (!OutFile.exists()) {
            OutFile.createNewFile();
        }

        FileWriter fw = new FileWriter(OutFile);
        BufferedWriter bw = new BufferedWriter(fw);

        for (Ngram ngram : ngrams) {
            bw.write(Arrays.toString(ngram.n_gram)
                    + "\t" + ngram.count + "\t" + ngram.probability + "\n");
        }

        System.out.println("Total: " + ngrams.size());

        try {
            bw.close();
        } catch (Exception ex) {
            System.out.println("Error in closing the BufferedWriter" + ex);
        }

    }
}
