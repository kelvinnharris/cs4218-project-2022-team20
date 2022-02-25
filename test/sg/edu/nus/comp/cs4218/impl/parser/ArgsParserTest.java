package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import static org.junit.jupiter.api.Assertions.*;

class ArgsParserTest {
    private ArgsParser argsParser;

    @BeforeEach
    void setUp() {
        argsParser = new ArgsParser();
    }

    @Test
    void testArgsParser_validateValidFlags_testPassed() throws InvalidArgsException {
        argsParser.flags.add('A');
        argsParser.legalFlags.add('A');
        assertDoesNotThrow(() -> argsParser.validateArgs());
    }

    @Test
    void testArgsParser_validateInvalidFlag_throwException() throws InvalidArgsException {
        argsParser.flags.add('A');
        assertThrows(InvalidArgsException.class, () -> argsParser.validateArgs());
    }

    @Test
    void testArgsParser_parseLegalFlag_testPassed() throws InvalidArgsException {
        argsParser.legalFlags.add('z');
        String[] args = new String[]{"-z"};
        argsParser.parse(args);

        assertEquals(1, argsParser.flags.size());
        assertTrue(argsParser.flags.contains('z'));

        assertEquals(0, argsParser.nonFlagArgs.size());
    }

    @Test
    void testArgsParser_parseComplexStringArgs_testPassed() throws InvalidArgsException {
        argsParser.legalFlags.add('a');
        argsParser.legalFlags.add('B');
        argsParser.legalFlags.add('c');

        String[] args = new String[]{"hello", "-BBc", "world!", "-B", "-"};
        argsParser.parse(args);

        assertEquals(2, argsParser.flags.size());
        assertTrue(argsParser.flags.contains('B'));
        assertTrue(argsParser.flags.contains('c'));

        assertEquals(3, argsParser.nonFlagArgs.size());
        assertTrue(argsParser.nonFlagArgs.contains("hello"));
        assertTrue(argsParser.nonFlagArgs.contains("world!"));
        assertTrue(argsParser.nonFlagArgs.contains("-"));
    }

    @Test
    void testArgsParser_parseInvalidFlagDifferentCase_throwsException() throws InvalidArgsException {
        argsParser.legalFlags.add('Z');

        String[] args = new String[]{"-z"};
        assertThrows(InvalidArgsException.class, () -> argsParser.parse(args));
    }

    @Test
    void testArgsParser_parseInvalidFlag_throwsException() throws InvalidArgsException {
        String[] args = new String[]{"-z"};
        assertThrows(InvalidArgsException.class, () -> argsParser.parse(args));
    }
}