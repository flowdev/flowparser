package org.flowdev.flowparser;

import mouse.runtime.SemanticsBase;
import org.flowdev.flowparser.rawdata.*;

import java.util.ArrayList;
import java.util.List;

class FlowParserSemantics extends SemanticsBase {

    private RawFlowFile flowFile;

    public void init() {
        flowFile = new RawFlowFile();
    }

    public RawFlowFile getFlowFile() {
        return flowFile;
    }

    // -------------------------------------------------------------------
    // start = SpcCom Version Flow+ EOF
    // -------------------------------------------------------------------
    void flowFile() {
        int N = rhsSize() - 1;

        flowFile.setVersion((RawVersion) rhs(1).get());
        flowFile.setFlows(new ArrayList<>(N - 2));

        for (int i = 2; i < N; i++) {
            flowFile.getFlows().add((RawFlow) rhs(i).get());
        }
    }

    // -------------------------------------------------------------------
    // Version = "version" ASpc Natural "." Natural SpcCom
    // -------------------------------------------------------------------
    void version() {
        RawVersion data = new RawVersion();
        data.setSourcePosition(lhs().where(0));
        data.setPolitical((Long) rhs(2).get());
        data.setMajor((Long) rhs(4).get());

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // Flow = FlowStart "{" SpcCom Connections "}" SpcCom
    // -------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    void flow() {
        RawFlow data = new RawFlow();
        data.setSourcePosition(lhs().where(0));
        data.setName((String) rhs(0).get());
        data.setConnections((List<RawConnectionChain>) rhs(3).get());

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // FlowStart = "flow" ASpc BigIdent SpcCom
    // -------------------------------------------------------------------
    void flowStart() {
        lhs().put(rhs(2).text());
    }


    // -------------------------------------------------------------------
    // Connections = ConnectionChain+
    // -------------------------------------------------------------------
    void connections() {
        int n = rhsSize();
        List<RawConnectionChain> data = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            data.add((RawConnectionChain) rhs(i).get());
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionChain = ConnectionChainBeg ConnectionChainMid* OpConnectionChainEnd StmtEnd
    // -------------------------------------------------------------------
    void connectionChain() {
        int n = rhsSize() - 2;
        RawConnectionChain data = (RawConnectionChain) rhs(0).get();

        for (int i = 1; i < n; i++) {
            data.getParts().add((RawConnectionPart) rhs(i).get());
        }

        data.setOutPort((RawPort) rhs(n).get());

        correctDataTypes(data);
        correctChainEnd(data);

        lhs().put(data);
    }

    private void correctDataTypes(RawConnectionChain chain) {
        RawDataType curType = chain.getInPort() != null ? copyDataType(chain.getInPort().getDataType()) : null;
        for (int i = 0; i < chain.getParts().size() - 1; i++) {
            RawConnectionPart curPart = chain.getParts().get(i);
            RawConnectionPart nextPart = chain.getParts().get(i + 1);
            if (nextPart.getInPort().getDataType() != null) {
                curPart.getOutPort().setDataType(nextPart.getInPort().getDataType());
                curType = copyDataType(nextPart.getInPort().getDataType());
            } else {
                curPart.getOutPort().setDataType(curType);
                nextPart.getInPort().setDataType(curType);
            }
        }
    }

    private void correctChainEnd(RawConnectionChain chain) {
        RawConnectionPart lastPart = chain.getParts().get(chain.getParts().size() - 1);
        if (chain.getOutPort() == null) {
            lastPart.setOutPort(null);
        } else {
            lastPart.getOutPort().setDataType(chain.getOutPort().getDataType());
            if (chain.getOutPort().getName() == null) {
                chain.getOutPort().setName(lastPart.getOutPort().getName());
                chain.getOutPort().setIndex(lastPart.getOutPort().getIndex());
            }
            if (lastPart.getOutPort().getDataType() == null && lastPart.getInPort() != null) {
                RawDataType dataType = copyDataType(lastPart.getInPort().getDataType());
                lastPart.getOutPort().setDataType(dataType);
                chain.getOutPort().setDataType(dataType);
            }
        }
    }

    private RawDataType copyDataType(RawDataType dataType) {
        if (dataType == null) {
            return null;
        }
        RawDataType ret = new RawDataType();
        ret.setType(dataType.getType());
        ret.setSourcePosition(dataType.getSourcePosition());
        ret.setFromDsl(false);
        return ret;
    }


    // -------------------------------------------------------------------
    // ConnectionChainBegMax = OpPort ConnectionChainMid
    // -------------------------------------------------------------------
    void connectionChainBegMax() {
        RawConnectionChain data = new RawConnectionChain();
        data.setSourcePosition(lhs().where(0));
        data.setInPort((RawPort) rhs(0).get());
        data.getInPort().setType(RawPortType.IN);
        data.setParts(new ArrayList<>());
        RawConnectionPart part = (RawConnectionPart) rhs(1).get();
        data.getParts().add(part);
        data.getInPort().setDataType(part.getInPort().getDataType());
        if (data.getInPort().getName() == null) {
            data.getInPort().setName(part.getInPort().getName());
            data.getInPort().setIndex(part.getInPort().getIndex());
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionChainBegMin = OperationNameParens OpPort
    // -------------------------------------------------------------------
    void connectionChainBegMin() {
        RawConnectionChain data = new RawConnectionChain();
        data.setSourcePosition(lhs().where(0));
        data.setInPort(null);
        data.setParts(new ArrayList<>());
        RawConnectionPart part = new RawConnectionPart();
        data.getParts().add(part);
        part.setInPort(null);
        part.setOperation((RawOperation) rhs(0).get());
        part.setOutPort((RawPort) rhs(1).get());
        part.getOutPort().setType(RawPortType.OUT);
        if (part.getOutPort().getName() == null) {
            part.getOutPort().setName(RawPortType.OUT.toString());
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionChainMid = Arrow ConnectionPart
    // -------------------------------------------------------------------
    void connectionChainMid() {
        RawConnectionPart data = (RawConnectionPart) rhs(1).get();
        data.getInPort().setDataType((RawDataType) rhs(0).get());

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // OpConnectionChainEnd = ConnectionChainEnd?
    // -------------------------------------------------------------------
    void opConnectionChainEnd() {
        if (rhsSize() > 0) {
            lhs().put(rhs(0).get());
        }
    }

    // -------------------------------------------------------------------
    // ConnectionChainEnd = Arrow OpPort
    // -------------------------------------------------------------------
    void connectionChainEnd() {
        RawPort data = (RawPort) rhs(1).get();
        data.setDataType((RawDataType) rhs(0).get());
        data.setType(RawPortType.OUT);

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionPart		= OpPortSpc OperationNameParens OpPort
    // -------------------------------------------------------------------
    void connectionPart() {
        RawConnectionPart data = new RawConnectionPart();
        data.setSourcePosition(lhs().where(0));
        data.setInPort((RawPort) rhs(0).get());
        data.getInPort().setType(RawPortType.IN);
        if (data.getInPort().getName() == null) {
            data.getInPort().setName(RawPortType.IN.toString());
        }

        data.setOperation((RawOperation) rhs(1).get());

        data.setOutPort((RawPort) rhs(2).get());
        data.getOutPort().setType(RawPortType.OUT);
        if (data.getOutPort().getName() == null) {
            data.getOutPort().setName(RawPortType.OUT.toString());
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // OperationNameParens = OpOperationName "(" Spc OpOperationType ")" Spc
    // -------------------------------------------------------------------
    void operationNameParens() {
        RawOperation data = new RawOperation();
        data.setSourcePosition(lhs().where(0));
        data.setName((String) rhs(0).get());
        data.setType((RawDataType) rhs(3).get());

        if (data.getName() == null || data.getName().isEmpty()) {
            if (data.getType().getType() == null || data.getType().getType().isEmpty()) {
                throw new RuntimeException("Operation name AND type are missing at: " + data.getSourcePosition());
            }
            data.setName(decapitalize(data.getType().getType()));
        }

        lhs().put(data);
    }

    private static String decapitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }


    // -------------------------------------------------------------------
    // Arrow = SpcCom OpDataType "->" SpcCom
    // -------------------------------------------------------------------
    public void arrow() {
        lhs().put(rhs(1).get());
    }

    // -------------------------------------------------------------------
    // OpDataType = DataType?
    // -------------------------------------------------------------------
    public void opDataType() {
        if (rhsSize() > 0) {
            lhs().put(rhs(0).get());
        }
    }

    // -------------------------------------------------------------------
    // DataType = "[" Spc BigIdent Spc "]"
    // -------------------------------------------------------------------
    public void dataType() {
        RawDataType data = new RawDataType();
        data.setType(rhs(2).text());
        data.setFromDsl(true);
        data.setSourcePosition(lhs().where(2));
        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // OpOperationType = OperationType?
    // -------------------------------------------------------------------
    public void opOperationType() {
        if (rhsSize() > 0) {
            lhs().put(rhs(0).get());
        }
    }

    // -------------------------------------------------------------------
    // OperationType = BigIdent Spc
    // -------------------------------------------------------------------
    public void operationType() {
        RawDataType data = new RawDataType();
        data.setType(rhs(0).text());
        data.setFromDsl(true);
        data.setSourcePosition(lhs().where(0));
        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // OpPortSpc = PortSpc?
    // -------------------------------------------------------------------
    public void opPortSpc() {
        opPort();
    }

    // -------------------------------------------------------------------
    // PortSpc = Port ASpc
    // -------------------------------------------------------------------
    public void portSpc() {
        lhs().put(rhs(0).get());
    }

    // -------------------------------------------------------------------
    // OpPort = Port?
    // -------------------------------------------------------------------
    void opPort() {
        RawPort data;

        if (rhsSize() > 0) {
            data = (RawPort) rhs(0).get();
        } else {
            data = new RawPort();
            data.setSourcePosition(lhs().where(0));
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // Port = SmallIdent PortNum?
    // -------------------------------------------------------------------
    void port() {
        RawPort data = new RawPort();
        data.setSourcePosition(lhs().where(0));
        data.setName(rhs(0).text());
        if (rhsSize() > 1) {
            data.setIndex((Integer) rhs(1).get());
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // PortNum = "." Natural
    // -------------------------------------------------------------------
    void portNum() {
        long l = (Long) rhs(1).get();
        if (l < 0 || l > Integer.MAX_VALUE) {
            throw new RuntimeException("Invalid port number '" + l + "' found at " + rhs(1).where(0));
        }
        lhs().put((int) l);
    }

    // -------------------------------------------------------------------
    // OpPort = PortName?
    // -------------------------------------------------------------------
    void opOperationName() {
        if (rhsSize() > 0) {
            lhs().put(rhs(0).get());
        }
    }

    // -------------------------------------------------------------------
    // OperationName = SmallIdent Spc
    // -------------------------------------------------------------------
    void operationName() {
        lhs().put(rhs(0).text());
    }


    // -------------------------------------------------------------------
    // String = ["] Char* ["]
    // -------------------------------------------------------------------
    void unescapeString() {
        int N = rhsSize() - 1;
        StringBuilder sb = new StringBuilder(N);

        for (int i = 1; i < N; i++) {
            sb.append(rhs(i).get());
        }

        lhs().put(sb.toString());
    }

    // -------------------------------------------------------------------
    // Char = "\ u" Hex Hex Hex Hex
    // -------------------------------------------------------------------
    void unicodeChar() {
        try {
            String num = rhsText(1, rhsSize());
            int codePoint = Integer.parseInt(num, 16);
            lhs().put(new String(Character.toChars(codePoint)));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e); // should never happen!
        }
    }

    // -------------------------------------------------------------------
    // Char = "\t"
    // -------------------------------------------------------------------
    void tabChar() {
        lhs().put("\t");
    }

    // -------------------------------------------------------------------
    // Char = "\n"
    // -------------------------------------------------------------------
    void newlineChar() {
        lhs().put("\n");
    }

    // -------------------------------------------------------------------
    // Char = "\r"
    // -------------------------------------------------------------------
    void carriagereturnChar() {
        lhs().put("\r");
    }

    // -------------------------------------------------------------------
    // Char = !"\ u" "\" _
    // -------------------------------------------------------------------
    void backslashChar() {
        lhs().put(rhs(1).text());
    }

    // -------------------------------------------------------------------
    // Char = ^[\r\n\\"]
    // -------------------------------------------------------------------
    void simpleChar() {
        lhs().put(rhs(0).text());
    }

    // -------------------------------------------------------------------
    // Int = [-+]? Natural
    // -------------------------------------------------------------------
    void integer() {
        RawInt data = new RawInt();
        data.setSourcePosition(lhs().where(0));
        data.setValue((Long) rhs(rhsSize() - 1).get());

        if ("-".equals(rhs(0).text())) {
            data.setValue(-data.getValue());
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // Natural = "0x" Hex+ ("_"+ Hex+)*
    // -------------------------------------------------------------------
    void hexInt() {
        try {
            String num = rhsText(1, rhsSize()).replace("_", "");
            lhs().put(Long.parseLong(num, 16));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e); // should never happen!
        }
    }

    // -------------------------------------------------------------------
    // Natural = "0b" Binary+ ("_"+ Binary+)*
    // -------------------------------------------------------------------
    void binaryInt() {
        try {
            String num = rhsText(1, rhsSize()).replace("_", "");
            lhs().put(Long.parseLong(num, 2));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e); // should never happen!
        }
    }

    // -------------------------------------------------------------------
    // Natural = "0c" Octal+ ("_"+ Octal+)*
    // -------------------------------------------------------------------
    void octalInt() {
        try {
            String num = rhsText(1, rhsSize()).replace("_", "");
            lhs().put(Long.parseLong(num, 8));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e); // should never happen!
        }
    }

    // -------------------------------------------------------------------
    // Natural = Decimal+ ("_"+ Decimal+)*
    // -------------------------------------------------------------------
    void decimalInt() {
        try {
            String num = lhs().text().replace("_", "");
            lhs().put(Long.parseLong(num, 10));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e); // should never happen!
        }
    }
}
