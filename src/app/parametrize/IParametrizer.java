package app.parametrize;

import app.Document;

public interface IParametrizer
{
    int getVectorLength();

    double[] parametrize(Document d);

    void initialize();
}
