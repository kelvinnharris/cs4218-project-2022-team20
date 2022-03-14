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
    void constructResult_emptyArgument_shouldReturnNewLine() throws EchoException {
        String[] emptyArgs = new String[]{};
        String outputEmpty = echoApplication.constructResult(emptyArgs);
        assertEquals(System.lineSeparator(), outputEmpty);
    }

    @Test
    void constructResult_emptyStringArgument_shouldReturnNewLine() throws EchoException {
        String[] emptyStringArgs = new String[]{""};
        String outputEmptyString = echoApplication.constructResult(emptyStringArgs);
        assertEquals(System.lineSeparator(), outputEmptyString);
    }

    @Test
    void constructResult_passUnicodeAsParameter_shouldReturnSameAsInput() throws EchoException {
        String[] unicodeArgs = new String[]{"🐯"};
        String outputUnicode = echoApplication.constructResult(unicodeArgs);

        assertEquals("🐯" + System.lineSeparator(), outputUnicode);
    }

    @Test
    void constructResult_passOneNormalText_shouldReturnSameAsInput() throws EchoException {
        String[] oneArgs = new String[]{"hello1"};
        String outputOne = echoApplication.constructResult(oneArgs);
        assertEquals("hello1" + System.lineSeparator(), outputOne);
    }

    @Test
    void constructResult_passMultipleNormalText_shouldReturnSameAsInput() throws EchoException {
        String[] multipleArgs = new String[]{"hello1", "hello2", "\"hello3\""};
        String outputMultiple = echoApplication.constructResult(multipleArgs);

        // on actually echo, args that is passed to constructResult, the double quote of hello3 has been removed
        assertEquals("hello1 hello2 \"hello3\"" + System.lineSeparator(), outputMultiple);
    }

    @Test
    void run_passNullStdin_shouldPassed() {
        String[] emptyArgs = new String[]{};
        assertDoesNotThrow(() -> echoApplication.run(emptyArgs, null, System.out));
    }

    @Test
    void run_passNullArgs_shouldThrowEchoException() {
        assertThrows(EchoException.class, () -> echoApplication.run(null, System.in, System.out));
    }

    @Test
    void run_passNullStdout_shouldThrowEchoException() {
        String[] emptyArgs = new String[]{};
        assertThrows(EchoException.class, () -> echoApplication.run(emptyArgs, System.in, null));
    }
}