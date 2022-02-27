package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WcArgsParserTest {
    private WcArgsParser wcArgsParser;

    @BeforeEach
    void setUp() {
        wcArgsParser = Mockito.spy(new WcArgsParser());
    }

    @Test
    void testWcArgsParserTest_isBytesOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('c')).thenReturn(true);
        wcArgsParser.flags = map;

        assertTrue(wcArgsParser.isBytes());
        assertFalse(wcArgsParser.isWords());
        assertFalse(wcArgsParser.isLines());
    }

    @Test
    void testWcArgsParserTest_isLinesOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('l')).thenReturn(true);
        wcArgsParser.flags = map;

        assertTrue(wcArgsParser.isLines());
        assertFalse(wcArgsParser.isWords());
        assertFalse(wcArgsParser.isBytes());
    }

    @Test
    void testWcArgsParserTest_isWordsOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('w')).thenReturn(true);
        wcArgsParser.flags = map;

        assertTrue(wcArgsParser.isWords());
        assertFalse(wcArgsParser.isBytes());
        assertFalse(wcArgsParser.isLines());
    }

    @Test
    void testWcArgsParserTest_isBytesLinesWordsNoFlags_testPassed() {
        Set map = mock(HashSet.class);
        wcArgsParser.flags = map;
        when(map.isEmpty()).thenReturn(true);

        assertTrue(wcArgsParser.isBytes());
        assertTrue(wcArgsParser.isWords());
        assertTrue(wcArgsParser.isLines());
    }

    @Test
    void testWcArgsParserTest_isBytesLinesWithFlags_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('w')).thenReturn(true);
        when(map.contains('l')).thenReturn(true);
        when(map.contains('c')).thenReturn(true);
        wcArgsParser.flags = map;

        assertTrue(wcArgsParser.isBytes());
        assertTrue(wcArgsParser.isWords());
        assertTrue(wcArgsParser.isLines());
    }
}
