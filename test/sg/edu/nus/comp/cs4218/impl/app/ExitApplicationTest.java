package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.ExitException;

import java.security.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ExitApplicationTest {
    //Referenced from https://stackoverflow.com/questions/309396/java-how-to-test-methods-that-call-system-exit
    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission permission) {
            // empty means no permission
        }

        @Override
        public void checkPermission(Permission permission, Object object) {
            // empty means no permission
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new TestExitException(status);
        }
    }

    private static class TestExitException extends SecurityException {
        final int status;

        TestExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    @Test
    void run_exitApplication_exitsSuccessfully() {
        System.setSecurityManager(new NoExitSecurityManager());

        try {
            String[] args = {};
            new ExitApplication().run(args, System.in, System.out);
            System.setSecurityManager(null);
        } catch (TestExitException e) {
            assertEquals(0, e.status);
            System.setSecurityManager(null);
        } catch (Exception e) {
            System.setSecurityManager(null);
            fail(e);
        }

        System.setSecurityManager(null);
    }

    @Test
    void run_exitApplication_shouldNotThrowError() throws ExitException {
        String[] emptyArgs = new String[]{};
        ExitApplication mockApp = spy(ExitApplication.class);
        doNothing().when(mockApp).terminateExecution();
        mockApp.run(emptyArgs, null, null);
        verify(mockApp, times(1)).terminateExecution();
    }
}