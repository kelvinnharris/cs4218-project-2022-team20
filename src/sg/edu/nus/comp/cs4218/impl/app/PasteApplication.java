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

    private static final String ERR_IS_A_DIRECTORY = ": Is a directory";
    private static final String ERR_NO_SUCH_FILE_OR_DIRECTORY = ": No such file or directory";

    private List<List<String>> listResult;
    private List<List<String>> tempListResult;
    private int numOfErrors = 0;
    private int maxFileLength = Integer.MIN_VALUE;

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

//        System.out.println("Arguments: ");
//        for (String arg : args) {
//            System.out.println(arg);
//        }

        try {
            pasteArgs.parse(args);
        } catch (Exception e) {
            String errorMessage = e.toString();
            StringBuilder sb = new StringBuilder();
            sb.append("invalid option -- '");
            sb.append(errorMessage.charAt(errorMessage.length()-1));
            sb.append("'");
            throw new PasteException(sb.toString());
        }

        String result;
        try {
            if (pasteArgs.getFiles().isEmpty()) {
                result = mergeStdin(pasteArgs.isSerial(), stdin);
            } else if (!pasteArgs.getFiles().contains("-")) {
                result = mergeFile(pasteArgs.isSerial(), pasteArgs.getFiles().toArray(new String[0]));
            } else {
                result = mergeFileAndStdin(pasteArgs.isSerial(), stdin, pasteArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            // Will never happen
            throw new PasteException(ERR_GENERAL);
        }

        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new PasteException(ERR_WRITE_STREAM);
        }
    }

    public String mergeStdin(Boolean isSerial, InputStream stdin) throws Exception {
        return null;
    }

    public String mergeFile(Boolean isSerial, String... fileName) throws Exception {
        if (fileName == null) {
            throw new Exception(ERR_GENERAL);
        }

        boolean fileNotExist = false;
        String fileNotExistName = "";

        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                fileNotExist = true;
                fileNotExistName = file;
                break;
            }
            if (node.isDirectory()) {
                String error = (new StringBuilder()).append("paste: ").append(file).append(ERR_IS_A_DIRECTORY).append(STRING_NEWLINE).toString();
                List<String> errList = new ArrayList<>();
                errList.add(error);
                tempListResult.add(errList);
                continue;
            }
            if (!node.canRead()) {
                List<String> errList = new ArrayList<>();
                errList.add("paste: " + ERR_NO_PERM + STRING_NEWLINE);
                tempListResult.add(errList);
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            List<String> fileDatas = IOUtils.getLinesFromInputStream(input);
            IOUtils.closeInputStream(input);
            maxFileLength = Math.max(maxFileLength, fileDatas.size());
//            System.out.println("File datas size: " + fileDatas.size());
            tempListResult.add(fileDatas);
//            System.out.println("tempListResult size: " + tempListResult.size());
        }

        // produce result in listResult
        if (isSerial) {
            mergeFileDataInSerial(tempListResult);
        } else {
            mergeFileDataInParallel(tempListResult);
        }

        if (fileNotExist) {
            StringBuilder sb = new StringBuilder();
            sb.append("paste: ").append(fileNotExistName).append(": ").append(ERR_NO_SUCH_FILE_OR_DIRECTORY);
            return sb.toString();
        }

        // each list in ListResult conatins all the elements for a single line of the output, therefore simply
        // append with tab (STRING_TAB)
        List<String> intermediateResult = new ArrayList<>();
        for (List<String> lst : listResult) {
            intermediateResult.add(String.join(STRING_TAB, lst));
        }

        return String.join(STRING_NEWLINE, intermediateResult);
    }

    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileName) throws Exception {
        return null;
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

//        for (List<String> lst : listResult) {
//            for (String s : lst) {
//                System.out.print(s + " ");
//            }
//            System.out.println();
//        }
    }
}
