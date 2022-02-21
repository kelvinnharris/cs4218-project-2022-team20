package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.EchoException;

class EchoApplicationTest {
    private EchoApplication echoApplication;

    @BeforeEach
    void setUp() {
        this.echoApplication = new EchoApplication();
    }

    @Test
    void testEcho_emptyArgument_shouldReturnNewLine() throws EchoException {
        String[] emptyArgs = new String[]{};
        String outputEmptyArgs = echoApplication.constructResult(emptyArgs);
        assertEquals(System.lineSeparator(), outputEmptyArgs);
    }

    @Test
    void testEcho_emptyStringArgument_shouldReturnNewLine() throws EchoException {
        String[] emptyStringArgs = new String[]{ "" };
        String outputEmptyStringArgs = echoApplication.constructResult(emptyStringArgs);
        assertEquals(System.lineSeparator(), outputEmptyStringArgs);
    }

    @Test
    void testEcho_passUnicodeAsParameter_shouldReturnSameAsInput() throws EchoException {
        String[] unicodeArgs = new String[]{ "🐯" };
        String outputUnicodeArgs = echoApplication.constructResult(unicodeArgs);

        assertEquals("🐯" + System.lineSeparator(), outputUnicodeArgs);
    }

    @Test
    void testEcho_passOneNormalText_shouldReturnSameAsInput() throws EchoException {
        String[] oneArgs = new String[]{ "hello1" };
        String outputOneArgs = echoApplication.constructResult(oneArgs);
        assertEquals("hello1" + System.lineSeparator(), outputOneArgs);
    }

    @Test
    void testEcho_passMultipleNormalText_shouldReturnSameAsInput() throws EchoException {
        String[] multipleArgs = new String[]{ "hello1", "hello2", "\"hello3\"" };
        String outputMultipleArgs = echoApplication.constructResult(multipleArgs);

        // on actually echo, args that is passed to constructResult, the double quote of hello3 has been removed
        assertEquals("hello1 hello2 \"hello3\"" + System.lineSeparator(), outputMultipleArgs);
    }

    @Test
    void testEcho_passNullStdin_shouldPassed() {
        String[] emptyArgs = new String[]{};
        assertDoesNotThrow(() -> echoApplication.run(emptyArgs, null, System.out));
    }

    @Test
    void testEcho_passNullArgs_shouldThrowEchoException() {
        assertThrows(EchoException.class, () -> echoApplication.run(null, System.in, System.out));
    }

    @Test
    void testEcho_passNullStdout_shouldThrowEchoException() {
        String[] emptyArgs = new String[]{};
        assertThrows(EchoException.class, () -> echoApplication.run(emptyArgs, System.in, null));
    }
}