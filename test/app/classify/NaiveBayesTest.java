package app.classify;

import app.Model;
import app.Settings;
import app.parametrize.NGrams;
import org.junit.jupiter.api.Test;

class NaiveBayesTest {

    @Test
    void classify()
    {

    }

    @Test
    void train()
    {
        Settings.trainingSetPath = "data/Train";
        Settings.classesPath = "data/classes.txt";
        Settings.testingSetPath = "data/Train";
        Settings.modelPath = "export.mod";

        Model m = new Model(new NGrams(true, 1), new NaiveBayes());
        m.train();
        m.test();
    }

    @Test
    void train2()
    {
        Settings.trainingSetPath = "data/Train";
        Settings.classesPath = "data/classes.txt";
        Settings.testingSetPath = "data/Test";
        Settings.modelPath = "export.mod";

        Model m = new Model(new NGrams(true, 1), new KNearestNeighbours(10));
        m.train();
        m.test();
        m.saveModel();
        m = Model.loadModel();
        m.test();
    }
}