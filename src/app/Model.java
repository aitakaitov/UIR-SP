package app;

import app.classify.IClassifier;
import app.classify.NaiveBayes;
import app.parametrize.BagOfWordsTFIDF;
import app.parametrize.IParametrizer;

import java.util.Arrays;

public class Model
{
    /** Model's parametrizer */
    public IParametrizer parametrizer;
    /** Model's classifier */
    public IClassifier classifier;

    public Model() {}

    public Model(IParametrizer parametrizer, IClassifier classifier)
    {
        this.parametrizer = parametrizer;
        this.classifier = classifier;
    }

    public void classifyText(String text) {}

    public void loadModel() {}

    /**
     * Returns a Model with parametrizer and classifier according to Settings arguments
     * If arguments are invalid, exits the program
     * @return model
     */
    public static Model getModelForSettings()
    {
        IParametrizer parametrizer = null;
        IClassifier classifier = null;

        if (Settings.parametrizerArgument.equals("bagofwords"))
        {
            parametrizer = new BagOfWordsTFIDF();
        }
        else
            {
                System.out.println("Invalid parametrizer argument, program will now exit.");
                System.exit(1);
            }

        if (Settings.classifierArgument.equals("nbayes"))
        {
            classifier = new NaiveBayes();
        }
        else
            {
                System.out.println("Invalid classifier argument, program will now exit.");
            }

        return new Model(parametrizer, classifier);
    }

    /**
     *  Exports the model
     */
    public void saveModel()
    {
    }

    /**
     * Tests the model and reports accuracy
     */
    public void test()
    {
        Document[] testDocuments = LibraryMethods.loadDocuments(Settings.testingSetPath);
        String[] results = new String[testDocuments.length];
        double[][] vectors = new double[testDocuments.length][];

        for (int i = 0; i < testDocuments.length; i++)
        {
            vectors[i] = parametrizer.parametrize(testDocuments[i]);
            results[i] = classifier.classify(parametrizer.parametrize(testDocuments[i]));
        }

        for (int i = 0; i < vectors.length; i++)
        {
            double[] vector1 = vectors[i];
            for (int j = 0; j < vectors.length; j++)
            {
                double[] vector2 = vectors[j];

                if (i == j)
                    continue;

                boolean diff = false;

                for (int k = 0; k < vectors[0].length; k++)
                {
                    if (vector1[k] != vector2[k])
                    {
                        diff = true;
                    }
                }

                if (!diff)
                    System.out.println(i + ", " + j + "not different");
            }
        }

        int rightCount = 0;

        for (int i = 0; i < testDocuments.length; i++)
        {
            if (testDocuments[i].classes.contains(results[i]))
            {
                System.out.print(" -ok,");
                rightCount++;
            }
            else
                System.out.print(" -ng,");
        }

        System.out.println();

        double err = 1 - (double)rightCount / testDocuments.length;
        System.out.println(Arrays.toString(results));
        System.out.println(err);
    }

    /**
     * Trains the model
     */
    public void train()
    {
        // Makes parametrizer ready to parametrize
        parametrizer.initialize();
        classifier.train(parametrizer);
    }
}
