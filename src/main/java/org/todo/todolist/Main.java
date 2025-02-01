package org.todo.todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.IOException;
import java.io.File;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        launchBackgroundProcess();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainUI.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 760, 538);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        ToDoList list = new ToDoList();
        MainController mControl = fxmlLoader.getController();
        ScreentimeList timeList = new ScreentimeList();
        mControl.setList(list, timeList);
        
        list.taskList = SaveController.loadTasksFromCSV("tasks.csv");
        list.activityTasklist = SaveController.loadActivitiesFromCSV("activities.csv");
        list.eventsList = SaveController.loadEventsFromCSV("events.csv");
        
        mControl.taskInitializer(list.taskList);
        mControl.eventInitializer(list.eventsList);
        mControl.activityInitializer(list.activityTasklist);
        
        // Run deadline checker once at startup
        list.deadLineChecker();
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Start background processes in a separate process
        startBackgroundProcesses();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void launchBackgroundProcess() {
        try {
            ProcessBuilder builder = new ProcessBuilder("java", "-jar", "tracker.jar");
            builder.start(); // Starts the process independently
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startBackgroundProcesses() {
        try {
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path") + File.pathSeparator + "build/libs/tracker.jar";
            String className = "org.todo.todolist.BackgroundTracker";
            
            ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className
            );
            
            System.out.println("Starting process with command: " + String.join(" ", builder.command()));
            Process process = builder.start();
            System.out.println("Process started: " + process.isAlive());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}