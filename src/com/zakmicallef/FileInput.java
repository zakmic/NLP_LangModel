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
import java.util.Random;

import static com.zakmicallef.Main.training;
import static com.zakmicallef.Main.test;

public class FileInput {

    public static ArrayList<Integer> Docs = new ArrayList<>();


    static void listFiles(String path) {
        File folder = new File(path);
        int i = 0;
        File[] files = folder.listFiles();

        try {
            for (File file : files) {
                if (file.isFile()) {
                    if (new Random().nextDouble() < 0.5) {
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
            writeToFile(training, "training.txt");
            writeToFile(test, "test.txt");
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
            allWords.add(".");
        }

        Docs.add(allWords.size());

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


        bw.write("Total " + allWords.size());

        try {
            bw.close();
        } catch (Exception ex) {
            System.out.println("Error in closing the BufferedWriter" + ex);
        }

    }

}
