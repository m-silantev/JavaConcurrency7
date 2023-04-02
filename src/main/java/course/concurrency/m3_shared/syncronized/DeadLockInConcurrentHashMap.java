package course.concurrency.m3_shared.syncronized;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeadLockInConcurrentHashMap {

    private final Map<String, Number> map = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    void fillMap() {
        map.put("0", 0);
        map.put("1", 1);
    }

    public void test() {
        map.compute("0", (k, v) -> {
            CountDownLatch latch = new CountDownLatch(1);
            executor.execute(() -> {
                map.clear();
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10;
        });
    }

    void shut() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        var sut = new DeadLockInConcurrentHashMap();
        sut.fillMap();
        System.out.println("before");
        sut.test();
        System.out.println("after");
        sut.shut();
    }

}


