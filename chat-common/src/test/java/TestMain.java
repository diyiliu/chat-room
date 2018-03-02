import org.junit.Test;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-03-01 13:42
 */
public class TestMain {

    @Test
    public void test(){
        String str = "abc^123456";

        System.out.println(str.split("\\^")[0]);

        System.out.println(str.startsWith("abc^"));

        System.out.println("================");

        String str1 = "efg~123456";

        System.out.println(str1.split("~")[0]);

        System.out.println(str1.startsWith("abc~"));
    }
}
