package org.todo.todolist;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.nio.file.Files;
import java.nio.file.Paths;


public class SaveController {

    public static void saveTasksToCSV(List<Tasks> tasks, String filename) {
        clearCSV(filename);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Tasks task : tasks) {
                String priority = "";
                switch(task.taskImportance){
                    case ToDoList.Hierarchy.LOW:
                        priority = "Low";
                        break;
                    case ToDoList.Hierarchy.MEDIUM:
                        priority = "Medium";
                        break;
                    case ToDoList.Hierarchy.HIGH:
                        priority = "High";
                        break;
                    case ToDoList.Hierarchy.IMPORTANT:
                        priority = "IMPORTANT";
                        break;
                }
                // Write task attributes to CSV
                String line = task.taskName + ","
                        + priority + ","
                        + task.deadline.format(formatter);
                writer.write(line);
                writer.newLine();  // Adds a new line after each task
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveActivityToCSV(List<Activity> activity, String filename) {
        clearCSV(filename);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Activity task : activity) {
                String priority = "";
                // Write task attributes to CSV
                switch(task.taskImportance){
                    case ToDoList.Hierarchy.LOW:
                        priority = "Low";
                        break;
                    case ToDoList.Hierarchy.MEDIUM:
                        priority = "Medium";
                        break;
                    case ToDoList.Hierarchy.HIGH:
                        priority = "High";
                        break;
                    case ToDoList.Hierarchy.IMPORTANT:
                        priority = "IMPORTANT";
                        break;
                }
                // Write task attributes to CSV
                String line = task.taskName + ","
                        + priority + ","
                        + task.deadline.format(formatter);
                writer.write(line);
                writer.newLine();  // Adds a new line after each task
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveEventsToCSV(List<Events> event, String filename) {
        clearCSV(filename);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            String priority = "";
            for (Events task : event)  {
                switch(task.taskImportance){
                    case ToDoList.Hierarchy.LOW:
                        priority = "Low";
                        break;
                    case ToDoList.Hierarchy.MEDIUM:
                        priority = "Medium";
                        break;
                    case ToDoList.Hierarchy.HIGH:
                        priority = "High";
                        break;
                    case ToDoList.Hierarchy.IMPORTANT:
                        priority = "IMPORTANT";
                        break;
                }
                // Write task attributes to CSV
                String line = task.taskName + ","
                        + priority + ","
                        + task.deadline.format(formatter);
                writer.write(line);
                writer.newLine();  // Adds a new line after each task
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Tasks> loadTasksFromCSV(String filename) {
        ArrayList<Tasks> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] taskData = line.split(",");
                
                // Verify we have all required fields
                if (taskData.length < 3) {
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }
    
                // Parse task data from CSV line
                String taskName = taskData[0].trim();
                ToDoList.Hierarchy priority = ToDoList.Hierarchy.LOW;
                
                switch(taskData[1].trim()){
                    case "Low":
                        priority = ToDoList.Hierarchy.LOW;
                        break;
                    case "Medium":
                        priority = ToDoList.Hierarchy.MEDIUM;
                        break;
                    case "High":
                        priority = ToDoList.Hierarchy.HIGH;
                        break;
                    case "IMPORTANT":
                        priority = ToDoList.Hierarchy.IMPORTANT;
                        break;
                    default:
                        System.err.println("Invalid priority found: " + taskData[1]);
                        priority = ToDoList.Hierarchy.LOW; // Default to LOW if invalid
                }
    
                try {
                    LocalDate deadline = LocalDate.parse(taskData[2].trim(), formatter);
                    Tasks task = new Tasks(taskName, priority, deadline);
                    tasks.add(task);
                } catch (Exception e) {
                    System.err.println("Error parsing date for line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    public static ArrayList<Activity> loadActivitiesFromCSV(String filename) {
        ArrayList<Activity> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] taskData = line.split(",");

                // Parse task data from CSV line
                String taskName = taskData[0];
                ToDoList.Hierarchy priority = ToDoList.Hierarchy.LOW;
                switch(taskData[1]){
                    case "Low":
                        priority = ToDoList.Hierarchy.LOW;
                        break;
                    case "Medium":
                        priority = ToDoList.Hierarchy.MEDIUM;
                        break;
                    case "High":
                        priority = ToDoList.Hierarchy.HIGH;
                        break;
                    case "IMPORTANT":
                        priority = ToDoList.Hierarchy.IMPORTANT;
                        break;
                }

                LocalDate deadline = LocalDate.parse(taskData[2], formatter);
                // Create Task object (you can modify this based on your Task constructor)
                Activity task = new Activity(taskName, priority, deadline);
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    public static ArrayList<Events> loadEventsFromCSV(String filename) {
        ArrayList<Events> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] taskData = line.split(",");

                // Parse task data from CSV line
                String taskName = taskData[0];
                ToDoList.Hierarchy priority = ToDoList.Hierarchy.LOW;
                switch(taskData[1]){
                    case "Low":
                        priority = ToDoList.Hierarchy.LOW;
                        break;
                    case "Medium":
                        priority = ToDoList.Hierarchy.MEDIUM;
                        break;
                    case "High":
                        priority = ToDoList.Hierarchy.HIGH;
                        break;
                    case "IMPORTANT":
                        priority = ToDoList.Hierarchy.IMPORTANT;
                        break;
                }

                LocalDate deadline = LocalDate.parse(taskData[2], formatter);
                // Create Task object (you can modify this based on your Task constructor)
                Events event = new Events(taskName, priority, deadline);
                tasks.add(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    public static void clearCSV(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Opening the file in write mode with an empty content will clear it
            // No need to write anything to the file; just opening it will erase the content.
            writer.write("");  // Optional: Clears the file content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static HashMap<String, String> loadTimeSettings(String filename) {
        HashMap<String, String> timeSettings = new HashMap<>();

        // Check if the file exists
        if (!Files.exists(Paths.get(filename))) {
            System.out.println("File not found: " + filename + ". Returning an empty map.");
            return timeSettings; // Return an empty map if the file does not exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] settingData = line.split(",");
                if (settingData.length == 2) {
                    timeSettings.put(settingData[0].trim(), settingData[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timeSettings;
    }
    public static void saveTimeSettings(HashMap<String, String> timeSettings, String filename) {
        clearCSV(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String key : timeSettings.keySet()) {
                String line = key + "," + timeSettings.get(key);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
