package org.flowdev.flowparser.util;

import org.flowdev.flowparser.data.PortData;

public class PortUtil {

    public static PortData defaultInPort(int srcPos) {
        return newPort(srcPos, "in");
    }

    public static PortData defaultOutPort(int srcPos) {
        return newPort(srcPos, "out");
    }

    public static PortData newPort(int srcPos, String name, boolean hasIndex, int index) {
        PortData port = new PortData().srcPos(srcPos).name(name).capName(capString(name));
        if (hasIndex) {
            return port.hasIndex(true).index(index);
        }
        return port;
    }

    public static PortData newPort(int srcPos, String name, int index) {
        return newPort(srcPos, name, true, index);
    }

    public static PortData newPort(int srcPos, String name) {
        return newPort(srcPos, name, false, 0);
    }

    public static PortData copyPort(PortData srcPort, int srcPos) {
        return newPort(srcPos, srcPort.name(), srcPort.hasIndex(), srcPort.index());
    }

    /**
     * Compares two ports and returns the result:
     * true  if they are logically equal (name and optionally index),
     * false if they aren't equal (name or index) and
     * null  if they are illegal in the same flow (the name is equal but only one of them has got an index).
     *
     * @param port1 first port to compare.
     * @param port2 second port to compare.
     * @return the result of the comparison.
     */
    public static Boolean equalPorts(PortData port1, PortData port2) {
        if (port1 == null && port2 == null) {
            return true;
        }
        if (port1 == null || port2 == null) {
            return false;
        }
        if (!equalObj(port1.name(), port2.name())) {
            return false;
        }
        if (port1.hasIndex() != port2.hasIndex()) {
            return null;
        }
        if (!port1.hasIndex()) {
            return true;
        }
        return port1.index() == port2.index();
    }

    private static String capString(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static boolean equalObj(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }
}
