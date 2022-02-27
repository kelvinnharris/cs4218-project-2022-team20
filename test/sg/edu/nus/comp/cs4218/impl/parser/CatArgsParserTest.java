package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatArgsParserTest {
    private CatArgsParser catArgsParser;

    @BeforeEach
    void setUp() {
        catArgsParser = Mockito.spy(new CatArgsParser());
    }

    @Test
    void testCatArgsParserTest_isFlagNumbers_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('n')).thenReturn(true);
        catArgsParser.flags = map;

        assertTrue(catArgsParser.isFlagNumber());
    }

    @Test
    void testCatArgsParserTest_isNotFlagNumbers_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('n')).thenReturn(false);
        catArgsParser.flags = map;

        assertFalse(catArgsParser.isFlagNumber());
    }
}
