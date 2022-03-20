package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.GrepArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface { //NOPMD - suppressed GodClass - Some of the methods are private to Grep and make more sense to put it inside the class
    public static final String INVALID_PATTERN = "Invalid pattern syntax";
    public static final String EMPTY_PATTERN = "Pattern should not be empty.";
    public static final String IS_DIRECTORY = "Is a directory";
    public static final String NULL_POINTER = "Null Pointer Exception";

    public static final String STDIN_NAME = "(standard input)";

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames) throws Exception {
        // TODO: To implement -H flag print file name with output lines
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }

        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);

        grepResultsFromFiles(pattern, isCaseInsensitive, isPrefixFileName, lineResults, countResults, fileNames);

        String results = "";
        if (isCountLines) {
            results = countResults + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {
                results = lineResults + STRING_NEWLINE;
            }
        }
        return results;
    }

    /**
     * Extract the lines and count number of lines for grep from files and insert them into
     * lineRes and countRes respectively.
     *
     * @param pattern     pattern supplied by user
     * @param isCaseInsen supplied by user
     * @param isPrefix    supplied by user
     * @param lineRes     a StringJoiner of the grep line results
     * @param countRes    a StringJoiner of the grep line count results
     * @param files       a String Array of file names supplied by user
     */
    private void grepResultsFromFiles(String pattern, Boolean isCaseInsen, Boolean isPrefix, // NOPMD - suppressed ExcessiveMethodLength - Part of functional requirements where Grep needs to handle
                                      StringJoiner lineRes, StringJoiner countRes, String... files) throws Exception {
        int count;
        boolean isSingleFile = (files.length == 1);
        Boolean isPrefixCopy = isPrefix;
        if (!isSingleFile) {
            isPrefixCopy = true;
        }

        for (String f : files) {
            // Ignore '-' input file here, to be read from stdin
            if (Objects.equals(f, String.valueOf(CHAR_FLAG_PREFIX))) {
                continue;
            }

            BufferedReader reader = null;
            try {
                Path path = IOUtils.resolveFilePath(f);
                File file = new File(path.toString());
                String formatString = "grep: %s: %s";
                if (!file.exists()) {
                    lineRes.add(String.format(formatString, f, ERR_FILE_NOT_FOUND));
                    countRes.add(String.format(formatString, f, ERR_FILE_NOT_FOUND));
                    continue;
                }
                if (file.isDirectory()) { // ignore if it's a directory
                    lineRes.add(String.format(formatString, f, IS_DIRECTORY));
                    countRes.add(String.format(formatString, f, IS_DIRECTORY));
                    countRes.add(f + ": 0");
                    continue;
                }
                reader = new BufferedReader(new FileReader(path.toString()));
                String line;
                Pattern compiledPattern;
                if (isCaseInsen) {
                    compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                } else {
                    compiledPattern = Pattern.compile(pattern);
                }
                count = 0;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = compiledPattern.matcher(line);
                    if (matcher.find()) { // match
                        if (isPrefixCopy) {
                            lineRes.add(f + ":" + line);
                        } else {
                            lineRes.add(line);
                        }
                        count++;
                    }
                }
                if (isPrefixCopy) {
                    countRes.add(f + ":" + count);
                } else {
                    countRes.add(String.valueOf(count));
                }
                reader.close();
            } catch (PatternSyntaxException pse) {
                throw new GrepException(ERR_INVALID_REGEX); //NOPMD - suppressed PreserveStackTrace - We expect Grep to output custom error message
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin) throws Exception {
        // TODO: To implement -H flag print file name with output lines
        int count = 0;
        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            String line;
            Pattern compiledPattern;
            if (isCaseInsensitive) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledPattern = Pattern.compile(pattern);
            }
            while ((line = reader.readLine()) != null) {
                Matcher matcher = compiledPattern.matcher(line);
                if (matcher.find()) { // match
                    if (isPrefixFileName) {
                        lineResults.add(STDIN_NAME + ":" + line);
                    } else {
                        lineResults.add(line);
                    }
                    count++;
                }
            }
            if (isPrefixFileName) {
                countResults.add(STDIN_NAME + ":" + count);
            } else {
                countResults.add(String.valueOf(count));
            }
            reader.close();
        } catch (PatternSyntaxException pse) {
            throw new GrepException(ERR_INVALID_REGEX); //NOPMD - suppressed PreserveStackTrace - We expect Grep to output custom error message
        } catch (NullPointerException npe) {
            throw new GrepException(ERR_FILE_NOT_FOUND); //NOPMD - suppressed PreserveStackTrace - We expect Grep to output custom error message
        }

        String results = "";
        if (isCountLines) {
            results = countResults + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {
                results = lineResults + STRING_NEWLINE;
            }
        }
        return results;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            GrepArgsParser parser = new GrepArgsParser();
            try {
                parser.parse(args);
            } catch (InvalidArgsException e) {
                throw new GrepException(e);
            }

            String result = "";
            String pattern = parser.getPattern();
            String[] inputFiles = parser.getFileNames();
            Boolean isCaseInsensitive = parser.isCaseInsensitive();
            Boolean isCountOnly = parser.isCountOnly();
            Boolean isPrintFilename = parser.isPrintFilename();

            if (stdin == null && (inputFiles == null || inputFiles.length == 0)) {
                throw new Exception(ERR_NO_INPUT);
            }
            if (pattern == null) {
                throw new Exception(ERR_SYNTAX);
            }

            if (pattern.isEmpty()) {
                throw new Exception(EMPTY_PATTERN);
            } else {
                if (inputFiles == null || inputFiles.length == 0) {
                    result = grepFromStdin(pattern, isCaseInsensitive, isCountOnly, isPrintFilename, stdin);
                } else {

                    Boolean toReadFromStdin = Stream.of(inputFiles).anyMatch(fileName -> Objects.equals(fileName, String.valueOf(CHAR_FLAG_PREFIX)));
                    Boolean toReadFromFiles = Stream.of(inputFiles).anyMatch(fileName -> !Objects.equals(fileName, String.valueOf(CHAR_FLAG_PREFIX)));

                    if (toReadFromFiles && toReadFromStdin) {
                        result = grepFromFileAndStdin(pattern, isCaseInsensitive, isCountOnly, isPrintFilename, stdin, inputFiles);
                    } else if (toReadFromFiles) {
                        result = grepFromFiles(pattern, isCaseInsensitive, isCountOnly, isPrintFilename, inputFiles);
                    } else if (toReadFromStdin) {
                        result = grepFromStdin(pattern, isCaseInsensitive, isCountOnly, isPrintFilename, stdin);
                    }
                }
            }
            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Override
    public String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines,
                                       Boolean isPrefixFileName, InputStream stdin, String... fileNames)
            throws Exception {
        // TODO: To implement
        String resultFromFile = grepFromFiles(pattern, isCaseInsensitive, isCountLines, true, fileNames);
        String resultFromStdin = grepFromStdin(pattern, isCaseInsensitive, isCountLines, true, stdin);
        return resultFromFile.concat(resultFromStdin);
    }
}
