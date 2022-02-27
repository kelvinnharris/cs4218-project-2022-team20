package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.ExitException;

import static org.mockito.Mockito.*;

class ExitApplicationTest {
    @Test
    void testExit_runApplication_shouldNotThrowError() throws ExitException {
        String[] emptyArgs = new String[]{};
        ExitApplication mockApp = spy(ExitApplication.class);
        doNothing().when(mockApp).terminateExecution();
        mockApp.run(emptyArgs, null, null);
        verify(mockApp, times(1)).terminateExecution();
    }
}