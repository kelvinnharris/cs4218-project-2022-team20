package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.parser.PasteArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_TAB;

public class PasteApplication implements PasteInterface {

    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    List<List<String>> listResult;
    List<List<String>> tempListResult;
    int numOfErrors = 0;
    private int maxFileLength = Integer.MIN_VALUE;

    private boolean fileNotExist = false;
    String fileNotExistName = "";

    private int currentOperation = 0;
    private static final int STDIN_OP = 1;
    private static final int FILE_OP = 2;
    private static final int STDIN_FILE_OP = 3;

    private static final String STRING_PASTE = "paste: ";

    public PasteApplication() {
        this.listResult = new ArrayList<>();
        this.tempListResult = new ArrayList<>();
    }

    /**
     * Runs the paste application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws PasteException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (stdout == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        PasteArgsParser pasteArgs = new PasteArgsParser();

        try {
            pasteArgs.parse(args);
        } catch (Exception e) {
            String errorMessage = e.toString();
            String sBuilder = "invalid option -- '" +
                    errorMessage.charAt(errorMessage.length() - 1) +
                    "'";
            throw new PasteException(sBuilder); // NOPMD
        }

        String result;
        try {
            if (pasteArgs.getFiles().isEmpty()) {
                currentOperation = STDIN_OP;
                result = mergeStdin(pasteArgs.isSerial(), stdin);
            } else if (!pasteArgs.getFiles().contains("-")) { // NOPMD
                currentOperation = FILE_OP;
                result = mergeFile(pasteArgs.isSerial(), pasteArgs.getFiles().toArray(new String[0]));
            } else {
                currentOperation = STDIN_FILE_OP;
                result = mergeFileAndStdin(pasteArgs.isSerial(), stdin, pasteArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            // Will never happen
            throw new PasteException(ERR_GENERAL); // NOPMD
        }

        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new PasteException(ERR_WRITE_STREAM); // NOPMD
        }
    }

    public String mergeStdin(Boolean isSerial, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }

        List<String> data = IOUtils.getLinesFromInputStream(stdin);
        maxFileLength = Math.max(maxFileLength, data.size());
        tempListResult.add(data);

        // produce result in listResult
        if (isSerial) {
            mergeFileDataInSerial(tempListResult);
        } else {
            mergeFileDataInParallel(tempListResult);
        }

        return stringifyListResult(listResult);
    }

    public String mergeFile(Boolean isSerial, String... fileName) throws Exception { // NOPMD
        if (fileName == null) {
            throw new Exception(ERR_GENERAL);
        }

        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                fileNotExist = true;
                fileNotExistName = file;
                break;
            }
            if (node.isDirectory()) {
                String error = STRING_PASTE + file + ERR_IS_DIRECTORY + STRING_NEWLINE;
                List<String> errList = new ArrayList<>();
                errList.add(error);
                tempListResult.add(errList);
                continue;
            }
            if (!node.canRead()) {
                List<String> errList = new ArrayList<>();
                errList.add(STRING_PASTE + ERR_NO_PERM + STRING_NEWLINE);
                tempListResult.add(errList);
                continue;
            }

            InputStream input = IOUtils.openInputStream(file); // NOPMD
            List<String> fileDatas = IOUtils.getLinesFromInputStream(input);
            IOUtils.closeInputStream(input);
            maxFileLength = Math.max(maxFileLength, fileDatas.size());
            tempListResult.add(fileDatas);
        }

        if (currentOperation != FILE_OP) {
            return "";
        }

        // produce result in listResult
        if (isSerial) {
            mergeFileDataInSerial(tempListResult);
        } else {
            mergeFileDataInParallel(tempListResult);
        }

        // If there is a file that doesn't exist, output file doesn't exist error
        if (fileNotExist) {
            return STRING_PASTE + fileNotExistName + ": " + ERR_NOT_FOUND;
        }

        return stringifyListResult(listResult);
    }

    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileName) throws Exception { // NOPMD
        if (stdin == null && fileName == null) {
            throw new Exception(ERR_GENERAL);
        }

        List<String> stdInData = IOUtils.getLinesFromInputStream(stdin);

        int numOfStdin = 0;
        for (String s : fileName) {
            if (s != null && s.equals("-")) {
                numOfStdin++;
            }
        }

        // If serial, the stdIn will all come out in the first "-"
        if (isSerial) {
            numOfStdin = 1;
        }

        int currStdin = 0;
        for (String s : fileName) {
            if (s != null && s.equals("-")) {
                List<String> currLst = new ArrayList<>();
                for (int i = currStdin; i < stdInData.size(); i += numOfStdin) {
                    currLst.add(stdInData.get(i));
                }
                if (isSerial) {
                    currStdin = stdInData.size();
                } else {
                    currStdin += 1;
                }
                tempListResult.add(currLst);
            } else {
                mergeFile(isSerial, s);
                if (fileNotExist) {
                    break;
                }
            }
        }

        if (fileNotExist) {
            return STRING_PASTE + fileNotExistName + ": " + ERR_NOT_FOUND;
        }

        if (isSerial) {
            mergeFileDataInSerial(tempListResult);
        } else {
            mergeFileDataInParallel(tempListResult);
        }

        return stringifyListResult(listResult);
    }

    // Produce the correct output to listResult
    public void mergeFileDataInSerial(List<List<String>> tempListResult) {
        for (List<String> lst : tempListResult) {
            List<String> currLst = new ArrayList<>();
            currLst.addAll(lst);
            this.listResult.add(currLst);
        }
    }

    // Produce the correct output to listResult
    public void mergeFileDataInParallel(List<List<String>> tempListResult) {
        for (int i = 0; i < maxFileLength; ++i) {
            List<String> currLstToAdd = new ArrayList<>();
            for (List<String> currLst : tempListResult) {
                if (i < currLst.size()) {
                    currLstToAdd.add(currLst.get(i));
                } else {
                    currLstToAdd.add("");
                }
            }
            this.listResult.add(currLstToAdd);
        }
    }

    public String stringifyListResult(List<List<String>> listResult) {
        // each list in ListResult contains all the elements for a single line of the output, therefore simply
        // append with tab (STRING_TAB)
        List<String> interRes = new ArrayList<>();
        for (List<String> lst : listResult) {
            interRes.add(String.join(STRING_TAB, lst));
        }

        return String.join(STRING_NEWLINE, interRes);
    }

    public void setCurrentOperation(int operation) {
        this.currentOperation = operation;
    }

    public int getFileOperation() {
        return this.FILE_OP;
    }
}