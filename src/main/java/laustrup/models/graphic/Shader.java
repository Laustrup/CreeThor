package laustrup.models.graphic;

import laustrup.utilities.console.Printer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int _id, _vertexId, _fragmentId;

    private String _vertex, _fragment, _filePath;

    public Shader(String filepath) {
        _filePath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] line = source.split("(#type)( )+([a-zA-Z]+)");

            int startOfLine = source.indexOf("#type") + 6,
                endOfLine = source.indexOf("\r\n",startOfLine);

            configureFilePattern(source.substring(startOfLine, endOfLine).trim(), line[1], filepath);
            configureFilePattern(source.substring(
                    source.indexOf("#type", endOfLine) + 6,
                    source.indexOf("\r\n",source.indexOf("#type", endOfLine) + 6)
            ), line[2], filepath);
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Couldn't open file: \"" + filepath + "\" for shader...";
        }
    }

    private void configureFilePattern(String pattern, String content, String filepath) throws IOException {
        if (pattern.equals("vertex"))
            _vertex = content;
        else if (pattern.equals("fragment"))
            _fragment = content;
        else
            throw new IOException("Unexpected token \"" + pattern + "\" in the file \"" + filepath + "\"...");
    }

    public void compile() {
        initiateVertex();
        initiateFragments();
        linkProgram();
    }

    /** Initiates Vertexes and checks if it is a success. */
    private void initiateVertex() {
        _vertexId = glCreateShader(GL_VERTEX_SHADER);
        passShaderSourceToGPU(_vertexId);
        checkInitiationStatus(_vertexId, "Vertex");
    }

    /** Initiates Fragments and checks if it is a success. */
    private void initiateFragments() {
        _fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        passShaderSourceToGPU(_fragmentId);
        checkInitiationStatus(_fragmentId, "Fragment");
    }

    /**
     * Will both pass and compile a shader source.
     * @param id The id of the shader source to be passed and compiled.
     */
    private void passShaderSourceToGPU(int id) {
        glShaderSource(id, id == _vertexId ? _vertex : _fragment);
        glCompileShader(id);
    }

    /**
     * Will check the status of various initialisations.
     * @param id The id of the object to be checked.
     * @param unit Is used to describe to the Printer what unit there was a issue with.
     */
    private void checkInitiationStatus(int id, String unit) {
        boolean isProgram = id == _id;

        if (isProgram ? glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE : glGetShaderi(id,GL_COMPILE_STATUS) == GL_FALSE) {
            Printer.get_instance().print("Error occurred when initiating " + unit + "...",
                    new InstantiationException(
                            "\n" + (isProgram ? glGetProgramInfoLog(_id, glGetProgrami(id, GL_LINK_STATUS)) : glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH))))
            );
            assert false : "";
        }
    }

    /** The program is created before linked and its status is checked. */
    private void linkProgram() {
        _id = glCreateProgram();
        glAttachShader(_id, _vertexId);
        glAttachShader(_id, _fragmentId);
        glLinkProgram(_id);
        checkInitiationStatus(_id, "program link");
    }

    public void use() {
        glUseProgram(_id);
    }

    public void detach() {
        glUseProgram(0);
    }
}
