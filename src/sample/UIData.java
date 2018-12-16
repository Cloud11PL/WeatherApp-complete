package sample;

import javafx.application.Platform;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.bson.Document;

import java.util.ArrayList;

public class UIData implements Observer{
    private Label curTemp;
    private Label curHum;
    private Label curPress;
    private String collectionName;
    private LineChart chart;

    DatabaseConnection databaseConnection = new DatabaseConnection();

    public UIData(Label curTemp, Label curHum, Label curPress, LineChart chart, String collectionName) {
        this.curTemp = curTemp;
        this.curHum = curHum;
        this.curPress = curPress;
        this.collectionName = collectionName;
        this.chart = chart;
    }

    @Override
    public void update() {
        Document cursor = databaseConnection.getLatestDataFromDB(collectionName);
        ArrayList<String> OXData = databaseConnection.getOXData(collectionName);
        ArrayList<Double> OYData = databaseConnection.getOYData(collectionName);

        XYChart.Series series = new XYChart.Series();
        series.setName("Temperature in time");

        for(int i = 0; i < OXData.size(); i++){
            series.getData().add(new XYChart.Data(OXData.get(i), OYData.get(i)));
        }

        /*
        Platform.runLater(()-> curHum.setText(cursor.get("humidity").toString()));
        Platform.runLater(()-> curTemp.setText(cursor.get("temp").toString()));
        Platform.runLater(()-> curPress.setText(cursor.get("pressure").toString()));
        */

        Platform.runLater(()-> {
            curHum.setText(cursor.get("humidity").toString());
            curTemp.setText(cursor.get("temp").toString());
            curPress.setText(cursor.get("pressure").toString());
            chart.getData().clear();
            chart.getData().add(series);
        });

    }
}
