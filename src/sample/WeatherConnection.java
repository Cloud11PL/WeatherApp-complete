package sample;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Separate thread class responsible for API calls and Observers manipulation.
 */
public class WeatherConnection implements Observable, Runnable {

    protected volatile boolean isRunning = false;  //volatile - zmiana od razu zostanie zapisana w pamieci glownej i chache zostaje uaktualniony //protected - dostep maja takze klasy dziedzieczace
    int ID;
    String collectionName;
    DatabaseConnection databaseConnection = new DatabaseConnection();
    private volatile ArrayList<Observer> observers = new ArrayList<>();
    private Thread worker;
    private int interval;
    private String timeStamp;
    private String city;

    public WeatherConnection() {
        interval = 5000;
    }

    public void getWeatherByID(int ID, String collectionName, String city) {
        this.ID = ID;
        this.collectionName = collectionName;
        this.city = city;
    }

    /**
     * getWeather is the main responsible for API calls. It manipulates the output to get the 'main' parameter from the JSON.
     * Next, it creates a variable that consists of current time that will be set as the OX data.
     * The JSON is manipulated, both city names and 'time' are added as parameters to be finally passed to a method that adds the JSON to the database.
     */
    void getWeather() {
        StringBuffer response = new StringBuffer();
        String id = "18ee68ce9ff2ad02a13567268869a245";
        String url = "http://api.openweathermap.org/data/2.5/weather?id=" + ID + "&units=metric&APPID=" + id;

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
        DBProp.getAsJsonObject().addProperty("time", timeStamp);
        DBProp.getAsJsonObject().addProperty("city", city);
        databaseConnection.addToDatabase(collectionName, DBProp);
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

    public void stop() {
        isRunning = false;
    }

    public void killObservers() {
        observers.clear();
    }

    @Override
    public void updateObservers() {
        for (Observer o : observers) {
            getWeather();
            o.update();
        }
    }

    public void interrupt() {
        isRunning = false;
        worker.suspend();
    }

    public void resume() {
        isRunning = true;
        worker.resume();
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                updateObservers();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Failed to complete operation");
            }

        }
    }
}

