import org.junit.Test;
import udp.UDPServer;

/**
 * Description: TestUDP
 * Author: DIYILIU
 * Update: 2018-06-08 15:55
 */
public class TestUDP {

    @Test
    public void test(){
        UDPServer server = new UDPServer();
        server.init();
    }
}
