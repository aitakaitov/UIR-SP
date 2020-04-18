package app;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length == 1)
        {
            Application app = new Application();
            Settings.modelPath = args[0];
            Settings.mode = Mode.LOAD;
            app.run();
        }
        else if (args.length == 6)
        {
            Settings.classesPath = args[0];
            Settings.trainingSetPath = args[1];
            Settings.testingSetPath = args[2];
            Settings.parametrizerArgument = args[3].toLowerCase();
            Settings.parametrizerArgument = args[4].toLowerCase();
            Settings.modelPath = args[5];
            Settings.mode = Mode.CREATE;

            Application app = new Application();
            app.run();
        }
    }
}
