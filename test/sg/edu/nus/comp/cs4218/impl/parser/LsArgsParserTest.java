package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LsArgsParserTest {
    private LsArgsParser lsArgsParser;

    @BeforeEach
    void setUp() {
        lsArgsParser = Mockito.spy(new LsArgsParser());
    }

    @Test
    void testLsArgsParserTest_isRecursiveOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('R')).thenReturn(true);
        lsArgsParser.flags = map;

        assertTrue(lsArgsParser.isRecursive());
        assertFalse(lsArgsParser.isSortByExt());
    }

    @Test
    void testLsArgsParserTest_isSortByExtOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('X')).thenReturn(true);
        lsArgsParser.flags = map;

        assertFalse(lsArgsParser.isRecursive());
        assertTrue(lsArgsParser.isSortByExt());
    }

    @Test
    void testLsArgsParserTest_isFolderRecursiveSortByExt_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('R')).thenReturn(true);
        when(map.contains('X')).thenReturn(true);
        lsArgsParser.flags = map;

        assertTrue(lsArgsParser.isRecursive());
        assertTrue(lsArgsParser.isSortByExt());
    }
}