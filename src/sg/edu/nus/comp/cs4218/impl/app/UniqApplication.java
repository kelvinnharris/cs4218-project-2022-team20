package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.UniqInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.parser.UniqArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class UniqApplication implements UniqInterface {
    InputStream stdin;

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        // Format: uniq [-cdD] [INPUT_FILE [OUTPUT_FILE]]
        if (stdout == null) {
            throw new UniqException(ERR_NULL_STREAMS);
        }
        this.stdin = stdin;
        UniqArgsParser parser = new UniqArgsParser();
        try {
            parser.parse(args);
            if (parser.getFiles().size() > 2) {
                throw new UniqException("extra operand '" + parser.getFiles().get(2) + "'");
            }
        } catch (InvalidArgsException e) {
            throw new UniqException(e.getMessage());//NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as reason is contained in message
        }
        StringBuilder output = new StringBuilder();
        try {
            if (parser.getFiles().isEmpty()) {
                output.append(uniqFromStdin(parser.isCount(), parser.isRepeated(), parser.isAllRepeated(), stdin, parser.getOutputFile()));
            } else {
                output.append(uniqFromFile(parser.isCount(), parser.isRepeated(), parser.isAllRepeated(), parser.getInputFile(), parser.getOutputFile()));
            }
        } catch (Exception e) {
            throw new UniqException(e.getMessage());//NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as reason is contained in message
        }
        try {
            if (!output.toString().isEmpty()) {
                if (parser.getOutputFile() == null) {
                    stdout.write(output.toString().getBytes());
                } else {
                    Path outputPath = IOUtils.resolveFilePath(parser.getOutputFile());
                    Files.write(outputPath, output.toString().getBytes());
                }
            }
        } catch (IOException e) {
            throw new UniqException(ERR_WRITE_STREAM);//NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as reason is contained in message
        }
    }

    @Override
    public String uniqFromFile(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String inputFileName, String outputFileName) throws Exception {
        if (inputFileName == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        if (inputFileName.length() == 1 && inputFileName.toCharArray()[0] == '-') {
            List<String> linesFromInput = IOUtils.getLinesFromInputStream(stdin);
            lines.addAll(linesFromInput);

            return uniqInputString(isCount, isRepeated, isAllRepeated, lines, outputFileName);
        }
        File node = IOUtils.resolveFilePath(inputFileName).toFile();
        if ("".equals(inputFileName)) {
            String errorMessage = "'': " + ERR_FILE_NOT_FOUND;
            throw new Exception(errorMessage);
        }
        if (!node.exists()) {
            String errorMessage = "'" + inputFileName + "': " + ERR_FILE_NOT_FOUND;
            throw new Exception(errorMessage);
        }
        if (node.isDirectory()) {
            String errorMessage = "error reading '" + inputFileName + "'";
            throw new Exception(errorMessage);
        }
        if (!node.canRead()) {
            String errorMessage = "'" + inputFileName + "': " + ERR_NO_PERM;
            throw new Exception(errorMessage);
        }
        if ("".equals(outputFileName)) {
            String errorMessage = "'': " + ERR_FILE_NOT_FOUND;
            throw new Exception(errorMessage);
        }

        try (InputStream input = IOUtils.openInputStream(inputFileName)) {
            lines.addAll(IOUtils.getLinesFromInputStream(input));
            IOUtils.closeInputStream(input);
        }

        return uniqInputString(isCount, isRepeated, isAllRepeated, lines, outputFileName);
    }

    @Override
    public String uniqFromStdin(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, InputStream stdin, String outputFileName) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        List<String> lines = IOUtils.getLinesFromInputStream(stdin);

        return uniqInputString(isCount, isRepeated, isAllRepeated, lines, outputFileName);
    }

    public String uniqInputString(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, List<String> input, String outputFileName) throws Exception { //NOPMD - suppressed ExcessiveMethodLength - from interface
        String output = "";
        List<String> lines = new ArrayList<>();
        List<Integer> count = new ArrayList<>();

        int counter = 0;
        String currString = "";
        for (String s : input) {
            if (currString.isEmpty()) {
                currString = s;
                counter = 1;
                continue;
            }

            if (currString.equals(s)) {
                counter += 1;
            } else {
                lines.add(currString);
                count.add(counter);
                currString = s;
                counter = 1;
            }
        }
        lines.add(currString);
        count.add(counter);


        if (isAllRepeated) {
            if (isCount) {
                for (int i = 0; i < lines.size(); i++) {
                    if (count.get(i) > 1) {
                        for (int j = 0; j < count.get(i); j++) {
                            output += count.get(i) + " " + lines.get(i) + STRING_NEWLINE;
                        }
                    }
                }
            } else {
                for (int i = 0; i < lines.size(); i++) {
                    if (count.get(i) > 1) {
                        for (int j = 0; j < count.get(i); j++) {
                            output += lines.get(i) + STRING_NEWLINE;
                        }
                    }
                }
            }
        } else if (isRepeated) {
            if (isCount) {
                for (int i = 0; i < lines.size(); i++) {
                    if (count.get(i) > 1) {
                        output += count.get(i) + " " + lines.get(i) + STRING_NEWLINE;
                    }
                }
            } else {
                for (int i = 0; i < lines.size(); i++) {
                    if (count.get(i) > 1) {
                        output += lines.get(i) + STRING_NEWLINE;
                    }
                }
            }
        } else if (isCount) {
            for (int i = 0; i < lines.size(); i++) {
                output += count.get(i) + " " + lines.get(i) + STRING_NEWLINE;
            }
        } else {
            for (int i = 0; i < lines.size(); i++) {
                output += lines.get(i) + STRING_NEWLINE;
            }
        }

        return output;
    }
}
