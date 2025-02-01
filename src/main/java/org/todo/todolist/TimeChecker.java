package org.todo.todolist;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

public class TimeChecker {
    private static Map<String, Long> timeData;
    private static Map<String, String> typeData;
    private static long minProductiveMinutes;
    private static long maxLeisureMinutes;
    private static long minPurposedMinutes;
    private static long maxPurposedMinutes;

    // Track if limits were exceeded for each app type (Productive, Leisure, Purposed)
    private static boolean productiveLimitExceededToday = false;
    private static boolean leisureLimitExceededToday = false;
    private static boolean purposedLimitExceededToday = false;

    // Track the date when the limit was last checked
    private static LocalDate lastCheckedDate = LocalDate.now();

    // Reload the AppTime data (from CSV, in seconds)
    public static void reloadAppTime() {
        String timeFileName = ScreentimeTracker.getFileName();  // Dynamic file name based on the date
        timeData = AppTime.readCSV(timeFileName);  // Assuming AppTime.readCSV reads time data from CSV

        // Check if the map is null or empty
        if (timeData == null || timeData.isEmpty()) {
            System.out.println("Warning: AppTime data is null or empty. No data loaded.");
            timeData = new HashMap<>(); // Initialize to an empty map to avoid NullPointerException
        }
    }

    // Reload the ScreentimeList data (from CSV)
    public static void reloadScreentimeList() {
        typeData = screenTimeList.readCSV();  // Assuming ScreentimeList.readCSV loads type data

        // Check if the map is null or empty
        if (typeData == null || typeData.isEmpty()) {
            System.out.println("Warning: ScreentimeList data is null or empty. No data loaded.");
            typeData = new HashMap<>(); // Initialize to an empty map to avoid NullPointerException
        }
    }

    // Reload the TimeSettings data (from CSV)
    public static void reloadTimeSettings() {
        Map<String, String> timeSettings = SaveController.loadTimeSettings("timeSettings.csv");

        // Check if the map is null or empty
        if (timeSettings == null || timeSettings.isEmpty()) {
            System.out.println("Warning: TimeSettings data is null or empty. Using default values.");
            timeSettings = new HashMap<>(); // Initialize to an empty map to avoid NullPointerException
        }

        // Convert time strings to seconds, defaulting to 0 if the key is missing or the value is null
        minProductiveMinutes = convertTimeToSeconds(timeSettings.getOrDefault("ProductiveMin", "00:00:00"));
        maxLeisureMinutes = convertTimeToSeconds(timeSettings.getOrDefault("LeisureMax", "00:00:00"));
        minPurposedMinutes = convertTimeToSeconds(timeSettings.getOrDefault("PurposedMin", "00:00:00"));
        maxPurposedMinutes = convertTimeToSeconds(timeSettings.getOrDefault("PurposedMax", "00:00:00"));
    }

    private static long convertTimeToSeconds(String timeString) {
        // Handle null or empty timeString
        if (timeString == null || timeString.trim().isEmpty()) {
            System.out.println("Warning: Time string is null or empty. Defaulting to 0 seconds.");
            return 0;
        }

        // Replace commas with colons to match the format "HH:mm:ss"
        String formattedTime = timeString.replace(',', ':');

        // Use DateTimeFormatter to parse the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse(formattedTime, formatter);

        // Convert time to total seconds
        return time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
    }

    // TimeChecker constructor: Initialize once and separate logic for reloading
    public TimeChecker() {
        reloadAppTime(); // Load AppTime data
        reloadScreentimeList(); // Load ScreentimeList data
        reloadTimeSettings(); // Load TimeSettings data
    }

    public void categorizeAndNotify() {
        // Check if we need to reset the daily tracking
        if (!lastCheckedDate.isEqual(LocalDate.now())) {
            lastCheckedDate = LocalDate.now();  // Reset to today's date
            productiveLimitExceededToday = false;  // Reset the flag for Productive
            leisureLimitExceededToday = false;     // Reset the flag for Leisure
            purposedLimitExceededToday = false;    // Reset the flag for Purposed
        }

        // Check if timeData or typeData is null or empty
        if (timeData == null || timeData.isEmpty() || typeData == null || typeData.isEmpty()) {
            System.out.println("Warning: No data available to categorize and notify.");
            return; // Exit the method early
        }

        long totalProductiveTime = 0;
        long totalLeisureTime = 0;
        long totalPurposedTime = 0;

        // Iterate over time data and check limits
        for (Map.Entry<String, Long> entry : timeData.entrySet()) {
            String appName = entry.getKey();
            long timeSpent = entry.getValue();

            String appType = typeData.getOrDefault(appName, "Default");
            if ("Default".equals(appType)) {
                continue;
            }

            // Skip processing if limit was already exceeded for the day
            if (appType.equals("Productive") && productiveLimitExceededToday) {
                totalProductiveTime = minProductiveMinutes;  // Set to the minimum to notify
                break;  // Skip further accumulation
            }
            if (appType.equals("Leisure") && leisureLimitExceededToday) {
                totalLeisureTime = maxLeisureMinutes;  // Set to the maximum to notify
                break;  // Skip further accumulation
            }
            if (appType.equals("Purposed") && purposedLimitExceededToday) {
                totalPurposedTime = minPurposedMinutes;  // Set to the minimum to notify
                break;  // Skip further accumulation
            }

            // Track total time for each type
            switch (appType) {
                case "Productive":
                    totalProductiveTime += timeSpent;
                    if (totalProductiveTime >= minProductiveMinutes && !productiveLimitExceededToday) {
                        productiveLimitExceededToday = true;  // Mark as exceeded for the day
                    }
                    break;
                case "Leisure":
                    totalLeisureTime += timeSpent;
                    if (totalLeisureTime > maxLeisureMinutes && !leisureLimitExceededToday) {
                        leisureLimitExceededToday = true;  // Mark as exceeded for the day
                    }
                    break;
                case "Purposed":
                    totalPurposedTime += timeSpent;
                    if (totalPurposedTime >= minPurposedMinutes && totalPurposedTime <= maxPurposedMinutes && !purposedLimitExceededToday) {
                        purposedLimitExceededToday = true;  // Mark as exceeded for the day
                    }
                    break;
            }
        }

        // Pass the flags to show notifications directly if limits were exceeded
        showTimeNotifications(totalProductiveTime, totalLeisureTime, totalPurposedTime);
    }

    private void showTimeNotifications(long productiveTime, long leisureTime, long purposedTime) {
        StringBuilder congratulationMessage = new StringBuilder();
        StringBuilder warningMessage = new StringBuilder();

        // Productive time check
        if (productiveLimitExceededToday) {
            congratulationMessage.append(String.format("Great job! You've met your productive time goal (%d mins) 🎉\n\n",
                    productiveTime));
        } else if (productiveTime >= minProductiveMinutes) {
            congratulationMessage.append(String.format("Great job! You've met your productive time goal (%d/%d mins) 🎉\n\n",
                    productiveTime, minProductiveMinutes));
        }

        // Leisure time check
        if (leisureLimitExceededToday) {
            warningMessage.append(String.format("Warning! You've exceeded your leisure time limit (%d mins) ⚠️\n\n",
                    leisureTime));
        } else if (leisureTime > maxLeisureMinutes) {
            warningMessage.append(String.format("Warning! You've exceeded your leisure time limit (%d/%d mins) ⚠️\n\n",
                    leisureTime, maxLeisureMinutes));
        }

        // Purposed time check
        if (purposedLimitExceededToday) {
            congratulationMessage.append(String.format("Well done! Your purposed time is within the ideal range (%d mins, target: %d-%d mins) 🎯\n\n",
                    purposedTime, minPurposedMinutes, maxPurposedMinutes));
        } else if (purposedTime >= minPurposedMinutes && purposedTime <= maxPurposedMinutes) {
            congratulationMessage.append(String.format("Well done! Your purposed time is within the ideal range (%d mins, target: %d-%d mins) 🎯\n\n",
                    purposedTime, minPurposedMinutes, maxPurposedMinutes));
        } else if (purposedTime > maxPurposedMinutes) {
            warningMessage.append(String.format("Warning! You've exceeded your purposed time limit (%d mins, should be max %d mins) ⚠️\n\n",
                    purposedTime, maxPurposedMinutes));
        }

        // Show congratulations if there's a message
        if (congratulationMessage.length() > 0) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Achievement Notification");
                alert.setHeaderText("Good News!");
                alert.setContentText(congratulationMessage.toString().trim());
                alert.showAndWait();
            });
        }

        // Show warning if there's a message
        if (warningMessage.length() > 0) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Time Limit Warning");
                alert.setHeaderText("Caution!");
                alert.setContentText(warningMessage.toString().trim());
                alert.showAndWait();
            });
        }
    }

    // Method to reload all the data sources (when needed)
    public static void reloadAllData() {
        reloadAppTime();
        reloadScreentimeList();
        reloadTimeSettings();
    }
}