package lawnlayer;

import processing.core.PImage;
import java.util.ArrayList;
import java.util.Random;

public abstract class Powerup {
    
    protected App app;
    protected PImage sprite;
    protected Level level;
    protected int x;
    protected int y;

    /**
     * Creates a new Powerup object with a specific sprite
     * related to a specific Level object.
     * 
     * @param app       PApplet object
     * @param sprite    PImage object for the mob's sprite
     * @param level     Level object on which the powerup will display
     */
    public Powerup(App app, PImage sprite, Level level) {
        this.app = app;
        this.sprite = sprite;        
        this.level = level;

        this.chooseSpawnPoint();
    }

    /**
     * Chooses a random soil tile for the powerup to spawn at.
     */
    protected void chooseSpawnPoint() {
        ArrayList<int[]> soilTiles = this.level.returnMaterialTiles(Material.SOIL);
        Random random = new Random();
        int randomInt = random.nextInt(soilTiles.size());
        int[] randomSoilPixel = soilTiles.get(randomInt);

        this.x = randomSoilPixel[0];
        this.y = randomSoilPixel[1];
    }

    /**
     * Places the powerup sprite image on the Level object at the random
     * spawn point decided in chooseSpawnPoint().
     */
    public void draw() {
        this.app.currentLevel.updateMaterialMatrix(this.x, this.y, Material.POWERUP);
        this.app.image(this.sprite, this.x, this.y);
    }

    /**
     * Reverts the process conducted in draw().
     */
    public void undraw() {
        this.app.currentLevel.updateMaterialMatrix(this.x, this.y, Material.SOIL);
    }

    /**
     * Defines the powerup's behaviour upon being activated.
     */
    public abstract void activate();
    
    /**
     * Reverses the process in activate();
     */
    public abstract void deactivate();

}
