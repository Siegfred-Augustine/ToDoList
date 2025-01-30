package org.todo.todolist;

import javafx.scene.control.SpinnerValueFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OptionsController {
    @FXML
    private Spinner<Integer> leisureHours;
    @FXML
    private Spinner<Integer> leisureMinutes;
    @FXML
    private Spinner<Integer> leisureSeconds;
    
    @FXML
    private Spinner<Integer> productiveHours;
    @FXML
    private Spinner<Integer> productiveMinutes;
    @FXML
    private Spinner<Integer> productiveSeconds;
    
    @FXML
    private Spinner<Integer> purposedMaxHours;
    @FXML
    private Spinner<Integer> purposedMaxMinutes;
    @FXML
    private Spinner<Integer> purposedMaxSeconds;
    
    @FXML
    private Spinner<Integer> purposedMinHours;
    @FXML
    private Spinner<Integer> purposedMinMinutes;
    @FXML
    private Spinner<Integer> purposedMinSeconds;
    
    @FXML
    private Button saveChanges;
    
    private MainController mcontroller;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // Time settings storage
    private LocalTime leisureTime;
    private LocalTime productiveTime;
    private LocalTime purposedMaxTime;
    private LocalTime purposedMinTime;

    public void setMainControl(MainController control) {
        mcontroller = control;
    }
    
    @FXML
    public void initialize() {
        // Initialize all spinners
        setupTimeSpinners();
        
        // Setup save button handler
        saveChanges.setOnAction(event -> saveTimeSettings());
    }
    
    private void setupTimeSpinners() {
        // Setup hours spinners (0-23)
        setupHourSpinner(leisureHours);
        setupHourSpinner(productiveHours);
        setupHourSpinner(purposedMaxHours);
        setupHourSpinner(purposedMinHours);
        
        // Setup minutes and seconds spinners (0-59)
        setupMinuteSecondSpinner(leisureMinutes);
        setupMinuteSecondSpinner(leisureSeconds);
        setupMinuteSecondSpinner(productiveMinutes);
        setupMinuteSecondSpinner(productiveSeconds);
        setupMinuteSecondSpinner(purposedMaxMinutes);
        setupMinuteSecondSpinner(purposedMaxSeconds);
        setupMinuteSecondSpinner(purposedMinMinutes);
        setupMinuteSecondSpinner(purposedMinSeconds);
    }
    
    private void setupHourSpinner(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
        
        // Add listener to wrap around values
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                spinner.getValueFactory().setValue(0);
            } else if (newValue > 23) {
                spinner.getValueFactory().setValue(0);
            } else if (newValue < 0) {
                spinner.getValueFactory().setValue(23);
            }
        });
    }
    
    private void setupMinuteSecondSpinner(Spinner<Integer> spinner) {
        // Create value factory for minutes/seconds (0-59)
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
        
        // Add listener to wrap around values
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                spinner.getValueFactory().setValue(0);
            } else if (newValue > 59) {
                spinner.getValueFactory().setValue(0);
            } else if (newValue < 0) {
                spinner.getValueFactory().setValue(59);
            }
        });
    }

    public void setInitialTimes(LocalTime leisure, LocalTime productive, 
                               LocalTime maxTime, LocalTime minTime) {
        // Set hours spinners
        leisureHours.getValueFactory().setValue(leisure.getHour());
        productiveHours.getValueFactory().setValue(productive.getHour());
        purposedMaxHours.getValueFactory().setValue(maxTime.getHour());
        purposedMinHours.getValueFactory().setValue(minTime.getHour());
        
        // Set minutes spinners
        leisureMinutes.getValueFactory().setValue(leisure.getMinute());
        productiveMinutes.getValueFactory().setValue(productive.getMinute());
        purposedMaxMinutes.getValueFactory().setValue(maxTime.getMinute());
        purposedMinMinutes.getValueFactory().setValue(minTime.getMinute());
        
        // Set seconds spinners
        leisureSeconds.getValueFactory().setValue(leisure.getSecond());
        productiveSeconds.getValueFactory().setValue(productive.getSecond());
        purposedMaxSeconds.getValueFactory().setValue(maxTime.getSecond());
        purposedMinSeconds.getValueFactory().setValue(minTime.getSecond());
    }
    
    private LocalTime getTimeFromSpinners(Spinner<Integer> hours, 
                                        Spinner<Integer> minutes, 
                                        Spinner<Integer> seconds) {
        return LocalTime.of(
            hours.getValue(),
            minutes.getValue(),
            seconds.getValue()
        );
    }
    
    private void saveTimeSettings() {
        try {
            // Get time values from spinners
            leisureTime = getTimeFromSpinners(leisureHours, leisureMinutes, leisureSeconds);
            productiveTime = getTimeFromSpinners(productiveHours, productiveMinutes, productiveSeconds);
            purposedMaxTime = getTimeFromSpinners(purposedMaxHours, purposedMaxMinutes, purposedMaxSeconds);
            purposedMinTime = getTimeFromSpinners(purposedMinHours, purposedMinMinutes, purposedMinSeconds);
            
            // Validate time relationships
            if (purposedMinTime.isAfter(purposedMaxTime)) {
                throw new IllegalArgumentException("Minimum time cannot be greater than maximum time");
            }
            
            notifyMainController();
            
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }
    
    private void showError(String message) {
        System.err.println(message); // Replace with proper error display
    }
    
    private void notifyMainController() {
        if (mcontroller != null) {
            mcontroller.updateTimeSettings(leisureTime, productiveTime, purposedMaxTime, purposedMinTime);
        }
    }
    
    // Getter methods
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