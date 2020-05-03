package app.parametrize;

import app.Document;
import app.LibraryMethods;
import app.Logger;
import app.Settings;
import java.io.File;
import java.util.*;

/**
 * Implements the N-Grams algorithm
 * @author Vojtěch Bartička
 * @version 1.0
 */
public class NGrams implements IParametrizer
{
    /** Grams from the training set sorted */
    private String[] grams;

    /** Number of words in a gram */
    private int n;

    /** IDF values for each gram */
    private double[] idf;

    /** Using TFIDF or not */
    private boolean tfidf;

    public static String identifier = "ngrams";

    public NGrams() {}

    public NGrams(boolean tfidf, int n)
    {
        this.n = n;
        this.tfidf = tfidf;
    }

    /**
     * Returns vector representing the document
     * @param d Document
     * @return vector representing the document
     */
    @Override
    public double[] parametrize(Document d)
    {
        double[] vector = new double[grams.length];

        // Calculate the word frequencies
        HashMap<String, Integer> wordCounts = new HashMap<>();

        for (int i = 0; i < d.documentWords.length; i++)
        {
            if (i + n >= d.documentWords.length)
                break;

            String gram = String.join(" ", Arrays.copyOfRange(d.documentWords, i, i + n));
            if (!wordCounts.containsKey(gram))
                wordCounts.put(gram, 1);
            else
                wordCounts.put(gram, wordCounts.get(gram) + 1);
        }

        for (Map.Entry<String, Integer> e : wordCounts.entrySet())
        {
            int index = Arrays.binarySearch(grams, e.getKey());
            if (index >= 0)
            {
                if (tfidf)
                    vector[index] = e.getValue() * idf[index];
                else
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
        TreeMap<String, Integer> gramsDictionary = new TreeMap<>();
        Logger.info("Processing training corpus");
        Document[] documents = LibraryMethods.loadDocuments(Settings.trainingSetPath);

        // Go trough the documents
        for (Document d : documents)
        {
            // Create grams
            for (int i = 0; i < d.documentWords.length; i++)
            {
                if (i + n > d.documentWords.length)
                    break;

                String gram = String.join(" ", Arrays.copyOfRange(d.documentWords, i, i + n));

                if (!gramsDictionary.containsKey(gram))
                    gramsDictionary.put(gram, 1);
                else
                    gramsDictionary.put(gram, gramsDictionary.get(gram) + 1);
            }
        }


        // Do postprocessing
        if (n == 1)
        {
            gramsDictionary = DictionaryCleaners.cleanDictNumbersInStrings(gramsDictionary);
            gramsDictionary = DictionaryCleaners.cleanDictStringOccurrences(gramsDictionary, 5);
        }
        else
            {
                gramsDictionary = DictionaryCleaners.cleanDictStringOccurrences(gramsDictionary, 2);
            }

        gramsDictionary = DictionaryCleaners.removeWhitespaces(gramsDictionary);
        disassembleTreeMap(gramsDictionary);

        // If we need to calculate IDF values
        if (tfidf)
        {
            // We'll just use the IDF field to temporarily save the grams' DF
            idf = new double[grams.length];
            // Go trough all the documents again
            for (Document d : documents)
            {
                // Filter out duplicates
                Set<String> temp = new TreeSet<>();
                for (int i = 0; i < d.documentWords.length; i++)
                {
                    if (i + n > d.documentWords.length)
                        break;
                    temp.add(String.join(" ", Arrays.copyOfRange(d.documentWords, i, i + n)));
                }

                // Add occurrences
                for (String w : temp)
                {
                    int index = Arrays.binarySearch(grams, w);
                    if (index >= 0)
                        idf[index] += 1;
                }
            }

            // calculate the IDF
            for (int i = 0; i < idf.length; i++)
            {
                idf[i] = Math.log(1 + documents.length / idf[i]);
            }
        }
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

        grams = new String[ws.size()];
        ws.toArray(grams);
    }

    /**
     * Returns vector length
     * @return vector length
     */
    public int getVectorLength()
    {
        return this.grams.length;
    }

    /**
     *
     * @return
     */
    @Override
    public List<String> export()
    {
        List<String> lines = new ArrayList<>();
        lines.add("**_parametrizer:" + identifier);
        lines.add("**_tfidf:" + (tfidf ? "true" : "false"));
        lines.add("**_n:" + n);
        lines.add("**_grams:");

        for (String gram : grams)
        {
            lines.add(gram);
        }

        if (tfidf)
        {
            lines.add("**_idf:");
            for (double v : idf)
                lines.add(Double.toString(v));
        }

        return lines;
    }

    /**
     * Parses the lines and loads parametrizer data
     * @param lines
     */
    @Override
    public void load(List<String> lines)
    {
        tfidf = lines.get(1).contains("true");
        String[] temp = lines.get(2).split(":");
        n = Integer.parseInt(temp[1]);

        int i = 4;
        if (tfidf)
        {
            ArrayList<String> gs = new ArrayList<>();
            String s = lines.get(i);
            while (!s.equals("**_idf:"))
            {
                gs.add(s);
                i++;
                s = lines.get(i);
            }
            i++;
            ArrayList<Double> idfs = new ArrayList<>();
            while (i < lines.size())
            {
                s = lines.get(i);
                idfs.add(Double.parseDouble(s));
                i++;
            }

            grams = new String[gs.size()];
            for (int j = 0; j < gs.size(); j++)
                grams[j] = gs.get(j);

            idf = new double[idfs.size()];
            for (int j = 0; j < idfs.size(); j++)
                idf[j] = idfs.get(j);
        }
        else
            {
                ArrayList<String> gs = new ArrayList<>();
                String s = lines.get(i);
                while (true)
                {
                    gs.add(s);
                    i++;

                    if (i >= lines.size()) break;

                    s = lines.get(i);
                }

                grams = new String[gs.size()];
                for (int j = 0; j < gs.size(); j++)
                    grams[j] = gs.get(j);
            }
    }

}
