package app.parametrize;

import app.Document;
import app.LibraryMethods;
import app.Logger;
import app.Settings;
import java.io.File;
import java.util.*;

/**
 * Implements the Bag of Words algorithm with TF-IDF
 * @author Vojtěch Bartička
 * @version 1.0
 */
public class BagOfWordsTF implements IParametrizer
{
    /** Words from the training set sorted */
    private String[] words;

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

        for (Map.Entry<String, Integer> e : wordCounts.entrySet())
        {
            int index = Arrays.binarySearch(words, e.getKey());
            if (index >= 0)
            {
                vector[index] = e.getValue();
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

            Logger.info("Processing file " + trainFile.getName());

            // Parse the file
            Document d = LibraryMethods.parseDocument(trainFile);

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

        wordsDictionary = cleanDictionary(wordsDictionary);
        disassembleTreeMap(wordsDictionary);
    }

    /**
     * Cleans the dictionary from low-occurence strings and number strings
     * @param map
     */
    private TreeMap<String, Integer> cleanDictionary(TreeMap<String, Integer> map)
    {
        TreeMap<String, Integer> newMap = new TreeMap<>();

        for (Map.Entry<String, Integer> e : map.entrySet())
        {
            if (e.getValue() <= 2)
            {
                continue;
            }

            try
            {
                Integer.parseInt(e.getKey());
            }
            catch (NumberFormatException exc)
            {
                newMap.put(e.getKey(), e.getValue());
            }
        }

        return newMap;
    }

    /**
     * Disassembles HashMap dictionary and saves it into counts[] and words[]
     * @param map
     */
    private void disassembleTreeMap(TreeMap<String, Integer> map)
    {
        Logger.info("Creating bag of words dictionary");
        ArrayList<String> ws = new ArrayList<>();

        map.forEach((k, v) ->
        {
            ws.add(k);
        });

        words = new String[ws.size()];
        ws.toArray(words);
    }

    public int getVectorLength()
    {
        return this.words.length;
    }
}
