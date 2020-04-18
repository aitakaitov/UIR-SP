package app.parametrize;

import app.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BagOfWordsTFIDFTest {

    BagOfWordsTFIDF bow;

    @BeforeEach
    void setUp()
    {
        bow = new BagOfWordsTFIDF();
        Settings.trainingSetPath = "data/Train";
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