package sg.edu.nus.comp.cs4218.impl.parser;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CutArgsParserTest {
    private CutArgsParser cutArgsParser;

    @BeforeEach
    void setUp() {
        cutArgsParser = Mockito.spy(new CutArgsParser());
    }

    @Test
    void testCutArgsParserTest_isCharPoOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('c')).thenReturn(true);
        cutArgsParser.flags = map;

        assertTrue(cutArgsParser.isCharPo());
        assertFalse(cutArgsParser.isBytePo());
    }

    @Test
    void testCutArgsParserTest_isBytePoOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('b')).thenReturn(true);
        cutArgsParser.flags = map;

        assertFalse(cutArgsParser.isCharPo());
        assertTrue(cutArgsParser.isBytePo());
    }

    @Test
    void testCutArgsParserTest_isCharPoIsBytePo_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('c')).thenReturn(true);
        when(map.contains('b')).thenReturn(true);
        cutArgsParser.flags = map;

        assertTrue(cutArgsParser.isCharPo());
        assertTrue(cutArgsParser.isBytePo());
    }

    @Test
    void testCutArgsParserTest_noFlags_testPassed() {
        Set map = mock(HashSet.class);
        cutArgsParser.flags = map;

        assertFalse(cutArgsParser.isCharPo());
        assertFalse(cutArgsParser.isBytePo());
    }

    @Test
    void testCutArgsParserTest_parseSingleIndex_testPassed() throws InvalidArgsException {
        List list = mock(List.class);
        cutArgsParser.nonFlagArgs = list;
        when(list.get(0)).thenReturn("1");
        cutArgsParser.parseIndex();
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));

        assertTrue(cutArgsParser.getRanges().equals(ranges));
    }

    @Test
    void testCutArgsParserTest_parseSingleRange_testPassed() throws InvalidArgsException {
        List list = mock(List.class);
        cutArgsParser.nonFlagArgs = list;
        when(list.get(0)).thenReturn("1-3");
        cutArgsParser.parseIndex();
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));

        assertTrue(cutArgsParser.getRanges().equals(ranges));
    }

    @Test
    void testCutArgsParserTest_parseMultipleIndex_testPassed() throws InvalidArgsException {
        List list = mock(List.class);
        cutArgsParser.nonFlagArgs = list;
        when(list.get(0)).thenReturn("1-3,4-6,7");
        cutArgsParser.parseIndex();
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        ranges.add(new Pair<>(3, 5));
        ranges.add(new Pair<>(6, 6));

        assertTrue(cutArgsParser.getRanges().equals(ranges));
    }

}
