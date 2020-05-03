package app.classify;

import app.parametrize.IParametrizer;
import java.util.List;

public interface IClassifier
{
    String classify(double[] vector);

    void train(IParametrizer parametrizer);

    List<String> export();

    void load(List<String> lines);
}
