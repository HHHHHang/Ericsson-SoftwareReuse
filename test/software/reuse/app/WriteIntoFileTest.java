package software.reuse.app;

import junit.framework.TestCase;

/**
 * Created by wangdechang on 2016/3/27.
 */
public class WriteIntoFileTest extends TestCase {
    WriteIntoFile writeIntoFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        writeIntoFile = WriteIntoFile.getWriteIntoFile();

    }

    public void testGetWriteIntoFile() throws Exception {
        assertSame(writeIntoFile, WriteIntoFile.getWriteIntoFile());
    }

    public void testWriteFile() throws Exception {
        writeIntoFile.writeFile("testFileName","succeedTest",9,"fail",1);
    }
}