package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CpArgsParserTest {
    private static final String SRC_FILE = "src.txt";
    private static final String DEST_FILE = "dest.txt";
    private CpArgsParser cpArgsParser;

    @BeforeEach
    void setUp() {
        cpArgsParser = Mockito.spy(new CpArgsParser());
    }

    @Test
    void isRecursive_parserContainsRecursiveFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('r')).thenReturn(true);
        cpArgsParser.flags = map;
        assertTrue(cpArgsParser.isRecursive());
    }

    @Test
    void isRecursive_parserContainsRecursiveUppercaseFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('R')).thenReturn(true);
        cpArgsParser.flags = map;
        assertTrue(cpArgsParser.isRecursive());
    }

    @Test
    void isRecursive_parserNotContainsRecursiveFlag_returnsFalse() {
        Set map = mock(HashSet.class);
        when(map.contains('r')).thenReturn(false);
        cpArgsParser.flags = map;
        assertFalse(cpArgsParser.isRecursive());
    }

    @Test
    void getSourceFiles_parserContainsSourceFile_returnsSourceFiles() {
        cpArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(SRC_FILE, DEST_FILE));
        assertTrue(Arrays.asList(cpArgsParser.getSourceFiles()).contains(SRC_FILE));
        assertFalse(Arrays.asList(cpArgsParser.getSourceFiles()).contains(DEST_FILE));
    }

    @Test
    void getSourceFiles_parserNotContainsSourceFile_returnsEmptyArray() {
        cpArgsParser.nonFlagArgs = new ArrayList<>(List.of(DEST_FILE));
        assertArrayEquals(new String[]{}, cpArgsParser.getSourceFiles());
    }

    @Test
    void getDestinationFile_parserContainsDestinationFile_returnsDestFile() {
        cpArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(SRC_FILE, DEST_FILE));
        assertEquals(DEST_FILE, cpArgsParser.getDestinationFile());
    }
}
