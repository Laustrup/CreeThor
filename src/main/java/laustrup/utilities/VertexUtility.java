package laustrup.utilities;

import laustrup.models.graphic.Vertex;
import laustrup.utilities.collections.lists.Liszt;

import java.util.function.Supplier;

public class VertexUtility {

    public static float[] vertexIntoGraphicFormat(Liszt<Vertex> vertexes) {
        float[] data = new float[new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Vertex vertex : vertexes)
                    length += vertex.get_positions().length + vertex.get_colors().length;

                return length;
            }
        }.get()];

        int dataIndex = 0;

        for (Vertex vertex : vertexes) {
            for (int i = 0; i < vertex.get_positions().length + vertex.get_colors().length; i++) {
                data[dataIndex] = i < 3
                        ? vertex.get_positions()[i]
                        : vertex.get_colors()[i - 3];
                dataIndex++;
            }
        }

        return data;
    }
}
