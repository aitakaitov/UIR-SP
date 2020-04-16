package app.parametrize;

import app.Document;
import app.Logger;
import app.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the Bag of Words algorithm with TF-IDF
 * @author Vojtěch Bartička
 * @version 1.0
 */
public class BagOfWords implements IParametrizer
{
    /** Regex used to split documents into words */
    private final String REGEX_SPLIT = "[^\\p{L}0-9]+";

    /** Number of documents - necessary for IDF */
    private int documentCount = 0;

    /** IDF values for words */
    private double[] idf;

    /** Words from the training set sorted */
    private String[] words;

    /** Word document occurrences */
    private int[] counts;

    /**
     * Returns vector representing the document
     * @param d Document
     * @return vector representing the document
     */
    @Override
    public double[] parametrize(Document d)
    {
        double[] vector = new double[words.length];

        // Calculate the word frequencies
        HashMap<String, Integer> wordCounts = new HashMap<>();
        int documentWordCounts = d.documentWords.length;

        for (String word : d.documentWords)
        {
            if (!wordCounts.containsKey(word))
            {
                wordCounts.put(word, 1);
            }
            else
                {
                    wordCounts.put(word, wordCounts.get(word) + 1);
                }
        }

        // Calculate TF-IDF
        for (Map.Entry<String, Integer> e : wordCounts.entrySet())
        {
            int index = Arrays.binarySearch(words, e.getKey());

            if (index >= 0)
            {
                vector[index] = (e.getValue() / documentWordCounts) * idf[index];
            }
        }

        return vector;
    }

    /**
     * Creates a dictionary of words
     * Initializes class attributes
     */
    @Override
    public void initialize()
    {
        TreeMap<String, Integer> wordsDictionary = new TreeMap<>();
        File trainDir = new File(Settings.trainingSetPath);

        Logger.info("Processing training corpus");

        // Go trough all files
        for (File trainFile : trainDir.listFiles())
        {
            if (!trainFile.canRead())
            {
                Logger.error("Could not read file " + trainFile.getName() + ", program will exit.");
                System.exit(1);
            }

            // Parse the file
            Document d = parseDocument(trainFile);

            Set<String> documentOccurences = new HashSet<>();

            // filter out duplicates
            for (String word : d.documentWords)
            {
                documentOccurences.add(word);
            }

            // Add the occurrences
            for (String word : documentOccurences)
            {
                if (!wordsDictionary.containsKey(word))
                {
                    wordsDictionary.put(word, 1);
                }
                else
                    {
                        wordsDictionary.put(word, wordsDictionary.get(word) + 1);
                    }
            }
        }

        disassembleHashMap(wordsDictionary);
        calculateIDF();
    }

    /**
     * Calculates IDF
     */
    private void calculateIDF()
    {
        Logger.info("Calculating IDF values");

        idf = new double[counts.length];

        for (int i = 0; i < words.length; i++)
        {
            idf[i] = 1 + Math.log(documentCount / counts[i]);
        }
    }

    /**
     * Disassembles HashMap dictionary and saves it into counts[] and words[]
     * @param map
     */
    private void disassembleHashMap(TreeMap<String, Integer> map)
    {
        Logger.info("Creating bag of words dictionary");


        ArrayList<String> ws = new ArrayList<>();
        ArrayList<Integer> cs = new ArrayList<>();

        map.forEach((k, v) ->
        {
            ws.add(k);
            cs.add(v);
        });

        words = new String[ws.size()];
        counts = new int[cs.size()];
        ws.toArray(words);

        for (int i = 0; i < counts.length; i++)
        {
            counts[i] = cs.get(i).intValue();
        }
    }


    /**
     * Parses document
     * @param docFile text file with document
     * @return parse document
     */
    private Document parseDocument(File docFile)
    {
        Document d = null;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(docFile));

            String classes = br.readLine();
            br.readLine();
            String text = br.readLine();

            d = new Document();
            d.documentWords = splitText(text);
            d.classes = splitText(classes);
        }
        catch (IOException e)
        {
            Logger.error("Error parsing file " + docFile.getName() + ", program will exit.");
            System.exit(1);
        }

        documentCount++;
        return d;
    }

    /**
     * Splits document text into words and converts them into lowercase
     * @param text
     * @return lowercase words
     */
    private String[] splitText(String text)
    {
        String[] words = text.split(REGEX_SPLIT);

        for (int i = 0; i < words.length; i++)
        {
            words[i] = words[i].toLowerCase();
        }

        return words;
    }
}
