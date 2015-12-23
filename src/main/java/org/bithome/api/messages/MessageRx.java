package org.bithome.api.messages;

import com.google.common.base.Optional;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public abstract class MessageRx extends Message {

    protected MessageRx(final long sourceNodeId) {
        super(Optional.of(sourceNodeId), Optional.<Long>absent());
    }
}
