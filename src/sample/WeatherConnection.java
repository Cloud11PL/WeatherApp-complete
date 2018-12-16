package sample;

import com.google.gson.*;
import com.mongodb.DB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;

public class WeatherConnection implements Observable, Runnable {

    int ID;
    String collectionName;

    private volatile ArrayList<Observer> observers = new ArrayList<>();
    private Thread worker;
    protected volatile boolean isRunning = false;  //volatile - zmiana od razu zostanie zapisana w pamieci glownej i chache zostaje uaktualniony //protected - dostep maja takze klasy dziedzieczace
    private int interval;
    private String timeStamp;
    DatabaseConnection databaseConnection = new DatabaseConnection();

    public WeatherConnection() {
        interval = 1000;
    }

    public void getWeatherByID(int ID, String collectionName){
        this.ID = ID;
        this.collectionName = collectionName;
        getWeather();
    }

    void getWeather(){
        StringBuffer response = new StringBuffer();
        String id = "18ee68ce9ff2ad02a13567268869a245";
        String url = "http://api.openweathermap.org/data/2.5/weather?id="+ID+"&units=metric&APPID="+id;

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("Weather for: " + ID);

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(String.valueOf(response)).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object main = json.getAsJsonObject("main");
        Weather e3 = gson.fromJson((JsonElement) main, Weather.class);

        LocalTime time = LocalTime.now();
        timeStamp = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();

        JsonElement DBProp = gson.toJsonTree(main);
        DBProp.getAsJsonObject().addProperty("time",timeStamp);

        System.out.println(DBProp);
        System.out.println(e3);
        System.out.println(timeStamp);

    }

    public void start() {
        worker = new Thread(this, "Clock thread");
        worker.start();

    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observers.contains(observer)) observers.remove(observer);
    }

    @Override
    public void updateObservers() {
        for (Observer o : observers) {
            getWeather();
            o.update(timeStamp);
        }
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {

            try {
                System.out.println(LocalTime.now());
                Thread.sleep(interval);
                updateObservers();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Failed to complete operation");
            }

        }
    }
}

