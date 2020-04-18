package app;

/**
 * Contains application settings and parameters
 */
public class Settings
{
    /** Points to classification classes file */
    public static String classesPath;

    /** Points to model file */
    public static String modelPath;

    /** Points to training set file or directory */
    public static String trainingSetPath;

    /** Points to testing set file or directory */
    public static String testingSetPath;

    /** CL argument defining the parametrizer */
    public static String parametrizerArgument;

    /** CL argument defining the classifier */
    public static String classifierArgument;

    /** Application mode - CREATE or RUN */
    public static Mode mode;
}
