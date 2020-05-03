package app.classify;

import app.Document;
import app.LibraryMethods;
import app.Logger;
import app.Settings;
import app.parametrize.IParametrizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NaiveBayes implements IClassifier
{
    /** classification classes */
    private String[] classes;

    /** probabilities of classes - number of class occurrences / number of all class occurrences (meaning potentially higher than document number) */
    private double[] classProbabilities;

    /** probabilities of words given classes */
    private double[][] wordClassProbabilities;

    public static String identifier = "nb";

    /**
     * Calculates probabilities for each class
     * Uses log probability to avoid underflow
     * @param vector vector
     * @return class
     */
    @Override
    public String classify(double[] vector)
    {
        double[] scores = new double[classes.length];

        for (int i = 0; i < classes.length; i++)
        {
            scores[i] = Math.log(classProbabilities[i]);

            for (int j = 0; j < vector.length; j++)
            {
                try {
                    scores[i] += vector[j] * Math.log(wordClassProbabilities[i][j]);
                }
                catch (IndexOutOfBoundsException e) {
                    int a = 0;
                }
            }
        }

        int maxIndex = -1;
        double maxScore = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < scores.length; i++)
        {
            if (scores[i] > maxScore)
            {
                maxScore = scores[i];
                maxIndex = i;
            }
        }

        return classes[maxIndex];
    }

    @Override
    public void train(IParametrizer parametrizer)
    {
        Logger.info("Loading classes");
        loadClasses();
        Logger.info("Loading documents");
        Document[] trainDocuments = LibraryMethods.loadDocuments(Settings.trainingSetPath);
        classProbabilities = getClassProbabilities(trainDocuments);
        double[][] documentVectors = new double[trainDocuments.length][parametrizer.getVectorLength()];

        Logger.info("Parametrizing documents");
        for (int i = 0; i < documentVectors.length; i++)
        {
            documentVectors[i] = parametrizer.parametrize(trainDocuments[i]);
        }

        Logger.info("Calculating word-class probabilities");
        calculateWordClassProbabilities(documentVectors, trainDocuments);
    }

    /**
     * Calculates probabilities of words occurring in a class
     * @param documentVectors parametrized documents
     * @param trainDocuments non-parametrized documents with class information
     */
    private void calculateWordClassProbabilities(double[][] documentVectors, Document[] trainDocuments)
    {
        ArrayList<double[]>[] documentsSeparatedByClass = new ArrayList[classes.length];
        for (int i = 0; i < classes.length; i++)
            documentsSeparatedByClass[i] = new ArrayList<>();

        // Separate documents by class
        for (int i = 0; i < classes.length; i++)
        {
            for (int j = 0; j < trainDocuments.length; j++)
            {
                if (trainDocuments[j].classes.contains(classes[i]))
                {
                    documentsSeparatedByClass[i].add(documentVectors[j]);
                }
            }
        }

        // For each class, calculate total counts of each token across all documents belonging to that class
        double[][] classTokenTotalCounts = new double[classes.length][documentVectors[0].length];
        for (int i = 0; i < classes.length; i++)
        {
            for (int j = 0; j < documentsSeparatedByClass[i].size(); j++)
            {
                for (int k = 0; k < documentVectors[0].length; k++)
                {
                    double[] vector = documentsSeparatedByClass[i].get(j);
                    classTokenTotalCounts[i][k] += vector[k];
                }
            }
        }

        // For each class, count totals of all vector parts
        double[] classTotals = new double[classes.length];
        for (int i = 0; i < classes.length; i++)
        {
            for (int j = 0; j < documentVectors[0].length; j++)
            {
                classTotals[i] += classTokenTotalCounts[i][j];
            }
        }

        // Calculate probabilities of vector parts for each class
        wordClassProbabilities = new double[classes.length][documentVectors[0].length];
        for (int i = 0; i < classes.length; i++)
        {
            for (int j = 0; j < documentVectors[0].length; j++)
            {
                wordClassProbabilities[i][j] = (classTokenTotalCounts[i][j] + 1) / (classTotals[i] + documentVectors[0].length);
            }
        }
    }

    /**
     * Calculates class probabilities
     * @param docs training documents
     * @return class probabilities
     */
    private double[] getClassProbabilities(Document[] docs)
    {
        // Number of classes specified within documents (pot. higher than number of documents - if a document has more than one class)
        int[] classOccurrences = new int[classes.length];
        double[] classProbabilities = new double[classes.length];
        int total = 0;

        for (Document d : docs)
        {
            for (String clss : d.classes)
            {
                int index = Arrays.binarySearch(classes, clss);

                if (index >= 0)
                {
                    classOccurrences[index] += 1;
                    total++;
                }
            }
        }

        for (int i = 0; i < classOccurrences.length; i++)
        {
            classProbabilities[i] = (double)classOccurrences[i] / total;
        }

        return classProbabilities;
    }

    /**
     * Loads classification classes
     */
    public void loadClasses()
    {
        classes = LibraryMethods.loadClassesSorted();
    }

    /**
     * Exports the classifier as a list of lines
     * @return lines to write into file
     */
    @Override
    public List<String> export()
    {
        List<String> lines = new ArrayList<>();
        lines.add("**_classifier:" + identifier);

        lines.add("**_classes:");
        StringBuilder cs = new StringBuilder();
        for (String c : classes)
            cs.append(c).append(";");

        lines.add(cs.toString());


        lines.add("**_class_probabilities:");
        cs = new StringBuilder();
        for (double p : classProbabilities)
            cs.append(p).append(";");
        lines.add(cs.toString());

        lines.add("**_token_probabilities");
        for (double[] p : wordClassProbabilities)
        {
            cs = new StringBuilder();
            for (int i = 0; i < p.length; i++)
            {
                cs.append(p[i]).append(";");
            }
            lines.add(cs.toString());
        }

        return lines;
    }

    @Override
    public void load(List<String> lines)
    {
        // Parse classes
        String s = lines.get(2);
        classes = s.split(";");

        // Parse class probabilities
        s = lines.get(4);
        String[] temp = s.split(";");
        classProbabilities = new double[classes.length];
        for (int i = 0; i < temp.length; i++)
        {
            classProbabilities[i] = Double.parseDouble(temp[i]);
        }

        // Parse word-class probabilities
        wordClassProbabilities = new double[classes.length][];
        for (int i = 6; i < lines.size(); i++)
        {
            s = lines.get(i);
            String[] ps = s.split(";");
            wordClassProbabilities[i - 6] = new double[ps.length];
            for (int j = 0; j < ps.length; j++)
            {
                wordClassProbabilities[i - 6][j] = Double.parseDouble(ps[j]);
            }
        }
    }
}

