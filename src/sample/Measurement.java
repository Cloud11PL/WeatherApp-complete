package sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class Measurement {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> collectionComboBox;

    @FXML
    private Button useCollBttn;

    @FXML
    void useCollection(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert collectionComboBox != null : "fx:id=\"collectionComboBox\" was not injected: check your FXML file 'measurement.fxml'.";
        assert useCollBttn != null : "fx:id=\"useCollBttn\" was not injected: check your FXML file 'measurement.fxml'.";
        ArrayList<String> arrayOfCollections = new ArrayList<>();
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoIterable<String> collections = database.listCollectionNames();
        for(String doc : collections){
            if(!doc.equals("cities")){
                arrayOfCollections.add(doc);
            }
        }
        ObservableList<String> options = FXCollections.observableArrayList(arrayOfCollections);
        collectionComboBox.getItems().addAll(options);
    }
}
