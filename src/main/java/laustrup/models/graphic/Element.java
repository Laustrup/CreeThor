package laustrup.models.graphic;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Element {

    @Getter
    private int[] _nodes;

    public Element(int[] nodes) {
        _nodes = nodes;
    }

    public int get_node(int index) {
        return _nodes[index];
    }
}
