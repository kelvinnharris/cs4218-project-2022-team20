package sg.edu.nus.comp.cs4218.impl.app;

import javafx.util.Pair;
import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CutArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplication implements CutInterface { //NOPMD
    InputStream stdin;

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args
     * @param stdin
     * @param stdout
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        // Format: cut [Option] [LIST] FILES...
        if (stdout == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        this.stdin = stdin;
        CutArgsParser parser = new CutArgsParser();
        try {
            parser.parse(args);
            if (parser.isCharPo() && parser.isBytePo()) {
                throw new InvalidArgsException("invalid byte, character or field list");
            }

            if (!parser.isCharPo() && !parser.isBytePo()) {
                throw new InvalidArgsException("you must specify a list of bytes, characters, or fields");
            }
            parser.parseIndex();

        } catch (InvalidArgsException e) {
            throw new CutException(e.getMessage());//NOPMD
        } catch (IndexOutOfBoundsException e) {
            String errorMessage = "option requires an argument -- '";
            if (parser.isCharPo()) {
                errorMessage += "c'";
            } else if (parser.isBytePo()) {
                errorMessage += "b'";
            }
            throw new CutException(errorMessage);//NOPMD
        }

        StringBuilder output = new StringBuilder();
        try {
            if (parser.getFiles().isEmpty()) {
                output.append(cutFromStdin(parser.isCharPo(), parser.isBytePo(), parser.getRanges(), stdin));
            } else {
                output.append(cutFromFiles(parser.isCharPo(), parser.isBytePo(), parser.getRanges(),parser.getFiles().toArray(new String[0])));

            }
        } catch (Exception e) {
            throw new CutException(e.getMessage());//NOPMD
        }
        try {
            if (!output.toString().isEmpty()) {
                stdout.write(output.toString().getBytes());
            }
        } catch (IOException e) {
            throw new CutException(ERR_WRITE_STREAM);//NOPMD
        }
    }


    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges   list of pairs containing the start and end indeces for cut
     * @param fileName Array of String of file names
     * @return
     * @throws Exception
     */
    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, List<Pair<Integer, Integer>> ranges, String... fileName) throws Exception {
        if (fileName == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        for (String file : fileName) {
            if (file.length() == 1 && file.toCharArray()[0] == '-') {
                List<String> linesFromInput = IOUtils.getLinesFromInputStream(stdin);
                lines.addAll(linesFromInput);
                continue;
            }
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                String errorMessage = file + "': " + ERR_FILE_NOT_FOUND;
                throw new Exception(errorMessage);
            }
            if (node.isDirectory()) {
                String errorMessage = file + "': " + ERR_IS_DIR;
                throw new Exception(errorMessage);
            }
            if (!node.canRead()) {
                String errorMessage = file + "': " + ERR_NO_PERM;
                throw new Exception(errorMessage);
            }
            InputStream input = IOUtils.openInputStream(file);//NOPMD
            try {
                lines.addAll(IOUtils.getLinesFromInputStream(input));
            } finally {
                IOUtils.closeInputStream(input);
            }
        }
        return cutInputString(isCharPo, isBytePo, ranges, lines);
    }

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges   list of pairs containing the start and end indeces for cut
     * @param stdin    InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, List<Pair<Integer, Integer>> ranges, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        List<String> lines = IOUtils.getLinesFromInputStream(stdin);

        return cutInputString(isCharPo, isBytePo, ranges, lines);
    }


    public String cutInputString(Boolean isCharPo, Boolean isBytePo, List<Pair<Integer, Integer>> ranges, List<String> input) {//NOPMD
        String output = "";
        ArrayList<Integer> index = new ArrayList<>();

        for (Pair<Integer, Integer> pair : ranges) {
            for (int i = pair.getKey(); i < pair.getValue() + 1; i++) {
                index.add(i);
            }
        }

        Collections.sort(index);
        index = removeDuplicates(index);
        int size = index.size();

        if (isCharPo) {
            char[] charArray;
            char[] currArray;
            List<Character> arr;

            for (String line : input) {
                arr = new ArrayList<>();
                charArray = line.toCharArray();
                for (int i = 0; i < size; i++) {
                    if (index.get(i) >= charArray.length) {
                        break;
                    }
                    arr.add(charArray[index.get(i)]);
                }
                currArray = new char[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    currArray[i] = arr.get(i);
                }
                output += new String(currArray) + STRING_NEWLINE;
            }
        } else if (isBytePo) {
            byte[] byteArray;
            byte[] currArray;
            List<Byte> arr;

            for (String line : input) {
                arr = new ArrayList<>();
                byteArray = line.getBytes();
                for (int i = 0; i < size; i++) {
                    if (index.get(i) >= byteArray.length) {
                        break;
                    }
                    arr.add(byteArray[index.get(i)]);
                }
                currArray = new byte[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    currArray[i] = arr.get(i);
                }
                output += new String(currArray) + STRING_NEWLINE;
            }
        }
        return output;
    }

    public ArrayList<Integer> removeDuplicates(ArrayList<Integer> index) {
        ArrayList<Integer> newIndex = new ArrayList<>();

        for (Integer i : index) {
            if (!newIndex.contains(i)) {
                newIndex.add(i);
            }
        }

        return newIndex;
    }
}
