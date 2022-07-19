package lawnlayer;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest extends App {
    @Test
    public void MyFirstTest() {
        PApplet.runSketch(new String[] {"App"}, this);
        delay(1000);
        noLoop();
    }
}