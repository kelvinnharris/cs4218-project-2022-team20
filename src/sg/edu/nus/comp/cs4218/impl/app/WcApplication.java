package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.args.WcArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class WcApplication implements WcInterface {

    static final String NUMBER_FORMAT = " %7d";
    private static final int LINES_INDEX = 0;
    private static final int WORDS_INDEX = 1;
    private static final int BYTES_INDEX = 2;

    private List<Result> listResult = new ArrayList<>();

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
        WcArguments wcArgs = new WcArguments();
        wcArgs.parse(args);
        String result;
        try {
            if (wcArgs.getFiles().isEmpty()) {
                System.out.println("Reading from stdin");
                result = countFromStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin);
            } else if (!wcArgs.getFiles().contains("-")){
                System.out.println("Reading from files");
                result = countFromFiles(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), wcArgs.getFiles().toArray(new String[0]));
            } else {
                System.out.println("Reading from files && stdin");
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
            throw new Exception(ERR_GENERAL);
        }
        List<String> result = new ArrayList<>();
        List<Result> listRes = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        for (String file : fileName) {
            Result res = new Result();
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                String error = "wc: " + ERR_FILE_NOT_FOUND;
                res.setIsErroneous(error);
                listRes.add(res);
                result.add(error);
                continue;
            }
            if (node.isDirectory()) {
                String error = "wc: " + ERR_IS_DIR;
                res.setIsErroneous(error);
                listRes.add(res);
                result.add(error);
                continue;
            }
            if (!node.canRead()) {
                String error = "wc: " + ERR_NO_PERM;
                res.setIsErroneous(error);
                listRes.add(res);
                result.add(error);
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
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
            StringBuilder sb = new StringBuilder(); //NOPMD
            if (isLines) {
                sb.append(String.format(NUMBER_FORMAT, totalLines));
            }
            if (isWords) {
                sb.append(String.format(NUMBER_FORMAT, totalWords));
            }
            if (isBytes) {
                sb.append(String.format(NUMBER_FORMAT, totalBytes));
            }
            sb.append(" total");
            result.add(sb.toString());
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
            throw new Exception(ERR_NULL_STREAMS);
        }
        long[] count = getCountReport(stdin); // lines words bytes;

        Result res = new Result(); //NOPMD
        StringBuilder sb = new StringBuilder();
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

        listResult.add(res);
        return sb.toString();
    }

    @Override
    public String countFromFileAndStdin(Boolean isBytes, Boolean isLines, Boolean isWords, InputStream stdin, String... fileName) throws Exception {
        // TODO: To implement
        if (stdin == null && fileName == null) {
            throw new Exception(ERR_GENERAL);
        }

        for (String s : fileName) {
            if (s.equals("-")) {
                String res = countFromStdin(isBytes, isLines, isWords, stdin);
            } else {
                String res = countFromFiles(isBytes, isLines, isWords, new String[]{s});
            }
        }

        List<String> result = new ArrayList<>();

        long totalBytes = 0, totalLines = 0, totalWords = 0;

        for (Result res : listResult) {
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

        StringBuilder sb = new StringBuilder();
        if (isLines) {
            sb.append(String.format(NUMBER_FORMAT, totalLines));
        }
        if (isWords) {
            sb.append(String.format(NUMBER_FORMAT, totalWords));
        }
        if (isBytes) {
            sb.append(String.format(NUMBER_FORMAT, totalBytes));
        }
        sb.append(" total");
        result.add(sb.toString());

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
            throw new Exception(ERR_NULL_STREAMS);
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
}

class Result {
    long bytes = -1;
    long lines = -1;
    long words = -1;

    String fileName;

    boolean isErroneous = false;
    String errorMessage;

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public void setLines(long lines) {
        this.lines = lines;
    }

    public void setWords(long words) {
        this.words = words;
    }

    public void setIsErroneous(String message) {
        this.errorMessage = message;
        this.isErroneous = true;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        if (isErroneous) {
            return errorMessage;
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
