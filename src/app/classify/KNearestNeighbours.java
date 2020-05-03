package app.classify;

import app.Document;
import app.LibraryMethods;
import app.Logger;
import app.Settings;
import app.parametrize.IParametrizer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of supervised K-NN algorithm
 */
public class KNearestNeighbours implements IClassifier
{
    /** All training vectors */
    private double[][] vectors;

    /** Class indexes for each vector */
    private int[][] vectorClasses;

    /** Class labels */
    private String[] classes;

    /** K */
    private int K;

    public static String identifier = "knn";

    public KNearestNeighbours() {}

    public KNearestNeighbours(int k)
    {
        K = k;
    }

    @Override
    public String classify(double[] vector)
    {
        HashMap<Integer, Double> distances = new HashMap<>();

        for (int i = 0; i < vectors.length; i++)
        {
            distances.put(i, cosineSimilarity(vector, vectors[i]));
        }

        Map<Integer, Double> sorted = distances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

        int i = 0;
        int[] classOccurrences = new int[classes.length];
        for (Map.Entry<Integer, Double> e : sorted.entrySet())
        {
            if (i == K) break;
            for (int j = 0; j < vectorClasses[e.getKey()].length; j++)
            {
                classOccurrences[vectorClasses[e.getKey()][j]] += 1 / (e.getValue() + 1);
            }
        }

        double maxValue = -1;
        int maxIndex = -1;
        for (i = 0; i < classOccurrences.length; i++)
        {
            if (maxValue < classOccurrences[i])
            {
                maxValue = classOccurrences[i];
                maxIndex = i;
            }
        }

        return classes[maxIndex];

        /*
        // Calculate all distances
        double[] distances = new double[vectors.length];
        for (int i = 0; i < vectors.length; i++)
        {
            double[] otherVector = vectors[i];
            distances[i] = cosineSimilarity(vector, otherVector);
        }

        // Get indexes of K smallest distances
        int[] smallestDistanceIndexes = new int[K];
        for (int i = 0; i < K; i++)
        {
            double minValue = Double.POSITIVE_INFINITY;
            int minIndex = -1;
            for (int j = 0; j < distances.length; j++)
            {
                if (minValue > distances[j])
                {
                    minValue = distances[j];
                    minIndex = j;
                }
            }
            smallestDistanceIndexes[i] = minIndex;
            try {
                distances[minIndex] = Double.POSITIVE_INFINITY;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                int a = 0;
            }
        }

        // Count all occurrences for each class
        int[] classesCounts = new int[classes.length];
        for (int i = 0; i < smallestDistanceIndexes.length; i++)
        {
            for (int j = 0; j < vectorClasses[i].length; j++)
            {
                classesCounts[vectorClasses[i][j]] += 1 / distances[i] + 1;
            }
        }

        // Select the maximum
        int maxIndex = 0;
        int maxCount = 0;
        for (int i = 0; i < classesCounts.length; i++)
        {
            if (maxCount < classesCounts[i])
            {
                maxCount = classesCounts[i];
                maxIndex = i;
            }
        }

        return classes[maxIndex];*/
    }

    /**
     * Exports the classifier as a list of lines
     * @return lines to export
     */
    @Override
    public List<String> export()
    {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        lines.add("**_classifier:knn");

        lines.add("**_classes:");
        for (String c : classes)
            sb.append(c).append(";");
        lines.add(sb.toString());

        lines.add("**_k:" + K);

        lines.add("**_vectors:");
        for (double[] vector : vectors)
        {
            sb = new StringBuilder();
            for (int i = 0; i < vector.length; i++)
            {
                sb.append(Double.toString(vector[i])).append(";");
            }
            lines.add(sb.toString());
        }

        lines.add("**_vector_classes:");
        for (int[] classes : vectorClasses)
        {
            sb = new StringBuilder();
            if (classes.length == 0)
                System.out.println("empty");
            for (int i = 0; i < classes.length; i++)
            {
                sb.append(classes[i]).append(";");
            }
            lines.add(sb.toString());
        }

        return lines;
    }

    /**
     * Loads and initializes the classifier
     * @param lines
     */
    @Override
    public void load(List<String> lines)
    {
        // get classes
        classes = lines.get(2).split(";");

        // get K
        K = Integer.parseInt(lines.get(3).split(":")[1]);

        // On line 5 vectors start
        int i = 5;
        String s = lines.get(i);
        ArrayList<ArrayList<Double>> vecs = new ArrayList<>();
        while (!s.equals("**_vector_classes:"))
        {
            // Split vectors
            String[] vals = s.split(";");
            vecs.add(new ArrayList<>());
            for (int j = 0; j < vals.length; j++)
            {
                vecs.get(i - 5).add(Double.parseDouble(vals[j]));
            }
            i++;
            s = lines.get(i);
        }

        // Convert the arraylists into arrays
        vectors = new double[vecs.size()][vecs.get(0).size()];
        for (int j = 0; j < vecs.size(); j++)
            for (int k = 0; k < vecs.get(0).size(); k++)
                vectors[j][k] = vecs.get(j).get(k);

        i++;
        int j = 0;
        vectorClasses = new int[vectors.length][];
        while (i < lines.size())
        {
            s = lines.get(i);
            String[] vals = s.split(";");
            vectorClasses[j] = new int[vals.length];
            for (int k = 0; k < vals.length; k++)
                vectorClasses[j][k] = Integer.parseInt(vals[k]);

            j++;
            i++;
        }
    }

    /**
     * Calculates N-dimensional euclidean distance between two vectors
     * @param vector vector
     * @param otherVector other vector
     * @return euclidean distance
     */
    private double getDistanceNDimensions(double[] vector, double[] otherVector)
    {
        double sum = 0;
        for (int i = 0; i < vector.length; i++)
        {
            sum += (vector[i] - otherVector[i]) * (vector[i] - otherVector[i]);
        }
        return Math.sqrt(sum);
    }

    private double getHammingDistance(double[] vector, double[] otherVector)
    {
        double sum = 0;
        for (int i = 0; i < vector.length; i++)
        {
            sum += Math.abs(vector[i] - otherVector[i]);
        }
        return sum;
    }

    /**
     *
     * @param vector
     * @param otherVector
     * @return
     */
    private double cosineSimilarity(double[] vector, double[] otherVector) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vector.length; i++) {
            dotProduct += vector[i] * otherVector[i];
            normA += Math.pow(vector[i], 2);
            normB += Math.pow(otherVector[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public void train(IParametrizer parametrizer)
    {
        Logger.info("Loading classes");
        classes = LibraryMethods.loadClassesSorted();
        Logger.info("Loading documents");
        Document[] trainDocuments = LibraryMethods.loadDocuments(Settings.trainingSetPath);
        Logger.info("Parametrizing documents");

        vectors = new double[trainDocuments.length][];
        vectorClasses = new int[trainDocuments.length][];
        for (int i = 0; i < vectors.length; i++)
        {
            vectors[i] = parametrizer.parametrize(trainDocuments[i]);
            List<String> documentClasses = trainDocuments[i].classes;
            List<Integer> validDocumentClasses = new ArrayList<>();

            for (String clss : documentClasses)
            {
                int index = Arrays.binarySearch(classes, clss);
                if (index >= 0)
                    validDocumentClasses.add(index);
            }

            vectorClasses[i] = new int[validDocumentClasses.size()];
            for (int j = 0; j < validDocumentClasses.size(); j++)
                vectorClasses[i][j] = validDocumentClasses.get(j);
        }
    }
}
