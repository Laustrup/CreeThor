package laustrup.models.scenes;

import laustrup.Program;
import laustrup.models.graphic.Element;
import laustrup.models.graphic.Vertex;
import laustrup.services.FileService;
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
    private int _vertexID, _fragmentID, _program, _vertexArrayObjectID, _vertexBufferObjectID, _elementBufferObjectID;

    private Liszt<Vertex> _vertexes = new Liszt<>(new Vertex[]{
            new Vertex(new float[]{0.5f, -0.5f, 0.0f}, new float[]{1.0f, 0.0f, 0.0f, 1.0f}),
            new Vertex(new float[]{-0.5f, 0.5f, 0.0f}, new float[]{0.0f, 1.0f, 0.0f, 1.0f}),
            new Vertex(new float[]{0.5f, 0.5f, 0.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}),
            new Vertex(new float[]{-0.5f, -0.5f, 0.0f}, new float[]{1.0f, 1.0f, 0.0f, 1.0f})
    });

    private Liszt<Element> _elements = new Liszt<>(new Element[]{
            new Element(new int[]{2, 1, 0}),
            new Element(new int[]{0, 1, 3})
    });

    private String defineShader(Shader type) {
        return FileService.get_instance().getContent(
                Program.get_path() + "\\assets\\shaders\\default.glsl"
        ).split("#type ")[switch (type) {
            case VERTEX -> 1;
            case FRAGMENT -> 2;
//            default -> throw new IllegalStateException();
        }].replace(type.get_value() + "\r\n","");
    }

    public LevelEditorScene() {
        Printer.get_instance().print("This is editor");
    }

    @Override
    public void init() {
        initiateVertex();
        initiateFragment();
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

                for (Element element : _elements)
                    length += element.get_nodes().length;

                return length;
            }
        }.get(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    private void initGPU() {
        createVertexArrayObject();
        createVertexBufferObject(createFloatBuffer());
        createElementBufferObject(createIndices());

        int positionsSize = 3, colorSize = 4, floatSizeBytes = 4, vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    private void createVertexArrayObject() {
        _vertexArrayObjectID = glGenVertexArrays();
        glBindVertexArray(_vertexArrayObjectID);
    }

    private FloatBuffer createFloatBuffer() {
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Vertex vertex : _vertexes)
                    length += vertex.get_positions().length + vertex.get_colors().length;

                return length;
            }
        }.get());
        vertexBuffer.put(vertexIntoGraphicFormat()).flip();

        return vertexBuffer;
    }

    private void createVertexBufferObject(FloatBuffer vertexBuffer) {
        _vertexBufferObjectID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _vertexBufferObjectID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
    }

    private void createElementBufferObject(IntBuffer elementBuffer) {
        _elementBufferObjectID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _elementBufferObjectID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    }

    private IntBuffer createIndices() {
        int amountOfElements = new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Element element : _elements)
                    length += element.get_nodes().length;

                return length;
            }
        }.get();
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(amountOfElements);

        int[] elements = new int[amountOfElements];
        int elementIndex = 0;

        for (Element element : _elements) {
            for (int i = 0; i < element.get_nodes().length; i++) {
                elements[elementIndex] = element.get_node(i);
                elementIndex++;
            }
        }

        elementBuffer.put(elements).flip();

        return elementBuffer;
    }

    public float[] vertexIntoGraphicFormat() {
        float[] data = new float[new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Vertex vertex : _vertexes)
                    length += vertex.get_positions().length + vertex.get_colors().length;

                return length;
            }
        }.get()];

        int dataIndex = 0;

        for (Vertex vertex : _vertexes) {
            for (int i = 0; i < vertex.get_positions().length + vertex.get_colors().length; i++) {
                data[dataIndex] = i < 3
                        ? vertex.get_positions()[i]
                        : vertex.get_colors()[i - 3];
                dataIndex++;
            }
        }

        return data;
    }

    private void initiateVertex() {
        _vertexID = glCreateShader(GL_VERTEX_SHADER);
        passShaderSourceToGPU(_vertexID);
        checkInitiationStatus(_vertexID, "Vertex");
    }

    private void initiateFragment() {
        _fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        passShaderSourceToGPU(_fragmentID);
        checkInitiationStatus(_fragmentID, "Fragment");
    }

    private void passShaderSourceToGPU(int id) {
        glShaderSource(id, id == _vertexID ? _vertexSource : _fragmentSource);
        glCompileShader(id);
    }

    private void linkProgram() {
        _program = glCreateProgram();
        glAttachShader(_program, _vertexID);
        glAttachShader(_program, _fragmentID);
        glLinkProgram(_program);
        checkInitiationStatus(_program, "program link");
    }

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
