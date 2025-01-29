
package org.todo.todolist;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

public class ScreentimeTracker {
        // Interface for Windows API calls
    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);
        HWND GetForegroundWindow();
        int GetWindowThreadProcessId(HWND hWnd, IntByReference processId);
        int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    }

    // Interface for Process API calls
    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
        boolean QueryFullProcessImageNameW(int hProcess, int dwFlags, char[] lpExeName, IntByReference lpdwSize);
        int OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
        void CloseHandle(int handle);
    }

    private static final int PROCESS_QUERY_LIMITED_INFORMATION = 0x1000;
    private static final Map<String, Long> appScreenTime = new HashMap<>();
    private static String lastActiveApp = "";
    private static long lastUpdateTime = System.currentTimeMillis();
    private static String fileName = generateFileName();
    
    public static void track() {
        if (!Platform.isWindows()) {
            System.err.println("This program only works on Windows.");
            return;
        }
        aggregateScreentime();
        try {
            while (true) {
                updateScreenTime();
                Thread.sleep(1000); // Update every second

                // Save to file every 5 minutes
                if (System.currentTimeMillis() - lastUpdateTime >= 10000) {
                    checkAndUpdateFileName();
                    saveToCSV();
                    
                    lastUpdateTime = System.currentTimeMillis();
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Tracking interrupted: " + e.getMessage());
            saveToCSV(); // Save final data before exiting
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void aggregateScreentime(){
        Map<String, Long> csvData = AppTime.readCSV(fileName);
        if (csvData == null) {
            csvData = new HashMap<>(); // Default to an empty map
        }

        // Merge the data from the CSV into the existing appScreenTime map
        csvData.forEach((appName, totalTime) -> 
            appScreenTime.merge(appName, totalTime, Long::sum)
        );
        System.out.println(appScreenTime);
    }

    private static String generateFileName() {
        // Generate file name with today's date
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "screentime_" + date + ".csv";
    }

    private static void checkAndUpdateFileName() {
        // Get the current date and update fileName if it doesn't match
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!fileName.contains(currentDate)) {
            fileName = generateFileName();
            System.out.println("Updated file name to: " + fileName);
        }
    }

    private static void updateScreenTime() {
        try {
            // Check if it's midnight and reset the screentime
            resetScreentimeAtMidnight();

            HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
            if (foregroundWindow == null) {
                return;
            }

            IntByReference processId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(foregroundWindow, processId);

            int process = Kernel32.INSTANCE.OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION, false, processId.getValue());
            if (process == 0) {
                return;
            }

            try {
                char[] filename = new char[512];
                IntByReference size = new IntByReference(512);
                if (Kernel32.INSTANCE.QueryFullProcessImageNameW(process, 0, filename, size)) {
                    String processName = new String(filename, 0, size.getValue()).trim();

                    if (!processName.isEmpty()) {
                        String appName = processName.substring(processName.lastIndexOf('\\') + 1);
                        if (!appName.equals(lastActiveApp)) {
                            // Update screen time for the app
                            appScreenTime.merge(appName, 1L, Long::sum);
                            lastActiveApp = appName;
                        } else {
                            // Increment time for current app
                            appScreenTime.merge(appName, 1L, Long::sum);
                        }
                    }
                }
            } finally {
                Kernel32.INSTANCE.CloseHandle(process);
            }
        } catch (Exception e) {
            System.err.println("Error updating screen time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to reset screen time at midnight
    private static void resetScreentimeAtMidnight() {
        LocalTime now = LocalTime.now();
        if (now.getHour() == 0 && now.getMinute() == 0 && now.getSecond() == 0) {
            appScreenTime.clear(); // Clear the screen time data at midnight
            System.out.println("Screen time data reset at midnight.");
        }
    }

    public static String getFileName(){
        return fileName;
    } 


    private static void saveToCSV() {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("App Name,Hours,Minutes,Seconds\n");

            // Sort apps by screen time
            List<Map.Entry<String, Long>> sortedApps = new ArrayList<>(appScreenTime.entrySet());
            sortedApps.sort(Map.Entry.<String, Long>comparingByValue().reversed());

            // Write app data
            for (Map.Entry<String, Long> entry : sortedApps) {
                String appName = entry.getKey();
                long seconds = entry.getValue();
                long minutes = seconds / 60;
                long hours = minutes / 60;
                minutes %= 60;
                seconds %= 60;
                writer.write(String.format("%s,%d,%d,%d\n", appName, hours, minutes, seconds));
            }

            System.out.println("Screen time data saved to: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving to CSV: " + e.getMessage());
        }
    }
}

























