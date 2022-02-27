package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.ExitException;

class ExitApplicationTest {
    private ExitApplication exitApplication;

    @BeforeEach
    void setUp() {
        this.exitApplication = new ExitApplication();
    }

    @Test
    void testExit_runApplication_shouldNotThrowError() throws ExitException {
        String[] emptyArgs = new String[]{};
        exitApplication.run(emptyArgs, null, null);
        throw new ExitException("should not be reachable");
    }
}