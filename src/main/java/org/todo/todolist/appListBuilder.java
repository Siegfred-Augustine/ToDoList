package org.todo.todolist;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class appListBuilder {
    String[] appCategory = new String[]{"Default (Ignored)", "Leisure"};
    HBox box = new HBox();
    Label appName = new Label("Sample app");
    ComboBox<String> choices = new ComboBox<>();
    Button addApp = new Button("Add");

    appListBuilder(String name, VBox vbox){
        choices.getItems().addAll(appCategory);
        appName.setText(name);
        addApp.setOnAction(Action -> appAdd());
        box.getChildren().addAll(appName, choices, addApp);
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        box.setMaxHeight(35);
        box.setMinHeight(35);
        box.setMinWidth(300);
        box.setMaxWidth(300);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #d1c9c5; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-thickness:2");
        addApp.getStyleClass().add("button-hover-effect3");
        choices.setStyle("-fx-background-color:#ffffff");
        vbox.getChildren().addAll(box);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.TOP_CENTER);
    }
    void appAdd(){
        String choice = choices.getValue();
        switch (choice){
            case "":
        }
        box.getChildren().clear();
    }
}
