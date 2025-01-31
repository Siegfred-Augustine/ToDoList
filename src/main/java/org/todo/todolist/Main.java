package org.todo.todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainUI.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 760, 538);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        ToDoList list = new ToDoList();
        MainController mControl = fxmlLoader.getController();

        ScreenTimeList timeList = new ScreenTimeList();
        mControl.setList(list, timeList);

        list.taskList = SaveController.loadTasksFromCSV("tasks.csv");

        list.activityTasklist = SaveController.loadActivitiesFromCSV("activities.csv");

        list.eventsList = SaveController.loadEventsFromCSV("events.csv");

        mControl.taskInitializer(list.taskList);
        mControl.eventInitializer(list.eventsList);
        mControl.activityInitializer(list.activityTasklist);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        checkDeadlines(list);

        // Initialize the TimeChecker
        TimeChecker timeChecker = new TimeChecker();

        // Start the time checking thread
        checkTimeLimits(timeChecker);

        Thread screenTimeThread = new Thread(ScreentimeTracker::track);
        screenTimeThread.setDaemon(true); // Ensures it stops when the app exits
        screenTimeThread.start();

    }

    public static void main(String[] args) {
        launch();
    }

    void checkDeadlines(ToDoList list){
        Thread deadlineThread = new Thread(() -> {
            while (true) {
                try {
                    // Sleep for some time before checking again (e.g., every minute)
                    Thread.sleep(300000); // 60 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.deadLineChecker();
            }
        });
        deadlineThread.setDaemon(true);  // This ensures the thread terminates when the app exits
        deadlineThread.start();
    }

    public static void checkTimeLimits(TimeChecker timeChecker) {
        Thread timeCheckThread = new Thread(() -> {
            while (true) {
                try {
                    // Sleep for some time before checking again (e.g., every minute)
                    Thread.sleep(30000); // 300000 milliseconds = 5 minutes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Reload data to ensure it's up-to-date
                TimeChecker.reloadAllData();
                // Categorize and notify based on the current time data
                timeChecker.categorizeAndNotify();
            }
        });
        timeCheckThread.setDaemon(true);  // This ensures the thread terminates when the app exits
        timeCheckThread.start();
    }
}