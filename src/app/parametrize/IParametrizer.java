package app.parametrize;

import app.Document;

public interface IParametrizer
{
    double[] parametrize(Document d);

    void initialize();
}
