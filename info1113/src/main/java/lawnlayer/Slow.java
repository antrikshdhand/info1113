package lawnlayer;

public class Slow extends Powerup {
    
    /**
     * Slows down all enemies to half their original speed.
     * @param app
     * @param level
     */
    public Slow(App app, Level level) {
        super(app, app.schoolZone, level);
    }

    /**
     * Activates the powerup flag in every Enemy object
     * present in the level.
     */
    public void activate() {
        Enemy[] enemies = this.app.currentLevel.getEnemyArray();
        for (Enemy enemy : enemies) {
            enemy.activateSlowPowerUp();
        }
    }

    /**
     * Deactivates the powerup flag in every Enemy object
     * present in the level.
     */
    public void deactivate() {
        Enemy[] enemies = this.app.currentLevel.getEnemyArray();
        for (Enemy enemy : enemies) {
            enemy.activateSlowPowerUp();
        }
    }
    
}
