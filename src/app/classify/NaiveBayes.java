package app.classify;

import app.Document;
import app.LibraryMethods;
import app.Logger;
import app.Settings;
import app.parametrize.IParametrizer;
import java.util.Arrays;

public class NaiveBayes implements IClassifier
{
    /** classification classes */
    private String[] classes;

    /** probabilities of classes - number of class occurences / number of all class occurencies (meaning potentially higher than document number) */
    private double[] classProbabilities;

    /** probabilities of words given classes */
    private double[][] wordClassProbabilities;

    @Override
    public String classify(double[] vector)
    {
        double[] scores = new double[classes.length];

        for (int i = 0; i < classes.length; i++)
        {
            scores[i] = Math.log(classProbabilities[i]);

            for (int j = 0; j < vector.length; j++)
            {
                scores[i] += vector[j] * Math.log(wordClassProbabilities[i][j]);
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
     * Calcultes probabilities of words occurring in a class
     * @param documentVectors parametrized documents
     * @param trainDocuments non-parametrized documents with class information
     */
    private void calculateWordClassProbabilities(double[][] documentVectors, Document[] trainDocuments)
    {
        // We will count the numbers of tokens in all docs in a class
        int[][] classTokenTotalCounts = new int[classes.length][documentVectors[0].length];

        wordClassProbabilities = new double[classes.length][documentVectors[0].length];

        // for each class
        for (int i = 0; i < classTokenTotalCounts.length; i++)
        {
            // for each document
            for (int j = 0; j < trainDocuments.length; j++)
            {
                // If the document has this class
                if (trainDocuments[j].classes.contains(classes[i]))
                {
                    // We add the counts to our totals
                    for (int k = 0; k < documentVectors[0].length; k++)
                    {
                        classTokenTotalCounts[i][k] += documentVectors[j][k];
                    }
                }
            }
        }

        // A helper array containing total word counts for each class so that we don't have to count it each time
        double[] classesCountsTotal = new double[classes.length];

        for (int i = 0; i < classesCountsTotal.length; i++)
        {
            for (int j = 0; j < documentVectors[0].length; j++)
            {
                 classesCountsTotal[i] += documentVectors[i][j];
            }
        }

        for (int i = 0; i < classes.length; i++)
        {
            for (int j = 0; j < documentVectors[0].length; j++)
            {
                // again the +1 for smoothing
                wordClassProbabilities[i][j] = (classTokenTotalCounts[i][j] + 1) / (classesCountsTotal[i] + documentVectors[0].length);
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
        // Number of classes specified within documents (pot. higher than number of documents)
        int[] classOccurences = new int[classes.length];
        double[] classProbabilities = new double[classes.length];
        int total = 0;

        for (Document d : docs)
        {
            for (String clss : d.classes)
            {
                int index = Arrays.binarySearch(classes, clss);

                if (index >= 0)
                {
                    classOccurences[index] += 1;
                    total++;
                }
            }
        }

        for (int i = 0; i < classOccurences.length; i++)
        {
            classProbabilities[i] = (double)classOccurences[i] / total;
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
}
