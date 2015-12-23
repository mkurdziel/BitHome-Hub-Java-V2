package org.bithome.core.services.storage;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.lifecycle.Managed;
import org.bithome.api.node.nodes.Node;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public interface StorageService extends Managed {
    void saveNode(Node node);

    void unsaveNode(Node node);

    ImmutableSet<Node> getNodes();
}
