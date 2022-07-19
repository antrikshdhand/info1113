package lawnlayer;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PFont;

public class App extends PApplet {

    public final int WIDTH = 1280;
    public final int HEIGHT = 720;
    public final int SPRITESIZE = 20;
    public final int TOPBAR = 80;
    public final int FPS = 60;

    public String configPath;
    public Level[] levels;
    public int numberOfLives;
    public int currentLevelNumber;
    public Level currentLevel;
    public Player player;
    public Boolean won;
    
	public PImage grass;
    public PImage concrete;
    public PImage worm;
    public PImage beetle;
    public PImage soil;
    public PImage ball;
    public PImage youDied;
    public PImage youWin;
    public PImage schoolZone;
    public PImage sonic;

    public App() {
        this.configPath = "config.json";
        this.currentLevelNumber = 0;

        this.won = false;
    }

    /**
     * Initialise the setting of the window size.
    */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and levels.
    */
    public void setup() {

        // Set graphics
        frameRate(FPS);
        PFont myFont = createFont("monospaced", 28, true);
        textFont(myFont);

        // Load images during setup
		this.grass = loadImage(this.getClass().getResource("grass.png").getPath());
        this.concrete = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        this.soil = loadImage(this.getClass().getResource("soil.png").getPath());
        this.worm = loadImage(this.getClass().getResource("worm.png").getPath());
        this.beetle = loadImage(this.getClass().getResource("beetle.png").getPath());
        this.ball = loadImage(this.getClass().getResource("ball.png").getPath());
        this.youDied = loadImage(this.getClass().getResource("you_died.png").getPath());
        this.youWin = loadImage(this.getClass().getResource("you_win.png").getPath());
        this.schoolZone = loadImage(this.getClass().getResource("school_zone.png").getPath());
        this.sonic = loadImage(this.getClass().getResource("sonic.png").getPath());

        // Create player
        this.player = new Player(this, this.ball);

        // Reading in JSON and creating Level array
        JSONObject config = loadJSONObject("config.json");
        JSONArray levelsJSON = config.getJSONArray("levels");
        int numberOfLevels = levelsJSON.size();
        
        numberOfLives = config.getInt("lives"); // class variable
        levels = new Level[numberOfLevels]; // class variable
        for (int i = 0; i < numberOfLevels; i++) {
            JSONObject level_i_JSON = levelsJSON.getJSONObject(i);

            String filePath = level_i_JSON.getString("outlay");
            double goal = level_i_JSON.getDouble("goal") * 100;
            JSONArray enemiesJSON = level_i_JSON.getJSONArray("enemies");

            Level level_i = new Level(this, filePath, goal, enemiesJSON);
            levels[i] = level_i;
        }

    }

    /**
     * Draw all elements in the game by current frame including text. 
     * This method also controls win/lose scenarios.
    */
    public void draw() {

        if (this.won) {
            background(this.youWin);
            return;
        }

        if (this.numberOfLives <= 0) {
            background(this.youDied);
            return;
        }

        background(50, 26, 21); // brown background

        this.currentLevel = this.levels[currentLevelNumber];
        currentLevel.renderLevel(); 
        
        this.writeText();

        // update and draw
        this.player.tick();
        this.player.draw();
        this.currentLevel.draw();
        
    }

    private void writeText() {

        this.fill(255); // white text

        // print text: current level
        String levelTextToPrint = String.format("Level %d", currentLevelNumber+1);
        text(levelTextToPrint, 1140, 50);

        // print text: lives remaining
        String livesTextToPrint = String.format("Lives: %d", numberOfLives);
        text(livesTextToPrint, 160, 50);

        // print text: percentage grass
        double percentage = currentLevel.getPercentageGrass();
        double goal = currentLevel.getGoal();
        String percentTextToPrint = String.format("%s%% / %s%%", percentage, goal);
        text(percentTextToPrint, 600, 50);

    }

    /** 
     * Processing in-built method for key presses.
     * Whenever a key is pressed, Processing stores the digit in this.keyCode.
     * Calls the onKeyPress() method in Player.java.
     */
    public void keyPressed() {
		player.onKeyPress(this.keyCode);
    }

    /**
     * Re-renders the level from scratch (no green/red/grass tiles) 
     * and decrements the number of lives remaining for the player.
     */
    public void resetLevel() {

        // Create player
        this.player = new Player(this, this.ball);

        // Reading in JSON and creating Level array
        JSONObject config = loadJSONObject("config.json");
        JSONArray levelsJSON = config.getJSONArray("levels");
        int numberOfLevels = levelsJSON.size();
        
        levels = new Level[numberOfLevels]; // class variable
        for (int i = 0; i < numberOfLevels; i++) {
            JSONObject level_i_JSON = levelsJSON.getJSONObject(i);

            String filePath = level_i_JSON.getString("outlay");
            double goal = level_i_JSON.getDouble("goal") * 100;
            JSONArray enemiesJSON = level_i_JSON.getJSONArray("enemies");

            Level level_i = new Level(this, filePath, goal, enemiesJSON);
            levels[i] = level_i;
        }

        this.numberOfLives--;
            
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }

}