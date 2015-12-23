package org.bithome.api.node.nodes;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public class BroadcastNode extends Node {
    public static final String NAME_STR = "Broadcast Node";

    public BroadcastNode() {
        this.setName(NAME_STR);
    }

    @Override
    public Node clone() {
        return null;
    }
}
