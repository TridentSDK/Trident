package net.tridentsdk.server;

import net.tridentsdk.api.perf.AddTakeQueue;
import net.tridentsdk.api.perf.ReImplLinkedQueue;

public class ReImplTest {
    private static final AddTakeQueue<Object> OBJECTS = new ReImplLinkedQueue<>();

    public static void main(String[] args) {
        OBJECTS.add(new Object());
        //OBJECTS.add(new Object());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(OBJECTS.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OBJECTS.add(new Object());
                    }
                }).start();
                try {
                    System.out.println(OBJECTS.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
