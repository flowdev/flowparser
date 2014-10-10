package org.flowdev.flowparser.util;

import org.flowdev.flowparser.data.PortData;
import org.flowdev.flowparser.data.PortPair;

import java.util.ArrayList;
import java.util.List;

public class PortUtil {

    public static List<PortPair> makePorts(PortPair port) {
        List<PortPair> ports = new ArrayList<>();
        ports.add(port);
        return ports;
    }

    public static PortData defaultInPort(int srcPos) {
        return newPort("in").srcPos(srcPos);
    }

    public static PortData defaultOutPort(int srcPos) {
        return newPort("out").srcPos(srcPos);
    }

    public static PortData newPort(String name, boolean hasIndex, int index) {
        return new PortData().name(name).hasIndex(hasIndex).index(index);
    }

    public static PortData newPort(String name, int index) {
        return new PortData().name(name).hasIndex(true).index(index);
    }

    public static PortData newPort(String name) {
        return new PortData().name(name).hasIndex(false);
    }

    public static PortData copyPort(PortData srcPort, int srcPos) {
        return newPort(srcPort.name(), srcPort.hasIndex(), srcPort.index()).srcPos(srcPos);
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
