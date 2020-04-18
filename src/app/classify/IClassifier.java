package app.classify;

import app.parametrize.IParametrizer;

public interface IClassifier
{
    String classify(double[] vector);

    void train(IParametrizer parametrizer);
}
