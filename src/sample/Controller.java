package sample;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
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

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Main JavaFX class
 */
public class Controller {

    private DatabaseConnection dbconn = new DatabaseConnection();
    private WeatherConnection weatherConnection = new WeatherConnection();
    private ArrayList possibleWordSet = new ArrayList();
    private String collectionName = getCollectionName();
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
    private Button getJsonBttn;

    /**
     * Function returns the collection name for the database. It consists of current date and time.
     * @return
     */
    private String getCollectionName() {
        LocalDateTime date = LocalDateTime.now();
        String collectionName = "date_" + date.getYear() + date.getMonth().getValue() + date.getDayOfMonth() + "_" + date.getHour() + date.getMinute() + date.getSecond();
        return collectionName;
    }

    /**
     * Method retrieves a collection with current data from the database using the current collectionName.
     * Each Document gets the '_id' parameter removed as it is automatically assigned by the Mongo Database and it doesn't add add anything significant for the user.
     * Then it parses it to .json file to be finally saved using JFileChooser (it works only on Windows).
     * @param event - trigger button
     */
    @FXML
    void downloadJson(ActionEvent event) {
        FindIterable<Document> dataForJson = dbconn.getCollection(collectionName);
        BasicDBList list = new BasicDBList();
        for(Document doc : dataForJson){
            doc.remove("_id");
            list.add(doc);
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("/home/me/Documents"));
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try {
                FileWriter fw = new FileWriter(chooser.getSelectedFile()+".json");
                fw.write(JSON.serialize(list));
                fw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

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
        assert getJsonBttn != null : "fx:id=\"maxTempInTime\" was not injected: check your FXML file 'sample.fxml'.";
        getJsonBttn.setDisable(true);
        playPauseBttn.setDisable(true);
        stopBttn.setDisable(true);
        newDoc.setDisable(true);
        listenKey();
    }

    /**
     * The function can be triggered by the user after the measurement starts. It pauses or resumes the Thread according to its actual status.
     * On top of that, playPause change some UI elements.
     * @param event
     */
    @FXML
    void playPause(ActionEvent event) {
        if (weatherConnection.isRunning) {
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

    /**
     * The stop method stops the Thread and changes some UI elements.
     * @param event
     */

    @FXML
    void stop(ActionEvent event) {
        weatherConnection.stop();
        UIData observersToRemove = new UIData(curTemp, curHum, curPress, chart, measurements, stDev, minTempInTime, maxTempInTime, collectionName);
        weatherConnection.removeObserver(observersToRemove);
        observersToRemove.clearData();
        playPauseBttn.setDisable(true);
        System.out.println("Process has been stopped.");
        stopBttn.setDisable(true);
        newDoc.setDisable(false);
        getJsonBttn.setDisable(false);
        addBttn.setDisable(false);
    }

    /**
     * addObservers creates a new UIData instance that has parameters of UI elements and the current collectionName.
     * The method adds the instance as a observer.
     */
    private void addObservers() {
        UIData displayCurrent = new UIData(curTemp, curHum, curPress, chart, measurements, stDev, minTempInTime, maxTempInTime, collectionName);
        weatherConnection.addObserver(displayCurrent);
    }

    /**
     * This method is responsible for showing the user possible name of the cities based on user's input.
     * As user types, user's input becomes a parameter for a function that fetches the database in search of cities that match the query.
     * The response is an array that is a set of possible cities that user can choose.
     *
     * On completion, ThextFields fires functions that fetch the weather, start the worker and make some changes to the UI.
     * For the sake of multiple measurements in one app launch without the need to reload the application, the method also calls a method that deletes all observers.
     */

    public void listenKey() {
        searchField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER) || searchField.getText().length() > 2) {
                System.out.println(searchField.getText());
                possibleWordSet = dbconn.getSuggestedData(searchField.getText());
                System.out.println(possibleWordSet);
                TextFields.bindAutoCompletion(searchField, possibleWordSet).setOnAutoCompleted(event -> {
                    System.out.println(searchField.getText());
                    weatherConnection.killObservers();
                    addObservers();
                    weatherConnection.getWeatherByID(dbconn.getSelectedCityID(possibleWordSet.lastIndexOf(searchField.getText())), collectionName, searchField.getText());
                    weatherConnection.start();
                    playPauseBttn.setDisable(false);
                    playPause.setImage(new Image(getClass().getResourceAsStream("assets/img/pause.png")));
                    playPause.setFitHeight(50);
                    playPause.setFitWidth(41);
                    searchField.setDisable(true);
                    addBttn.setDisable(true);
                });
            }
        });
    }

    /**
     * Method allows user to make a new measurement by creating a new collectionName and assigning it to the variable.
     * @param event
     */
    @FXML
    void newMeasurementInit(ActionEvent event) {
        LocalDateTime date = LocalDateTime.now();
        this.collectionName = "date_" + date.getYear() + date.getMonth().getValue() + date.getDayOfMonth() + "_" + date.getHour() + date.getMinute() + date.getSecond();
        System.out.println(collectionName);
        searchField.setDisable(false);
        newDoc.setDisable(true);
    }

    /**
     * displayImportedData allows user to display data from previous measurements fetched from the database.
     * @param selectedData
     */
    void displayImportedData(String selectedData) {
        UICalculations uiCalculations = new UICalculations();
        Document cursor = dbconn.getLatestDataFromDB(selectedData);
        ArrayList<String> OXData = dbconn.getOXData(selectedData);
        ArrayList<Double> OYData = dbconn.getOYData(selectedData);

        XYChart.Series series = new XYChart.Series();
        series.setName("Temperature in time");

        for (int i = 0; i < OXData.size(); i++) {
            series.getData().add(new XYChart.Data(OXData.get(i), OYData.get(i)));
        }

        curHum.setText(cursor.get("humidity").toString());
        curTemp.setText(cursor.get("temp").toString());
        curPress.setText(cursor.get("pressure").toString());
        stDev.setText(Double.toString(uiCalculations.getStDeviation(OYData)));
        measurements.setText(Integer.toString((int)dbconn.getCollectionSize(selectedData).countDocuments()));
        minTempInTime.setText(Double.toString(Collections.min((Collection<? extends Double>) OYData)));
        maxTempInTime.setText(Double.toString(Collections.max((Collection<? extends Double>) OYData)));
        chart.getData().clear();
        chart.getData().add(series);

        getJsonBttn.setDisable(false);
        stopBttn.setDisable(false);
    }

    /**
     * Method addWindow creates a new window so that the user can choose a collection to fetch measurement data from.
     * Method utilizes 'Customer' https://docs.oracle.com/javase/9/docs/api/java/util/function/Consumer.html
     * Customer is used to get user's selection from the Measurement controller to the main Controller.
     * @param event
     */
    @FXML
    void addWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("measurement.fxml"));
            Parent root2 = loader.load();

            Measurement measurement = loader.getController();
            System.out.println(measurement.getSelect());
            measurement.setCustomerSelectCallback(customer -> {
                this.collectionName = customer;
                displayImportedData(customer);
            });

            Stage stage2 = new Stage();
            stage2.setTitle("Chose the measurement");
            stage2.setScene(new Scene(root2, 450, 450));
            stage2.show();
            addBttn.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
