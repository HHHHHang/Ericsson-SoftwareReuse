package software.reuse.app;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangdechang on 2016/3/27.
 */
/**
 *
 * before testing  all method of the class Client2ServerTest, we must start server
 */
public class Client2ServerTest extends TestCase {

    Client2Server client2Server;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        client2Server = new Client2Server("127.0.0.1",8888,"wang","wangpass");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testWriteIntoFile(){
        try {
            Method method = client2Server.getClass().getDeclaredMethod("writeIntoFile");
            method.setAccessible(true);
            method.invoke(client2Server);
            method.setAccessible(false);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void testClientLogin() throws Exception {
        assertTrue(client2Server.clientLogin());
        /* username = 'wang' password = 'wangpass' here pass a worng password*/
        Client2Server client2 = new Client2Server("127.0.0.1",8888,"wang","wang");
        assertFalse(client2.clientLogin());
    }

    public void testClientRegister() throws Exception {

    }

    public void testSendMessage() throws Exception {

    }

    public void testGetSocket() throws Exception {

    }
}