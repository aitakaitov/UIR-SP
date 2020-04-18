package app.classify;

import app.Model;
import app.Settings;
import app.parametrize.BagOfWordsTF;
import app.parametrize.BagOfWordsTFIDF;
import org.junit.jupiter.api.Test;

class NaiveBayesTest {

    @Test
    void classify() {
    }

    @Test
    void train() {
        Settings.trainingSetPath = "data/Train";
        Settings.classesPath = "data/classes.txt";
        Settings.testingSetPath = "data/Test";

        Model m = new Model(new BagOfWordsTF(), new NaiveBayes());
        m.train();
        m.test();
    }

    @Test
    void loadClasses() {
    }
}