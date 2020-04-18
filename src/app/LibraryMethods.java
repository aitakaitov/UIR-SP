package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class LibraryMethods
{
    /** Regex used to split documents into words */
    private static final String REGEX_SPLIT = "[^\\p{L}0-9]+";

    /**
     * Loads and returns classes from classes file
     * expects classes to be in one line separated with single spaces
     * Classes are sorted
     * @return sorted classes
     */
    public static String[] loadClassesSorted()
    {
        File classesFile = new File(Settings.classesPath);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(classesFile));
            String[] classes = br.readLine().split("\\s+");
            Arrays.sort(classes);
            return classes;
        }
        catch (IOException e)
        {
            Logger.error("Error reading classes file, program will now exit.");
            System.out.println("Error reading classes file, program will now exit.");
            System.exit(1);
        }

        return null;
    }

    /**
     * Parses document
     * @param docFile text file with document
     * @return parse document
     */
    public static Document parseDocument(File docFile)
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
            d.classes = Arrays.asList(splitText(classes));
        }
        catch (IOException e)
        {
            Logger.error("Error parsing file " + docFile.getName() + ", program will exit.");
            System.exit(1);
        }

        return d;
    }

    /**
     * Splits document text into words and converts them into lowercase
     * @param text
     * @return lowercase words
     */
    public static String[] splitText(String text)
    {
        String[] words = text.split(REGEX_SPLIT);

        for (int i = 0; i < words.length; i++)
        {
            words[i] = words[i].toLowerCase();
        }

        return words;
    }

    /**
     * Loads and parses all documents in a directory
     * @param dirPath path to directory
     * @return Documents
     */
    public static Document[] loadDocuments(String dirPath)
    {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        Document[] docs = new Document[files.length];

        for (int i = 0; i < files.length; i++)
        {
            docs[i] = parseDocument(files[i]);
        }

        return docs;
    }
}
