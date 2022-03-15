package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniqArgsParserTest {
    private UniqArgsParser uniqArgsParser;

    @BeforeEach
    void setUp() {
        uniqArgsParser = Mockito.spy(new UniqArgsParser());
    }

    @Test
    void testUniqArgsParserTest_isCountOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('c')).thenReturn(true);
        uniqArgsParser.flags = map;

        assertTrue(uniqArgsParser.isCount());
        assertFalse(uniqArgsParser.isRepeated());
        assertFalse(uniqArgsParser.isAllRepeated());
    }

    @Test
    void testUniqArgsParserTest_isRepeatedOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('d')).thenReturn(true);
        uniqArgsParser.flags = map;

        assertFalse(uniqArgsParser.isCount());
        assertTrue(uniqArgsParser.isRepeated());
        assertFalse(uniqArgsParser.isAllRepeated());
    }

    @Test
    void testUniqArgsParserTest_isAllRepeatedOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('D')).thenReturn(true);
        uniqArgsParser.flags = map;

        assertFalse(uniqArgsParser.isCount());
        assertFalse(uniqArgsParser.isRepeated());
        assertTrue(uniqArgsParser.isAllRepeated());
    }

    @Test
    void testUniqArgsParserTest_isCountIsRepeatedIsAllRepeated_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('c')).thenReturn(true);
        when(map.contains('d')).thenReturn(true);
        when(map.contains('D')).thenReturn(true);
        uniqArgsParser.flags = map;

        assertTrue(uniqArgsParser.isCount());
        assertTrue(uniqArgsParser.isRepeated());
        assertTrue(uniqArgsParser.isAllRepeated());
    }

    @Test
    void testUniqArgsParserTest_noFlags_testPassed() {
        Set map = mock(HashSet.class);
        uniqArgsParser.flags = map;

        assertFalse(uniqArgsParser.isCount());
        assertFalse(uniqArgsParser.isRepeated());
        assertFalse(uniqArgsParser.isAllRepeated());
    }

    @Test
    void testUniqArgsParserTest_getInputOutputFile_testPassed() {
        List<String> list = new ArrayList<String>();
        list.add("first.txt");
        list.add("second.txt");
        uniqArgsParser.nonFlagArgs = list;

        assertEquals("first.txt", uniqArgsParser.getInputFile());
        assertEquals("second.txt", uniqArgsParser.getOutputFile());
    }
}
