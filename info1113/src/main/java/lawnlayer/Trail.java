package lawnlayer;

import java.util.ArrayList;

public class Trail {
    
    private App app;
    private Player player;
    
    private Material previousMaterial;
    private Material currentMaterial;
    private Direction direction;
    private int x;
    private int y;

    private ArrayList<int[]> greenTiles;
    private boolean trailMaking; // whether a trail is currently in progress or not

    /**
     * Creates a new Trail object, which concerns itself with the green path 
     * created behind the player as it travels across soil.
     * 
     * @param app       the PApplet object
     * @param player    the player object travelling along the soil
     */
    public Trail(App app, Player player) {
        this.app = app;
        this.player = player;

        this.previousMaterial = player.getPreviousMaterial(); // player always starts on concrete
        this.currentMaterial = player.getCurrentMaterial(); 
        this.trailMaking = false;
    }

    /**
     * Called every frame, this method updates the basic
     * player attributes for easy retrieval later in the code.
     * 
     * This method calls crashDetection().
     */
    public void updatePosition() {
        this.x = player.getX();
        this.y = player.getY();
        this.direction = player.getDirection();
        this.greenTiles = this.app.currentLevel.returnMaterialTiles(Material.GREEN);
        
        // check if player hit their own trail
        this.crashDetection();

        // update materials
        this.previousMaterial = player.getPreviousMaterial();
        this.currentMaterial = player.getCurrentMaterial();
    }

    /**
     * Checks to see whether the player has hit their own green trail;
     * if they have, the method produces a red tile at that spot and
     * then resets the level.
     */
    private void crashDetection() {

        int[] tileToRemove = null;

        for (int[] tile : this.greenTiles) {
            if (this.direction == Direction.UP && this.y > tile[1]) {
                if (this.y < tile[1] + 20 && this.x == tile[0]) tileToRemove = tile; 
            } else if (this.direction == Direction.DOWN && this.y < tile[1]) {
                if (this.y > tile[1] - 20 && this.x == tile[0]) tileToRemove = tile;
            } else if (this.direction == Direction.RIGHT && this.x < tile[0]) {
                if (this.x > tile[0] - 20 && this.y == tile[1]) tileToRemove = tile;
            } else if (this.direction == Direction.LEFT && this.x > tile[0]) {
                if (this.x < tile[0] + 20 && this.y == tile[1]) tileToRemove = tile;
            }
        }

        if (tileToRemove != null) {
            this.app.fill(200, 0, 0);
            this.app.rect(tileToRemove[0], tileToRemove[1], 20, 20);
            
            this.trailMaking = false;
            //this.app.currentLevel.resetLevel();
            this.app.resetLevel();

            return;
        }

    }

    /**
     * Called every 10 frames (to ensure any trails are produced only
     * once the player is perfectly on a tile), this method holds the 
     * main logic behind appending green tiles behind the player.
     * 
     * The method splits the behaviour up into 6 cases depending on 
     * what the player's current and previous materials are.
     * 
     * It calls the methods appendTrails() and fillGrass() as required.
     */
    public void calculateTrails() {

        // 1. current material == concrete, previous material == concrete --> do nothing
        // 2. current material == soil, previous material == concrete --> begin a trail
        // 3. current material == soil, previous material == soil --> draw a green tile at prior position 
        // 4. current material == concrete, previous material == soil --> fill grass
        // 5. current material == grass, previous material == soil --> fill grass
        // 6. current material == soil, previous material == grass --> begin a trail
        // 7. current material == powerup, previous material == soil --> continue trail
        // 8. current material == powerup, previous material == powerup --> continue trail
        // 9. current material == soil, previous material == powerup --> continue trail

        if (this.previousMaterial == Material.CONCRETE && this.currentMaterial == Material.CONCRETE) {
            this.trailMaking = false; 
        } 
        if (this.previousMaterial == Material.CONCRETE && this.currentMaterial == Material.SOIL) {
            this.trailMaking = true;
            this.appendTrails();
        } 
        if (this.previousMaterial == Material.SOIL && this.currentMaterial == Material.SOIL) {            
            this.appendTrails();
        } 
        if (this.previousMaterial == Material.SOIL && this.currentMaterial == Material.CONCRETE) { 
            this.fillGrass();
            this.trailMaking = false;
        } 
        if (this.previousMaterial == Material.SOIL && this.currentMaterial == Material.GRASS) {
            this.fillGrass();
            this.trailMaking = false;
        } 
        if (this.previousMaterial == Material.GRASS && this.currentMaterial == Material.SOIL) {
            this.trailMaking = true;
            this.appendTrails();
        } 
        if (this.previousMaterial == Material.SOIL && this.currentMaterial == Material.POWERUP) {
            this.appendTrails();
        } 
        if (this.previousMaterial == Material.POWERUP && this.currentMaterial == Material.POWERUP) {
            this.appendTrails(); 
        }
        if (this.previousMaterial == Material.POWERUP && this.currentMaterial == Material.SOIL) {
            this.appendTrails();
        }

    }
    
    /**
     * As the tile is to be produced behind the player,
     * this method involves the player's current direction
     * to determine the coordinates for the previous tile.
     */
    private void appendTrails() {
        if (this.trailMaking == false) return;
    
        if (this.direction == Direction.RIGHT) {
            int[] tileCoords = {this.x - 20, this.y};
            this.app.currentLevel.updateMaterialMatrix(tileCoords[0], tileCoords[1], Material.GREEN);
        } else if (this.direction == Direction.DOWN) {
            int[] tileCoords = {this.x, this.y - 20};
            this.app.currentLevel.updateMaterialMatrix(tileCoords[0], tileCoords[1], Material.GREEN);
        } else if (this.direction == Direction.LEFT) {
            int[] tileCoords = {this.x + 20, this.y};
            this.app.currentLevel.updateMaterialMatrix(tileCoords[0], tileCoords[1], Material.GREEN);
        } else if (this.direction == Direction.UP) {
            int[] tileCoords = {this.x, this.y + 20};
            this.app.currentLevel.updateMaterialMatrix(tileCoords[0], tileCoords[1], Material.GREEN);
        }
    }

    /**
     * A small method which fills in every tile produced in
     * calculateGrass() with grass.
     */
    private void fillGrass() {
        // set each calculated tile which has been determined to be grass to be grass in the level file
        for (int[] tile : this.calculateGrass()) {
            this.app.currentLevel.updateMaterialMatrix(tile[0], tile[1], Material.GRASS);
        }
    }

    /**
     * Contains the main logic for calculating which tiles should
     * be converted to grass given a green trail.
     * 
     * All green tiles produced as a trail are converted to grass.
     * 
     * Calls the recursive function enemyChecker() to check for the presence
     * of enemy mobs in either of the segments formed by the player's trail.
     * 
     * @return  an ArrayList containing the coordinates of tiles which are to be converted to grass
     */
    private ArrayList<int[]> calculateGrass() {

        ArrayList<int[]> potentialGrassTiles = new ArrayList<int[]>();

        // convert all green tiles to grass
        for (int[] tile : this.greenTiles) {
            potentialGrassTiles.add(tile);
        }

        // calculate enemy presence
        boolean enemyPresent = true;
        int i = 1;
        while (enemyPresent && i < 1000) {
            enemyPresent = this.enemyChecker(i);
            i++;
        }
        
        return potentialGrassTiles;

    }

    /**
     * Calculates whether an enemy is present in the flood fill
     * algorithm given that we start from the i-th soil tile; 
     * returns true if enemy is present.
     *
     * @param i refers to the i-th soil tile in the level
     * @return  true if enemy is present in the ith-tile flood fill algorithm
     */
    private boolean enemyChecker(int i) {

        ArrayList<int[]> allSoilTiles = this.app.currentLevel.getInitialSoilTiles();
        int[] aSoilTile = allSoilTiles.get(i);
        
        ArrayList<int[]> allUpdatedTiles = this.app.currentLevel.rReplaceMaterialStored(aSoilTile, Material.SOIL, Material.GRASS);
        if (allUpdatedTiles == null) return true;

        // check if enemy is present in recursive fill
        for (Enemy enemy : this.app.currentLevel.getEnemyArray()) {
            for (int[] updatedTile : allUpdatedTiles) {
                if ((enemy.getX() < updatedTile[0] + 5 && enemy.getX() > updatedTile[0] - 5) &&
                    (enemy.getY() < updatedTile[1] + 5 && enemy.getY() > updatedTile[1] - 5)) {
                    // enemy is present in that segment
                    return true;
                }
            }
        }

        return false;

    }

}
