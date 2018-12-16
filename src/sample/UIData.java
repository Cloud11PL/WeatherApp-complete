package sample;

import javafx.scene.control.Label;

public class UIData implements Observer{
    private Label curTemp;
    private Label curHum;
    private Label curPress;

    public UIData(Label curTemp, Label curHum, Label curPress) {
        this.curTemp = curTemp;
        this.curHum = curHum;
        this.curPress = curPress;
    }


    @Override
    public void update(String timeStamp) {

    }
}
