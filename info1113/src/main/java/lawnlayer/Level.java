package lawnlayer;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class Level {

    private App app;
    private double goal;
    private JSONArray enemiesJSON;
    private Enemy[] enemies;
    private Trail trail;
    private double numSoilPix;
    private ArrayList<int[]> initialSoilTiles;
    private ArrayList<int[]> recursionStorer;
    private double percentageGrass;
    private int renderCount;
    private Powerup powerup;
    private boolean powerupObtained;
    private int powerupObtainedAt;

    private String filePath;
    private File file;
    private Scanner scan;
    
    private final int TILES_WIDE = 64;
    private final int TILES_TALL = 32;

    private String[][] tileList; 
    private String[][] pixelList;
    private Material[][] materialMatrix;
    
    /**
     * Constructs a level object and initialises variables such as
     * goal, Trail, and enemies.
     * 
     * @param app           the Papplet object
     * @param filePath      filePath to the levels.txt file
     * @param goal          from the config.json file, how much of the level must be covered in grass to win
     * @param enemiesJSON   from the config.json file, a JSONArray containing enemy information
     */
    public Level(App app, String filePath, double goal, JSONArray enemiesJSON) {
        this.app = app;
        this.goal = goal;
        this.enemiesJSON = enemiesJSON;
        this.trail = new Trail(app, app.player);
        this.filePath = filePath;

        this.powerupObtained = false;
        this.tileList = new String[TILES_TALL][TILES_WIDE];
        this.materialMatrix = new Material[this.app.HEIGHT - this.app.TOPBAR][this.app.WIDTH];
        this.recursionStorer = new ArrayList<int[]>();
    }

    /* PRIVATE METHODS */

    /**
     * Read in level.txt file and create the level's materialMatrix,
     * as well as initialise and spawn all enemies.
     * <p>
     * Upon first render we also count the initial soil tiles
     * and the inital soil pixels to be used when calculating
     * the progress made in completing the level.
     */
    private void firstRender() {
        this.app.player.reset();
        this.percentageGrass = 0;

        // read in the level.txt file
        try {
            this.file = new File(filePath);
            this.scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Please try again.");
            return;
        }

        // tileList stores string[] for each line in the .txt file in ***TILE DIMENSIONS***
        for (int i = 0; i < TILES_TALL; i++) {
            String line = scan.nextLine();
            tileList[i] = line.split("");
        }

        // pixelList stores string[] for each line in the .txt file in ***PIXELS***
        this.pixelList = enlargeMatrix(tileList, 20);
        
        // materialMatrix stores Material[] for each line in the .txt file in ***PIXELS***
        for (int i = 0; i < pixelList.length; i++) {
            for (int j = 0; j < pixelList[0].length; j++) {
                if (pixelList[i][j].equals("X")) materialMatrix[i][j] = Material.CONCRETE;
                if (pixelList[i][j].equals(" ")) materialMatrix[i][j] = Material.SOIL;                
            }
        }

        this.initialSoilTiles = this.app.currentLevel.returnMaterialTiles(Material.SOIL);
        this.countSoilPix();

        // add enemies to Enemy array
        int numberOfEnemies = this.enemiesJSON.size();
        this.enemies = new Enemy[numberOfEnemies];
        for (int i = 0; i < numberOfEnemies; i++) {
            JSONObject enemy_i = enemiesJSON.getJSONObject(i);
            int type = enemy_i.getInt("type");
            String spawnType = enemy_i.getString("spawn");
            
            Enemy enemyTemp = null;
            if (type == 0) {
                enemyTemp = new Worm(this.app, this.app.worm, spawnType);
            } else if (type == 1) {
                enemyTemp = new Beetle(this.app, this.app.beetle, spawnType);
            }
            if (enemyTemp != null) this.enemies[i] = enemyTemp;
        }
    }
    
    private void countSoilPix() {
        this.numSoilPix = 0;
        for (int i = 0; i < materialMatrix.length; i++) {
            for (int j = 0; j < materialMatrix[0].length; j++) {
                Material pixel = materialMatrix[i][j];
                if (pixel == Material.SOIL) this.numSoilPix++;
            }
        }
    }

    /**
     * Calculates how much soil the player has covered
     * with grass.
     * 
     * Each grass pixel is (1/x * 100)% of the map, where
     * x is the number of original soil pixels.
     * If there are n grass pixels, this is (n/x * 100)%.
     */
    private void updatePercentage() {
        double numGrassPix = 0;
        for (int i = 0; i < materialMatrix.length; i++) {
            for (int j = 0; j < materialMatrix[0].length; j++) {
                Material pixel = materialMatrix[i][j];
                if (pixel == Material.GRASS) numGrassPix++;
            }
        }
        
        this.percentageGrass = Math.ceil((numGrassPix/numSoilPix) * 100); 
    }

    /**
     * Randomly generates a new powerup.
     */
    private void launchPowerup() {
        String[] powerups = new String[] {"speed", "slow"};
        
        Random r = new Random();
        int randomInt = r.nextInt(powerups.length);
        String chosenPowerupName = powerups[randomInt];
        if (chosenPowerupName.equals("speed")) {
            this.powerup = new Speed(this.app, this);
        } else {
            this.powerup = new Slow(this.app, this);
        }
    }

    /* PUBLIC METHODS */
    
    /** 
     * The primary method which runs the game, rendering
     * each material and their PImage at the correct spot 
     * on PApplet and updating enemies every frame.
     * 
     * This method also contains the win condition for the level.
     */
    public void renderLevel() {

        // upon first time rendering the level
        if (this.renderCount == 0) this.firstRender();

        // 5 seconds in, launch the powerup
        if (this.renderCount == 300) this.launchPowerup();
        // after 5 seconds
        if (this.renderCount > 300) {
            if (this.getMaterialPixel(app.player.x, app.player.y) == Material.POWERUP) this.powerupObtained = true;
            if (this.powerupObtained) {
                this.powerupObtainedAt = this.renderCount;
                if (this.powerupObtained) {
                    this.powerup.activate();
                }
                this.powerup.undraw();
            } else {
                if (this.renderCount < 900) {
                    this.powerup.draw();
                } else {
                    this.powerup.undraw();
                }
            }
        }

        // continually rendering each pixel onto the application
        int backgroundY = this.app.TOPBAR;
        for (int i = 0; i < materialMatrix.length; i += 20) {
            Material[] line = materialMatrix[i];
            int backgroundX = 0; // X needs to reset each line
            for (int j = 0; j < line.length; j += 20) {
                if (line[j] == Material.CONCRETE) this.app.image(this.app.concrete, backgroundX, backgroundY);
                if (line[j] == Material.SOIL) this.app.image(this.app.soil, backgroundX, backgroundY);
                if (line[j] == Material.GRASS) this.app.image(this.app.grass, backgroundX, backgroundY);
                if (line[j] == Material.GREEN) {
                    this.app.fill(0, 200, 0);
                    this.app.rect(backgroundX, backgroundY, 20, 20);
                }
                if (line[j] == Material.RED) {
                    this.app.fill(200, 0, 0);
                    this.app.rect(backgroundX, backgroundY, 20, 20);
                }
                backgroundX += 20;
            }
            backgroundY += 20; // Y needs to increment each line
        }

        for (Enemy eachEnemy : enemies) {
            eachEnemy.tick();
            eachEnemy.draw();
        }

        this.updatePercentage();
        if (this.percentageGrass > this.goal) {
            if (this.app.currentLevelNumber + 1 == app.levels.length) {
                this.app.won = true;
            }
            this.app.currentLevelNumber++;
            return;
        }
        
        this.renderCount++;

    }

    /**
     * Updates the Trail object which deals with green paths
     * along the map.
     * Note that it only appends a new tile once the player has
     * completely moved to another tile.
     * Called every frame, but only after the player has been updated.
     */
    public void draw() {
        trail.updatePosition();
        if (this.app.player.getTickCounter() == 0 || this.app.player.getTickCounter() == 10) {
            trail.calculateTrails();
        }
    }

    /**
     * Returns the material underneath a specific pixel
     * coordinate (x, y). 
     * 
     * @param x     the x value of the player in pixels
     * @param y     the y value of the player in pixels
     * @return      the material underneath the player at (x, y)
     */
    public Material getMaterialPixel(int x, int y) {
        Material tile;
        try {
            tile = materialMatrix[y - this.app.TOPBAR][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Oh no!");
            return Material.INVALID;
        }
        return tile;
    }

    /**
     * Updates the object (Level.materialMatrix) used to 
     * render every tile onto the map every frame
     * with a specific material at a specific coordinate.
     * Note that these coordinates refer to the initial
     * position (top-left) of the sprite, not the centre.
     * 
     * @param x         the sprite x-coordinate to update
     * @param y         the sprite y-coordinate to update
     * @param material  the material to place at these pixels
     */
    public void updateMaterialMatrix(int x, int y, Material material) {
        for (int i = y - this.app.TOPBAR; i < y - this.app.TOPBAR + 20; i++) {
            for (int j = x; j < x + 20; j++) {
                this.materialMatrix[i][j] = material;
            }
        }
    }

    /**
     * Returns an ArrayList of all the coordinates in the map
     * which are of type material.
     * Note that the coordinates returned are sprite coordinates.
     * 
     * @param material  material we want the coordinates of
     * @return          an ArrayList of int[2] containing x and y coordinates
     */
    public ArrayList<int[]> returnMaterialTiles(Material material) {
        ArrayList<int[]> materialCoords = new ArrayList<int[]>();
        for (int i = 0; i < materialMatrix.length; i += 20) {
            for (int j = 0; j < materialMatrix[0].length; j += 20) {
                if (materialMatrix[i][j] == material) {
                    materialCoords.add(new int[] {j, i + this.app.TOPBAR});
                }
            }
        }
        return materialCoords;
    }

    /*
    THE FOLLOWING METHODS BELOW ARE ALL RELATED
     - getSurroundingCoords
     - getNearestSurroundingCoords
     - createMaterialsMatrix
     - materialAt
     - checkReflectivityAt
     - checkOppositionAt
     - getMaterialTilesTouched
     - recursiveReplaceMaterial
     - rReplaceMaterialStored
    */

    /**
     * Given a sprite coordinate (x, y), this method
     * returns the sprite coordinates for all 8 
     * surrounding tiles.
     * 
     * @param x the sprite x-coordinate of the centre tile
     * @param y the sprite y-coordinate of the centre tile
     * @return  a matrix of int[] with the coordinates of all 9 positions related to the chosen tile
     */    
    public int[][][] getSurroundingCoords(int x, int y) {
        
        // these are the tile coordinates of the tile sprites surrounding the current tile
        int[] top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft, current;
        topLeft = new int[] {x - 20, y - 20};
        top = new int[] {x, y - 20};
        topRight = new int[] {x + 20, y - 20};
        left = new int[] {x - 20, y};
        current = new int[] {x, y};
        right = new int[] {x + 20, y};
        bottomRight = new int[] {x + 20, y + 20};
        bottom = new int[] {x, y + 20};
        bottomLeft = new int[] {x - 20, y + 20};

        int[][] row1 = new int[][] {topLeft, top, topRight};
        int[][] row2 = new int[][] {left, current, right};
        int[][] row3 = new int[][] {bottomLeft, bottom, bottomRight};
        int[][][] surroundingCoords = new int[][][] {row1, row2, row3};

        return surroundingCoords;

    }

    /**
     * Given a sprite coordinate (x, y), this method returns
     * the MINIMUM coordinates to move to reach an adjacent tile.
     * 
     * @param x the sprite x-coordinate of the centre tile
     * @param y the sprite y-coordinate of the centre tile
     * @return  a matrix of int[] with the coordinates of all 8 adjacent positions
     */
    public int[][][] getNearestSurroundingCoords(int x, int y) {
        int[] top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft, current;
        topLeft = new int[] {x - 1, y - 1};
        top = new int[] {x, y - 1};
        topRight = new int[] {x + 20, y - 1};
        left = new int[] {x - 1, y};
        current = new int[] {x, y};
        right = new int[] {x + 20, y};
        bottomRight = new int[] {x + 20, y + 20};
        bottom = new int[] {x, y + 20};
        bottomLeft = new int[] {x - 1, y + 20};

        int[][] row1 = new int[][] {topLeft, top, topRight};
        int[][] row2 = new int[][] {left, current, right};
        int[][] row3 = new int[][] {bottomLeft, bottom, bottomRight};
        int[][][] nearestSurroundingCoords = new int[][][] {row1, row2, row3};
        
        return nearestSurroundingCoords;
    }

    /**
     * Given an array of surrounding coordinates
     * the method converts the array to a matrix containing the
     * material on each tile.
     * 
     * coordsArray should be either NearestSurroundingCoords or SurroundingCoords
     * 
     * @param coordsArray   Either NearestSurroundingCoords or SurroundingCoords
     * @return              A MaterialsMatrix consisting of 8 surrounding tiles
     */
    public Material[][] createMaterialsMatrix(int[][][] coordsArray) {

        // surroundingMaterials is a 3x3 matrix 
        // element 1: [0, 0] is the material to the top left of the player
        // element 2: [0, 1] is the material above the player
        // element 5: [1, 1] is the current material underneath the player
        // element 9: [2, 2] is the material to the bottom right of the player
        // and so on...

        Material[][] surroundingMaterials = new Material[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int[] tile = coordsArray[i][j];
                Material material = this.app.currentLevel.getMaterialPixel(tile[0], tile[1]);
                surroundingMaterials[i][j] = material;
            }
        }

        return surroundingMaterials;

    }

    /**
     * An extension of createMaterialsMatrix, this method returns the material
     * at any specified direction 1 - 9, corresponding to NW through SE.
     * 
     * @param materialArray a materialsMatrix
     * @param position      1 - 9, directional indicators which each correspond to a position in the matrix
     * @return              the material in the materialsMatrix at the specified position
     */
    public Material materialAt(Material[][] materialArray, int position) {
        if (position == 1) return materialArray[0][0];
        if (position == 2) return materialArray[0][1];
        if (position == 3) return materialArray[0][2];
        if (position == 4) return materialArray[1][0];
        if (position == 5) return materialArray[1][1];
        if (position == 6) return materialArray[1][2];
        if (position == 7) return materialArray[2][0];
        if (position == 8) return materialArray[2][1];
        if (position == 9) return materialArray[2][2];
        else return Material.INVALID;
    }

    /**
     * A small method which allows Enemy.checkReflections() to be written
     * more succinctly; essentially checks whether the material is grass
     * or concrete, both materials which enemies can reflect off.
     * 
     * @param materialArray     materialMatrix created using NearestSurroundingCoords
     * @param position          1 - 9, directional indicators which each correspond to a position in the matrix
     * @return                  true if the material is either grass or concrete
     */
    public boolean checkReflectivityAt(Material[][] materialArray, int position) {
        if (this.materialAt(materialArray, position) == Material.GRASS ||
            this.materialAt(materialArray, position) == Material.CONCRETE) return true;
        else return false;
    }

    /**
     * A method which allows which allows Enemy.checkReflections() to be written
     * more succinctly; checks whether all 3 corner pieces are concrete or grass.
     * 
     * @param materialArray     materialMatrix created using NearestSurroundingCoords
     * @param cornerPosition    either 1, 3, 7 or 9
     * @return                  true if all 3 corner pieces are either concrete or grass
     */
    public boolean checkOppositionAt(Material[][] materialArray, int cornerPosition) {
        if (cornerPosition == 1) {
            // check 1, 2, and 4 are ALL concrete or ALL grass
            if ((materialAt(materialArray, 1) == Material.CONCRETE && 
                materialAt(materialArray,2) == Material.CONCRETE && 
                materialAt(materialArray, 4) == Material.CONCRETE) ||
                (materialAt(materialArray, 1) == Material.GRASS && 
                materialAt(materialArray, 2) == Material.GRASS &&
                materialAt(materialArray, 4) == Material.GRASS)) return true;
            else return false;
        } else if (cornerPosition == 3) {
            // check 2, 3 and 6
            if ((materialAt(materialArray, 2) == Material.CONCRETE && 
                materialAt(materialArray,3) == Material.CONCRETE && 
                materialAt(materialArray, 6) == Material.CONCRETE) ||
                (materialAt(materialArray, 2) == Material.GRASS && 
                materialAt(materialArray, 3) == Material.GRASS &&
                materialAt(materialArray, 6) == Material.GRASS)) return true;
            else return false;
        } else if (cornerPosition == 7) {
            // check 4, 7, and 8
            if ((materialAt(materialArray, 4) == Material.CONCRETE && 
                materialAt(materialArray,7) == Material.CONCRETE && 
                materialAt(materialArray, 8) == Material.CONCRETE) ||
                (materialAt(materialArray, 4) == Material.GRASS && 
                materialAt(materialArray, 7) == Material.GRASS &&
                materialAt(materialArray, 8) == Material.GRASS)) return true;
            else return false;
        } else if (cornerPosition == 9) {
            // check 6, 8, and 9
            if ((materialAt(materialArray, 6) == Material.CONCRETE && 
                materialAt(materialArray,8) == Material.CONCRETE && 
                materialAt(materialArray, 9) == Material.CONCRETE) ||
                (materialAt(materialArray, 6) == Material.GRASS && 
                materialAt(materialArray, 8) == Material.GRASS &&
                materialAt(materialArray, 9) == Material.GRASS)) return true;
            else return false;
        }
        else return false;
    }

    /**
     * Using a combination of getMaterialTilesTouched(), getNearestSurroundingCoords(),
     * and createMaterialsMatrix(), this method returns the sprite coordinates
     * of all the tiles surrounding (x, y) of a certain material.
     * 
     * @param material  material to check
     * @param x         sprite x-coordinate to check surrounds of
     * @param y         sprite y-coordinate to check surrounds of
     * @return          an ArrayList of coordinates of material-tiles touching (x, y)
     */
    public ArrayList<int[]> getMaterialTilesTouched(Material material, int x, int y) {
        int[][][] nearestSurroundingCoords = this.getNearestSurroundingCoords(x, y);
        int[][][] surroundingCoords = this.getSurroundingCoords(x, y);
        Material[][] nearestMaterials = this.createMaterialsMatrix(nearestSurroundingCoords);
        
        ArrayList<int[]> tilesTouched = new ArrayList<int[]>();
        
        for (int i = 0; i < nearestMaterials.length; i++) {
            for (int j = 0; j < nearestMaterials[0].length; j++) {
                if (nearestMaterials[i][j] == material) {
                    int[] tile = surroundingCoords[i][j];
                    tilesTouched.add(tile);
                }
            }
        }
        return tilesTouched;
    }

    /**
     * A recursive flood-fill method to replace every tile of a specific material 
     * surrounding a certain tile with another material.
     * 
     * The flood-fill stops when it encounters a material that isn't newMaterial.
     * This method is primarily used to to replace all green tiles with red tiles 
     * when an enemy hits the player's trail.
     * 
     * @param tile          initial tile coordinates from which to begin the flood fill algorithm
     * @param oldMaterial   the material you want to replace
     * @param newMaterial   the material you want to replace the oldMaterial with
     * @return              true on success
     */
    public boolean recursiveReplaceMaterial(int[] tile, Material oldMaterial, Material newMaterial) {
        ArrayList<int[]> oldMaterialTilesTouched = app.currentLevel.getMaterialTilesTouched(oldMaterial, tile[0], tile[1]);
        if (oldMaterialTilesTouched.isEmpty()) return false;  

        for (int[] materialTile : oldMaterialTilesTouched) {
            this.app.currentLevel.updateMaterialMatrix(materialTile[0], materialTile[1], newMaterial);
            recursiveReplaceMaterial(materialTile, oldMaterial, newMaterial);
        }

        return true;
        
    }

    /**
     * Similar to recursiveReplaceMaterial(), except this time it also returns
     * an array containing the coordinates of every tile the flood fill
     * algorithm has reached.
     * 
     * @param tile          initial tile coordinates from which to begin the flood fill algorithm
     * @param oldMaterial   the material you want to replace
     * @param newMaterial   the material you want to replace the oldMaterial with
     * @return              true on success
     */
    public ArrayList<int[]> rReplaceMaterialStored(int[] tile, Material oldMaterial, Material newMaterial) {        
        ArrayList<int[]> oldMaterialTilesTouched = app.currentLevel.getMaterialTilesTouched(oldMaterial, tile[0], tile[1]);
        if (oldMaterialTilesTouched.isEmpty()) return null;  

        for (int[] materialTile : oldMaterialTilesTouched) {
            this.app.currentLevel.updateMaterialMatrix(materialTile[0], materialTile[1], newMaterial);
            this.recursionStorer.add(materialTile);
            rReplaceMaterialStored(materialTile, oldMaterial, newMaterial);
        }

        return this.recursionStorer;
        
    }

    public double getPercentageGrass() {
        return this.percentageGrass;
    }

    public double getGoal() {
        return this.goal;
    }

    public void resetRenderCount() {
        this.renderCount = 0;
    }

    public Enemy[] getEnemyArray() {
        return this.enemies;
    }
    
    public ArrayList<int[]> getInitialSoilTiles() {
        return initialSoilTiles;
    }

    /**
     * Enlarges a tile matrix into a pixel matrix given a tileSize.
     * 
     * The following algorithm turns a tile matrix such as
     * 
     * [["C", "C", "C"],
     *  ["C", "S", "C"],
     *  ["C", "C", "C"]]
     * 
     * into a pixel matrix such as
     * 
     * [["C", "C", "C", "C", "C", "C", "C", "C", "C"],
     *  ["C", "C", "C", "C", "C", "C", "C", "C", "C"],
     *  ["C", "C", "C", "C", "C", "C", "C", "C", "C"],
     *  ["C", "C", "C", "S", "S", "S", "C", "C", "C"],
     *  ["C", "C", "C", "S", "S", "S", "C", "C", "C"],
     *  ["C", "C", "C", "S", "S", "S", "C", "C", "C"],
     *  ["C", "C", "C", "C", "C", "C", "C", "C", "C"],
     *  ["C", "C", "C", "C", "C", "C", "C", "C", "C"],
     *  ["C", "C", "C", "C", "C", "C", "C", "C", "C"]]
     * 
     * depending on the tileSize.
     * 
    */
    private String[][] enlargeMatrix(String[][] smallMatrix, int tileSize) {
        
        int newHeight = smallMatrix.length * tileSize;
        int newWidth = smallMatrix[0].length * tileSize;
        String[][] bigMatrix = new String[newHeight][newWidth];
        
        int newY = 0;
        for (int i = 0; i < smallMatrix.length; i++) {
            int newX = 0;
            for (int j = 0; j < smallMatrix[i].length; j++) {
                String tile = smallMatrix[i][j];
                for (int k = newY; k < newY+tileSize; k++) {
                    for (int l = newX; l < newX+tileSize; l++) {
                        bigMatrix[k][l] = tile;
                    }
                }
                newX += tileSize;
            }
            newY += tileSize;
        }

        return bigMatrix;

    }

}



