package app;

public class Application
{
    private Model model;

    public void run()
    {
        if (Settings.mode == Mode.CREATE)
        {
            model = Model.getModelForSettings();
            model.train();
            model.test();
            model.saveModel();
        }
        else if (Settings.mode == Mode.LOAD)
        {
            model = new Model();
            model.loadModel();
        }
    }
}
