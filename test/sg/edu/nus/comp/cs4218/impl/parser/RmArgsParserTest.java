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

public class RmArgsParserTest {
    private RmArgsParser rmArgsParser;

    @BeforeEach
    void setUp() {
        rmArgsParser = Mockito.spy(new RmArgsParser());
    }

    @Test
    void testRmArgsParserTest_isRecursiveOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('r')).thenReturn(true);
        rmArgsParser.flags = map;

        assertTrue(rmArgsParser.isRecursive());
        assertFalse(rmArgsParser.isEmptyDir());
    }

    @Test
    void testRmArgsParserTest_isEmptyDirOnly_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('d')).thenReturn(true);
        rmArgsParser.flags = map;

        assertFalse(rmArgsParser.isRecursive());
        assertTrue(rmArgsParser.isEmptyDir());
    }

    @Test
    void testRmArgsParserTest_isRecursiveEmptyDir_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('r')).thenReturn(true);
        when(map.contains('d')).thenReturn(true);
        rmArgsParser.flags = map;

        assertTrue(rmArgsParser.isRecursive());
        assertTrue(rmArgsParser.isEmptyDir());
    }

    @Test
    void testRmArgsParserTest_noFlags_testPassed() {
        Set map = mock(HashSet.class);
        rmArgsParser.flags = map;

        assertFalse(rmArgsParser.isRecursive());
        assertFalse(rmArgsParser.isEmptyDir());
    }
}
