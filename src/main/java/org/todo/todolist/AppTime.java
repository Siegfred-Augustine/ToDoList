package org.todo.todolist;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class AppTime {

    public static Map<String, Long> readCSV(String fileName) {
        Map<String, Long> appScreenTime = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Read header and discard it
            
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(","); // Assuming CSV uses commas
                if (fields.length < 4) continue; // Skip malformed lines
                
                String appName = fields[0].trim();
                int hours = Integer.parseInt(fields[1].trim());
                int minutes = Integer.parseInt(fields[2].trim());
                int seconds = Integer.parseInt(fields[3].trim());
                
                long totalTimeInSeconds = (hours * 3600L) + (minutes * 60L) + seconds;
                
                // Merge into the map (or add if not present)
                appScreenTime.merge(appName, totalTimeInSeconds, Long::sum);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing a number in the CSV file.");
        }

        return appScreenTime;
    }

    public static class Time {
        protected Long duration;
        protected LocalDateTime lastResetTime;
    
        public Time(Long duration) {
            this.duration = duration;
            this.lastResetTime = LocalDateTime.now();
        }
    
        public void trackScreenTime(Long duration) {
            checkAndResetDaily();
            this.duration += duration;
        }
    
        // Check if it's a new day and reset if necessary
        protected void checkAndResetDaily() {
            LocalDateTime now = LocalDateTime.now();
            if (!now.toLocalDate().equals(lastResetTime.toLocalDate())) {
                resetDuration();
                lastResetTime = now;
            }
        }
    
        // Reset duration
        public void resetDuration() {
            this.duration = Long.valueOf(0);
        }
    
        public Long getDuration() {
            checkAndResetDaily();
            return duration;
        }
    
        public String getDetails() {
            checkAndResetDaily();
            return "Duration: " + duration + " minutes";
        }
    }
    
    public static class Productive extends Time {
    
        protected Long minTime;
        protected boolean exceededMin;
    
        public Productive(Long duration, Long minTime) {
            super(duration);
            this.minTime = minTime;
        }
    
        @Override
        public void trackScreenTime(Long duration) {
            checkAndResetDaily();
            super.trackScreenTime(duration);
            this.exceededMin = this.duration > minTime;
        }
    
        public void setMinTime(Long newMin) {
            this.minTime = newMin;
        }
    
        @Override
        public String getDetails() {
            checkAndResetDaily();
            return super.getDetails() + "\nType: Productive";
        }
    }
    
    public static class Leisure extends Time {
        protected Long timeLimit;
        protected boolean exceededLimit;
    
        public Leisure(Long duration, Long timeLimit) {
            super(duration);
            this.timeLimit = timeLimit;
            this.exceededLimit = duration > timeLimit;
        }
    
        @Override
        public void trackScreenTime(Long duration) {
            checkAndResetDaily();
            super.trackScreenTime(duration);
            this.exceededLimit = this.duration > timeLimit;
        }
    
        @Override
        public String getDetails() {
            checkAndResetDaily();
            String limitStatus = exceededLimit ? "Time limit exceeded!" : "Within time limit.";
            return super.getDetails() + "\nTime Limit: " + timeLimit + " minutes\n" + limitStatus + "\nType: Leisure";
        }
    
        public void setTimeLimit(Long newLimit) {
            this.timeLimit = newLimit;
            this.exceededLimit = duration > newLimit;
        }
    }
    
    public static class Purposed extends Leisure {
        protected Long minTimeLimit;
        protected boolean addToLeisureTime;
    
        public Purposed(Long duration, Long timeLimit, Long minTimeLimit, boolean addToLeisureTime) {
            super(duration, timeLimit);
            this.minTimeLimit = minTimeLimit;
            this.addToLeisureTime = addToLeisureTime;
        }
    
        @Override
        public void trackScreenTime(Long duration) {
            checkAndResetDaily();
            if (duration >= minTimeLimit) {
                super.trackScreenTime(duration);
            } else {
                System.out.println("Time spent is below the minimum limit.");
            }
        }
    
        public void setMinTime(Long newMin) {
            this.minTimeLimit = newMin;
        }
    
        @Override
        public String getDetails() {
            checkAndResetDaily();
            return super.getDetails() + "\n" +
                    "Minimum Time Limit: " + minTimeLimit + " minutes\n" +
                    "Add to Leisure Time: " + addToLeisureTime;
        }
    
        public boolean shouldAddToLeisureTime() {
            return addToLeisureTime;
        }
    }
    
    public static class Dynamic extends Time {
        protected boolean isProductive;
        protected Long productiveTime;
        protected Long leisureTime;
        protected Long startTime;
        protected Long finishTime;
    
        public Dynamic(Long duration, boolean isProductive) {
            super(duration);
            this.isProductive = isProductive;
            this.startTime = Long.valueOf(0);
            this.finishTime = duration;
            if (isProductive) {
                this.productiveTime = duration;
                this.leisureTime = Long.valueOf(0);
            } else {
                this.leisureTime = duration;
                this.productiveTime = Long.valueOf(0);
            }
        }
    
        @Override
        public void trackScreenTime(Long duration) {
            checkAndResetDaily();
            finishTime = startTime + duration;
            Long timeSpent = finishTime - startTime;
    
            if (isProductive) {
                productiveTime += timeSpent;
            } else {
                leisureTime += timeSpent;
            }
            this.duration += timeSpent;
            startTime = finishTime;
        }
    
        @Override
        public void resetDuration() {
            super.resetDuration();
            this.productiveTime = Long.valueOf(0);
            this.leisureTime = Long.valueOf(0);
            this.startTime = Long.valueOf(0);
            this.finishTime = Long.valueOf(0);
        }
    
        public void toggleActivityType() {
            isProductive = !isProductive;
        }
    
        public Long getProductiveTime() {
            checkAndResetDaily();
            return productiveTime;
        }
    
        public Long getLeisureTime() {
            checkAndResetDaily();
            return leisureTime;
        }
    
        @Override
        public String getDetails() {
            checkAndResetDaily();
            String activityType = isProductive ? "Productive" : "Leisure";
            return super.getDetails() + "\nActivity Type: " + activityType +
                    "\nProductive Time: " + productiveTime + " minutes" +
                    "\nLeisure Time: " + leisureTime + " minutes";
        }
    }
}
