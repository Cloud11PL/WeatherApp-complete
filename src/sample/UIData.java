package sample;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class UIData implements Observer {
    DatabaseConnection databaseConnection = new DatabaseConnection();
    UICalculations uiCalculations = new UICalculations();
    private Label curTemp;
    private Label curHum;
    private Label curPress;
    private String collectionName;
    private LineChart chart;
    private Label measurements;
    private Label stDev;
    private Label minTempInTime;
    private Label maxTempInTime;

    public UIData(Label curTemp, Label curHum, Label curPress, LineChart chart, Label measurements, Label stDev, Label minTempInTime, Label maxTempInTime, String collectionName) {
        this.curTemp = curTemp;
        this.curHum = curHum;
        this.curPress = curPress;
        this.collectionName = collectionName;
        this.chart = chart;
        this.measurements = measurements;
        this.stDev = stDev;
        this.minTempInTime = minTempInTime;
        this.maxTempInTime = maxTempInTime;
    }

    @Override
    public void update() {
        Document cursor = databaseConnection.getLatestDataFromDB(collectionName);
        ArrayList<String> OXData = databaseConnection.getOXData(collectionName);
        ArrayList<Double> OYData = databaseConnection.getOYData(collectionName);

        XYChart.Series series = new XYChart.Series();
        series.setName("Temperature in time");

        for (int i = 0; i < OXData.size(); i++) {
            series.getData().add(new XYChart.Data(OXData.get(i), OYData.get(i)));
        }

        Platform.runLater(() -> {
            curHum.setText(cursor.get("humidity").toString());
            curTemp.setText(cursor.get("temp").toString());
            curPress.setText(cursor.get("pressure").toString());
            stDev.setText(Double.toString(uiCalculations.getStDeviation(OYData)));
            measurements.setText(Integer.toString(databaseConnection.getCollectionSize(collectionName)));
            minTempInTime.setText(Double.toString(Collections.min((Collection<? extends Double>) OYData)));
            maxTempInTime.setText(Double.toString(Collections.max((Collection<? extends Double>) OYData)));
            chart.getData().clear();
            chart.getData().add(series);
        });

    }

    public void displayDataFromDB() {
        Document cursor = databaseConnection.getLatestDataFromDB(collectionName);
        ArrayList<String> OXData = databaseConnection.getOXData(collectionName);
        ArrayList<Double> OYData = databaseConnection.getOYData(collectionName);

        XYChart.Series series = new XYChart.Series();
        series.setName("Temperature in time");

        for (int i = 0; i < OXData.size(); i++) {
            series.getData().add(new XYChart.Data(OXData.get(i), OYData.get(i)));
        }

        Platform.runLater(() -> {
            curHum.setText(cursor.get("humidity").toString());
            curTemp.setText(cursor.get("temp").toString());
            curPress.setText(cursor.get("pressure").toString());
            stDev.setText(Double.toString(uiCalculations.getStDeviation(OYData)));
            measurements.setText(Integer.toString(databaseConnection.getCollectionSize(collectionName)));
            minTempInTime.setText(Double.toString(Collections.min((Collection<? extends Double>) OYData)));
            maxTempInTime.setText(Double.toString(Collections.max((Collection<? extends Double>) OYData)));
            chart.getData().clear();
            chart.getData().add(series);
        });

    }

    public void clearData() {
        Platform.runLater(() -> {
            curHum.setText("");
            curTemp.setText("");
            curPress.setText("");
            stDev.setText("");
            measurements.setText("");
            minTempInTime.setText("");
            maxTempInTime.setText("");
            chart.getData().clear();
        });
    }
}
