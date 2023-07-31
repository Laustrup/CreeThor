package laustrup.models.scenes;

/**
 * Contains the abstract functions required for a Scene.
 */
public interface IScene {

    /**
     * This will be run for each frame.
     * @param dt The change in time, is needed to calculate fps.
     */
    void update(float dt);

    /**
     * Will set up the scene before updating.
     */
    void init();
}
