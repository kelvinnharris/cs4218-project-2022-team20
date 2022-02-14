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
    void shouldReturnNewLineOnEmptyArgument() throws EchoException {
        String[] emptyArgs = new String[]{};
        String outputEmptyArgs = echoApplication.constructResult(emptyArgs);
        assertEquals(outputEmptyArgs, System.lineSeparator());
    }

    @Test
    void shouldReturnNewLineOnEmptyStringArgument() throws EchoException {
        String[] emptyStringArgs = new String[]{ "" };
        String outputEmptyStringArgs = echoApplication.constructResult(emptyStringArgs);
        assertEquals(outputEmptyStringArgs, System.lineSeparator());
    }

    @Test
    void passUnicodeAsParameterShouldReturnSameAsInput() throws EchoException {
        String[] unicodeArgs = new String[]{ "🐯" };
        String outputUnicodeArgs = echoApplication.constructResult(unicodeArgs);

        assertEquals("🐯" + System.lineSeparator(), outputUnicodeArgs);
    }

    @Test
    void passOneNormalTextShouldReturnSameAsInput() throws EchoException {
        String[] oneArgs = new String[]{ "hello1" };
        String outputOneArgs = echoApplication.constructResult(oneArgs);
        assertEquals("hello1" + System.lineSeparator(), outputOneArgs);
    }

    @Test
    void passMultipleNormalTextShouldReturnSameAsInput() throws EchoException {
        String[] multipleArgs = new String[]{ "hello1", "hello2", "\"hello3\"" };
        String outputMultipleArgs = echoApplication.constructResult(multipleArgs);

        // on actually echo, args that is passed to constructResult, the double quote of hello3 has been removed
        assertEquals("hello1 hello2 \"hello3\"" + System.lineSeparator(), outputMultipleArgs);
    }

    @Test
    void passNullStdinShouldPassed() {
        String[] emptyArgs = new String[]{};
        assertDoesNotThrow(() -> echoApplication.run(emptyArgs, null, System.out));
    }

    @Test
    void passNullArgsShouldThrowEchoException() {
        assertThrows(EchoException.class, () -> echoApplication.run(null, System.in, System.out));
    }

    @Test
    void passNullStdoutShouldThrowEchoException() {
        String[] emptyArgs = new String[]{};
        assertThrows(EchoException.class, () -> echoApplication.run(emptyArgs, System.in, null));
    }
}