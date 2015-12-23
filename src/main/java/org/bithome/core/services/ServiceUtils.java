package org.bithome.core.services;

import org.apache.commons.math3.random.MersenneTwister;

/**
 * User: Mike Kurdziel
 * Date: 4/19/14
 */
public class ServiceUtils {

    private static final long MAX = 9223372036854775807L;
    private static final MersenneTwister random = new MersenneTwister();

    public static long getRandomId() {
        return (long)(random.nextDouble()*MAX);
    }
}