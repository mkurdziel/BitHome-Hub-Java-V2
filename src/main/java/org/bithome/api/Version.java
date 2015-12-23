package org.bithome.api;

/**
 * Created by Mike Kurdziel on 5/19/14.
 */
public class Version implements Comparable<Version>{
    private int majorVersion;
    private int minorVersion;

    public Version(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public String toString() {
        return String.format("%d.%d", majorVersion, minorVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;

        Version version = (Version) o;

        return compareTo(version) == 0;
    }

    @Override
    public int hashCode() {
        int result = majorVersion;
        result = 31 * result + minorVersion;
        return result;
    }

    @Override
    public int compareTo(Version other) {
        if (this == other) return 0;

        if (this.majorVersion > other.majorVersion) {
            return 1;
        }

        if (this.majorVersion < other.majorVersion) {
            return -1;
        }

        if (this.minorVersion > other.minorVersion) {
            return 1;
        }

        if (this.minorVersion < other.minorVersion) {
            return -1;
        }

        return 0;
    }
}
