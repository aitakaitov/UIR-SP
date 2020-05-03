package app;

import app.classify.IClassifier;
import app.classify.KNearestNeighbours;
import app.classify.NaiveBayes;
import app.parametrize.IParametrizer;
import app.parametrize.NGrams;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * Parametrizes and classifies the text
     * Returns the estimated class
     * @param text
     * @return class
     */
    public String classifyText(String text)
    {
        return classifier.classify(parametrizer.parametrize(LibraryMethods.parseString(text)));
    }

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
            parametrizer = new NGrams(false, 1);
        }
        else if (Settings.parametrizerArgument.equals("tfidf"))
        {
            parametrizer = new NGrams(true, 1);
        }
        else if (Settings.parametrizerArgument.equals("ngrams"))
        {
            parametrizer = new NGrams(false, 2);
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
        else if (Settings.classifierArgument.equals("knn"))
        {
            classifier = new KNearestNeighbours(5);
        }
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
        Logger.info("Exporting model");
        Logger.info("Exporting parametrizer");
        List<String> paramExport = parametrizer.export();
        Logger.info("Exporting classifier");
        List<String> classifierExport = classifier.export();
        Logger.info("Writing into file");
        try
        {
            File f = new File(Settings.modelPath);
            if (!f.exists())
                f.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            for (String s : paramExport)
            {
                bw.write(s);
                bw.newLine();
            }
            for (String s : classifierExport)
            {
                bw.write(s);
                bw.newLine();
            }

            bw.flush();
            bw.close();
        }
        catch (IOException e)
        {
            Logger.error("Error writing into file, program will exit.");
            System.exit(1);
        }
        Logger.info("Successfully exported the model into file " + Settings.modelPath);
    }

    public static Model loadModel()
    {
        Model m = new Model();
        Logger.info("Loading model");
        List<String> classifierLines = new ArrayList<>();
        List<String> parametrizerLines = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(Settings.modelPath)));
            String s = br.readLine();
            while (!s.contains("**_classifier:"))
            {
                parametrizerLines.add(s);
                s = br.readLine();
            }
            while (s != null)
            {
                classifierLines.add(s);
                s = br.readLine();
            }
        }
        catch (IOException e)
        {
            Logger.error("Error while loading the model, program will exit.");
            System.exit(0);
        }

        IClassifier classifier = null;
        IParametrizer parametrizer = null;

        Logger.info("Loading classifier");
        String cLine = classifierLines.get(0);
        if (cLine.equals("**_classifier:" + NaiveBayes.identifier))
        {
            classifier = new NaiveBayes();
            classifier.load(classifierLines);
        }
        else if (cLine.equals("**_classifier:" + KNearestNeighbours.identifier))
        {
            classifier = new KNearestNeighbours();
            classifier.load(classifierLines);
        }

        Logger.info("Loading parametrizer");
        String pLine = parametrizerLines.get(0);
        if (pLine.equals("**_parametrizer:" + NGrams.identifier))
        {
            parametrizer = new NGrams();
            parametrizer.load(parametrizerLines);
        }

        m.parametrizer = parametrizer;
        m.classifier = classifier;

        Logger.info("Model loaded");

        return m;
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
