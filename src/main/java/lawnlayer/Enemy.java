package lawnlayer;

import java.util.ArrayList;
import java.util.Random;
import processing.core.PImage;

public abstract class Enemy extends Mover {
    
    protected String spawnType;
    protected boolean hasCollided;
    protected boolean slowPowerUp;

    /**
     * Constructor for the abstract class Enemy which
     * spawns the enemy mob based on the spawnType.
     * 
     * <p>
     * If spawnType is random, a random soil pixel will be 
     * chosen for spawn; if spawnType is a tile coordinate, 
     * a random soil pixel on that tile is chosen for spawn.
     * Spawn direction is randomised.
     * 
     * @param app           The PApplet object
     * @param sprite        The PImage used for the sprite of the enemy
     * @param spawnType     Either "random" or a tile coordinate
     */
    public Enemy(App app, PImage sprite, String spawnType) {
        super(app, sprite);
        this.spawnType = spawnType;
        this.spawnEnemy(spawnType);
        
        this.slowPowerUp = false;
       
        // pick a random direction for the sprite to move in
        Direction[] directionArray = new Direction[] {Direction.NE, Direction.SE, Direction.SW, Direction.NW};
        Random rand = new Random();
        this.direction = directionArray[rand.nextInt(directionArray.length)];

    }

    private void spawnEnemy(String spawnType) {
        // TWO SPAWN TYPES
        // 1. RANDOM
        // 2. COORDINATE
        if (spawnType.equals("random")) {
            // need to spawn on a random soil pixel
            ArrayList<int[]> soilTiles = super.app.currentLevel.returnMaterialTiles(Material.SOIL);
            
            Random r = new Random();        
            int[] randomSoilPixel = soilTiles.get(r.nextInt(soilTiles.size()));
            this.x = randomSoilPixel[0];
            this.y = randomSoilPixel[1];
        } else {
            String[] tileCoordinates = spawnType.split(",");
            int tileX = Integer.parseInt(tileCoordinates[0]);
            int tileY = Integer.parseInt(tileCoordinates[1]);
            
            // tileX * 20           <=  pixelX  < tileX * 20 + 20
            // tileY * 20 + TOPBAR  <=  pixelY  < tileY * 20 + TOPBAR + 20
            int baseX = tileX*20;
            int baseY = tileY*20 + this.app.TOPBAR;
            Random randAdd = new Random();
            int pixelX = baseX + randAdd.nextInt(20);
            int pixelY = baseY + randAdd.nextInt(20);
            this.x = pixelX;
            this.y = pixelY;
        }
    }

    public void goNE() {
        this.direction = Direction.NE;
    }

    public void goSE() {
        this.direction = Direction.SE;
    }
    
    public void goSW() {
        this.direction = Direction.SW;
    }
    
    public void goNW() {
        this.direction = Direction.NW;
    }

    public void activateSlowPowerUp() {
        this.slowPowerUp = true;
    }

    public void deactivateSlowPowerUp() {
        this.slowPowerUp = false;
    }

    /**
     * Based on the current direction of travel, this method
     * controls an enemy's movement upon colliding with a wall 
     * or a grass tile.
     */
    protected abstract void checkReflections();

    /**
     * Basic diagonal movement implementation.
     */
    public void move() {
        if (!slowPowerUp) {
            // basic movement
            if (this.direction == Direction.NE) {
                this.x += 2;
                this.y -= 2;
            } else if (this.direction == Direction.SE) {
                this.x += 2;
                this.y += 2;
            } else if (this.direction == Direction.SW) {
                this.x -= 2;
                this.y += 2;
            } else if (this.direction == Direction.NW) {
                this.x -= 2;
                this.y -= 2;
            }
        } else {
            // slow movement
            if (this.direction == Direction.NE) {
                this.x += 1;
                this.y -= 1;
            } else if (this.direction == Direction.SE) {
                this.x += 1;
                this.y += 1;
            } else if (this.direction == Direction.SW) {
                this.x -= 1;
                this.y += 1;
            } else if (this.direction == Direction.NW) {
                this.x -= 1;
                this.y -= 1;
            }
        }   
    }

    /**
     * Updates the enemy sprite position every frame.
     * 
     * Checks for collisions between the enemy sprite
     * and green tiles. If collided, it resets the level.
     * 
     * Called every frame from Level.renderLevel()
     */
    public void tick() {

        if (this.hasCollided) {
            this.app.player.stop();
            this.app.resetLevel();
            return;
        }

        // check enemy green tile collision
        int[] currentPosition = new int[] {this.x, this.y};
        boolean greenCollision = this.app.currentLevel.recursiveReplaceMaterial(currentPosition, Material.GREEN, Material.RED);
        
        if (greenCollision) {
            this.hasCollided = true;
            return;
        }

        this.checkReflections();
        this.move();
        
    }

}
