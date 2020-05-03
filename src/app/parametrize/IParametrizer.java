package app.parametrize;

import app.Document;

import java.util.List;

public interface IParametrizer
{
    int getVectorLength();

    double[] parametrize(Document d);

    void initialize();

    List<String> export();

    void load(List<String> lines);
}
