package sg.edu.nus.comp.cs4218;

public final class Environment { //NOPMD - suppressed ClassNamingConventions - Default Environment Class Name from Base Code

    /**
     * Java VM does not support changing the current working directory.
     * For this reason, we use Environment.currentDirectory instead.
     */
    public static volatile String currentDirectory = System.getProperty("user.dir");


    private Environment() {
    }

}
