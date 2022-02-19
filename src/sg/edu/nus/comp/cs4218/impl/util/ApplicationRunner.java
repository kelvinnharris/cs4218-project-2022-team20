package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.*;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;


public class ApplicationRunner {
    public final static String APP_CAT = "cat";
    public final static String APP_CP = "cp";
    public final static String APP_CUT = "cut";
    public final static String APP_ECHO = "echo";
    public final static String APP_EXIT = "exit";
    public final static String APP_GREP = "grep";
    public final static String APP_LS = "ls";
    public final static String APP_RM = "rm";
    public final static String APP_SORT = "sort";
    public final static String APP_TEE = "tee";
    public final static String APP_WC = "wc";

    // EF2 Not implemented
    public final static String APP_PASTE = "paste";
    public final static String APP_CD = "cd";

    /**
     * Run the application as specified by the application command keyword and arguments.
     *
     * @param app          String containing the keyword that specifies what application to run.
     * @param argsArray    String array containing the arguments to pass to the applications for
     *                     running.
     * @param inputStream  InputStream for the application to get input from, if needed.
     * @param outputStream OutputStream for the application to write its output to.
     * @throws AbstractApplicationException If an exception happens while running an application.
     * @throws ShellException               If an unsupported or invalid application command is
     *                                      detected.
     */
    public void runApp(String app, String[] argsArray, InputStream inputStream,
                       OutputStream outputStream)
            throws AbstractApplicationException, ShellException {
        Application application;

        switch (app) {
            case APP_CAT:
                application = new CatApplication();
                System.out.println("[CAT] CALLED");
                break;
            case APP_CP:
                application = new CpApplication();
                System.out.println("[CP] CALLED");
                break;
            case APP_CUT:
                application = new CutApplication();
                System.out.println("[CUT] CALLED");
                break;
            case APP_ECHO:
                application = new EchoApplication();
                System.out.println("[ECHO] CALLED");
                break;
            case APP_EXIT:
                application = new ExitApplication();
                System.out.println("[EXIT] CALLED");
                break;
            case APP_GREP:
                application = new GrepApplication();
                System.out.println("[GREP] CALLED");
                break;
            case APP_LS:
                application = new LsApplication();
                System.out.println("[LS] CALLED");
                break;
            case APP_RM:
                application = new RmApplication();
                System.out.println("[RM] CALLED");
                break;
            case APP_SORT:
                application = new SortApplication();
                System.out.println("[SORT] CALLED");
                break;
            case APP_TEE:
                application = new TeeApplication();
                System.out.println("[TEE] CALLED");
                break;
            case APP_WC:
                application = new WcApplication();
                System.out.println("[WC] CALLED");
                break;
            case APP_PASTE:
                application = new PasteApplication();
                System.out.println("[PASTE] CALLED");
                break;
            default:
                System.out.println("[ERROR]");
                throw new ShellException(app + ": " + ERR_INVALID_APP);
        }

        application.run(argsArray, inputStream, outputStream);
    }
}
