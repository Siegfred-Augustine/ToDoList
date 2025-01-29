package org.todo.todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        mControl.setList(list);

        list.taskList = SaveController.loadTasksFromCSV("tasks.csv");

        list.activityTasklist = SaveController.loadActivitiesFromCSV("activities.csv");

        list.eventsList = SaveController.loadEventsFromCSV("events.csv");


        mControl.taskInitializer(list.taskList);
        mControl.eventInitializer(list.eventsList);
        mControl.activityInitializer(list.activityTasklist);


        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        list.deadLineChecker();
    }

    public static void main(String[] args) {
        launch();
    }
}