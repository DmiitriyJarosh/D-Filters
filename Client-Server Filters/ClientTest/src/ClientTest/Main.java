package ClientTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static final int NUM_OF_THREADS = 1;
    public static final int NUM_OF_ITERATIONS = 50;

    public static void main(String[] args) {
        long sum = 0;
        try {
            FileWriter output = new FileWriter(new File("output.txt"));
            for (int j = 0; j < NUM_OF_ITERATIONS; j++) {
                Receiver receiver;
                AtomicInteger flag = new AtomicInteger(0);
                long[] time = new long[NUM_OF_THREADS];
                for (int i = 0; i < NUM_OF_THREADS; i++) {
                    receiver = new Receiver(time, i, flag);
                    receiver.loadImage(new File("test.jpg"));
                    receiver.setSelectedFilter(receiver.getFiltersList().get(0));
                    receiver.setAskToStart(true);
                    new Thread(receiver).start();
                    flag.getAndIncrement();
                }

                while (flag.get() != 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < NUM_OF_THREADS; i++) {
                    if (i == 10) {
                        sum += time[i];
                        output.write(time[i] + ";\n");
                    }
                    System.out.println("Thread# " + i + " used: " + time[i]);
                }
                System.out.println("Iteration# " + j + " finished!!");
            }
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Result: " + (sum / NUM_OF_ITERATIONS));
    }
}
