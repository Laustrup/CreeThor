package laustrup.models.scenes;

import laustrup.Program;
import laustrup.models.graphic.fragment.Fragment;
import laustrup.models.graphic.Vertex;
import laustrup.models.graphic.fragment.FragmentCollection;
import laustrup.services.FileService;
import laustrup.utilities.FragmentUtility;
import laustrup.utilities.VertexUtility;
import laustrup.utilities.collections.lists.Liszt;
import laustrup.utilities.console.Printer;

import lombok.Getter;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene implements IScene {

    /** Defines the options of Shaders. */
    private enum Shader {
        VERTEX("vertex"),
        FRAGMENT("fragment");

        @Getter
        public String _value;
        Shader(String value) {
            _value = value;
        }
    }

    /**
     * The source of a Vertex.
     * Is defined from the default.glsl shader file.
     */
    private final String _vertexSource = defineShader(Shader.VERTEX);

    /**
     * The source of a Vertex.
     * Is defined from the default.glsl shader file.
     */
    private final String _fragmentSource = defineShader(Shader.FRAGMENT);

    /**
     * Takes the content from default.glsl shader file that fits the input.
     * @param type The type of shader that should be defined.
     * @return The correct content for the shader type.
     */
    private String defineShader(Shader type) {
        return FileService.get_instance().getContent(
                Program.get_path() + "\\assets\\shaders\\default.glsl"
        ).split("#type ")[switch (type) {
            case VERTEX -> 1;
            case FRAGMENT -> 2;
//            default -> throw new IllegalStateException();
        }].replace(type.get_value() + "\r\n","");
    }

    /**
     * The id that is created for the vertexes.
     * Is generated from gl.
     */
    private int _vertexID;

    /**
     * The id that is created for the fragments.
     * Is generated from gl.
     */
    private int _fragmentID;

    /**
     * The id that is created for the program of shaders.
     * Is generated from gl.
     */
    private int _program;

    /**
     * The id that is created for the vertexes to the screen.
     * Is generated from gl.
     */
    private int _vertexArrayObjectID;

    /**
     * The id that is created for thebuffer of vertexes.
     * Is generated from gl.
     */
    private int _vertexBufferObjectID;

    /**
     * The id that is created for the buffer of elements.
     * Is generated from gl.
     */
    private int _elementBufferObjectID;

    /**
     * The preset of vertexes for this scene,
     * with both positions and colors.
     */
    private Liszt<Vertex> _vertexes = new Liszt<>(new Vertex[]{
            new Vertex(new float[]{0.5f, -0.5f, 0.0f}, new float[]{1.0f, 0.0f, 0.0f, 1.0f}),
            new Vertex(new float[]{-0.5f, 0.5f, 0.0f}, new float[]{0.0f, 1.0f, 0.0f, 1.0f}),
            new Vertex(new float[]{0.5f, 0.5f, 0.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}),
            new Vertex(new float[]{-0.5f, -0.5f, 0.0f}, new float[]{1.0f, 1.0f, 0.0f, 1.0f})
    });

    /**
     * The preset of elements for this scene,
     * consists of the vertexes.
     */
    private FragmentCollection _fragmentCollection = new FragmentCollection(new Liszt<>(new Fragment[]{
            new Fragment(new int[]{2, 1, 0}),
            new Fragment(new int[]{0, 1, 3})
        })
    );

    /**
     * After creation, it will write a message to the screen.
     */
    public LevelEditorScene() {
        Printer.get_instance().print("This is editor");
    }

    @Override
    public void init() {
        initiateVertex();
        initiateFragments();
        linkProgram();
        initGPU();
    }

    @Override
    public void update(float dt) {
        _fps = 1.0f/dt;

        glUseProgram(_program);
        glBindVertexArray(_vertexArrayObjectID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Fragment element : _fragmentCollection.get_fragments())
                    length += element.get_nodes().length;

                return length;
            }
        }.get(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    /**
     * Initialises the different objects, that is needed for the GPU.
     * Which is performed by many other private methods.
     */
    private void initGPU() {
        generateVertexArrayObject();
        generateVertexBufferObject(generateFloatBuffer());
        generateElementBufferObject(createIndices());

        int positionsSize = 3, colorSize = 4, floatSizeBytes = 4, vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    /**
     * Generates and binds the VertexArrayObject.
     * The id is also initialised.
     */
    private void generateVertexArrayObject() {
        _vertexArrayObjectID = glGenVertexArrays();
        glBindVertexArray(_vertexArrayObjectID);
    }

    /** Generates the vertexBuffer with the vertexes values. */
    private FloatBuffer generateFloatBuffer() {
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Vertex vertex : _vertexes)
                    length += vertex.get_positions().length + vertex.get_colors().length;

                return length;
            }
        }.get());
        vertexBuffer.put(VertexUtility.vertexIntoGraphicFormat(_vertexes)).flip();

        return vertexBuffer;
    }

    /**
     * Vertex buffer is generated, binded with the data.
     * @param vertexBuffer The data to be binded.
     */
    private void generateVertexBufferObject(FloatBuffer vertexBuffer) {
        _vertexBufferObjectID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _vertexBufferObjectID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
    }

    /**
     * Element buffer is generated, binded with the data.
     * @param elementBuffer The data to be binded.
     */
    private void generateElementBufferObject(IntBuffer elementBuffer) {
        _elementBufferObjectID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _elementBufferObjectID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    }

    /**
     * Indices are created from the fragments.
     * Afterwards put into the element buffer.
     * @return The element buffer with the created indices.
     */
    private IntBuffer createIndices() {
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(_fragmentCollection.amount());
        elementBuffer.put(FragmentUtility.elementGraphicFormat(_fragmentCollection)).flip();

        return elementBuffer;
    }

    /** Initiates Vertexes and checks if it is a success. */
    private void initiateVertex() {
        _vertexID = glCreateShader(GL_VERTEX_SHADER);
        passShaderSourceToGPU(_vertexID);
        checkInitiationStatus(_vertexID, "Vertex");
    }

    /** Initiates Fragments and checks if it is a success. */
    private void initiateFragments() {
        _fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        passShaderSourceToGPU(_fragmentID);
        checkInitiationStatus(_fragmentID, "Fragment");
    }

    /** Passes the sources of default.glsl into the shader sources. Afterwards compiles it.*/
    private void passShaderSourceToGPU(int id) {
        glShaderSource(id, id == _vertexID ? _vertexSource : _fragmentSource);
        glCompileShader(id);
    }

    /** The program is created before linked and its status is checked. */
    private void linkProgram() {
        _program = glCreateProgram();
        glAttachShader(_program, _vertexID);
        glAttachShader(_program, _fragmentID);
        glLinkProgram(_program);
        checkInitiationStatus(_program, "program link");
    }

    /**
     * Will check the status of various initialisations.
     * @param id The id of the object to be checked.
     * @param unit Is used to describe to the Printer what unit there was a issue with.
     */
    private void checkInitiationStatus(int id, String unit) {
        boolean isProgram = id == _program;

        if (isProgram ? glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE : glGetShaderi(id,GL_COMPILE_STATUS) == GL_FALSE) {
            Printer.get_instance().print("Error occurred when initiating " + unit + "...",
                new InstantiationException(
                    "\n" + (isProgram ? glGetProgramInfoLog(_program, glGetProgrami(id, GL_LINK_STATUS)) : glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH))))
            );
            assert false : "";
        }
    }
}
