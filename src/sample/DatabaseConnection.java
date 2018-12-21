package sample;

import com.google.gson.JsonElement;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import static com.mongodb.client.model.Filters.regex;


/**
 * Class containing methods for connecting, adding and getting data from database.
 */
public class DatabaseConnection {

    ArrayList<String> possibleCities = new ArrayList();
    ArrayList<Integer> possibleCitiesID = new ArrayList();

    /**
     * Method is used to create an array of possible cities user wants to choose.
     * It makes a connection with the database to find Documents that match the query.
     * The method also assigns city IDs to a separate ArrayList that will be later used to fetch the data from the database.
     * @param input user input
     * @return ArrayList that consists of city names.
     */
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

    /**
     * Returns city ID
     * @param index Index of selected by the user city
     * @return ID of a city.
     */
    public int getSelectedCityID(int index){
        return possibleCitiesID.get(index);
    }

    /**
     * Method adds a JSON element to the database based on the collectionName.
     * @param collectionName Name of the current collection
     * @param currentWeather JSON element that consists of current weather
     */
    public void addToDatabase(String collectionName, JsonElement currentWeather){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        Document doc = Document.parse(currentWeather.toString());
        collection.insertOne(doc);
    }

    /**
     * Method returns the last inserted document to the collection in the database.
     * @param collectionName Name of the current collection
     * @return Latest document in the collection.
     */
    public Document getLatestDataFromDB(String collectionName){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        Document myDoc = (Document)collection.find().sort(new BasicDBObject("time",-1)).first();
        return myDoc;
    }

    /**
     * Method uses the collectionName to fetch all documents in the collection.
     * Next, it manipulates them to get the 'time' parameter that will be assigned to the OXData ArrayList.
     * @param collectionName Name of the current collection
     * @return ArrayList that consists of all 'time' parameters in the collection.
     */
    public ArrayList getOXData(String collectionName){
        ArrayList<String> OXData = new ArrayList();
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        FindIterable<Document> cursor = collection.find();
        for(Document doc : cursor){
            OXData.add(doc.get("time").toString());
        }
        return OXData;
    }

    /**
     * Method uses the collectionName to fetch all documents in the collection.
     * Next, it manipulates them to get the 'temp' parameter that will be assigned to the OYData ArrayList.
     * Some location have data saved with Integers and others with Double variables, therefore it is necessary to check what type of variable the function is going to assign to the ArrayList.
     * If the 'temp' is a Double it is saved as a Double but when temperature is a Integer it is parsed to 'Double' and assigned to the ArrayList.
     * @param collectionName Name of the current collection
     * @return ArrayList that consists of all 'temp' parameters in the collection.
     */
    public ArrayList getOYData(String collectionName){
        ArrayList<Double> OYData = new ArrayList();
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        FindIterable<Document> cursor = collection.find();
        for(Document doc : cursor){
            if(doc.get("temp") instanceof Double){
                OYData.add((Double)doc.get("temp"));
            } else if (doc.get("temp") instanceof Integer){
                OYData.add(((Integer) doc.get("temp")).doubleValue());
            }
        }
        return OYData;
    }

    /**
     * Method uses the collectionName to fetch the collection.
     * @param collectionName Name of the current collection
     * @return MongoCollection based on the collectionName
     */
    public MongoCollection getCollectionSize(String collectionName){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        return collection;
    }

    /**
     * Method uses the collectionName to fetch the collection.
     * @param collectionName Name of the current collection
     * @return FindIterable containing all documents in the collection based on the collectionName
     */
    public FindIterable<Document> getCollection(String collectionName){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        FindIterable<Document> cursor = collection.find();
        return cursor;
    }



}
