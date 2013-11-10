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

        flowFile.version = (RawVersion) rhs(1).get();
        flowFile.flows = new ArrayList<>(N - 2);

        for (int i = 2; i < N; i++) {
            flowFile.flows.add((RawFlow) rhs(i).get());
        }
    }

    // -------------------------------------------------------------------
    // Version = "version" ASpc Natural "." Natural SpcCom
    // -------------------------------------------------------------------
    void version() {
        RawVersion data = new RawVersion();
        data.sourcePosition = lhs().where(0);
        data.political = (Long) rhs(2).get();
        data.major = (Long) rhs(4).get();

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // Flow = FlowStart "{" SpcCom Connections "}" SpcCom
    // -------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    void flow() {
        RawFlow data = new RawFlow();
        data.sourcePosition = lhs().where(0);
        data.name = (String) rhs(0).get();
        data.connections = (List<RawConnectionChain>) rhs(3).get();

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
            data.parts.add((RawConnectionPart) rhs(i).get());
        }

        data.outPort = (RawPort) rhs(n).get();

        correctDataTypes(data);
        correctChainEnd(data);

        lhs().put(data);
    }
    private void correctDataTypes(RawConnectionChain chain) {
        RawDataType curType = chain.inPort != null ? chain.inPort.dataType : null;
        for (int i = 0; i < chain.parts.size() - 1; i++) {
            RawConnectionPart nextPart = chain.parts.get(i+1);
            RawConnectionPart curPart = chain.parts.get(i);
            if (nextPart.inPort.dataType != null) {
                curType = nextPart.inPort.dataType;
                curPart.outPort.dataType = curType;
            } else {
                curPart.outPort.dataType = curType;
                nextPart.inPort.dataType = curType;
            }
        }
    }
    private void correctChainEnd(RawConnectionChain chain) {
        RawConnectionPart lastPart = chain.parts.get(chain.parts.size()-1);
        if (chain.outPort == null) {
            lastPart.outPort = null;
        } else {
            lastPart.outPort.dataType = chain.outPort.dataType;
            if (chain.outPort.name == null) {
                chain.outPort.name = lastPart.outPort.name;
            }
            if (lastPart.outPort.dataType == null && lastPart.inPort != null) {
                lastPart.outPort.dataType = lastPart.inPort.dataType;
                chain.outPort.dataType = lastPart.inPort.dataType;
            }
        }
    }


    // -------------------------------------------------------------------
    // ConnectionChainBegMax = OpPort ConnectionChainMid
    // -------------------------------------------------------------------
    void connectionChainBegMax() {
        RawConnectionChain data = new RawConnectionChain();
        data.sourcePosition = lhs().where(0);
        data.inPort = (RawPort) rhs(0).get();
        data.inPort.type = RawPortType.IN;
        data.parts = new ArrayList<>();
        RawConnectionPart part = (RawConnectionPart) rhs(1).get();
        data.parts.add(part);
        data.inPort.dataType = part.inPort.dataType;
        if (data.inPort.name == null) {
            data.inPort.name = part.inPort.name;
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionChainBegMin = OperationNameParens OpPort
    // -------------------------------------------------------------------
    void connectionChainBegMin() {
        RawConnectionChain data = new RawConnectionChain();
        data.sourcePosition = lhs().where(0);
        data.inPort = null;
        data.parts = new ArrayList<>();
        RawConnectionPart part = new RawConnectionPart();
        data.parts.add(part);
        part.inPort = null;
        part.operation = (RawOperation) rhs(0).get();
        part.outPort = (RawPort) rhs(1).get();
        part.outPort.type = RawPortType.OUT;
        if (part.outPort.name == null) {
            part.outPort.name = RawPortType.OUT.toString();
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionChainMid = Arrow ConnectionPart
    // -------------------------------------------------------------------
    void connectionChainMid() {
        RawConnectionPart data = (RawConnectionPart) rhs(1).get();
        data.inPort.dataType = (RawDataType) rhs(0).get();

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
        data.dataType = (RawDataType) rhs(0).get();
        data.type = RawPortType.OUT;

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // ConnectionPart		= OpPortSpc OperationNameParens OpPort
    // -------------------------------------------------------------------
    void connectionPart() {
        RawConnectionPart data = new RawConnectionPart();
        data.sourcePosition = lhs().where(0);
        data.inPort = (RawPort) rhs(0).get();
        data.inPort.type = RawPortType.IN;
        if (data.inPort.name == null) {
            data.inPort.name = RawPortType.IN.toString();
        }

        data.operation = (RawOperation) rhs(1).get();

        data.outPort = (RawPort) rhs(2).get();
        data.outPort.type = RawPortType.OUT;
        if (data.outPort.name == null) {
            data.outPort.name = RawPortType.OUT.toString();
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // OperationNameParens = OperationName "(" Spc OpOperationType ")" Spc
    // -------------------------------------------------------------------
    void operationNameParens() {
        RawOperation data = new RawOperation();
        data.sourcePosition = lhs().where(0);
        data.name =  (String) rhs(0).get();
        data.type = (RawDataType) rhs(3).get();

        lhs().put(data);
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
        data.type = rhs(2).text();
        data.sourcePosition = lhs().where(2);
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
        data.type = rhs(0).text();
        data.sourcePosition = lhs().where(0);
        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // OpPortSpc = PortSpc?
    // -------------------------------------------------------------------
    public void opPortSpc() {
        RawPort data = new RawPort();
        data.sourcePosition = lhs().where(0);

        if (rhsSize() > 0) {
            data.name = (String) rhs(0).get();
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // PortSpc = PortName ASpc
    // -------------------------------------------------------------------
    public void portSpc() {
        lhs().put(rhs(0).get());
    }

    // -------------------------------------------------------------------
    // OpPort = PortName?
    // -------------------------------------------------------------------
    void opPort() {
        RawPort data = new RawPort();
        data.sourcePosition = lhs().where(0);

        if (rhsSize() > 0) {
            data.name = (String) rhs(0).get();
        }

        lhs().put(data);
    }

    // -------------------------------------------------------------------
    // PortName = SmallIdent
    // -------------------------------------------------------------------
    void portName() {
        lhs().put(rhs(0).text());
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
        data.sourcePosition = lhs().where(0);
        data.value = (Long) rhs(rhsSize() - 1).get();

        if ("-".equals(rhs(0).text())) {
            data.value = -data.value;
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
