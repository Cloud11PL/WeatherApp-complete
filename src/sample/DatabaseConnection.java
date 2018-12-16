package sample;

import com.google.gson.JsonElement;
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

    public void addToDatabase(String collectionName, JsonElement currentWeather){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        Document doc = Document.parse(currentWeather.toString());
        collection.insertOne(doc);
    }

    public Document getLatestDataFromDB(String collectionName){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("weatherApp");
        MongoCollection collection = database.getCollection(collectionName);
        Document myDoc = (Document)collection.find().sort(new BasicDBObject("time",-1)).first();
        return myDoc;
    }

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



}
