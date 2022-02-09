package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.app.TeeInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;
import sg.edu.nus.comp.cs4218.impl.parser.TeeArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardOpenOption.APPEND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ISTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class TeeApplication implements TeeInterface {
    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args      The arguments representing the files/folders
     * @param stdin     The input stream
     * @param stdout    The output stream
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
            throw new TeeException(e.getMessage());
        }

        Boolean isAppend = parser.isAppend();
        String[] files = parser.getFiles();

        String result = teeFromStdin(isAppend, stdin, files);
        try {
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new TeeException("Cannot write to output stream");
        }
    }

    /**
     * Reads from standard input and write to both the standard output and files.
     *
     * @param isAppend      Boolean option to append the standard input to the contents of the input files
     * @param stdin         Input stream containing arguments from Stdin
     * @param fileName      Array of String of file names
     * @return              A string to be written to output stream
     * @throws TeeException Exception related to tee
     */
    @Override
    public String teeFromStdin(Boolean isAppend, InputStream stdin, String... fileName) throws TeeException {
        // check all files are regular files and exist
        for (String file : fileName) {
            String cwd = Environment.currentDirectory;
            Path filePath = Paths.get(cwd, file).normalize();
            if (!Files.exists(filePath)) {
                throw new TeeException(String.format("File '%s' does not exist.", filePath));
            }
            if (!Files.isRegularFile(filePath)) {
                throw new TeeException(String.format("File '%s' is not a regular file.", filePath));
            }
        }

        Set<String> uniqueFiles = new HashSet<>(List.of(fileName));

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        StringBuilder sb = new StringBuilder();

        while (true) {
            try {
                String input;

                try {
                    input = reader.readLine();
                } catch (IOException e) {
                    throw new TeeException("Streams are closed"); // Streams are closed, terminate process
                }

                if (StringUtils.isBlank(input)) {
                    break;
                }
                sb.append(input);
                sb.append(STRING_NEWLINE);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        for (String file : uniqueFiles) {
            String cwd = Environment.currentDirectory;
            Path filePath = Paths.get(cwd, file).normalize();
            try {
                if (isAppend) {
                    Files.write(filePath, sb.toString().getBytes(), APPEND);
                } else {
                    Files.write(filePath, sb.toString().getBytes());
                }
            } catch (Exception e) {
                throw new TeeException(String.format("Cannot write to file '%s'.", filePath));
            }
        }
        return sb.toString();
    }
}
