package org.bithome.api.messages;

import com.google.common.base.Optional;
import org.bithome.api.protocol.MessageApi;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by Mike Kurdziel on 5/21/14.
 */
public abstract class Message implements Serializable {
    private final Optional<Long> sourceNode;
    private final Optional<Long> destNode;
    private DateTime timeStamp;

    protected Message(final Optional<Long> sourceNode, final Optional<Long> destNode) {
        this.timeStamp = DateTime.now();
        this.sourceNode = sourceNode;
        this.destNode = destNode;
    }

    protected Message() {
        this.timeStamp = DateTime.now();
        this.sourceNode = Optional.absent();
        this.destNode = Optional.absent();
    }

    protected Message(final DateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.sourceNode = Optional.absent();
        this.destNode = Optional.absent();
    }

    public Optional<Long> getSourceNode() {
        return sourceNode;
    }

    public Optional<Long> getDestNode() {
        return destNode;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public abstract MessageApi getMessageApi();
}
