package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MvArgsParserTest {
    private final String SRC_FILE = "src.txt";
    private final String DEST_FILE = "dest.txt";
    private MvArgsParser mvArgsParser;

    @BeforeEach
    void setUp() {
        mvArgsParser = Mockito.spy(new MvArgsParser());
    }

    @Test
    void isOverwrite_parserNotContainsDoNotOverWriteFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('n')).thenReturn(false);
        mvArgsParser.flags = map;
        assertTrue(mvArgsParser.isOverWrite());
    }

    @Test
    void getSourceFiles_parserContainsSourceFile_returnsSourceFiles() {
        mvArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(SRC_FILE, DEST_FILE));
        assertTrue(Arrays.asList(mvArgsParser.getSourceFiles()).contains(SRC_FILE));
        assertFalse(Arrays.asList(mvArgsParser.getSourceFiles()).contains(DEST_FILE));
    }

    @Test
    void getSourceFiles_parserNotContainsSourceFile_returnsEmptyArray() {
        mvArgsParser.nonFlagArgs = new ArrayList<>(List.of(DEST_FILE));
        assertArrayEquals(new String[]{}, mvArgsParser.getSourceFiles());
    }

    @Test
    void getDestinationFile_parserContainsDestinationFile_returnsDestFile() {
        mvArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(SRC_FILE, DEST_FILE));
        assertEquals(DEST_FILE, mvArgsParser.getDestinationFile());
    }
}
