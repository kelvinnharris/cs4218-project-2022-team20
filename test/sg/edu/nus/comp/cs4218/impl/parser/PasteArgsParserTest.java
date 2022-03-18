package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasteArgsParserTest {
    private PasteArgsParser pasteArgsParser;

    @BeforeEach
    void setUp() {
        pasteArgsParser = Mockito.spy(new PasteArgsParser());
    }

    @Test
    void testPasteArgsParserTest_isSerik_testPassed() {
        Set map = mock(HashSet.class);
        when(map.contains('s')).thenReturn(true);
        pasteArgsParser.flags = map;

        assertTrue(pasteArgsParser.isSerial());
    }

    @Test
    void testPasteArgsParserTest_isNotSerial_testPassed() {
        Set map = mock(HashSet.class);
        pasteArgsParser.flags = map;
        when(map.isEmpty()).thenReturn(true);

        assertFalse(pasteArgsParser.isSerial());
    }

    @Test
    void testPasteArgsParserTest_getFiles_testPassed() {
        Set map = mock(HashSet.class);
        pasteArgsParser.nonFlagArgs.add("file1.txt");
        pasteArgsParser.flags = map;
        when(map.contains('s')).thenReturn(true);

        assertTrue(pasteArgsParser.isSerial());
        Assertions.assertEquals(pasteArgsParser.getFiles().get(0), "file1.txt");
    }
}
