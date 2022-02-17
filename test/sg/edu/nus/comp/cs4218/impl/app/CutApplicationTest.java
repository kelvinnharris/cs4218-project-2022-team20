package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class CutApplicationTest {
    private CutApplication cutApplication;

    @BeforeEach
    void setUp() {
        cutApplication = new CutApplication();
    }

    @Test
    void cutFromFiles_oneFile_returnsSuccessful() {
        String[] args = new String[]{ "-b", "1", "test.txt" };
    }

    @Test
    void cutFromStdin_oneLine_returnsSuccessful() {
        String[] args = new String[]{ "-b", "1", "abc" };
    }

    @Test
    void cutInputString_byCharSingleIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abc"));
        String output = cutApplication.cutInputString(true, false, false, 1, 2, new int[]{1}, input);
        assertEquals(output, "a");
    }

    @Test
    void cutInputString_byByteSingleIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abc"));
        String output = cutApplication.cutInputString(false, true, false, 1, 2, new int[]{1}, input);
        assertEquals(output, "a");
    }

    @Test
    void cutInputString_byCharCommaSeparatedIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(true, false, false, 1, 2, new int[]{1,3,4}, input);
        assertEquals(output, "acd");
    }

    @Test
    void cutInputString_byByteCommaSeparatedIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(false, true, false, 1, 2, new int[]{1,3,4}, input);
        assertEquals(output, "acd");
    }

    @Test
    void cutInputString_byCharUnsortedCommaSeparatedIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(true, false, false, 1, 2, new int[]{3,4,1}, input);
        assertEquals(output, "acd");
    }

    @Test
    void cutInputString_byByteUnsortedCommaSeparatedIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(false, true, false, 1, 2, new int[]{3,4,1}, input);
        assertEquals(output, "acd");
    }

    @Test
    void cutInputString_byCharDuplicateCommaSeparatedIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(true, false, false, 1, 2, new int[]{1,1,2}, input);
        assertEquals(output, "ab");
    }

    @Test
    void cutInputString_byByteDuplicateCommaSeparatedIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(false, true, false, 1, 2, new int[]{1,1,2}, input);
        assertEquals(output, "ab");
    }

    @Test
    void cutInputString_byCharRangeIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(true, false, false, 1, 2, new int[]{1-3}, input);
        assertEquals(output, "abc");
    }

    @Test
    void cutInputString_byByteRangeIndexSingleLine_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abcd"));
        String output = cutApplication.cutInputString(false, true, false, 1, 2, new int[]{1-3}, input);
        assertEquals(output, "abc");
    }

    @Test
    void cutInputString_byCharSingleIndexMultipleLines_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abc"));
        input.add(("def"));
        input.add(("ghi"));
        String output = cutApplication.cutInputString(true, false, false, 1, 2, new int[]{1}, input);
        assertEquals(output, "a\nd\ng");
    }

    @Test
    void cutInputString_byByteSingleIndexMultipleLines_returnsSuccessful() {
        List<String> input = new ArrayList<>();
        input.add(("abc"));
        input.add(("def"));
        input.add(("ghi"));
        String output = cutApplication.cutInputString(false, true, false, 1, 2, new int[]{1}, input);
        assertEquals(output, "a\nd\ng");
    }


}