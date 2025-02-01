package org.todo.todolist;

import java.lang.Thread;
import java.lang.InterruptedException;


public class BackgroundTracker {
    public static void main(String[] args) {
        startScreenTimeChecker();
        startTimeChecker();
        
        // Keep main thread alive
        try {
            while (true) {
                Thread.sleep(60000); // Main thread is kept alive by this
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startScreenTimeChecker() {
        Thread screenTimeThread = new Thread(() -> {
            try {
                while (true) {
                    ScreentimeTracker.track(); // Run the tracking continuously without any sleep
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        screenTimeThread.start();
    }
    
    private static void startTimeChecker() {
        TimeChecker timeChecker = new TimeChecker();
        Thread timeCheckThread = new Thread(() -> {
            try {
                while (true) {
                    TimeChecker.reloadAllData();
                    timeChecker.categorizeAndNotify();
                    Thread.sleep(30000); // Check every 30 seconds
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        timeCheckThread.start();
    }
}
