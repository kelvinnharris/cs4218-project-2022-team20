package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.TeeInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.parser.TeeArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.APPEND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ISTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class TeeApplication implements TeeInterface {
    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args   The arguments representing the files/folders
     * @param stdin  The input stream
     * @param stdout The output stream
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (stdin == null) {
            throw new TeeException(ERR_NO_ISTREAM);
        }
        if (stdout == null) {
            throw new TeeException(ERR_NO_OSTREAM);
        }

        TeeArgsParser parser = new TeeArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new TeeException(e);
        }

        Boolean isAppend = parser.isAppend();
        String[] files = parser.getFiles();

        String result = teeFromStdin(isAppend, stdin, files);
        try {
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    /**
     * Reads from standard input and write to both the standard output and files.
     *
     * @param isAppend Boolean option to append the standard input to the contents of the input files
     * @param stdin    Input stream containing arguments from Stdin
     * @param files    Array of String of file names
     * @return A string to be written to output stream
     * @throws TeeException Exception related to tee
     */
    @Override
    public String teeFromStdin(Boolean isAppend, InputStream stdin, String... files) throws TeeException { //NOPMD
        StringBuilder errorMsgBuilder = new StringBuilder();
        StringBuilder resultBuilder = new StringBuilder();
        ArrayList<String> writableFiles = filterWritableFiles(errorMsgBuilder, files);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));

        while (true) {
            try {
                String input;

                try {
                    input = reader.readLine();
                } catch (IOException e) {
                    throw new TeeException("Streams are closed"); // Streams are closed, terminate proces // NOPMD
                }

                if (StringUtils.isBlank(input)) {
                    break;
                }

                String toAppend = input + STRING_NEWLINE;
                resultBuilder.append(toAppend);

                // write each line to files immediately to preserve order
                if (isAppend) {
                    for (String file : writableFiles) {
                        Path filePath = IOUtils.resolveFilePath(file);
                        try {
                            Files.write(filePath, toAppend.getBytes(), APPEND);
                        } catch (Exception e) {
                            throw new TeeException(String.format("Cannot write to file '%s'.", filePath)); //NOPMD
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (!isAppend) {
            for (String file : writableFiles) {
                Path filePath = IOUtils.resolveFilePath(file);
                try {
                    Files.write(filePath, resultBuilder.toString().getBytes());
                } catch (Exception e) {
                    throw new TeeException(String.format("Cannot write to file '%s'.", filePath)); //NOPMD
                }
            }
        }

        return errorMsgBuilder.toString() + resultBuilder;
    }

    public ArrayList<String> filterWritableFiles(StringBuilder stringBuilder, String... files) throws TeeException {
        // check all files are regular files and exist
        ArrayList<String> writableFiles = new ArrayList<>();
        for (String file : files) {
            Path filePath = IOUtils.resolveFilePath(file);
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                    writableFiles.add(file);
                } catch (IOException ioe) {
                    throw new TeeException(ioe);
                }
            } else if (Files.isDirectory(filePath)) {
                stringBuilder.append(String.format("%s: Is a directory" + STRING_NEWLINE, file));
            } else {
                writableFiles.add(file);
            }
        }
        return writableFiles;
    }
}
