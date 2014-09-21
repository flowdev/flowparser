package org.flowdev.flowparser.util;

import org.flowdev.flowparser.data.Connection;
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

    public static PortData emptyPort() {
        return null;
    }

    public static PortData defaultInPort() {
        return newPort("in");
    }

    public static PortData defaultOutPort() {
        return newPort("out");
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

    public static void movePortIn2Out(PortPair port) {
        port.outPort(port.inPort()).inPort(null);
    }

    public static void copyPortIn2Out(PortPair src, PortPair dst) {
        dst.outPort(src.inPort());
    }

    public static void copyPortOut2From(PortPair src, Connection dst) {
        dst.fromPort(src.outPort());
    }

    public static void copyPortIn2To(PortPair src, Connection dst) {
        dst.toPort(src.inPort());
    }

    public static void copyPortFrom2To(Connection conn) {
        conn.toPort(conn.fromPort());
    }
}
