package sample;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.textfield.TextFields;

public class Controller {

    private DatabaseConnection dbconn = new DatabaseConnection();
    private WeatherConnection weatherConnection = new WeatherConnection();
    private ArrayList possibleWordSet = new ArrayList();
    /*
    CollectionName musi sie tworzyc dla nowego pomiaru!!!!!
     */
    private final String initialCollectionName = getCollectionName();
    private String getCollectionName(){
        LocalDateTime date = LocalDateTime.now();
        String collectionName = "date_" + date.getYear() + date.getMonth().getValue() + date.getDayOfMonth() + "_" + date.getHour() + date.getMinute() + date.getSecond();
        return collectionName;
    }

    @FXML
    private Button addBttn;

    @FXML
    private ImageView bttnAdd;

    @FXML
    private Button playPauseBttn;

    @FXML
    private ImageView playPause;

    @FXML
    private Button stopBttn;

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
        playPauseBttn.setDisable(true);
        stopBttn.setDisable(true);
        listenKey();

        UIData displayCurrent = new UIData(curTemp,curHum,curPress,chart,measurements,stDev,minTempInTime,maxTempInTime,initialCollectionName);
        weatherConnection.addObserver(displayCurrent);
    }

    @FXML
    void add(ActionEvent event) {

    }

    @FXML
    void playPause(ActionEvent event) {
        if(weatherConnection.isRunning){
            playPause.setImage(new Image(getClass().getResourceAsStream("assets/img/play.png")));
            playPause.setCache(true);
            playPause.setFitHeight(50);
            playPause.setFitWidth(41);
            weatherConnection.stop();
            stopBttn.setDisable(false);
        } else {
            playPause.setImage(new Image(getClass().getResourceAsStream("assets/img/pause.png")));
            playPause.setCache(true);
            playPause.setFitHeight(50);
            playPause.setFitWidth(41);
            weatherConnection.start();
        }
    }

    @FXML
    void stop(ActionEvent event) {
        weatherConnection.stop();
        UIData observersToRemove = new UIData(curTemp,curHum,curPress,chart,measurements,stDev,minTempInTime,maxTempInTime,initialCollectionName);
        weatherConnection.removeObserver(observersToRemove);
        searchField.setDisable(false);
        playPauseBttn.setDisable(true);
        System.out.println("Process has been stopped.");
        stopBttn.setDisable(true);
    }



    public void listenKey(){
        searchField.setOnKeyPressed(ke -> {
            if(ke.getCode().equals(KeyCode.ENTER) || searchField.getText().length() > 2){
                System.out.println(searchField.getText());
                possibleWordSet = dbconn.getSuggestedData(searchField.getText());
                System.out.println(possibleWordSet);
                TextFields.bindAutoCompletion(searchField,possibleWordSet).setOnAutoCompleted(event -> {
                            System.out.println(searchField.getText());
                            weatherConnection.getWeatherByID(dbconn.getSelectedCityID(possibleWordSet.lastIndexOf(searchField.getText())),initialCollectionName,searchField.getText());
                            weatherConnection.start();
                            playPauseBttn.setDisable(false);
                            playPause.setImage(new Image(getClass().getResourceAsStream("assets/img/pause.png")));
                            playPause.setFitHeight(50);
                            playPause.setFitWidth(41);
                            searchField.setDisable(true);
                });
            }
        });
    }
}
