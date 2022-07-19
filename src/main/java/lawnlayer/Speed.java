package lawnlayer;

public class Speed extends Powerup {
    
    /**
     * Speeds up the player to double their original speed.
     * @param app
     * @param level
     */
    public Speed(App app, Level level) {
        super(app, app.sonic, level);
    }

    /**
     * Activates the powerup flag in the Player object.
     */
    public void activate() {
        this.app.player.activateSpeedPowerUp();
    }

    /**
     * Deactivates the powerup flag in the Player object.
     */
    public void deactivate() {
        this.app.player.deactivateSpeedPowerUp();
    }

}
