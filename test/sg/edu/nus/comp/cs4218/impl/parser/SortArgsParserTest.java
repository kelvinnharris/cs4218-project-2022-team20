package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SortArgsParserTest {
    private SortArgsParser sortArgsParser;

    @BeforeEach
    void setUp() {
        sortArgsParser = Mockito.spy(new SortArgsParser());
    }

    @Test
    void testSortArgsParserTest_isFirstWordNumberOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('n')).thenReturn(true);
        sortArgsParser.flags = map;

        assertTrue(sortArgsParser.isFirstWordNumber());
        assertFalse(sortArgsParser.isReverseOrder());
        assertFalse(sortArgsParser.isCaseIndependent());
    }

    @Test
    void testSortArgsParserTest_isReverseOrderOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('r')).thenReturn(true);
        sortArgsParser.flags = map;

        assertFalse(sortArgsParser.isFirstWordNumber());
        assertTrue(sortArgsParser.isReverseOrder());
        assertFalse(sortArgsParser.isCaseIndependent());
    }

    @Test
    void testSortArgsParserTest_isCaseIndependentOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('f')).thenReturn(true);
        sortArgsParser.flags = map;

        assertFalse(sortArgsParser.isFirstWordNumber());
        assertFalse(sortArgsParser.isReverseOrder());
        assertTrue(sortArgsParser.isCaseIndependent());
    }

    @Test
    void testSortArgsParserTest_isFirstWordNumberIsReverseOrderIsCaseIndependent_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('n')).thenReturn(true);
        when(map.contains('r')).thenReturn(true);
        when(map.contains('f')).thenReturn(true);
        sortArgsParser.flags = map;

        assertTrue(sortArgsParser.isFirstWordNumber());
        assertTrue(sortArgsParser.isReverseOrder());
        assertTrue(sortArgsParser.isCaseIndependent());
    }

    @Test
    void testSortArgsParserTest_noFlags_testPassed() {
        Set map = mock(HashSet.class);
        sortArgsParser.flags = map;

        assertFalse(sortArgsParser.isFirstWordNumber());
        assertFalse(sortArgsParser.isReverseOrder());
        assertFalse(sortArgsParser.isCaseIndependent());
    }

}
