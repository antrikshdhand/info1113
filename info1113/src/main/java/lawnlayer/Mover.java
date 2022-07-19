package lawnlayer;

import processing.core.PImage;

public abstract class Mover {
    
    // basic parameters
    protected App app;
    protected int x;    
    protected int y;
    protected PImage sprite;
    protected Direction direction;

    /**
     * Constructor for the abstract Mover class.
     * 
     * @param app       the PApplet object
     * @param sprite    the PImage sprite used for the mob
     */
    public Mover(App app, PImage sprite) {
        this.app = app;
        this.sprite = sprite;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void stop() {
        this.direction = Direction.EMERGENCYSTOP;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    /**
     * Prints the image of the Mover's sprite onto the app at
     * their current coordinates every frame.
     */
    public void draw() {        
        this.app.image(this.sprite, this.x, this.y);
    }

}
