package laustrup.models.scenes;

import laustrup.models.Window;
import laustrup.models.listeners.KeyListener;
import laustrup.utilities.console.Printer;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene implements IScene {

    private boolean _change = false;
    private float _intermission = 2.0f;

    public LevelEditorScene() {
        Printer.get_instance().print("This is editor");
    }

    @Override
    public void update(float dt) {
        _fps = 1.0f/dt;

        if (!_change && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
            _change = true;

        if (_change) {
            if (_intermission > 0)
                _intermission -= dt;

            Window.set_scene(new LevelScene());
        }

    }
}
