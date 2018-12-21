package sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 * Controller for an additional window.
 */
public class Measurement{

    private String select;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> collectionComboBox;

    @FXML
    private Button useCollBttn;

    /**
     * Method is used to display all collection names in a current database.
     * @param event
     */
    @FXML
    void useCollection(ActionEvent event) {
        String selection = collectionComboBox.getSelectionModel().getSelectedItem();
        System.out.println(selection);
        select = selection;
        customerSelectCallback.accept(selection);
    }

    private Consumer<String> customerSelectCallback ;

    public void setCustomerSelectCallback(Consumer<String> callback) {
        this.customerSelectCallback = callback ;
    }

    public String getSelect() {
        return select;
    }

    /**
     * Initialize fetches the database to get all available collections and adds them to a ObservableList.
     */
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
