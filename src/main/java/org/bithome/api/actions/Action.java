package org.bithome.api.actions;

import com.google.common.base.Optional;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by Mike Kurdziel on 5/19/14.
 */
public class Action {

    public Optional<Pair<Integer, Integer>> getNextUnknownParameter() {
        return Optional.absent();
    }
}
