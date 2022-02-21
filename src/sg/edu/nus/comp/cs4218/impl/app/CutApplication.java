package sg.edu.nus.comp.cs4218.impl.app;

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
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;

public class CutApplication implements CutInterface {
    CutArgsParser parser;
    InputStream stdin;
    int[] index;
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
        CutArgsParser parser = new CutArgsParser();
        try {
            parser.parse(args);
            parser.parseIndex();
            if ((parser.isCharPo() && parser.isBytePo()) || (!parser.isCharPo() && !parser.isBytePo())) {
                String exceptionMessage = ILLEGAL_FLAG_MSG;
                throw new InvalidArgsException(exceptionMessage);
            }
        } catch (InvalidArgsException e) {
            throw new CutException(e.getMessage());
        }

        // Format: cut [Option] [LIST] FILES...
        if (stdout == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        this.stdin = stdin;
        this.parser = new CutArgsParser();
        try {
            parser.parse(args);
            parser.parseIndex();
            this.index = parser.getIndex();
            if ((parser.isCharPo() && parser.isBytePo()) || (!parser.isCharPo() && !parser.isBytePo())) {
                String exceptionMessage = ILLEGAL_FLAG_MSG;
                throw new InvalidArgsException(exceptionMessage);
            }
        } catch (InvalidArgsException e) {
            throw new CutException(e.getMessage());
        }

        StringBuilder output = new StringBuilder();
        try {
            if (parser.getFiles().isEmpty()) {
                output.append(cutFromStdin(parser.isCharPo(), parser.isBytePo(), parser.isRange(), parser.getStartIdx(), parser.getEndIdx(), stdin));
            } else {
                output.append(cutFromFiles(parser.isCharPo(), parser.isBytePo(), parser.isRange(), parser.getStartIdx(), parser.getEndIdx(), parser.getFiles().toArray(new String[0])));

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

    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx,
                        String... fileName) throws Exception {
        return cutFromFiles(isCharPo, isBytePo, isRange, startIdx, endIdx, index, stdin, fileName);

    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx,
                               InputStream stdin) throws Exception {
        return cutFromStdin(isCharPo, isBytePo, isRange, startIdx, endIdx, index, stdin);

    }

        /**
         * Cuts out selected portions of each line
         *
         * @param isCharPo Boolean option to cut by character position
         * @param isBytePo Boolean option to cut by byte position
         * @param isRange  Boolean option to perform range-based cut
         * @param startIdx index to begin cut
         * @param endIdx   index to end cut
         * @param fileName Array of String of file names
         * @return
         * @throws Exception
         */
    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, int[] index, InputStream stdin, String... fileName) throws Exception {
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
                throw new Exception(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new Exception(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new Exception(ERR_NO_PERM);
            }
            InputStream input = IOUtils.openInputStream(file);
            lines.addAll(IOUtils.getLinesFromInputStream(input));
            IOUtils.closeInputStream(input);
        }
        String output = cutInputString(isCharPo, isBytePo, isRange, startIdx, endIdx, index, lines);
        return output;
    }

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param isRange  Boolean option to perform range-based cut
     * @param startIdx index to begin cut
     * @param endIdx   index to end cut
     * @param stdin    InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, int[] index, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        List<String> lines = IOUtils.getLinesFromInputStream(stdin);

        String output = cutInputString(isCharPo, isBytePo, isRange, startIdx, endIdx, index, lines);
        return output;
    }


    public String cutInputString(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, int[] index, List<String> input) {
        Arrays.sort(index);
        int len = index.length;
        int[] res;
        res = removeDuplicates(index, len);
        len = res.length;
        String output = "";
        String charsetName = "IBM01140";
        if (isCharPo) {
            char[] charArray;
            char[] currArray;
            int counter;
            if (isRange) {
                for (String line : input) {
                    currArray = new char[endIdx + 1 - startIdx < line.length() ? endIdx + 1 - startIdx : line.length()];
                    counter = 0;
                    charArray = line.toCharArray();
                    for (int i = startIdx; i < endIdx + 1; i++) {

                        if (i >= charArray.length) {
                            break;
                        }
                        currArray[counter] = charArray[i];
                        counter += 1;
                    }
                    output += new String(currArray) + "\n";
                }
            } else {
                for (String line : input) {
                    currArray = new char[len < line.length() ? len : line.length()];

                    counter = 0;
                    charArray = line.toCharArray();
                    for (Integer i : res) {
                        if (i >= charArray.length) {
                            break;
                        }
                        currArray[counter] = charArray[i];
                        counter += 1;
                    }
                    output += new String(currArray) + "\n";
                }
            }
        } else if (isBytePo) {
            byte[] byteArray;
            byte[] currArray;
            int counter;
            if (isRange) {
                for (String line : input) {
                    currArray = new byte[endIdx + 1 - startIdx < line.length() ? endIdx + 1 - startIdx : line.length()];
                    counter = 0;
                    byteArray = line.getBytes();
                    for (int i = startIdx; i < endIdx + 1; i++) {

                        if (i >= byteArray.length) {
                            break;
                        }
                        currArray[counter] = byteArray[i];
                        counter += 1;
                    }
                    output += new String(currArray) + "\n";
                }
            } else {
                for (String line : input) {
                    currArray = new byte[len < line.length() ? len : line.length()];

                    counter = 0;
                    byteArray = line.getBytes();
                    for (Integer i : res) {
                        if (i >= byteArray.length) {
                            break;
                        }
                        currArray[counter] = byteArray[i];
                        counter += 1;
                    }
                    output += new String(currArray) + "\n";
                }
            }
        }
        return output;
    }

    public int[] removeDuplicates(int[] arr, int len) {
        if (len == 0 || len == 1){
            return arr;
        }
        int[] temp = new int[len];
        int j = 0;
        for (int i = 0; i < len - 1; i++){
            if (arr[i] != arr[i + 1]){
                temp[j++] = arr[i];
            }
        }
        temp[j++] = arr[len - 1];
        int[] res = new int[j];

        for (int i = 0; i < j; i++){
            res[i] = temp[i];
        }

        return res;
    }
}
