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

    public static PortData defaultInPort() {
        return newPort("in");
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
}
