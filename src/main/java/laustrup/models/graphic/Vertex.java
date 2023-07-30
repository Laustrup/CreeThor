package laustrup.models.graphic;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Vertex {

    @Getter
    private float[] _positions, _colors;

    public Vertex(float[] positions, float[] colors) {
        _positions = positions;
        _colors = colors;
    }

    public float[][] add(float position, float color) {
        return add(new float[]{position}, new float[]{color});
    }

    public float[][] add(float[] positions, float[] colors) {
        float[] positionStorage = new float[_positions.length+ positions.length],
                colorStorage = new float[_colors.length+colors.length];

        for (int i = 0; i < positionStorage.length; i++)
            positionStorage[i] = i < _positions.length ? _positions[i] : positions[i];
        for (int i = 0; i < colorStorage.length; i++)
            colorStorage[i] = i < _colors.length ? _colors[i] : colors[i];

        _positions = positionStorage;
        _colors = colorStorage;

        return new float[][]{
            _positions,
            _colors
        };
    }
}
