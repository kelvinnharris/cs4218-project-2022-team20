/*
 * This file was automatically generated by EvoSuite
 * Fri Mar 18 09:54:34 GMT 2022
 */

package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import static org.evosuite.runtime.EvoAssertions.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import org.evosuite.runtime.EvoRunnerJUnit5;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.mock.java.io.MockFileOutputStream;
import org.junit.jupiter.api.extension.RegisterExtension;
import sg.edu.nus.comp.cs4218.impl.app.CatApplication;

@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = false)
public class CatApplication_ESTest extends CatApplication_ESTest_scaffolding {
@RegisterExtension
  static EvoRunnerJUnit5 runner = new EvoRunnerJUnit5(CatApplication_ESTest.class);

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test00()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      LinkedList<String> linkedList0 = new LinkedList<String>();
      linkedList0.add("Exception Caught");
      // Undeclared exception!
      try { 
        catApplication0.appendLineNumberToListString(linkedList0, linkedList0, 0L);
        fail("Expecting exception: ConcurrentModificationException");
      
      } catch(ConcurrentModificationException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("java.util.LinkedList$ListItr", e);
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test01()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      String[] stringArray0 = new String[4];
      stringArray0[0] = "";
      stringArray0[1] = "This is a directory";
      stringArray0[2] = "Null Pointer Exception";
      stringArray0[3] = "";
      Boolean boolean0 = Boolean.TRUE;
      InputStream inputStream0 = InputStream.nullInputStream();
      String string0 = catApplication0.catFileAndStdin(boolean0, inputStream0, stringArray0);
      assertEquals("cat: : Is a directory\ncat: This is a directory: No such file or directory\ncat: Null Pointer Exception: No such file or directory\ncat: : Is a directory", string0);
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test02()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      InputStream inputStream0 = InputStream.nullInputStream();
      Boolean boolean0 = Boolean.valueOf(false);
      try { 
        catApplication0.catFileAndStdin(boolean0, inputStream0, (String[]) null);
        fail("Expecting exception: Exception");
      
      } catch(Exception e) {
         //
         // cat: Null Files
         //
         verifyException("sg.edu.nus.comp.cs4218.impl.app.CatApplication", e);
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test03()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      Boolean boolean0 = Boolean.valueOf("Could not write to output stream");
      try { 
        catApplication0.catFileAndStdin(boolean0, (InputStream) null, (String[]) null);
        fail("Expecting exception: Exception");
      
      } catch(Exception e) {
         //
         // cat: Null Pointer Exception
         //
         verifyException("sg.edu.nus.comp.cs4218.impl.app.CatApplication", e);
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test04()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      Boolean boolean0 = Boolean.TRUE;
      InputStream inputStream0 = InputStream.nullInputStream();
      String string0 = catApplication0.catStdin(boolean0, inputStream0);
      assertEquals("", string0);
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test05()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      Boolean boolean0 = Boolean.valueOf(false);
      InputStream inputStream0 = InputStream.nullInputStream();
      String string0 = catApplication0.catStdin(boolean0, inputStream0);
      assertEquals("", string0);
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test06()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      String[] stringArray0 = new String[4];
      stringArray0[0] = "";
      Boolean boolean0 = Boolean.TRUE;
      InputStream inputStream0 = InputStream.nullInputStream();
      try { 
        catApplication0.catFileAndStdin(boolean0, inputStream0, stringArray0);
        fail("Expecting exception: NullPointerException");
      
      } catch(NullPointerException e) {
         //
         // no message in exception (getMessage() returned null)
         //
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test07()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      Boolean boolean0 = Boolean.FALSE;
      try { 
        catApplication0.catFiles(boolean0, (String[]) null);
        fail("Expecting exception: Exception");
      
      } catch(Exception e) {
         //
         // cat: Null Files
         //
         verifyException("sg.edu.nus.comp.cs4218.impl.app.CatApplication", e);
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test08()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      String[] stringArray0 = new String[4];
      stringArray0[0] = "Exception Caught";
      stringArray0[1] = "Exception Caught";
      stringArray0[2] = "Exception Caught";
      stringArray0[3] = "Null Pointer Exception";
      MockFileOutputStream mockFileOutputStream0 = new MockFileOutputStream("Could not read file");
      catApplication0.run(stringArray0, (InputStream) null, mockFileOutputStream0);
      assertEquals(4, stringArray0.length);
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test09()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      OutputStream outputStream0 = OutputStream.nullOutputStream();
      String[] stringArray0 = new String[0];
      try { 
        catApplication0.run(stringArray0, (InputStream) null, outputStream0);
        fail("Expecting exception: Exception");
      
      } catch(Exception e) {
         //
         // cat: Exception Caught
         //
         verifyException("sg.edu.nus.comp.cs4218.impl.app.CatApplication", e);
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test10()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      String[] stringArray0 = new String[5];
      stringArray0[0] = "Could not write to output stream";
      stringArray0[1] = "This is a directory";
      stringArray0[2] = "Could not read file";
      stringArray0[3] = "sg.edu.nus.comp.cs4218.exception.AbstractApplicationException";
      stringArray0[4] = "-";
      BufferedInputStream bufferedInputStream0 = new BufferedInputStream((InputStream) null);
      ByteArrayOutputStream byteArrayOutputStream0 = new ByteArrayOutputStream(1806);
      try { 
        catApplication0.run(stringArray0, bufferedInputStream0, byteArrayOutputStream0);
        fail("Expecting exception: Exception");
      
      } catch(Exception e) {
         //
         // cat: Exception Caught
         //
         verifyException("sg.edu.nus.comp.cs4218.impl.app.CatApplication", e);
      }
  }

  @Test
  @Timeout(value = 4000 , unit = TimeUnit.MILLISECONDS)
  public void test11()  throws Throwable  {
      CatApplication catApplication0 = new CatApplication();
      String[] stringArray0 = new String[4];
      InputStream inputStream0 = InputStream.nullInputStream();
      try { 
        catApplication0.run(stringArray0, inputStream0, (OutputStream) null);
        fail("Expecting exception: Exception");
      
      } catch(Exception e) {
         //
         // cat: Null Pointer Exception
         //
         verifyException("sg.edu.nus.comp.cs4218.impl.app.CatApplication", e);
      }
  }
}
