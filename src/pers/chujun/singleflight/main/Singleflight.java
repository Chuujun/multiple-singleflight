package pers.chujun.singleflight.main;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;


public class Singleflight {
    // protects m
    private final ReentrantLock mu = new ReentrantLock();

    // lazy initialized
    private ConcurrentHashMap<String, Call> m;

    public <T> T run(String key, Supplier<T> func) throws Exception {
        mu.lock();

        // lazy initialized
        if (this.m == null) {
            this.m = new ConcurrentHashMap<>();
        }

        try {
            // use the existing result if exist
            if (m.containsKey(key)) {
                Call<T> call = m.get(key);
                int dups = call.getDups();
                call.setDups(++dups);
                mu.unlock();
                call.getLatch().await();

                return call.getResult();
            }

            // get the result from the given function if not exist
            Call<T> call = new Call<>();
            // set other calls to wait until get the current call
            call.setLatch(new CountDownLatch(1));
            m.put(key, call);
            mu.unlock();

            return doCall(call, key, func);

        } finally {
            if (mu.isHeldByCurrentThread()) {
                mu.unlock();
            }
        }
    }

    // doCall handles the single call for a key.
    private <T> T doCall(Call<T> call, String key, Supplier<T> func) {
        try {
            call.setResult(func.get());

            mu.lock();
            call.getLatch().countDown();
            // remove the value after the function call done
            if (m.get(key) == call) {
                m.remove(key);
            }
            mu.unlock();

            // TODO: pack the result into Result class
            return call.getResult();

        } finally {
            if (mu.isHeldByCurrentThread()) {
                mu.unlock();
            }
        }
    }

    // Forget tells the singleflight to forget about a key.  Future calls
    // to Do for this key will call the function rather than waiting for
    // an earlier call to complete.
    public void forget(String key) {
        mu.lock();
        m.remove(key);
        mu.unlock();
    }
}
