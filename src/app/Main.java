package app;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length == 1)
        {
            Application app = new Application();
            Settings.modelPath = args[0];
            app.run(Mode.LOAD);
        }
        else if (args.length == 6)
        {
            Settings.classesPath = args[0];
            Settings.trainingSetPath = args[1];
            Settings.testingSetPath = args[2];
            Settings.modelPath = args[5];

            Application app = new Application();
            app.run(Mode.CREATE);
        }
    }
}
