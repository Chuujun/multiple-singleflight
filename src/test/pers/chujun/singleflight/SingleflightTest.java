package test.pers.chujun.singleflight;

import main.pers.chujun.singleflight.Singleflight;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

public class SingleflightTest {
    private Singleflight singleFlight;

    @Test
    public void test() throws InterruptedException {
        Singleflight sf = new Singleflight();

        int n = 10;
        CountDownLatch latch = new CountDownLatch(n);

        for (int i = 0; i < n; i++) {
            new Thread(() -> {
                try{
                    // String data = getData("key");

                    String data = sf.run("key", () -> {
                        return getData("key");
                    });
                    System.out.println(data);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
    }

    @Test
    public String getData(String key) {
        try {
            return getDataFromCache(key);
        } catch (Exception e) {
            if (e.getMessage().equals("data not exist")) {
                return getDataFromDB(key);
            } else {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

    @Test
    public String getDataFromCache(String key) throws Exception {
        throw new Exception("data not exist");
    }

    @Test
    public String getDataFromDB(String key) {
        System.out.println("get key from DB");
        return new String("data");
    }
}
