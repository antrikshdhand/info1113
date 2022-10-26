package lawnlayer;

import processing.core.PImage;

public class Beetle extends Enemy {

    /**
     * Creates a Beetle object with a specific PImage as a sprite
     * and a specific spawn type ("random" or coordinates).
     * 
     * @param app       the PApplet object
     * @param sprite    the specific PImage sprite used for the mob
     * @param spawn     either "random" or coordinates
     */
    public Beetle(App app, PImage sprite, String spawn) {
        super(app, sprite, spawn);
    }
    
    /**
     * Override abstract method in lawnlayer.Enemy; the Beetle enemy
     * reflects off of grass and concrete but upon colliding with grass
     * it transforms that grass tile back into soil.
     */
    public void checkReflections() {
        int[][][] nearSurroundingCoords = app.currentLevel.getNearestSurroundingCoords(this.x, this.y);
        int[][][] surroundingCoords = app.currentLevel.getSurroundingCoords(this.x, this.y);
        Material[][] surMat = app.currentLevel.createMaterialsMatrix(nearSurroundingCoords);

        if (this.direction == Direction.NE) {

            if (app.currentLevel.materialAt(surMat, 2) == Material.CONCRETE) {
                // if the material above is reflectable, reflect over x-axis
                this.goSE();
            } else if (app.currentLevel.materialAt(surMat, 2) == Material.GRASS) {
                this.goSE();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[0][1][0], surroundingCoords[0][1][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 6) == Material.CONCRETE) {
                // if the material to the right is reflectable, reflect over y-axis
                this.goNW();
            } else if (app.currentLevel.materialAt(surMat, 6) == Material.GRASS) {
                this.goNW();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[1][2][0], surroundingCoords[1][2][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 3) == Material.CONCRETE) {
                // if the material at the top right (3) is reflectable, go opposite
                this.goSW();
            } else if (app.currentLevel.materialAt(surMat, 3) == Material.GRASS) {
                this.goSW();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[0][2][0], surroundingCoords[0][2][1], Material.SOIL);
            }

            // if (app.currentLevel.checkOppositionAt(surMat, 3)) {
            //     this.goSW();
            //     return;
            // }

        } else if (this.direction == Direction.SE) {

            if (app.currentLevel.materialAt(surMat, 8) == Material.CONCRETE) {
                // if the material below is reflectable, reflect over x-axis
                this.goNE();
            } else if (app.currentLevel.materialAt(surMat, 8) == Material.GRASS) {
                this.goNE();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[2][1][0], surroundingCoords[2][1][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 6) == Material.CONCRETE) {
                // if the material to the right is reflectable, reflect over x-axis
                this.goSW();
            } else if (app.currentLevel.materialAt(surMat, 6) == Material.GRASS) {
                this.goSW();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[1][2][0], surroundingCoords[1][2][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 9) == Material.CONCRETE) {
                // if the material at the bottom right (9) is reflectable, go opposite
                this.goNW();
            } else if (app.currentLevel.materialAt(surMat, 9) == Material.GRASS) {
                this.goNW();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[2][2][0], surroundingCoords[2][2][1], Material.SOIL);
            }

            // // if materials at bottom right (9) are reflectable, go opposite
            // if (app.currentLevel.checkOppositionAt(surMat, 9)) {
            //     this.goNW();
            //     return;
            // }

        } else if (this.direction == Direction.SW) {
            
            if (app.currentLevel.materialAt(surMat, 8) == Material.CONCRETE) {
                // if the material below is reflectable, reflect over x-axis
                this.goNW();
            } else if (app.currentLevel.materialAt(surMat, 8) == Material.GRASS) {
                this.goNW();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[2][1][0], surroundingCoords[2][1][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 4) == Material.CONCRETE) {
                // if the material to the left is reflectable, reflect over x-axis
                this.goSE();
            } else if (app.currentLevel.materialAt(surMat, 4) == Material.GRASS) {
                this.goSE();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[1][0][0], surroundingCoords[1][0][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 7) == Material.CONCRETE) {
                // if the material at the bottom left (7) is reflectable, go opposite
                this.goNE();
            } else if (app.currentLevel.materialAt(surMat, 7) == Material.GRASS) {
                this.goNE();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[2][0][0], surroundingCoords[2][0][1], Material.SOIL);
            }

            // // if materials at bottom left (7) are reflectable, go opposite
            // if (app.currentLevel.checkOppositionAt(surMat, 7)) {
            //     this.goNE();
            //     return;
            // }

        } else if (this.direction == Direction.NW) {
            
            if (app.currentLevel.materialAt(surMat, 2) == Material.CONCRETE) {
                // if the material above is reflectable, reflect over x-axis
                this.goSW();
            } else if (app.currentLevel.materialAt(surMat, 2) == Material.GRASS) {
                this.goSW();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[0][1][0], surroundingCoords[0][1][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 4) == Material.CONCRETE) {
                // if the material to the left is reflectable, reflect over x-axis
                this.goNE();
            } else if (app.currentLevel.materialAt(surMat, 4) == Material.GRASS) {
                this.goNE();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[1][0][0], surroundingCoords[1][0][1], Material.SOIL);
            } else if (app.currentLevel.materialAt(surMat, 1) == Material.CONCRETE) {
                // if the material at the top left (1) is reflectable, go opposite
                this.goNE();
            } else if (app.currentLevel.materialAt(surMat, 1) == Material.GRASS) {
                this.goNE();
                app.currentLevel.updateMaterialMatrix(surroundingCoords[0][0][0], surroundingCoords[0][0][1], Material.SOIL);
            }

            // // if materials at top left (1) are reflectable, go opposite
            // if (app.currentLevel.checkOppositionAt(surMat, 1)) {
            //     this.goNE();
            //     return;
            // }
        }
    }

}