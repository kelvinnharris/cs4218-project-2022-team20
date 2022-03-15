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

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ISTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_TAB;

public class PasteApplication implements PasteInterface {

    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    List<List<String>> tempListResult;
    private int maxFileLength = Integer.MIN_VALUE;

    private boolean fileNotExist = false;
    String fileNotExistName = "";

    private static final String STRING_PASTE = "paste: ";

    public PasteApplication() {
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
        if (stdin == null) {
            throw new PasteException(ERR_NO_ISTREAM);
        }
        if (stdout == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        PasteArgsParser pasteArgs = new PasteArgsParser();

        try {
            pasteArgs.parse(args);
        } catch (Exception e) {
            throw new PasteException(STRING_PASTE + e.getMessage()); //NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as this is the only Exception
        }

        String result;
        try {
            if (pasteArgs.getFiles().isEmpty()) {
                result = mergeStdin(pasteArgs.isSerial(), stdin);
            } else if (!pasteArgs.getFiles().contains("-")) { //NOPMD - suppressed ConfusingTernary - This was the initial implementation and changing might cause regression
                result = mergeFile(pasteArgs.isSerial(), pasteArgs.getFiles().toArray(new String[0]));
            } else {
                result = mergeFileAndStdin(pasteArgs.isSerial(), stdin, pasteArgs.getFiles().toArray(new String[0]));
            }
        } catch (PasteException e) {
            throw e;
        } catch (Exception e) {
            // Will never happen
            throw new PasteException(ERR_GENERAL); //NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as this is the only Exception
        }

        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new PasteException(ERR_WRITE_STREAM); // NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as reason is contained in message
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
        List<List<String>> listResult;
        if (isSerial) {
            listResult = mergeFileDataInSerial(tempListResult);
        } else {
            listResult = mergeFileDataInParallel(tempListResult);
        }

        return stringifyListResult(listResult);
    }

    public String mergeFile(Boolean isSerial, String... fileName) throws Exception { //NOPMD - suppressed ExcessiveMethodLength - keep to preserve readability of method
        if (fileName == null) {
            throw new PasteException(ERR_GENERAL);
        }

        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                fileNotExist = true;
                fileNotExistName = file;
                throw new PasteException("paste: " + file + ERR_NOT_FOUND);
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

            InputStream input = IOUtils.openInputStream(file); //NOPMD - suppressed CloseResource - Resource has been closed at line 146
            List<String> fileDatas = IOUtils.getLinesFromInputStream(input);
            IOUtils.closeInputStream(input);
            maxFileLength = Math.max(maxFileLength, fileDatas.size());
            tempListResult.add(fileDatas);
        }

        // produce result in listResult
        List<List<String>> listResult;
        if (isSerial) {
            listResult = mergeFileDataInSerial(tempListResult);
        } else {
            listResult = mergeFileDataInParallel(tempListResult);
        }

        return stringifyListResult(listResult);
    }

    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileName) throws Exception { //NOPMD - suppressed ExcessiveMethodLength - keep to preserve readability of method
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
        maxFileLength = Math.max(maxFileLength, (int) (stdInData.size() / numOfStdin));

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

        List<List<String>> listResult;
        if (isSerial) {
            listResult = mergeFileDataInSerial(tempListResult);
        } else {
            listResult = mergeFileDataInParallel(tempListResult);
        }

        return stringifyListResult(listResult);
    }

    // Produce the correct output to listResult
    public List<List<String>> mergeFileDataInSerial(List<List<String>> tempListResult) {
        List<List<String>> listResult = new ArrayList<>();
        for (List<String> lst : tempListResult) {
            List<String> currLst = new ArrayList<>(lst);
            listResult.add(currLst);
        }
        return listResult;
    }

    // Produce the correct output to listResult
    public List<List<String>> mergeFileDataInParallel(List<List<String>> tempListResult) {
        List<List<String>> listResult = new ArrayList<>();
        for (int i = 0; i < maxFileLength; ++i) {
            List<String> currLstToAdd = new ArrayList<>();
            for (List<String> currLst : tempListResult) {
                if (i < currLst.size()) {
                    currLstToAdd.add(currLst.get(i));
                } else {
                    currLstToAdd.add("");
                }
            }
            listResult.add(currLstToAdd);
        }
        return listResult;
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
}
