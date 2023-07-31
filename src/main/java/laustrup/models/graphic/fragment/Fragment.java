package laustrup.models.graphic.fragment;

import lombok.Getter;
import lombok.ToString;

/**
 * Defines a collection of up to three vertexes,
 * which defines the positions shared for a figure with shaders.
 */
@ToString
public class Fragment {

    /** The positions of the vertex for this fragment. */
    @Getter
    private int[] _nodes;

    /** An empty constructor, that only initializes the node array. */
    public Fragment() {
        _nodes = new int[0];
    }

    /**
     * Initiates the node array with the input.
     * @param nodes The nodes to be initiated in this Fragment.
     */
    public Fragment(int[] nodes) {
        _nodes = nodes;
    }

    /**
     * Finds a specified node.
     * @param index The index of the node in the array to be found.
     * @return The found node.
     */
    public int get_node(int index) {
        return _nodes[index];
    }
}
