package lawnlayer;

import processing.core.PImage;

public class Player extends Mover {

    private int tickCounter;
    private int currentKey; // last key pressed/released
    private Material previousMaterial;
    private Material currentMaterial;
    private boolean speedPowerUp;

    /**
     * Creates a player object which will allow for movement control.
     * 
     * Sets the initial coordinates to (0, 80) with no direction.
     * 
     * @param app       PApplet object
     * @param sprite    PImage sprite used for mob representation
     */
    public Player(App app, PImage sprite) {

        super(app, sprite);

        // player always begins in the top-left corner of the map
        this.x = 0;
        this.y = 80;
        this.speedPowerUp = false;
        
        // key is not pressed when game starts
        this.direction = Direction.STATIC;
        this.tickCounter = 0;
    }

    public int getTickCounter() {
        return this.tickCounter;
    }

    public Material getCurrentMaterial() {
        return this.currentMaterial;
    }
    
    public Material getPreviousMaterial() {
        return this.previousMaterial;
    }

    public void activateSpeedPowerUp() {
        this.speedPowerUp = true;
    }

    public void deactivateSpeedPowerUp() {
        this.speedPowerUp = false;
    }

    /* MOVEMENT */

    public void pressUp() {
        this.direction = Direction.UP;
    }

    public void pressDown() {
        this.direction = Direction.DOWN;
    }

    public void pressLeft() {
        this.direction = Direction.LEFT;
    }

    public void pressRight() {
        this.direction = Direction.RIGHT;
    }

    public void release() {
        this.direction = Direction.STATIC;
    }

    public void reset() {
        this.x = 0;
        this.y = 80;
    }

    public void onKeyPress(int currentKey) {
        this.currentKey = currentKey;
    }

    /** 
     * Called every frame, this method contains all major player
     * movement logic.
     * It ensures players can only change directions once they 
     * are perfectly situated on a tile.
     * It ensures players on concrete must hold down a key to move.
     * It ensures nothing happens when a player on grass or soil
     * presses the opposing key to the direction they are moving in.
     * It ensures players cannot move outside the bounds of the game.
     * It ensures the player's speed is 2 pixels per frame.
     */
    public void tick() {

        if (this.direction == Direction.EMERGENCYSTOP) return;

        // once the player is perfectly on a tile
        if (this.tickCounter == 0 || this.tickCounter == 10) {

            // update materials
            Material temp = this.currentMaterial;
            this.currentMaterial = this.app.currentLevel.getMaterialPixel(this.x, this.y); 
            this.previousMaterial = temp;

            this.tickCounter = 0;

            ////// RULES FOR CHANGING DIRECTION //////

            // RULES FOR CONCRETE //
            if (this.currentMaterial == Material.CONCRETE) {
                // when on concrete the player needs to hold down a key to move
                if (!app.keyPressed) {
                    this.release();
                } else if (currentKey == 37) {
                    this.pressLeft();
                } else if (currentKey == 38) {
                    this.pressUp();
                } else if (currentKey == 39) {
                    this.pressRight();
                } else if (currentKey == 40) {
                    this.pressDown();
                }
            }

            // RULES FOR SOIL AND GRASS //
            if (this.currentMaterial == Material.SOIL || this.currentMaterial == Material.GRASS) {
                // nothing happens if user presses the opposite direction to current movement
                if (currentKey == 37) {
                    if (this.direction != Direction.RIGHT) this.pressLeft();
                } else if (currentKey == 38) {
                    if (this.direction != Direction.DOWN) this.pressUp();
                } else if (currentKey == 39) {
                    if (this.direction != Direction.LEFT) this.pressRight();
                } else if (currentKey == 40) {
                    if (this.direction != Direction.UP) this.pressDown();
                }
            }

        }

        if (!speedPowerUp) {
            // player's movement speed is 2 pixels per frame, hence +=/-= 2
            // only move if the player is within the bounds of the game
            if (this.direction == Direction.RIGHT) {
                if (this.x < 1260) this.x += 2; 
                this.tickCounter++;
            } else if (this.direction == Direction.LEFT) {
                if (this.x > 0) this.x -= 2;
                this.tickCounter++;
            } else if (this.direction == Direction.DOWN) {
                if (this.y < 700) this.y += 2;
                this.tickCounter++;
            } else if (this.direction == Direction.UP) {
                if (this.y > 80) this.y -= 2;
                this.tickCounter++;
            }
        } else {
            // speed increases
            if (this.direction == Direction.RIGHT) {
                if (this.x < 1260) this.x += 4; 
                this.tickCounter++;
            } else if (this.direction == Direction.LEFT) {
                if (this.x > 0) this.x -= 4;
                this.tickCounter++;
            } else if (this.direction == Direction.DOWN) {
                if (this.y < 700) this.y += 4;
                this.tickCounter++;
            } else if (this.direction == Direction.UP) {
                if (this.y > 80) this.y -= 4;
                this.tickCounter++;
            }
        }
        

    }

}
