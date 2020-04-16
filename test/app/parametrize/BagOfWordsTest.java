package app.parametrize;

import app.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BagOfWordsTest {

    BagOfWords bow;


    @BeforeEach
    void setUp()
    {
        bow = new BagOfWords();
        Settings.trainingSetPath = "testdata";
    }

    @Test
    void parametrize()
    {
        
    }

    @Test
    void initialize()
    {
        bow.initialize();
    }
}