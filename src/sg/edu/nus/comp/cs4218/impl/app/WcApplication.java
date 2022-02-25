package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.parser.WcArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_COLON;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIRECTORY;

public class WcApplication implements WcInterface {

    static final String NUMBER_FORMAT = " %7d";
    private static final int LINES_INDEX = 0;
    private static final int WORDS_INDEX = 1;
    private static final int BYTES_INDEX = 2;

    private static final String ERR_IS_DIR = "" + CHAR_COLON + CHAR_SPACE + ERR_IS_DIRECTORY;
    private static final String ERR_NOT_FOUND = "" + CHAR_COLON + CHAR_SPACE + ERR_FILE_NOT_FOUND;

    static final Character ERR_CODE_DIR = 'd';

    List<Result> listResult = new ArrayList<>();

    /**
     * Runs the wc application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws WcException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws WcException {
        // Format: wc [-clw] [FILES]
        if (stdout == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }

        WcArgsParser wcArgs = new WcArgsParser();
        try {
            wcArgs.parse(args);
        } catch (Exception e) {
            String errorMessage = e.toString();
            String sBuilder = "invalid option -- '" +
                    errorMessage.charAt(errorMessage.length() - 1) +
                    "'";
            throw new WcException(sBuilder); //NOPMD
        }

        String result;
        try {
            if (wcArgs.getFiles().isEmpty()) {
                result = countFromStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin);
            } else if (!wcArgs.getFiles().contains("-")) { //NOPMD
                result = countFromFiles(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), wcArgs.getFiles().toArray(new String[0]));
            } else {
                result = countFromFileAndStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin, wcArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            // Will never happen
            throw new WcException(ERR_GENERAL); //NOPMD
        }
        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new WcException(ERR_WRITE_STREAM);//NOPMD
        }
    }

    /**
     * Returns string containing the number of lines, words, and bytes in input files
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param fileName Array of String of file names
     * @throws Exception
     */
    @Override
    public String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords, //NOPMD
                                 String... fileName) throws Exception {
        if (fileName == null) {
            throw new WcException(ERR_NULL_FILES);
        }
        List<String> result = new ArrayList<>();
        List<Result> listRes = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        for (String file : fileName) {
            Result res = new Result();
            res.setFileName(file);
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                String error = (new StringBuilder()).append("wc: ").append(file).append(ERR_NOT_FOUND).toString();
                res.setIsErroneous(error, 'n');
                listRes.add(res);
                result.add(error);
                continue;
            }
            if (node.isDirectory()) {
                // wc: folder1/: Is a directory
                String error = (new StringBuilder()).append("wc: ").append(file).append(ERR_IS_DIR).toString();
                res.setIsErroneous(error, ERR_CODE_DIR);
                listRes.add(res);
                result.add(error);
                result.add(getCountReportInString(isBytes, isLines, isWords, new long[]{0, 0, 0}, file));
                continue;
            }
            if (!node.canRead()) {
                String error = "wc: " + ERR_NO_PERM;
                res.setIsErroneous(error, 'n');
                listRes.add(res);
                result.add(error);
                continue;
            }

            InputStream input = IOUtils.openInputStream(file); // NOPMD
            long[] count = getCountReport(input); // lines words bytes
            IOUtils.closeInputStream(input);

            // Update total count
            totalLines += count[0];
            totalWords += count[1];
            totalBytes += count[2];

            // Format all output: " %7d %7d %7d %s"
            // Output in the following order: lines words bytes filename
            StringBuilder sb = new StringBuilder(); //NOPMD
            if (isLines) {
                sb.append(String.format(NUMBER_FORMAT, count[0]));
                res.setLines(count[0]);
            }
            if (isWords) {
                sb.append(String.format(NUMBER_FORMAT, count[1]));
                res.setWords(count[1]);
            }
            if (isBytes) {
                sb.append(String.format(NUMBER_FORMAT, count[2]));
                res.setBytes(count[2]);
            }
            res.setFileName(file);
            listRes.add(res);
            sb.append(String.format(" %s", file));
            result.add(sb.toString());
        }

        // Print cumulative counts for all the files
        if (fileName.length > 1) {
            result.add(getCountReportInString(isBytes, isLines, isWords, new long[]{totalLines, totalWords, totalBytes}, "total"));
        }

        listResult.addAll(listRes);
        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns string containing the number of lines, words, and bytes in standard input
     *
     * @param isBytes Boolean option to count the number of Bytes
     * @param isLines Boolean option to count the number of lines
     * @param isWords Boolean option to count the number of words
     * @param stdin   InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                                 InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new WcException(ErrorConstants.ERR_NULL_STREAMS);
        }
        long[] count = getCountReport(stdin); // lines words bytes;

        Result res = new Result(); //NOPMD
        StringBuilder sBuilder = new StringBuilder();
        if (isLines) {
            sBuilder.append(String.format(NUMBER_FORMAT, count[0]));
            res.setLines(count[0]);
        }
        if (isWords) {
            sBuilder.append(String.format(NUMBER_FORMAT, count[1]));
            res.setWords(count[1]);
        }
        if (isBytes) {
            sBuilder.append(String.format(NUMBER_FORMAT, count[2]));
            res.setBytes(count[2]);
        }

        listResult.add(res);
        return sBuilder.toString();
    }

    @Override
    public String countFromFileAndStdin(Boolean isBytes, Boolean isLines, Boolean isWords, InputStream stdin, String... fileName) throws Exception {
        // TODO: To implement
        // Only when the [Filename] "-" is used then this function will be called
        if (stdin == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        if (fileName == null) {
            throw new WcException(ERR_NULL_FILES);
        }

        for (String s : fileName) {
            if (s != null && s.equals("-")) {
                String res = countFromStdin(isBytes, isLines, isWords, stdin);
                this.listResult.get(listResult.size() - 1).setFileName("-");
            } else {
                String res = countFromFiles(isBytes, isLines, isWords, new String[]{s});
            }
        }

        List<String> result = new ArrayList<>();

        long totalBytes = 0, totalLines = 0, totalWords = 0;

        for (Result res : listResult) {
            if (res.isErroneous()) {
                result.add(res.toString());
                continue;
            }
            if (isBytes) {
                totalBytes += res.bytes;
            }
            if (isLines) {
                totalLines += res.lines;
            }
            if (isWords) {
                totalWords += res.words;
            }
            result.add(res.toString());
        }

        if (fileName.length > 1) {
            result.add(getCountReportInString(isBytes, isLines, isWords, new long[]{totalLines, totalWords, totalBytes}, "total"));
        }

        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns array containing the number of lines, words, and bytes based on data in InputStream.
     *
     * @param input An InputStream
     * @throws IOException
     */
    public long[] getCountReport(InputStream input) throws Exception {
        if (input == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        long[] result = new long[3]; // lines, words, bytes

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int inRead = 0;
        boolean inWord = false;
        while ((inRead = input.read(data, 0, data.length)) != -1) {
            for (int i = 0; i < inRead; ++i) {
                if (Character.isWhitespace(data[i])) {
                    // Use <newline> character here. (Ref: UNIX)
                    if (data[i] == '\n') {
                        ++result[LINES_INDEX];
                    }
                    if (inWord) { // might fail if there are a lot of spaces at the end ??
                        ++result[WORDS_INDEX];
                    }

                    inWord = false;
                } else {
                    inWord = true;
                }
            }
            result[BYTES_INDEX] += inRead;
            buffer.write(data, 0, inRead);
        }
        buffer.flush();
        if (inWord) {
            ++result[WORDS_INDEX]; // To handle last word
        }

        return result;
    }

    public String getCountReportInString(boolean isBytes, boolean isLines, boolean isWords, long[] count, String name) {
        StringBuilder sb = new StringBuilder(); //NOPMD
        if (isLines) {
            sb.append(String.format(NUMBER_FORMAT, count[0]));
        }
        if (isWords) {
            sb.append(String.format(NUMBER_FORMAT, count[1]));
        }
        if (isBytes) {
            sb.append(String.format(NUMBER_FORMAT, count[2]));
        }
        return sb.append(' ').append(name).toString();
    }

    static class Result { // NOPMD
        long bytes = -1;
        long lines = -1;
        long words = -1;

        String fileName;

        boolean erroneous = false;
        String errorMessage;
        Character errorCode;

        public void setBytes(long bytes) {
            this.bytes = bytes;
        }

        public void setLines(long lines) {
            this.lines = lines;
        }

        public void setWords(long words) {
            this.words = words;
        }

        public void setIsErroneous(String message, Character errorCode) {
            this.errorMessage = message;
            this.erroneous = true;
            this.errorCode = errorCode;
            bytes = 0;
            lines = 0;
            words = 0;
        }

        public Character getErrorCode() {
            return this.errorCode;
        }

        public boolean isErroneous() {
            return this.erroneous;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            if (erroneous && errorCode.compareTo(WcApplication.ERR_CODE_DIR) != 0) {
                return errorMessage;
            } else if (erroneous && errorCode.compareTo(WcApplication.ERR_CODE_DIR) == 0) {
                String countReportErr = (new WcApplication()).getCountReportInString(bytes != -1, lines != -1, words != -1, new long[]{0, 0, 0}, fileName);
                return errorMessage + STRING_NEWLINE + countReportErr;
            }

            StringBuilder sb = new StringBuilder(); //NOPMD
            if (lines != -1) {
                sb.append(String.format(WcApplication.NUMBER_FORMAT, lines));
            }
            if (words != -1) {
                sb.append(String.format(WcApplication.NUMBER_FORMAT, words));
            }
            if (bytes != -1) {
                sb.append(String.format(WcApplication.NUMBER_FORMAT, bytes));
            }
            if (fileName != null) {
                sb.append(String.format(" %s", fileName));
            }
            return sb.toString();
        }
    }
}


