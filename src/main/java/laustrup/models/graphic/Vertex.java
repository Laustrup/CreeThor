package laustrup.models.graphic;

import lombok.Getter;
import lombok.ToString;

/** Defines positions at the screen for use. */
@ToString
public class Vertex {

    /** The positions for this Vertex. */
    @Getter
    private float[] _positions;

    /** The colours applied to the positions of this Vertex. */
    @Getter
    private float[] _colors;

    /**
     * Initiates positions and colors.
     * @param positions The positions for this Vertex.
     * @param colors The colors for the positions of this Vertex.
     */
    public Vertex(float[] positions, float[] colors) {
        _positions = positions;
        _colors = colors;
    }

    /**
     * Appends new position and color data to this Vertex.
     * Length of positions must be modulus 3 == 0 and colors modulus 4 == 0.
     * @param positions The position values of the screen to be added.
     * @param colors The colors assign for the appending positions.
     * @return The positions and colors of this Vertex.
     */
    public float[][] append(float[] positions, float[] colors) {
        if (3 % positions.length == 0 && 4 % colors.length == 0) {
            float[] positionStorage = new float[_positions.length+ positions.length],
                    colorStorage = new float[_colors.length+colors.length];

            for (int i = 0; i < positionStorage.length; i++)
                positionStorage[i] = i < _positions.length ? _positions[i] : positions[i];
            for (int i = 0; i < colorStorage.length; i++)
                colorStorage[i] = i < _colors.length ? _colors[i] : colors[i];

            _positions = positionStorage;
            _colors = colorStorage;
        }

        return new float[][]{
            _positions,
            _colors
        };
    }
}
