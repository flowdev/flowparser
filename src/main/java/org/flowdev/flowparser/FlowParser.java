//=========================================================================
//
//  This file was generated by Mouse 1.5 at 2013-11-10 20:11:29 GMT
//  from grammar
//    '/home/ole/work/flowdev/flowparser/src/main/peg/flowdsl.peg'.
//
//=========================================================================

package org.flowdev.flowparser;

import mouse.runtime.Source;

public class FlowParser extends mouse.runtime.ParserBase {
    final FlowParserSemantics sem;

    //=======================================================================
    //
    //  Initialization
    //
    //=======================================================================
    //-------------------------------------------------------------------
    //  Constructor
    //-------------------------------------------------------------------
    public FlowParser() {
        sem = new FlowParserSemantics();
        sem.rule = this;
        super.sem = sem;
    }

    //-------------------------------------------------------------------
    //  Run the parser
    //-------------------------------------------------------------------
    public boolean parse(Source src) {
        super.init(src);
        sem.init();
        if (start()) return true;
        return failure();
    }

    //-------------------------------------------------------------------
    //  Get semantics
    //-------------------------------------------------------------------
    public FlowParserSemantics semantics() {
        return sem;
    }

    //=======================================================================
    //
    //  Parsing procedures
    //
    //=======================================================================
    //=====================================================================
    //  start = SpcCom Version Flow+ EOF {flowFile} ;
    //=====================================================================
    private boolean start() {
        begin("start");
        SpcCom();
        if (!Version()) return reject();
        if (!Flow()) return reject();
        while (Flow()) ;
        if (!EOF()) return reject();
        sem.flowFile();
        return accept();
    }

    //=====================================================================
    //  Version = "version" ASpc Natural "." Natural SpcCom {version} ;
    //=====================================================================
    private boolean Version() {
        begin("Version");
        if (!next("version")) return reject();
        if (!ASpc()) return reject();
        if (!Natural()) return reject();
        if (!next('.')) return reject();
        if (!Natural()) return reject();
        SpcCom();
        sem.version();
        return accept();
    }

    //=====================================================================
    //  Flow = FlowStart "{" SpcCom Connections "}" SpcCom {flow} ;
    //=====================================================================
    private boolean Flow() {
        begin("Flow");
        if (!FlowStart()) return reject();
        if (!next('{')) return reject();
        SpcCom();
        if (!Connections()) return reject();
        if (!next('}')) return reject();
        SpcCom();
        sem.flow();
        return accept();
    }

    //=====================================================================
    //  FlowStart = "flow" ASpc BigIdent SpcCom {flowStart} ;
    //=====================================================================
    private boolean FlowStart() {
        begin("FlowStart");
        if (!next("flow")) return reject();
        if (!ASpc()) return reject();
        if (!BigIdent()) return reject();
        SpcCom();
        sem.flowStart();
        return accept();
    }

    //=====================================================================
    //  Connections = ConnectionChain+ {connections} ;
    //=====================================================================
    private boolean Connections() {
        begin("Connections");
        if (!ConnectionChain()) return reject();
        while (ConnectionChain()) ;
        sem.connections();
        return accept();
    }

    //=====================================================================
    //  ConnectionChain = ConnectionChainBeg ConnectionChainMid*
    //    OpConnectionChainEnd StmtEnd {connectionChain} ;
    //=====================================================================
    private boolean ConnectionChain() {
        begin("ConnectionChain");
        if (!ConnectionChainBeg()) return reject();
        while (ConnectionChainMid()) ;
        OpConnectionChainEnd();
        if (!StmtEnd()) return reject();
        sem.connectionChain();
        return accept();
    }

    //=====================================================================
    //  ConnectionChainBeg = OpPort ConnectionChainMid
    //    {connectionChainBegMax} / OperationNameParens OpPort
    //    {connectionChainBegMin} ;
    //=====================================================================
    private boolean ConnectionChainBeg() {
        begin("ConnectionChainBeg");
        if (ConnectionChainBeg_0()) {
            sem.connectionChainBegMax();
            return accept();
        }
        if (ConnectionChainBeg_1()) {
            sem.connectionChainBegMin();
            return accept();
        }
        return reject();
    }

    //-------------------------------------------------------------------
    //  ConnectionChainBeg_0 = OpPort ConnectionChainMid
    //-------------------------------------------------------------------
    private boolean ConnectionChainBeg_0() {
        begin("");
        OpPort();
        if (!ConnectionChainMid()) return rejectInner();
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  ConnectionChainBeg_1 = OperationNameParens OpPort
    //-------------------------------------------------------------------
    private boolean ConnectionChainBeg_1() {
        begin("");
        if (!OperationNameParens()) return rejectInner();
        OpPort();
        return acceptInner();
    }

    //=====================================================================
    //  ConnectionChainMid = Arrow ConnectionPart {connectionChainMid} ;
    //=====================================================================
    private boolean ConnectionChainMid() {
        begin("ConnectionChainMid");
        if (!Arrow()) return reject();
        if (!ConnectionPart()) return reject();
        sem.connectionChainMid();
        return accept();
    }

    //=====================================================================
    //  OpConnectionChainEnd = ConnectionChainEnd? {opConnectionChainEnd}
    //    ;
    //=====================================================================
    private boolean OpConnectionChainEnd() {
        begin("OpConnectionChainEnd");
        ConnectionChainEnd();
        sem.opConnectionChainEnd();
        return accept();
    }

    //=====================================================================
    //  ConnectionChainEnd = Arrow OpPort {connectionChainEnd} ;
    //=====================================================================
    private boolean ConnectionChainEnd() {
        begin("ConnectionChainEnd");
        if (!Arrow()) return reject();
        OpPort();
        sem.connectionChainEnd();
        return accept();
    }

    //=====================================================================
    //  ConnectionPart = OpPortSpc OperationNameParens OpPort
    //    {connectionPart} ;
    //=====================================================================
    private boolean ConnectionPart() {
        begin("ConnectionPart");
        OpPortSpc();
        if (!OperationNameParens()) return reject();
        OpPort();
        sem.connectionPart();
        return accept();
    }

    //=====================================================================
    //  OperationNameParens = OperationName "(" Spc OpOperationType ")" Spc
    //    {operationNameParens} ;
    //=====================================================================
    private boolean OperationNameParens() {
        begin("OperationNameParens");
        if (!OperationName()) return reject();
        if (!next('(')) return reject();
        Spc();
        OpOperationType();
        if (!next(')')) return reject();
        Spc();
        sem.operationNameParens();
        return accept();
    }

    //=====================================================================
    //  OperationName = SmallIdent Spc {operationName} ;
    //=====================================================================
    private boolean OperationName() {
        begin("OperationName");
        if (!SmallIdent()) return reject();
        Spc();
        sem.operationName();
        return accept();
    }

    //=====================================================================
    //  OpOperationType = OperationType? {opOperationType} ;
    //=====================================================================
    private boolean OpOperationType() {
        begin("OpOperationType");
        OperationType();
        sem.opOperationType();
        return accept();
    }

    //=====================================================================
    //  OperationType = BigIdent Spc {operationType} ;
    //=====================================================================
    private boolean OperationType() {
        begin("OperationType");
        if (!BigIdent()) return reject();
        Spc();
        sem.operationType();
        return accept();
    }

    //=====================================================================
    //  Arrow = SpcCom OpDataType "->" SpcCom {arrow} ;
    //=====================================================================
    private boolean Arrow() {
        begin("Arrow");
        SpcCom();
        OpDataType();
        if (!next("->")) return reject();
        SpcCom();
        sem.arrow();
        return accept();
    }

    //=====================================================================
    //  OpDataType = DataType? {opDataType} ;
    //=====================================================================
    private boolean OpDataType() {
        begin("OpDataType");
        DataType();
        sem.opDataType();
        return accept();
    }

    //=====================================================================
    //  DataType = "[" Spc BigIdent Spc "]" {dataType} ;
    //=====================================================================
    private boolean DataType() {
        begin("DataType");
        if (!next('[')) return reject();
        Spc();
        if (!BigIdent()) return reject();
        Spc();
        if (!next(']')) return reject();
        sem.dataType();
        return accept();
    }

    //=====================================================================
    //  OpPortSpc = PortSpc? {opPortSpc} ;
    //=====================================================================
    private boolean OpPortSpc() {
        begin("OpPortSpc");
        PortSpc();
        sem.opPortSpc();
        return accept();
    }

    //=====================================================================
    //  OpPort = PortName? {opPort} ;
    //=====================================================================
    private boolean OpPort() {
        begin("OpPort");
        PortName();
        sem.opPort();
        return accept();
    }

    //=====================================================================
    //  PortSpc = PortName ASpc {portSpc} ;
    //=====================================================================
    private boolean PortSpc() {
        begin("PortSpc");
        if (!PortName()) return reject();
        if (!ASpc()) return reject();
        sem.portSpc();
        return accept();
    }

    //=====================================================================
    //  PortName = SmallIdent {portName} ;
    //=====================================================================
    private boolean PortName() {
        begin("PortName");
        if (!SmallIdent()) return reject();
        sem.portName();
        return accept();
    }

    //=====================================================================
    //  StmtEnd = SpcCom ";" SpcCom ;
    //=====================================================================
    private boolean StmtEnd() {
        begin("StmtEnd");
        SpcCom();
        if (!next(';')) return reject();
        SpcCom();
        return accept();
    }

    //=====================================================================
    //  BigIdent = [A-Z] WordChar+ ;
    //=====================================================================
    private boolean BigIdent() {
        begin("BigIdent");
        if (!nextIn('A', 'Z')) return reject();
        if (!WordChar()) return reject();
        while (WordChar()) ;
        return accept();
    }

    //=====================================================================
    //  SmallIdent = [a-z] WordChar* ;
    //=====================================================================
    private boolean SmallIdent() {
        begin("SmallIdent");
        if (!nextIn('a', 'z')) return reject();
        while (WordChar()) ;
        return accept();
    }

    //=====================================================================
    //  WordChar = [A-Z] / [a-z] / [0-9] ;
    //=====================================================================
    private boolean WordChar() {
        begin("WordChar");
        if (nextIn('A', 'Z')) return accept();
        if (nextIn('a', 'z')) return accept();
        if (nextIn('0', '9')) return accept();
        return reject();
    }

    //=====================================================================
    //  String = ["] Char* ["] {unescapeString} ;
    //=====================================================================
    private boolean String() {
        begin("String");
        if (!next('"')) return reject();
        while (Char()) ;
        if (!next('"')) return reject();
        sem.unescapeString();
        return accept();
    }

    //=====================================================================
    //  Char = "\ u" Hex Hex Hex Hex {unicodeChar} / "\t" {tabChar} / "\n"
    //    {newlineChar} / "\r" {carriagereturnChar} / !"\ u" "\" _
    //    {backslashChar} / ^[\r\n\"] {simpleChar} ;
    //=====================================================================
    private boolean Char() {
        begin("Char");
        if (Char_0()) {
            sem.unicodeChar();
            return accept();
        }
        if (next("\\t")) {
            sem.tabChar();
            return accept();
        }
        if (next("\\n")) {
            sem.newlineChar();
            return accept();
        }
        if (next("\\r")) {
            sem.carriagereturnChar();
            return accept();
        }
        if (Char_1()) {
            sem.backslashChar();
            return accept();
        }
        if (nextNotIn("\r\n\\\"")) {
            sem.simpleChar();
            return accept();
        }
        return reject();
    }

    //-------------------------------------------------------------------
    //  Char_0 = "\ u" Hex Hex Hex Hex
    //-------------------------------------------------------------------
    private boolean Char_0() {
        begin("");
        if (!next("\\u")) return rejectInner();
        if (!Hex()) return rejectInner();
        if (!Hex()) return rejectInner();
        if (!Hex()) return rejectInner();
        if (!Hex()) return rejectInner();
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Char_1 = !"\ u" "\" _
    //-------------------------------------------------------------------
    private boolean Char_1() {
        begin("");
        if (!aheadNot("\\u")) return rejectInner();
        if (!next('\\')) return rejectInner();
        if (!next()) return rejectInner();
        return acceptInner();
    }

    //=====================================================================
    //  Int = [-+]? Natural {integer} ;
    //=====================================================================
    private boolean Int() {
        begin("Int");
        nextIn("-+");
        if (!Natural()) return reject();
        sem.integer();
        return accept();
    }

    //=====================================================================
    //  Natural = "0x" Hex+ ("_"+ Hex+)* {hexInt} / "0b" Binary+ ("_"+
    //    Binary+)* {binaryInt} / "0c" Octal+ ("_"+ Octal+)* {octalInt} /
    //    Decimal+ ("_"+ Decimal+)* {decimalInt} ;
    //=====================================================================
    private boolean Natural() {
        begin("Natural");
        if (Natural_0()) {
            sem.hexInt();
            return accept();
        }
        if (Natural_1()) {
            sem.binaryInt();
            return accept();
        }
        if (Natural_2()) {
            sem.octalInt();
            return accept();
        }
        if (Natural_3()) {
            sem.decimalInt();
            return accept();
        }
        return reject();
    }

    //-------------------------------------------------------------------
    //  Natural_0 = "0x" Hex+ ("_"+ Hex+)*
    //-------------------------------------------------------------------
    private boolean Natural_0() {
        begin("");
        if (!next("0x")) return rejectInner();
        if (!Hex()) return rejectInner();
        while (Hex()) ;
        while (Natural_4()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_1 = "0b" Binary+ ("_"+ Binary+)*
    //-------------------------------------------------------------------
    private boolean Natural_1() {
        begin("");
        if (!next("0b")) return rejectInner();
        if (!Binary()) return rejectInner();
        while (Binary()) ;
        while (Natural_5()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_2 = "0c" Octal+ ("_"+ Octal+)*
    //-------------------------------------------------------------------
    private boolean Natural_2() {
        begin("");
        if (!next("0c")) return rejectInner();
        if (!Octal()) return rejectInner();
        while (Octal()) ;
        while (Natural_6()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_3 = Decimal+ ("_"+ Decimal+)*
    //-------------------------------------------------------------------
    private boolean Natural_3() {
        begin("");
        if (!Decimal()) return rejectInner();
        while (Decimal()) ;
        while (Natural_7()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_4 = "_"+ Hex+
    //-------------------------------------------------------------------
    private boolean Natural_4() {
        begin("");
        if (!next('_')) return rejectInner();
        while (next('_')) ;
        if (!Hex()) return rejectInner();
        while (Hex()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_5 = "_"+ Binary+
    //-------------------------------------------------------------------
    private boolean Natural_5() {
        begin("");
        if (!next('_')) return rejectInner();
        while (next('_')) ;
        if (!Binary()) return rejectInner();
        while (Binary()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_6 = "_"+ Octal+
    //-------------------------------------------------------------------
    private boolean Natural_6() {
        begin("");
        if (!next('_')) return rejectInner();
        while (next('_')) ;
        if (!Octal()) return rejectInner();
        while (Octal()) ;
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Natural_7 = "_"+ Decimal+
    //-------------------------------------------------------------------
    private boolean Natural_7() {
        begin("");
        if (!next('_')) return rejectInner();
        while (next('_')) ;
        if (!Decimal()) return rejectInner();
        while (Decimal()) ;
        return acceptInner();
    }

    //=====================================================================
    //  Decimal = [0-9] ;
    //=====================================================================
    private boolean Decimal() {
        begin("Decimal");
        if (!nextIn('0', '9')) return reject();
        return accept();
    }

    //=====================================================================
    //  Hex = [0-9] / [a-f] / [A-F] ;
    //=====================================================================
    private boolean Hex() {
        begin("Hex");
        if (nextIn('0', '9')) return accept();
        if (nextIn('a', 'f')) return accept();
        if (nextIn('A', 'F')) return accept();
        return reject();
    }

    //=====================================================================
    //  Binary = [0-1] ;
    //=====================================================================
    private boolean Binary() {
        begin("Binary");
        if (!nextIn('0', '1')) return reject();
        return accept();
    }

    //=====================================================================
    //  Octal = [0-7] ;
    //=====================================================================
    private boolean Octal() {
        begin("Octal");
        if (!nextIn('0', '7')) return reject();
        return accept();
    }

    //=====================================================================
    //  SpcCom = (AnySpace / Comment)* ;
    //=====================================================================
    private boolean SpcCom() {
        begin("SpcCom");
        while (SpcCom_0()) ;
        return accept();
    }

    //-------------------------------------------------------------------
    //  SpcCom_0 = AnySpace / Comment
    //-------------------------------------------------------------------
    private boolean SpcCom_0() {
        begin("");
        if (AnySpace()) return acceptInner();
        if (Comment()) return acceptInner();
        return rejectInner();
    }

    //=====================================================================
    //  AnySpace = [ \t\r\n]+ ;
    //=====================================================================
    private boolean AnySpace() {
        begin("AnySpace");
        if (!nextIn(" \t\r\n")) return reject();
        while (nextIn(" \t\r\n")) ;
        return accept();
    }

    //=====================================================================
    //  Comment = "//" _*+ EOL / "/*" _*+ "*/" ;
    //=====================================================================
    private boolean Comment() {
        begin("Comment");
        if (Comment_0()) return accept();
        if (Comment_1()) return accept();
        return reject();
    }

    //-------------------------------------------------------------------
    //  Comment_0 = "//" _*+ EOL
    //-------------------------------------------------------------------
    private boolean Comment_0() {
        begin("");
        if (!next("//")) return rejectInner();
        while (!EOL())
            if (!next()) return rejectInner();
        return acceptInner();
    }

    //-------------------------------------------------------------------
    //  Comment_1 = "/*" _*+ "*/"
    //-------------------------------------------------------------------
    private boolean Comment_1() {
        begin("");
        if (!next("/*")) return rejectInner();
        while (!next("*/"))
            if (!next()) return rejectInner();
        return acceptInner();
    }

    //=====================================================================
    //  Spc = ASpc? ;
    //=====================================================================
    private boolean Spc() {
        begin("Spc");
        ASpc();
        return accept();
    }

    //=====================================================================
    //  ASpc = [ \t]+ ;
    //=====================================================================
    private boolean ASpc() {
        begin("ASpc");
        if (!nextIn(" \t")) return reject();
        while (nextIn(" \t")) ;
        return accept();
    }

    //=====================================================================
    //  EOL = [\r]? [\n] / EOF ;
    //=====================================================================
    private boolean EOL() {
        begin("EOL");
        if (EOL_0()) return accept();
        if (EOF()) return accept();
        return reject();
    }

    //-------------------------------------------------------------------
    //  EOL_0 = [\r]? [\n]
    //-------------------------------------------------------------------
    private boolean EOL_0() {
        begin("");
        next('\r');
        if (!next('\n')) return rejectInner();
        return acceptInner();
    }

    //=====================================================================
    //  EOF = !_ ;
    //=====================================================================
    private boolean EOF() {
        begin("EOF");
        if (!aheadNot()) return reject();
        return accept();
    }

}
