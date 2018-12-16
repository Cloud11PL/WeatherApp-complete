package sample;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.textfield.TextFields;

public class Controller {

    private DatabaseConnection dbconn = new DatabaseConnection();
    private WeatherConnection weatherConnection = new WeatherConnection();
    private ArrayList possibleWordSet = new ArrayList();
    private LocalDateTime date = LocalDateTime.now();
    /*
    CollectionName musi sie tworzyc dla nowego pomiaru!!!!!
     */
    private String collectionName = "date_" + date.getYear() + date.getMonth().getValue() + date.getDayOfMonth() + "_" + date.getHour() + date.getMinute() + date.getSecond();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField searchField;

    @FXML
    private LineChart<String, Double> chart;

    @FXML
    private Label curTemp;

    @FXML
    private Label curHum;

    @FXML
    private Label curPress;

    @FXML
    private Label measurements;

    @FXML
    private Label stDev;

    @FXML
    private Label minTempInTime;

    @FXML
    private Label maxTempInTime;

    @FXML
    void initialize() {
        assert chart != null : "fx:id=\"chart\" was not injected: check your FXML file 'sample.fxml'.";
        assert curTemp != null : "fx:id=\"curTemp\" was not injected: check your FXML file 'sample.fxml'.";
        assert curHum != null : "fx:id=\"curHum\" was not injected: check your FXML file 'sample.fxml'.";
        assert curPress != null : "fx:id=\"curPress\" was not injected: check your FXML file 'sample.fxml'.";
        assert measurements != null : "fx:id=\"measurements\" was not injected: check your FXML file 'sample.fxml'.";
        assert stDev != null : "fx:id=\"stDev\" was not injected: check your FXML file 'sample.fxml'.";
        assert minTempInTime != null : "fx:id=\"minTempInTime\" was not injected: check your FXML file 'sample.fxml'.";
        assert maxTempInTime != null : "fx:id=\"maxTempInTime\" was not injected: check your FXML file 'sample.fxml'.";
        listenKey();

        UIData displayCurrent = new UIData(curTemp,curHum,curPress,chart,measurements,stDev,minTempInTime,maxTempInTime,collectionName);
        weatherConnection.addObserver(displayCurrent);
    }

    public void listenKey(){
        searchField.setOnKeyPressed(ke -> {
            if(ke.getCode().equals(KeyCode.ENTER) || searchField.getText().length() > 2){
                System.out.println(searchField.getText());
                possibleWordSet = dbconn.getSuggestedData(searchField.getText());
                System.out.println(possibleWordSet);
                TextFields.bindAutoCompletion(searchField,possibleWordSet).setOnAutoCompleted(event -> {
                            System.out.println(searchField.getText());
                            weatherConnection.getWeatherByID(dbconn.getSelectedCityID(possibleWordSet.lastIndexOf(searchField.getText())),collectionName,searchField.getText());
                            weatherConnection.start();
                        });
            }
        });
    }
}
