package sample;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.bson.Document;
import org.controlsfx.control.textfield.TextFields;

public class Controller {

    private DatabaseConnection dbconn = new DatabaseConnection();
    private WeatherConnection weatherConnection = new WeatherConnection();
    private ArrayList possibleWordSet = new ArrayList();
    public String collectionNameForImport;

    private String collectionName = getCollectionName();
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
    private Button newDoc;

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
        newDoc.setDisable(true);
        listenKey();
    }


    @FXML
    void playPause(ActionEvent event) {
        if(weatherConnection.isRunning){
            playPause.setImage(new Image(getClass().getResourceAsStream("assets/img/play.png")));
            playPause.setCache(true);
            playPause.setFitHeight(50);
            playPause.setFitWidth(41);
            weatherConnection.interrupt();
            System.out.println("WeatherConnection interrupted");
            stopBttn.setDisable(false);
        } else {
            playPause.setImage(new Image(getClass().getResourceAsStream("assets/img/pause.png")));
            playPause.setCache(true);
            playPause.setFitHeight(50);
            playPause.setFitWidth(41);
            weatherConnection.resume();
            System.out.println("WeatherConnection started");
        }
    }

    @FXML
    void stop(ActionEvent event) {
        weatherConnection.stop();
        /*
        ????????????????
         */
        UIData observersToRemove = new UIData(curTemp,curHum,curPress,chart,measurements,stDev,minTempInTime,maxTempInTime,collectionName);
        weatherConnection.removeObserver(observersToRemove);
        observersToRemove.clearData();
        playPauseBttn.setDisable(true);
        System.out.println("Process has been stopped.");
        stopBttn.setDisable(true);
        newDoc.setDisable(false);
    }

    private void addObservers(){
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
                            weatherConnection.killObservers();
                            addObservers();
                            weatherConnection.getWeatherByID(dbconn.getSelectedCityID(possibleWordSet.lastIndexOf(searchField.getText())),collectionName,searchField.getText());
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

    @FXML
    void newMeasurementInit(ActionEvent event) {
        LocalDateTime date = LocalDateTime.now();
        this.collectionName = "date_" + date.getYear() + date.getMonth().getValue() + date.getDayOfMonth() + "_" + date.getHour() + date.getMinute() + date.getSecond();
        System.out.println(collectionName);
        searchField.setDisable(false);
        newDoc.setDisable(true);
    }

    void displayImportedData(String selectedData){
        UICalculations uiCalculations = new UICalculations();
        Document cursor = dbconn.getLatestDataFromDB(selectedData);
        ArrayList<String> OXData = dbconn.getOXData(selectedData);
        ArrayList<Double> OYData = dbconn.getOYData(selectedData);

        XYChart.Series series = new XYChart.Series();
        series.setName("Temperature in time");

        for (int i = 0; i < OXData.size(); i++) {
            series.getData().add(new XYChart.Data(OXData.get(i), OYData.get(i)));
        }

        System.out.println(cursor.get("humidity").toString());

        getCurHum().setText(cursor.get("humidity").toString());
        curTemp.setText(cursor.get("temp").toString());
        curPress.setText(cursor.get("pressure").toString());
        stDev.setText(Double.toString(uiCalculations.getStDeviation(OYData)));
        measurements.setText(Integer.toString(dbconn.getCollectionSize(selectedData)));
        minTempInTime.setText(Double.toString(Collections.min((Collection<? extends Double>) OYData)));
        maxTempInTime.setText(Double.toString(Collections.max((Collection<? extends Double>) OYData)));
        chart.getData().clear();
        chart.getData().add(series);

    }

    public Label getCurHum() {
        return curHum;
    }

    @FXML
    void addWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("measurement.fxml"));
            Parent root2 = loader.load();

            Measurement measurement = loader.getController();
            System.out.println(measurement.getSelect());
            measurement.setCustomerSelectCallback(customer -> {
                displayImportedData(customer);
            });

            Stage stage2 = new Stage();
            stage2.setTitle("Chose the measurement");
            stage2.setScene(new Scene(root2, 450, 450));
            stage2.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
