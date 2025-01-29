package org.todo.todolist;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.WindowEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class screenTimeList {

    private String inputPath = ScreentimeTracker.getFileName();
    private static String filePath = "processed_data.csv";

    List<String> productive = new ArrayList<>();
    List<String> leisure = new ArrayList<>();
    List<String> purposed = new ArrayList<>();
    List<String> ignore = new ArrayList<>();
    public Map<String, String> processed = new HashMap<>();

    String[] appCategory = new String[]{"Default (Ignored)", "Leisure", "Productive", "Dynamic", "Purposed"};
    HBox box = new HBox();
    Label appName = new Label("Sample app");
    ComboBox<String> choices = new ComboBox<>();
    Button addApp = new Button("Add");

    public void initialize(VBox vbox) {
        Map<String, Long> appScreenTime = AppTime.readCSV(inputPath);
        System.out.println(appScreenTime);
        for (Map.Entry<String, Long> entry : appScreenTime.entrySet()) {
            if (processed.containsKey(entry.getKey())){
                continue;
            }
            HBox box = new HBox();
            Label appName = new Label("Sample app");
            ComboBox<String> choices = new ComboBox<>();
            Button addApp = new Button("Add");
            choices.getItems().addAll(appCategory);
            appName.setText(entry.getKey());
            box.setSpacing(20);
            box.setAlignment(Pos.CENTER);
            box.setMaxHeight(35);
            box.setMinHeight(35);
            box.setMinWidth(400);
            box.setMaxWidth(400);
            box.setPadding(new Insets(10));
            box.setStyle("-fx-background-color: #d1c9c5; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-thickness:2");
            addApp.getStyleClass().add("button-hover-effect3");
            choices.setStyle("-fx-background-color:#ffffff");

            // Create labels for app name and time spent
            Label timeLabel = new Label(entry.getValue() + " minutes");

            // Create a ComboBox with the given categories
            choices.setValue("Default (Ignored)"); // Set default value

            // Create the 'Add' button (action to be added later)

            // Add action listener for ComboBox
            addApp.setOnAction(event -> {
                String selectedCategory = choices.getValue();
                System.out.println(entry.getKey() + " set to " + selectedCategory);

                // Switch case for handling different categories
                switch (selectedCategory) {
                    case "Productive":
                        // Action for Productive category
                        productive.add(entry.getKey());
                        processed.put(entry.getKey(), "Productive");
                        System.out.println(entry.getKey() + " is categorized as Productive.");
                        break;
                    case "Leisure":
                        // Action for Leisure category
                        leisure.add(entry.getKey());
                        processed.put(entry.getKey(), "Leisure");
                        System.out.println(entry.getKey() + " is categorized as Leisure.");
                        break;
                    case "Purposed":
                        // Action for Purposed category
                        purposed.add(entry.getKey());
                        processed.put(entry.getKey(), "Purposed");
                        System.out.println(entry.getKey() + " is categorized as Purposed.");
                        break;
                    case "Default (Ignored)":
                    default:
                        // Action for Default category
                        ignore.add(entry.getKey());
                        processed.put(entry.getKey(), "Default (Ignored)");
                        System.out.println(entry.getKey() + " is categorized as Default (Ignored).");
                        break;
                }
                addApp.setDisable(true);
            });


            // Add all components to the row
            box.getChildren().addAll(appName, timeLabel, choices, addApp);

            // Add the row to the content box
            vbox.getChildren().add(box);

            vbox.setSpacing(10);
            vbox.setPadding(new Insets(15));
            vbox.setAlignment(Pos.TOP_CENTER);
        }

    }
    public static Map<String, String> readCSV() {
        Map<String, String> appTypeMap = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header if there is one
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length >= 2) {
                    String appName = columns[0].trim();
                    String appType = columns[1].trim();
                    appTypeMap.put(appName, appType);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return appTypeMap;
    }

    public void saveProcessedToCSV() {
        // Define the path where the CSV will be saved
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the header
            writer.write("App Name, Category");
            writer.newLine();

            // Iterate over the processed map and write each entry to the CSV
            for (Map.Entry<String, String> entry : processed.entrySet()) {
                String appName = entry.getKey();
                String category = entry.getValue();
                writer.write(appName + "," + category);
                writer.newLine();  // Move to the next line
            }

            System.out.println("Processed data saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

}
