package pers.chujun.singleflight.main;

import java.util.concurrent.CountDownLatch;

public class Call<T> {

    private CountDownLatch latch;
    private T result;
    private int dups;

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getDups() {
        return dups;
    }

    public void setDups(int dups) {
        this.dups = dups;
    }
}
