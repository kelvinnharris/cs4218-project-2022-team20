package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeeArgsParserTest {
    private final String SRC_FILE = "src.txt";
    private final String DEST_FILE = "dest.txt";
    private TeeArgsParser teeArgsParser;

    @BeforeEach
    void setUp() {
        teeArgsParser = Mockito.spy(new TeeArgsParser());
    }

    @Test
    void isAppend_parserContainsIsAppendFlag_returnsTrue() {
        Set map = mock(HashSet.class);
        when(map.contains('a')).thenReturn(true);
        teeArgsParser.flags = map;
        assertTrue(teeArgsParser.isAppend());
    }

    @Test
    void getFiles_parserContainsFiles_returnsFiles() {
        teeArgsParser.nonFlagArgs = new ArrayList<>(Arrays.asList(SRC_FILE, DEST_FILE));
        assertArrayEquals(new String[]{SRC_FILE, DEST_FILE}, teeArgsParser.getFiles());
    }

    @Test
    void getFiles_parserNotContainsFiles_returnsEmptyArray() {
        teeArgsParser.nonFlagArgs = new ArrayList<>();
        assertArrayEquals(new String[]{}, teeArgsParser.getFiles());
    }
}
