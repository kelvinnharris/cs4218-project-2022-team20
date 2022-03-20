package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GrepArgsParserTest {
    private static final String PATTERN = "abc";
    private static final String SRC_FILE1 = "src1.txt";
    private static final String SRC_FILE2 = "src2.txt";
    private GrepArgsParser grepArgsParser;

    @BeforeEach
    void setUp() {
        grepArgsParser = Mockito.spy(new GrepArgsParser());
    }

    @Test
    void isCountOnly_parserContainsIsCountOnlyFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('c')).thenReturn(true);
        grepArgsParser.flags = map;
        assertTrue(grepArgsParser.isCountOnly());
    }

    @Test
    void isCaseInsensitive_parserContainsIsCaseInsensitiveFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('i')).thenReturn(true);
        grepArgsParser.flags = map;
        assertTrue(grepArgsParser.isCaseInsensitive());
    }

    @Test
    void isPrintFilename_parserContainsIsPrintFilenameFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('H')).thenReturn(true);
        grepArgsParser.flags = map;
        assertTrue(grepArgsParser.isPrintFilename());
    }

    @Test
    void getPattern_parserContainsPattern_returnsPattern() {
        grepArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(PATTERN, SRC_FILE1));
        assertEquals(PATTERN, grepArgsParser.getPattern());
    }

    @Test
    void getFilenames_parserContainsFilenames_returnsFilenames() {
        grepArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(PATTERN, SRC_FILE1, SRC_FILE2));
        assertArrayEquals(new String[]{SRC_FILE1, SRC_FILE2}, grepArgsParser.getFileNames());
    }

    @Test
    void getFilenames_parserNotContainsFiles_returnsNull() {
        grepArgsParser.nonFlagArgs = new ArrayList<>(List.of(SRC_FILE1));
        assertArrayEquals(null, grepArgsParser.getFileNames());
    }
}
