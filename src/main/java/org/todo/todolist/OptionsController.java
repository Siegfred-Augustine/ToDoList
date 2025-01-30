package org.todo.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class OptionsController {
    @FXML
    private TextField leisureTX;
    @FXML
    private TextField productiveTX;
    @FXML
    private TextField purposedMax;
    @FXML
    private TextField purposedMin;
    @FXML
    private Button saveChanges;
    
    private MainController mcontroller;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // Time settings storage
    private LocalTime leisureTime;
    private LocalTime productiveTime;
    private LocalTime purposedMaxTime;
    private LocalTime purposedMinTime;

    public void setMainControl(MainController control){
        mcontroller = control;
    }
    
    @FXML
    public void initialize() {
        // Add listeners to all time input fields
        setupTimeField(leisureTX);
        setupTimeField(productiveTX);
        setupTimeField(purposedMax);
        setupTimeField(purposedMin);
        
        // Setup save button handler
        saveChanges.setOnAction(event -> saveTimeSettings());
    }
    
    private void setupTimeField(TextField field) {
        // Add input formatter while typing
        field.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String text = field.getText();
            if (!text.matches("^[0-9:]*$")) {
                event.consume();
            }
            
            // Auto-add colons after hours and minutes
            if (text.length() == 2 || text.length() == 5) {
                field.setText(text + ":");
                field.positionCaret(text.length() + 1);
            }
        });
        
        // Validate on focus loss
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // On focus lost
                validateAndFormatTimeField(field);
            }
        });
    }
    
    private void validateAndFormatTimeField(TextField field) {
        try {
            String text = field.getText();
            LocalTime time = LocalTime.parse(text, TIME_FORMATTER);
            field.setText(time.format(TIME_FORMATTER));
            field.setStyle("-fx-text-fill: black;"); // Reset to normal if valid
        } catch (DateTimeParseException e) {
            field.setStyle("-fx-text-fill: red;"); // Show error state
        }
    }
    
    private void saveTimeSettings() {
        try {
            // Parse and store all time values
            leisureTime = LocalTime.parse(leisureTX.getText(), TIME_FORMATTER);
            productiveTime = LocalTime.parse(productiveTX.getText(), TIME_FORMATTER);
            purposedMaxTime = LocalTime.parse(purposedMax.getText(), TIME_FORMATTER);
            purposedMinTime = LocalTime.parse(purposedMin.getText(), TIME_FORMATTER);
            
            // Validate time relationships if needed
            if (purposedMinTime.isAfter(purposedMaxTime)) {
                throw new IllegalArgumentException("Minimum time cannot be greater than maximum time");
            }
            
            // You can store these values in your main controller or a settings manager
            notifyMainController();
            
        } catch (DateTimeParseException e) {
            showError("Invalid time format. Please use HH:mm:ss");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }
    
    private void showError(String message) {
        // You can implement this using JavaFX Alert or your preferred error display method
        System.err.println(message); // Replace with proper error display
    }
    
    private void notifyMainController() {
        if (mcontroller != null) {
            // Add methods to MainController to handle these time settings
            // For example:
            mcontroller.updateTimeSettings(leisureTime, productiveTime, purposedMaxTime, purposedMinTime);
        }
    }
    
    // Getter methods for stored times
    public LocalTime getLeisureTime() {
        return leisureTime;
    }
    
    public LocalTime getProductiveTime() {
        return productiveTime;
    }
    
    public LocalTime getPurposedMaxTime() {
        return purposedMaxTime;
    }
    
    public LocalTime getPurposedMinTime() {
        return purposedMinTime;
    }
}