package org.flowdev.flowparser.util;

import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.PortPair;

import java.util.ArrayList;
import java.util.List;

public class PortUtil {
    public static List<PortPair> makePorts(PortPair port) {
        List<PortPair> ports = new ArrayList<>();
        ports.add(port);
        return ports;
    }

    public static void movePortIn2Out(PortPair port) {
        port.outPort(port.inPort()).hasOutPortIndex(port.hasInPortIndex()).outPortIndex(port.inPortIndex());
        port.inPort(null).hasInPortIndex(false).inPortIndex(0);
    }

    public static void copyPortIn2Out(PortPair src, PortPair dst) {
        dst.outPort(src.inPort()).hasOutPortIndex(src.hasInPortIndex()).outPortIndex(src.inPortIndex());
    }

    public static void copyPortOut2From(PortPair src, Connection dst) {
        dst.fromPort(src.outPort()).hasFromPortIndex(src.hasOutPortIndex()).fromPortIndex(src.outPortIndex());
    }

    public static void copyPortIn2To(PortPair src, Connection dst) {
        dst.toPort(src.inPort()).hasToPortIndex(src.hasInPortIndex()).toPortIndex(src.inPortIndex());
    }

    public static void copyPortFrom2To(Connection conn) {
        conn.toPort(conn.fromPort()).hasToPortIndex(conn.hasFromPortIndex()).toPortIndex(conn.fromPortIndex());
    }
}
