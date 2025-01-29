package org.todo.todolist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

//import static org.todo.todolist.Main.list;

public class MainController implements Initializable {
    @FXML
    private ScrollPane activitiesPane;

    @FXML
    private Button sortBy;

    @FXML
    private Button addTask;

    @FXML
    private ScrollPane eventsPane;

    @FXML
    private Button options;

    @FXML
    private ScrollPane screenTimePane;

    @FXML
    private ScrollPane tasksPane;

    @FXML
    private VBox vboxAT;

    @FXML
    private VBox vboxEV;
    @FXML
    private Tab tasksTab;

    @FXML
    private VBox vboxST;

    @FXML
    private VBox vboxTK;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private ImageView tabImageView;

    @FXML
    private ImageView gif;

    ToDoList list;

    private boolean sortImportanceToggle = false;

    @FXML
    void sort(ActionEvent event){
        sortImportance();
        System.out.println("task sorted");
    }
    public void setList(ToDoList list){
        this.list = list;
    }

    @FXML
    public void toggle(ActionEvent event){
        if(sortImportanceToggle) {
            sortByDeadline(); // Sort by deadline when toggling off
            sortBy.setText("Sort By Priority");
        } else {
            sortImportance(); // Sort by importance when toggling on
            sortBy.setText("Sort By Deadline");
        }
        sortImportanceToggle = !sortImportanceToggle; // Toggle the flag
    }

    public void addTaskPane(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("addTaskEntry.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 430, 470);

        addTaskEntryController tControl = fxmlLoader.getController();
        tControl.setMainController(this);
        tControl.list = list;

        Stage stage = new Stage();

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    public void listUpdater(Tasks task, ToDoList list) {
        list.addTask(task);
        if(sortImportanceToggle)
            sortImportance();
        else
            sortByDeadline();
        SaveController.saveTasksToCSV(list.taskList, "tasks.csv");
    }
    public void listUpdater(Activity activity,ToDoList list) {
        list.addActivity(activity);

        if(sortImportanceToggle)
            sortImportance();
        else
            sortByDeadline();
        SaveController.saveActivityToCSV(list.activityTasklist, "activities.csv");
    }
    public void listUpdater(Events event,ToDoList list) {
        list.addEvent(event);

        if(sortImportanceToggle)
            sortImportance();
        else
            sortByDeadline();
        SaveController.saveEventsToCSV(list.eventsList, "events.csv");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabImageView.setImage(new Image(getClass().getResource("/images/Task-Backsplash.png").toExternalForm()));

        mainTabPane.getSelectionModel().select(0);

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                switch (newTab.getText().trim()) {
                    case "Tasks":
                        tabImageView.setImage(new Image(getClass().getResource("/images/Task-Backsplash.png").toExternalForm()));
                        break;
                    case "Activities":
                        tabImageView.setImage(new Image(getClass().getResource("/images/Activities.png").toExternalForm()));
                        break;
                    case "Events":
                        tabImageView.setImage(new Image(getClass().getResource("/images/Untitled-3Events.png").toExternalForm()));
                        break;
                    case "Screen Time":
                        tabImageView.setImage(new Image(getClass().getResource("/images/Untitled-3Screentime.png").toExternalForm()));
                        break;
                    default:
                        tabImageView.setImage(null);
                }
            }

            if (oldTab != null) {
                oldTab.getStyleClass().remove("tab-highlighted");
            }
            if (newTab != null) {
                newTab.getStyleClass().add("tab-highlighted");
            }
        });

        for (Tab tab : mainTabPane.getTabs()) {
            tab.getStyleClass().remove("tab-highlighted");
        }
        for(int i = 0; i<5; i++){
            appListBuilder builder = new appListBuilder("Sample.exe", vboxST);
        }
    }

    public void taskInitializer(ArrayList<Tasks> task) {
        for(Tasks t : task){
            TaskBuilder build = new TaskBuilder(t, list, vboxTK, gif);
            build.addBox(vboxTK);
        }
        if(sortImportanceToggle)
            sortImportance();
        else
            sortByDeadline();
        SaveController.saveTasksToCSV(list.taskList, "tasks.csv");
    }
    public void activityInitializer(ArrayList<Activity> activity) {
        for(Activity a : activity){
            TaskBuilder build = new TaskBuilder(a, list, vboxAT, gif);
            build.addBox(vboxAT);
        }
        if(sortImportanceToggle)
            sortImportance();
        else
            sortByDeadline();
        SaveController.saveActivityToCSV(list.activityTasklist, "activities.csv");
    }
    public void eventInitializer(ArrayList<Events> event) {
        for(Events e : event){
            TaskBuilder build = new TaskBuilder(e, list, vboxEV, gif);
            build.addBox(vboxEV);
        }
        if(sortImportanceToggle)
            sortImportance();
        else
            sortByDeadline();
        SaveController.saveEventsToCSV(list.eventsList, "events.csv");
    }

    public void sortImportance(){
        System.out.println("Sorting");
        list.sortByImportance();

        vboxTK.getChildren().clear();
        vboxAT.getChildren().clear();
        vboxEV.getChildren().clear();

        for(Tasks t : list.taskList){
            TaskBuilder build = new TaskBuilder(t, list, vboxTK, gif);
            build.addBox(vboxTK);
        }
        for(Activity a : list.activityTasklist){
            TaskBuilder build = new TaskBuilder(a, list, vboxAT, gif);
            build.addBox(vboxAT);
        }
        for(Events e : list.eventsList){
            TaskBuilder build = new TaskBuilder(e, list, vboxEV, gif);
            build.addBox(vboxEV);
        }
        SaveController.saveTasksToCSV(list.taskList, "tasks.csv");
        SaveController.saveEventsToCSV(list.eventsList, "events.csv");
        SaveController.saveActivityToCSV(list.activityTasklist, "activity.csv");
    }

    void sortByDeadline(){
        list.sortByDeadline();

        vboxTK.getChildren().clear();
        vboxAT.getChildren().clear();
        vboxEV.getChildren().clear();

        for(Tasks t : list.taskList){
            TaskBuilder build = new TaskBuilder(t, list, vboxTK, gif);
            build.addBox(vboxTK);
        }
        for(Activity a : list.activityTasklist){
            TaskBuilder build = new TaskBuilder(a, list, vboxAT, gif);
            build.addBox(vboxAT);
        }
        for(Events e : list.eventsList){
            TaskBuilder build = new TaskBuilder(e, list, vboxEV, gif);
            build.addBox(vboxEV);
        }
        SaveController.saveTasksToCSV(list.taskList, "tasks.csv");
        SaveController.saveEventsToCSV(list.eventsList, "events.csv");
        SaveController.saveActivityToCSV(list.activityTasklist, "activities.csv");
    }
}

