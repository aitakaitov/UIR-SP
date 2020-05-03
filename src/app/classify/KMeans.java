package app.classify;

import app.parametrize.IParametrizer;

import java.util.List;

/**
 * K-means clustering
 * Training data is clustered into K classes (number of classes loaded from the classes file)
 * Then
 */
public class KMeans implements IClassifier
{
    @Override
    public String classify(double[] vector)
    {
        return null;
    }

    @Override
    public void train(IParametrizer parametrizer)
    {
        return;
    }

    @Override
    public List<String> export()
    {
        return null;
    }

    @Override
    public void load(List<String> lines)
    {
        return;
    }
}
