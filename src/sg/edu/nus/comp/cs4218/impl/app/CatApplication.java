package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.parser.CatArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplication implements CatInterface {
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    private static final String NUMBER_FORMAT = "%6d ";
    private static final String CAT = "cat: ";

    List<String> listResult = new ArrayList<>();
    private int numOfErrors = 0;

    /**
     * Runs the cat application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws CatException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {
        if (stdin == null) {
            throw new CatException(ERR_NO_ISTREAM);
        }
        if (stdout == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }
        CatArgsParser catArgs = new CatArgsParser();

        try {
            catArgs.parse(args);
        } catch (Exception e) {
            throw new CatException(e.getMessage());  //NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as this is the only Exception
        }

        String result;
        try {
            if (catArgs.getFiles().isEmpty()) {
                result = catStdin(catArgs.isFlagNumber(), stdin);
            } else if (catArgs.getFiles().contains("-")) {
                result = catFileAndStdin(catArgs.isFlagNumber(), stdin, catArgs.getFiles().toArray(new String[0]));
            } else {
                result = catFiles(catArgs.isFlagNumber(), catArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            // Will never happen
            throw new CatException(ERR_GENERAL);  //NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as this is the only Exception
        }

        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new CatException(ERR_WRITE_STREAM);  // NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as reason is contained in message
        }
    }

    @Override
    public String catFiles(Boolean isLineNumber, String... fileName) throws Exception {
        if (fileName == null) {
            throw new CatException(ERR_NULL_FILES);
        }

        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if ("".equals(file)) {
                String error = CAT + "''" + ERR_NOT_FOUND;
                listResult.add(error);
                numOfErrors++;
                continue;
            }
            if (!node.exists()) {
                String error = CAT + file + ERR_NOT_FOUND;
                listResult.add(error);
                numOfErrors++;
                continue;
            }
            if (node.isDirectory()) {
                String error = (new StringBuilder()).append("cat: ").append(file).append(ERR_IS_DIRECTORY).toString();
                listResult.add(error);
                numOfErrors++;
                continue;
            }
            if (!node.canRead()) {
                listResult.add("cat: " + ERR_NO_PERM);
                numOfErrors++;
                continue;
            }

            List<String> fileDatas;
            try (InputStream input = IOUtils.openInputStream(file)) {
                fileDatas = IOUtils.getLinesFromInputStream(input);
                IOUtils.closeInputStream(input);
            }

            // Format all output: " %6d "
            if (isLineNumber) {
                appendLineNumberToListString(fileDatas, listResult, listResult.size() + 1 - numOfErrors);
            } else {
                listResult.addAll(fileDatas);
            }
        }

        return String.join(STRING_NEWLINE, listResult);
    }

    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new CatException(ErrorConstants.ERR_NULL_STREAMS);
        }

        List<String> data = IOUtils.getLinesFromInputStream(stdin);

        if (isLineNumber) {
            appendLineNumberToListString(data, listResult, listResult.size() + 1 - numOfErrors);
        } else {
            listResult.addAll(data);
        }

        return String.join(STRING_NEWLINE, listResult);
    }

    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws Exception {
        if (stdin == null) {
            throw new CatException(ErrorConstants.ERR_NULL_STREAMS);
        }
        if (fileName == null) {
            throw new CatException(ERR_NULL_FILES);
        }

        for (String s : fileName) {
            if (s != null && s.equals("-")) {
                String res = catStdin(isLineNumber, stdin);
            } else {
                String res = catFiles(isLineNumber, s);
            }
        }

        return String.join(STRING_NEWLINE, listResult);
    }

    public void appendLineNumberToListString(List<String> data, List<String> result, long lineNumber) {
        long lineNum = lineNumber;
        for (String s : data) {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(String.format(NUMBER_FORMAT, lineNum)).append(s);
            result.add(sBuilder.toString());
            lineNum += 1;
        }
    }
}
