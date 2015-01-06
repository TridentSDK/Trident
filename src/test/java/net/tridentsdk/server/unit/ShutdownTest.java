package net.tridentsdk.server.unit;

import net.tridentsdk.Trident;
import net.tridentsdk.server.TridentStart;
import org.junit.Test;

public class ShutdownTest {
    public static void main(String[] args) {
        new ShutdownTest().startStop();
    }

    @Test
    public void startStop() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Trident.shutdown();
                }
            }).start();
            TridentStart.main(new String[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
