package sample;

public class Weather {
    private double temp;
    private double pressure;
    private double humidity;
    private double temp_min;
    private double temp_max;

    public Weather(double temp, double pressure, double humidity, double temp_min, double temp_max) {
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
    }

    public double getTemp() {
        return temp;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    @Override
    public String toString() {
        return String.format("%nHumidity: %s%nPressure: %s%nTempMin: %s%nTempMax: %s%nTemp: %s%n", getHumidity(), getPressure(), getTemp_min(), getTemp_max(), getTemp());
    }
}

