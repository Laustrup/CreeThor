package laustrup.models.scenes;

//import laustrup.models.Window;
//import laustrup.models.listeners.KeyListener;
import laustrup.services.FileService;
import laustrup.utilities.console.Printer;

import lombok.Getter;

import static org.lwjgl.opengl.GL20.*;

//import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene implements IScene {

    private enum Shader {
        VERTEX("vertex"),
        FRAGMENT("fragment");

        @Getter
        public String _value;
        Shader(String value) {
            _value = value;
        }
    }

    private final String _vertexSource = defineShader(Shader.VERTEX), _fragmentSource = defineShader(Shader.FRAGMENT);
    private int _vertexID, _fragmentID, _shaderID;

    private String defineShader(Shader type) {
        String s = FileService.get_instance().getContent(
                "C:\\Users\\Laust\\IdeaProjects\\CreeThor\\assets\\shaders\\default.glsl"
        ).split("#type ")[switch (type) {
            case VERTEX -> 1;
            case FRAGMENT -> 2;
//            default -> throw new IllegalStateException();
        }].replace(type.get_value() + "\r\n","");

        return s;
    }

    private boolean _change = false;
    private float _intermission = 2.0f;

    public LevelEditorScene() {
        Printer.get_instance().print("This is editor");
    }

    @Override
    public void update(float dt) {
        _fps = 1.0f/dt;

//        if (!_change && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
//            _change = true;
//
//        if (_change) {
//            if (_intermission > 0)
//                _intermission -= dt;
//
//            Window.set_scene(new LevelScene());
//        }

    }

    @Override
    public void init() {
        initiateVertex();
        initiateFragment();
    }

    private void initiateVertex() {
        _vertexID = glCreateShader(GL_VERTEX_SHADER);
        passShaderSourceToGPU(_vertexID);
        checkShaderInitiationStatus(_vertexID);
    }

    private void initiateFragment() {
        _fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        passShaderSourceToGPU(_fragmentID);
        checkShaderInitiationStatus(_fragmentID);
    }

    private void passShaderSourceToGPU(int id) {
        glShaderSource(id, id == _vertexID ? _vertexSource : _fragmentSource);
        glCompileShader(id);
    }

    private void checkShaderInitiationStatus(int id) {
        if (glGetShaderi(id,GL_COMPILE_STATUS) == GL_FALSE) {
            Printer.get_instance().print("Error occurred when initiating shader of " +
                    (id == _vertexID ? "vertex" : "fragment") + "...",
                    new InstantiationException("\n" + glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH)))
            );
            assert false : "";
        }
    }
}
