package org.todo.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class OptionsController {

    @FXML
    private TextField dynamicTX;

    @FXML
    private TextField leisureTX;

    @FXML
    private TextField productiveTX;

    @FXML
    private TextField purposedMax;

    @FXML
    private TextField purposedMin;

    MainController mcontroller;

    public void setMainControl(MainController control){
        mcontroller = control;
    }
}
