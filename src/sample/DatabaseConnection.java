package sample;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import static com.mongodb.client.model.Filters.regex;



public class DatabaseConnection {

    ArrayList<String> possibleCities = new ArrayList();
    ArrayList<Integer> possibleCitiesID = new ArrayList();

    public ArrayList getSuggestedData(String input){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection("cities");
        FindIterable<Document> cursor = collection.find(regex("name", input+".*"));
        for(Document doc : cursor){
            if(!possibleCities.contains(doc.get("name")+" "+doc.get("country"))){
                possibleCities.add(doc.get("name")+" "+doc.get("country"));
                possibleCitiesID.add((int)doc.get("id"));
            }
        }
        return possibleCities;
    }

    public int getSelectedCityID(int index){
        return possibleCitiesID.get(index);
    }

    public void addToDatabase(String collectionName){

    }



}
