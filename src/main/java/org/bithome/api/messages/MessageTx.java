package org.bithome.api.messages;

import com.google.common.base.Optional;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public abstract class MessageTx extends Message {

    protected MessageTx(final long destinationNodeId) {
        super(Optional.<Long>absent(), Optional.of(destinationNodeId));
    }

    public abstract byte[] getBytes();
}
