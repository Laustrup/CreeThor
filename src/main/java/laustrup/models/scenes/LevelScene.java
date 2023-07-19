package laustrup.models.scenes;

import laustrup.utilities.console.Printer;

public class LevelScene extends Scene implements IScene {

    public LevelScene() {
        Printer.get_instance().print("This is level");
    }

    @Override
    public void update(float dt) {
        _fps = 1.0f/dt;
    }

    @Override
    public void init() {

    }
}
