package org.jetbrains.haskell.util;

import java.util.List;

public final class GHCVersion implements Comparable<GHCVersion> {

    private final List<Integer> version;

    GHCVersion(List<Integer> version) {
        this.version = version;
    }

    public boolean equals(Object obj) {
        if (obj instanceof GHCVersion) {
            GHCVersion that = (GHCVersion) obj;
            List<Integer> v1 = this.version;
            List<Integer> v2 = that.version;
            if (v1.size() != v2.size())
                return false;
            for (int i = 0; i < v1.size(); i++) {
                if (!v1.get(i).equals(v2.get(i)))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 1;
        for (Integer v : version) {
            result = 31 * result + v.intValue();
        }
        return result;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Integer v : version) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(v);
        }
        return buf.toString();
    }

    public int compareTo(GHCVersion that) {
        List<Integer> v1 = this.version;
        List<Integer> v2 = that.version;
        int minSize = Math.min(v1.size(), v2.size());
        for (int i = 0; i < minSize; i++) {
            int compare = v1.get(i).compareTo(v2.get(i));
            if (compare != 0)
                return compare;
        }
        return v1.size() - v2.size();
    }
}
