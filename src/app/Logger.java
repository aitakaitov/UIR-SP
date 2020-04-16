package app;

public class Logger
{
    public static void error(String message)
    {
        System.out.println("[ERROR] " + message);
    }

    public static void info(String message)
    {
        System.out.println("[INFO] " + message);
    }
}
