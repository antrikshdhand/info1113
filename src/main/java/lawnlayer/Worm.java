package lawnlayer;

import processing.core.PImage;

public class Worm extends Enemy {

    /**
     * Creates a Worm object with a specific PImage as a sprite
     * and a specific spawn type ("random" or coordinates).
     * 
     * @param app       the PApplet object
     * @param sprite    the specific PImage sprite used for the mob
     * @param spawn     either "random" or coordinates
     */
    public Worm(App app, PImage sprite, String spawnType) {
        super(app, sprite, spawnType);
    }  
    
    /**
     * Override abstract method in lawnlayer.Enemy; the Worm enemy
     * simply reflects off grass and concrete while making no changes
     * to the map.
     */
    public void checkReflections() {
        int[][][] surroundingCoords = app.currentLevel.getNearestSurroundingCoords(this.x, this.y);
        Material[][] surMat = app.currentLevel.createMaterialsMatrix(surroundingCoords);

        if (this.direction == Direction.NE) {
            if (app.currentLevel.checkReflectivityAt(surMat, 2)) {
                // if the material above is reflectable, reflect over x-axis
                this.goSE();
            } else if (app.currentLevel.checkReflectivityAt(surMat, 6)) {
                // if the material to the right is reflectable, reflect over y-axis
                this.goNW();
                return;
            } else if (app.currentLevel.checkOppositionAt(surMat, 3)) {
                // if materials at top right (3) are reflectable, go opposite
                this.goSW();
                return;
            }
        } else if (this.direction == Direction.SE) {
            if (app.currentLevel.checkReflectivityAt(surMat, 8)) {
                // if the material below is reflectable, reflect over x-axis
                this.goNE();
                return;
            } else if (app.currentLevel.checkReflectivityAt(surMat, 6)) {
                // if the material to the right is reflectable, reflect over y-axis
                this.goSW();
                return;
            } else if (app.currentLevel.checkOppositionAt(surMat, 9)) {
                // if materials at bottom right (9) are reflectable, go opposite
                this.goNW();
                return;
            }
        } else if (this.direction == Direction.SW) {
            if (app.currentLevel.checkReflectivityAt(surMat, 8)) {
                // if the material below is reflectable, reflect over x-axis
                this.goNW();
                return;
            } else if (app.currentLevel.checkReflectivityAt(surMat, 4)) {
                // if the material to the left is reflectable, reflect over y-axis
                this.goSE();
                return;
            } else if (app.currentLevel.checkOppositionAt(surMat, 7)) {
                // if materials at bottom left (7) are reflectable, go opposite
                this.goNE();
                return;
            }
        } else if (this.direction == Direction.NW) {
            if (app.currentLevel.checkReflectivityAt(surMat, 2)) {
                // if the material above is reflectable, reflect over x-axis
                this.goSW();
                return;
            } else if (app.currentLevel.checkReflectivityAt(surMat, 4)) {
                this.goNE();
                return;
            } else if (app.currentLevel.checkOppositionAt(surMat, 1)) {
                // if materials at top left (1) are reflectable, go opposite
                this.goNE();
                return;
            }
        }
    }

}
