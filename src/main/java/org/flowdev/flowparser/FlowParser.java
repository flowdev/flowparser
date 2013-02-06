//=========================================================================
//
//  This file was generated by Mouse 1.5 at 2012-12-12 15:23:35 GMT
//  from grammar '/home/ole/work/flowdev/wikide/flowdsl.peg'.
//
//=========================================================================

package org.flowdev.flowparser;

import mouse.runtime.Source;

public class FlowParser extends mouse.runtime.ParserBase
{
  final FlowParserSemantics sem;
  
  //=======================================================================
  //
  //  Initialization
  //
  //=======================================================================
  //-------------------------------------------------------------------
  //  Constructor
  //-------------------------------------------------------------------
  public FlowParser()
    {
      sem = new FlowParserSemantics();
      sem.rule = this;
      super.sem = sem;
    }
  
  //-------------------------------------------------------------------
  //  Run the parser
  //-------------------------------------------------------------------
  public boolean parse(Source src)
    {
      super.init(src);
      sem.init();
      if (start()) return true;
      return failure();
    }
  
  //-------------------------------------------------------------------
  //  Get semantics
  //-------------------------------------------------------------------
  public FlowParserSemantics semantics()
    { return sem; }
  
  //=======================================================================
  //
  //  Parsing procedures
  //
  //=======================================================================
  //=====================================================================
  //  start = SpcCom Version Flow+ EOF {flowFile} ;
  //=====================================================================
  private boolean start()
    {
      begin("start");
      SpcCom();
      if (!Version()) return reject();
      if (!Flow()) return reject();
      while (Flow());
      if (!EOF()) return reject();
      sem.flowFile();
      return accept();
    }
  
  //=====================================================================
  //  Version = "version" ASpc Natural "." Natural SpcCom {version} ;
  //=====================================================================
  private boolean Version()
    {
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
  //  Flow = FlowStart "{" SpcCom Ports Connections Operations "}" SpcCom
  //    {flow} ;
  //=====================================================================
  private boolean Flow()
    {
      begin("Flow");
      if (!FlowStart()) return reject();
      if (!next('{')) return reject();
      SpcCom();
      if (!Ports()) return reject();
      if (!Connections()) return reject();
      if (!Operations()) return reject();
      if (!next('}')) return reject();
      SpcCom();
      sem.flow();
      return accept();
    }
  
  //=====================================================================
  //  FlowStart = "flow" ASpc BigIdent SpcCom {flowStart} ;
  //=====================================================================
  private boolean FlowStart()
    {
      begin("FlowStart");
      if (!next("flow")) return reject();
      if (!ASpc()) return reject();
      if (!BigIdent()) return reject();
      SpcCom();
      sem.flowStart();
      return accept();
    }
  
  //=====================================================================
  //  Ports = "ports" SpcCom "{" SpcCom Port+ "}" SpcCom {ports} ;
  //=====================================================================
  private boolean Ports()
    {
      begin("Ports");
      if (!next("ports")) return reject();
      SpcCom();
      if (!next('{')) return reject();
      SpcCom();
      if (!Port()) return reject();
      while (Port());
      if (!next('}')) return reject();
      SpcCom();
      sem.ports();
      return accept();
    }
  
  //=====================================================================
  //  Port = PortType ASpc PortNameColon? SpcCom DataType StmtEnd {port}
  //    ;
  //=====================================================================
  private boolean Port()
    {
      begin("Port");
      if (!PortType()) return reject();
      if (!ASpc()) return reject();
      PortNameColon();
      SpcCom();
      if (!DataType()) return reject();
      if (!StmtEnd()) return reject();
      sem.port();
      return accept();
    }
  
  //=====================================================================
  //  PortType = ("in" / "out") {portType} ;
  //=====================================================================
  private boolean PortType()
    {
      begin("PortType");
      if (!next("in")
       && !next("out")
         ) return reject();
      sem.portType();
      return accept();
    }
  
  //=====================================================================
  //  PortNameColon = PortName ":" {portNameColon} ;
  //=====================================================================
  private boolean PortNameColon()
    {
      begin("PortNameColon");
      if (!PortName()) return reject();
      if (!next(':')) return reject();
      sem.portNameColon();
      return accept();
    }
  
  //=====================================================================
  //  Connections = "connections" SpcCom "{" SpcCom ConnectionChain+ "}"
  //    SpcCom {connections} ;
  //=====================================================================
  private boolean Connections()
    {
      begin("Connections");
      if (!next("connections")) return reject();
      SpcCom();
      if (!next('{')) return reject();
      SpcCom();
      if (!ConnectionChain()) return reject();
      while (ConnectionChain());
      if (!next('}')) return reject();
      SpcCom();
      sem.connections();
      return accept();
    }
  
  //=====================================================================
  //  ConnectionChain = ConnectionChainBeg ConnectionChainMid*
  //    ConnectionChainEnd? StmtEnd {connectionChain} ;
  //=====================================================================
  private boolean ConnectionChain()
    {
      begin("ConnectionChain");
      ConnectionChainBeg();
      while (ConnectionChainMid());
      ConnectionChainEnd();
      if (!StmtEnd()) return reject();
      sem.connectionChain();
      return accept();
    }
  
  //=====================================================================
  //  ConnectionChainBeg = OperationNameParens OpOutPort
  //    {connectionChainBegMax} / InPort? {connectionChainBegMin} ;
  //=====================================================================
  private boolean ConnectionChainBeg()
    {
      begin("ConnectionChainBeg");
      if (ConnectionChainBeg_0())
      { sem.connectionChainBegMax(); return accept(); }
      if (ConnectionChainBeg_1())
      { sem.connectionChainBegMin(); return accept(); }
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  ConnectionChainBeg_0 = OperationNameParens OpOutPort
  //-------------------------------------------------------------------
  private boolean ConnectionChainBeg_0()
    {
      begin("");
      if (!OperationNameParens()) return rejectInner();
      OpOutPort();
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  ConnectionChainBeg_1 = InPort?
  //-------------------------------------------------------------------
  private boolean ConnectionChainBeg_1()
    {
      begin("");
      InPort();
      return acceptInner();
    }
  
  //=====================================================================
  //  ConnectionChainMid = "->" Spc OpInPort OperationNameParens
  //    OpOutPort {connectionChainMid} ;
  //=====================================================================
  private boolean ConnectionChainMid()
    {
      begin("ConnectionChainMid");
      if (!next("->")) return reject();
      Spc();
      OpInPort();
      if (!OperationNameParens()) return reject();
      OpOutPort();
      sem.connectionChainMid();
      return accept();
    }
  
  //=====================================================================
  //  ConnectionChainEnd = "->" Spc OutPort? {connectionChainEnd} ;
  //=====================================================================
  private boolean ConnectionChainEnd()
    {
      begin("ConnectionChainEnd");
      if (!next("->")) return reject();
      Spc();
      OutPort();
      sem.connectionChainEnd();
      return accept();
    }
  
  //=====================================================================
  //  OperationNameParens = "(" Spc OperationName ")" Spc
  //    {operationNameParens} ;
  //=====================================================================
  private boolean OperationNameParens()
    {
      begin("OperationNameParens");
      if (!next('(')) return reject();
      Spc();
      if (!OperationName()) return reject();
      if (!next(')')) return reject();
      Spc();
      sem.operationNameParens();
      return accept();
    }
  
  //=====================================================================
  //  OpInPort = InPort? {opInPort} ;
  //=====================================================================
  private boolean OpInPort()
    {
      begin("OpInPort");
      InPort();
      sem.opInPort();
      return accept();
    }
  
  //=====================================================================
  //  OpOutPort = OutPort? {opOutPort} ;
  //=====================================================================
  private boolean OpOutPort()
    {
      begin("OpOutPort");
      OutPort();
      sem.opOutPort();
      return accept();
    }
  
  //=====================================================================
  //  InPort = PortName "." Spc {inPort} ;
  //=====================================================================
  private boolean InPort()
    {
      begin("InPort");
      if (!PortName()) return reject();
      if (!next('.')) return reject();
      Spc();
      sem.inPort();
      return accept();
    }
  
  //=====================================================================
  //  OutPort = "." Spc PortName {outPort} ;
  //=====================================================================
  private boolean OutPort()
    {
      begin("OutPort");
      if (!next('.')) return reject();
      Spc();
      if (!PortName()) return reject();
      sem.outPort();
      return accept();
    }
  
  //=====================================================================
  //  PortName = SmallIdent Spc {portName} ;
  //=====================================================================
  private boolean PortName()
    {
      begin("PortName");
      if (!SmallIdent()) return reject();
      Spc();
      sem.portName();
      return accept();
    }
  
  //=====================================================================
  //  Operations = "operations" SpcCom "{" SpcCom Operation+ "}" SpcCom
  //    {operations} ;
  //=====================================================================
  private boolean Operations()
    {
      begin("Operations");
      if (!next("operations")) return reject();
      SpcCom();
      if (!next('{')) return reject();
      SpcCom();
      if (!Operation()) return reject();
      while (Operation());
      if (!next('}')) return reject();
      SpcCom();
      sem.operations();
      return accept();
    }
  
  //=====================================================================
  //  Operation = OperationDecl "{" SpcCom OperationPart* "}" SpcCom
  //    {operation} ;
  //=====================================================================
  private boolean Operation()
    {
      begin("Operation");
      if (!OperationDecl()) return reject();
      if (!next('{')) return reject();
      SpcCom();
      while (OperationPart());
      if (!next('}')) return reject();
      SpcCom();
      sem.operation();
      return accept();
    }
  
  //=====================================================================
  //  OperationDecl = OperationNameColon SpcCom DataType SpcCom
  //    {operationDecl} ;
  //=====================================================================
  private boolean OperationDecl()
    {
      begin("OperationDecl");
      if (!OperationNameColon()) return reject();
      SpcCom();
      if (!DataType()) return reject();
      SpcCom();
      sem.operationDecl();
      return accept();
    }
  
  //=====================================================================
  //  OperationNameColon = OperationName ":" {operationNameColon} ;
  //=====================================================================
  private boolean OperationNameColon()
    {
      begin("OperationNameColon");
      if (!OperationName()) return reject();
      if (!next(':')) return reject();
      sem.operationNameColon();
      return accept();
    }
  
  //=====================================================================
  //  OperationName = SmallIdent Spc {operationName} ;
  //=====================================================================
  private boolean OperationName()
    {
      begin("OperationName");
      if (!SmallIdent()) return reject();
      Spc();
      sem.operationName();
      return accept();
    }
  
  //=====================================================================
  //  OperationPart = OperationGetter / OperationSetter /
  //    OperationCreator / OperationConfig ;
  //=====================================================================
  private boolean OperationPart()
    {
      begin("OperationPart");
      if (OperationGetter()) return accept();
      if (OperationSetter()) return accept();
      if (OperationCreator()) return accept();
      if (OperationConfig()) return accept();
      return reject();
    }
  
  //=====================================================================
  //  OperationGetter = "getter" OperationPartName OperationGetterImpl
  //    StmtEnd {operationGetter} ;
  //=====================================================================
  private boolean OperationGetter()
    {
      begin("OperationGetter");
      if (!next("getter")) return reject();
      if (!OperationPartName()) return reject();
      if (!OperationGetterImpl()) return reject();
      if (!StmtEnd()) return reject();
      sem.operationGetter();
      return accept();
    }
  
  //=====================================================================
  //  OperationGetterImpl = "getFrom" Spc "(" OperationDataPath ","
  //    ConstExpr ")" {operationGetterGetFrom} / DataPath
  //    {operationGetterData} / ConstExpr {operationGetterConst} ;
  //=====================================================================
  private boolean OperationGetterImpl()
    {
      begin("OperationGetterImpl");
      if (OperationGetterImpl_0())
      { sem.operationGetterGetFrom(); return accept(); }
      if (DataPath())
      { sem.operationGetterData(); return accept(); }
      if (ConstExpr())
      { sem.operationGetterConst(); return accept(); }
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  OperationGetterImpl_0 = "getFrom" Spc "(" OperationDataPath ","
  //    ConstExpr ")"
  //-------------------------------------------------------------------
  private boolean OperationGetterImpl_0()
    {
      begin("");
      if (!next("getFrom")) return rejectInner();
      Spc();
      if (!next('(')) return rejectInner();
      if (!OperationDataPath()) return rejectInner();
      if (!next(',')) return rejectInner();
      if (!ConstExpr()) return rejectInner();
      if (!next(')')) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  OperationSetter = "setter" OperationPartName OperationSetterImpl
  //    StmtEnd {operationSetter} ;
  //=====================================================================
  private boolean OperationSetter()
    {
      begin("OperationSetter");
      if (!next("setter")) return reject();
      if (!OperationPartName()) return reject();
      if (!OperationSetterImpl()) return reject();
      if (!StmtEnd()) return reject();
      sem.operationSetter();
      return accept();
    }
  
  //=====================================================================
  //  OperationSetterImpl = "addTo" Spc "(" OperationDataPath ")"
  //    {operationSetterAddTo} / "setTo" Spc "(" OperationDataPath ","
  //    ConstExpr ")" {operationSetterSetTo} / DataPath
  //    {operationSetterData} ;
  //=====================================================================
  private boolean OperationSetterImpl()
    {
      begin("OperationSetterImpl");
      if (OperationSetterImpl_0())
      { sem.operationSetterAddTo(); return accept(); }
      if (OperationSetterImpl_1())
      { sem.operationSetterSetTo(); return accept(); }
      if (DataPath())
      { sem.operationSetterData(); return accept(); }
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  OperationSetterImpl_0 = "addTo" Spc "(" OperationDataPath ")"
  //-------------------------------------------------------------------
  private boolean OperationSetterImpl_0()
    {
      begin("");
      if (!next("addTo")) return rejectInner();
      Spc();
      if (!next('(')) return rejectInner();
      if (!OperationDataPath()) return rejectInner();
      if (!next(')')) return rejectInner();
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  OperationSetterImpl_1 = "setTo" Spc "(" OperationDataPath ","
  //    ConstExpr ")"
  //-------------------------------------------------------------------
  private boolean OperationSetterImpl_1()
    {
      begin("");
      if (!next("setTo")) return rejectInner();
      Spc();
      if (!next('(')) return rejectInner();
      if (!OperationDataPath()) return rejectInner();
      if (!next(',')) return rejectInner();
      if (!ConstExpr()) return rejectInner();
      if (!next(')')) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  OperationCreator = "create" OperationPartName OperationCreatorImpl
  //    StmtEnd {operationCreator} ;
  //=====================================================================
  private boolean OperationCreator()
    {
      begin("OperationCreator");
      if (!next("create")) return reject();
      if (!OperationPartName()) return reject();
      if (!OperationCreatorImpl()) return reject();
      if (!StmtEnd()) return reject();
      sem.operationCreator();
      return accept();
    }
  
  //=====================================================================
  //  OperationCreatorImpl = "typeOf" Spc "(" SpcCom DataType SpcCom ")"
  //    {operationCreatorType} / DataType {operationCreatorData} ;
  //=====================================================================
  private boolean OperationCreatorImpl()
    {
      begin("OperationCreatorImpl");
      if (OperationCreatorImpl_0())
      { sem.operationCreatorType(); return accept(); }
      if (DataType())
      { sem.operationCreatorData(); return accept(); }
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  OperationCreatorImpl_0 = "typeOf" Spc "(" SpcCom DataType SpcCom
  //    ")"
  //-------------------------------------------------------------------
  private boolean OperationCreatorImpl_0()
    {
      begin("");
      if (!next("typeOf")) return rejectInner();
      Spc();
      if (!next('(')) return rejectInner();
      SpcCom();
      if (!DataType()) return rejectInner();
      SpcCom();
      if (!next(')')) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  OperationConfig = "config" OperationPartName ConstExpr StmtEnd
  //    {operationConfig} ;
  //=====================================================================
  private boolean OperationConfig()
    {
      begin("OperationConfig");
      if (!next("config")) return reject();
      if (!OperationPartName()) return reject();
      if (!ConstExpr()) return reject();
      if (!StmtEnd()) return reject();
      sem.operationConfig();
      return accept();
    }
  
  //=====================================================================
  //  OperationDataPath = SpcCom DataPath SpcCom {operationDataPath} ;
  //=====================================================================
  private boolean OperationDataPath()
    {
      begin("OperationDataPath");
      SpcCom();
      if (!DataPath()) return reject();
      SpcCom();
      sem.operationDataPath();
      return accept();
    }
  
  //=====================================================================
  //  OperationPartName = Spc SmallIdent Spc ":" SpcCom
  //    {operationPartName} ;
  //=====================================================================
  private boolean OperationPartName()
    {
      begin("OperationPartName");
      Spc();
      if (!SmallIdent()) return reject();
      Spc();
      if (!next(':')) return reject();
      SpcCom();
      sem.operationPartName();
      return accept();
    }
  
  //=====================================================================
  //  StmtEnd = SpcCom ";" SpcCom ;
  //=====================================================================
  private boolean StmtEnd()
    {
      begin("StmtEnd");
      SpcCom();
      if (!next(';')) return reject();
      SpcCom();
      return accept();
    }
  
  //=====================================================================
  //  DataPath = SmallIdent (Spc "." Spc SmallIdent)* {dataPath} ;
  //=====================================================================
  private boolean DataPath()
    {
      begin("DataPath");
      if (!SmallIdent()) return reject();
      while (DataPath_0());
      sem.dataPath();
      return accept();
    }
  
  //-------------------------------------------------------------------
  //  DataPath_0 = Spc "." Spc SmallIdent
  //-------------------------------------------------------------------
  private boolean DataPath_0()
    {
      begin("");
      Spc();
      if (!next('.')) return rejectInner();
      Spc();
      if (!SmallIdent()) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  DataType = DataModule? BigIdent {dataType} ;
  //=====================================================================
  private boolean DataType()
    {
      begin("DataType");
      DataModule();
      if (!BigIdent()) return reject();
      sem.dataType();
      return accept();
    }
  
  //=====================================================================
  //  DataModule = FilePath "/" {dataModule} ;
  //=====================================================================
  private boolean DataModule()
    {
      begin("DataModule");
      if (!FilePath()) return reject();
      if (!next('/')) return reject();
      sem.dataModule();
      return accept();
    }
  
  //=====================================================================
  //  FilePath = StartFilePath RestFilePath / RestFilePath ;
  //=====================================================================
  private boolean FilePath()
    {
      begin("FilePath");
      if (FilePath_0()) return accept();
      if (RestFilePath()) return accept();
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  FilePath_0 = StartFilePath RestFilePath
  //-------------------------------------------------------------------
  private boolean FilePath_0()
    {
      begin("");
      if (!StartFilePath()) return rejectInner();
      if (!RestFilePath()) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  StartFilePath = "../"+ / "./" / "/" ;
  //=====================================================================
  private boolean StartFilePath()
    {
      begin("StartFilePath");
      if (StartFilePath_0()) return accept();
      if (next("./")) return accept();
      if (next('/')) return accept();
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  StartFilePath_0 = "../"+
  //-------------------------------------------------------------------
  private boolean StartFilePath_0()
    {
      begin("");
      if (!next("../")) return rejectInner();
      while (next("../"));
      return acceptInner();
    }
  
  //=====================================================================
  //  RestFilePath = SmallIdent ("/" SmallIdent)* ;
  //=====================================================================
  private boolean RestFilePath()
    {
      begin("RestFilePath");
      if (!SmallIdent()) return reject();
      while (RestFilePath_0());
      return accept();
    }
  
  //-------------------------------------------------------------------
  //  RestFilePath_0 = "/" SmallIdent
  //-------------------------------------------------------------------
  private boolean RestFilePath_0()
    {
      begin("");
      if (!next('/')) return rejectInner();
      if (!SmallIdent()) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  BigIdent = [A-Z] WordChar+ ;
  //=====================================================================
  private boolean BigIdent()
    {
      begin("BigIdent");
      if (!nextIn('A','Z')) return reject();
      if (!WordChar()) return reject();
      while (WordChar());
      return accept();
    }
  
  //=====================================================================
  //  SmallIdent = [a-z] WordChar* ;
  //=====================================================================
  private boolean SmallIdent()
    {
      begin("SmallIdent");
      if (!nextIn('a','z')) return reject();
      while (WordChar());
      return accept();
    }
  
  //=====================================================================
  //  WordChar = [A-Z] / [a-z] / [0-9] ;
  //=====================================================================
  private boolean WordChar()
    {
      begin("WordChar");
      if (nextIn('A','Z')) return accept();
      if (nextIn('a','z')) return accept();
      if (nextIn('0','9')) return accept();
      return reject();
    }
  
  //=====================================================================
  //  ConstExpr = SpcCom (String / Int / Bool) SpcCom {constExpr} ;
  //=====================================================================
  private boolean ConstExpr()
    {
      begin("ConstExpr");
      SpcCom();
      if (!String()
       && !Int()
       && !Bool()
         ) return reject();
      SpcCom();
      sem.constExpr();
      return accept();
    }
  
  //=====================================================================
  //  String = ["] Char*+ ["] {unescapeString} ;
  //=====================================================================
  private boolean String()
    {
      begin("String");
      if (!next('"')) return reject();
      while (!next('"'))
        if (!Char()) return reject();
      sem.unescapeString();
      return accept();
    }
  
  //=====================================================================
  //  Char = EscapeChar / ^[\r\n\] ;
  //=====================================================================
  private boolean Char()
    {
      begin("Char");
      if (EscapeChar()) return accept();
      if (nextNotIn("\r\n\\")) return accept();
      return reject();
    }
  
  //=====================================================================
  //  EscapeChar = "\ u" Hex Hex Hex Hex {unicodeChar} / "\t" {tabChar} /
  //    "\n" {newlineChar} / "\r" {carriagereturnChar} / !"\ u" "\" _
  //    {backslashChar} ;
  //=====================================================================
  private boolean EscapeChar()
    {
      begin("EscapeChar");
      if (EscapeChar_0())
      { sem.unicodeChar(); return accept(); }
      if (next("\\t"))
      { sem.tabChar(); return accept(); }
      if (next("\\n"))
      { sem.newlineChar(); return accept(); }
      if (next("\\r"))
      { sem.carriagereturnChar(); return accept(); }
      if (EscapeChar_1())
      { sem.backslashChar(); return accept(); }
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  EscapeChar_0 = "\ u" Hex Hex Hex Hex
  //-------------------------------------------------------------------
  private boolean EscapeChar_0()
    {
      begin("");
      if (!next("\\u")) return rejectInner();
      if (!Hex()) return rejectInner();
      if (!Hex()) return rejectInner();
      if (!Hex()) return rejectInner();
      if (!Hex()) return rejectInner();
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  EscapeChar_1 = !"\ u" "\" _
  //-------------------------------------------------------------------
  private boolean EscapeChar_1()
    {
      begin("");
      if (!aheadNot("\\u")) return rejectInner();
      if (!next('\\')) return rejectInner();
      if (!next()) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  Int = [-+]? Natural {integer} ;
  //=====================================================================
  private boolean Int()
    {
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
  private boolean Natural()
    {
      begin("Natural");
      if (Natural_0())
      { sem.hexInt(); return accept(); }
      if (Natural_1())
      { sem.binaryInt(); return accept(); }
      if (Natural_2())
      { sem.octalInt(); return accept(); }
      if (Natural_3())
      { sem.decimalInt(); return accept(); }
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  Natural_0 = "0x" Hex+ ("_"+ Hex+)*
  //-------------------------------------------------------------------
  private boolean Natural_0()
    {
      begin("");
      if (!next("0x")) return rejectInner();
      if (!Hex()) return rejectInner();
      while (Hex());
      while (Natural_4());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_1 = "0b" Binary+ ("_"+ Binary+)*
  //-------------------------------------------------------------------
  private boolean Natural_1()
    {
      begin("");
      if (!next("0b")) return rejectInner();
      if (!Binary()) return rejectInner();
      while (Binary());
      while (Natural_5());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_2 = "0c" Octal+ ("_"+ Octal+)*
  //-------------------------------------------------------------------
  private boolean Natural_2()
    {
      begin("");
      if (!next("0c")) return rejectInner();
      if (!Octal()) return rejectInner();
      while (Octal());
      while (Natural_6());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_3 = Decimal+ ("_"+ Decimal+)*
  //-------------------------------------------------------------------
  private boolean Natural_3()
    {
      begin("");
      if (!Decimal()) return rejectInner();
      while (Decimal());
      while (Natural_7());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_4 = "_"+ Hex+
  //-------------------------------------------------------------------
  private boolean Natural_4()
    {
      begin("");
      if (!next('_')) return rejectInner();
      while (next('_'));
      if (!Hex()) return rejectInner();
      while (Hex());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_5 = "_"+ Binary+
  //-------------------------------------------------------------------
  private boolean Natural_5()
    {
      begin("");
      if (!next('_')) return rejectInner();
      while (next('_'));
      if (!Binary()) return rejectInner();
      while (Binary());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_6 = "_"+ Octal+
  //-------------------------------------------------------------------
  private boolean Natural_6()
    {
      begin("");
      if (!next('_')) return rejectInner();
      while (next('_'));
      if (!Octal()) return rejectInner();
      while (Octal());
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Natural_7 = "_"+ Decimal+
  //-------------------------------------------------------------------
  private boolean Natural_7()
    {
      begin("");
      if (!next('_')) return rejectInner();
      while (next('_'));
      if (!Decimal()) return rejectInner();
      while (Decimal());
      return acceptInner();
    }
  
  //=====================================================================
  //  Decimal = [0-9] ;
  //=====================================================================
  private boolean Decimal()
    {
      begin("Decimal");
      if (!nextIn('0','9')) return reject();
      return accept();
    }
  
  //=====================================================================
  //  Hex = [0-9] / [a-f] / [A-F] ;
  //=====================================================================
  private boolean Hex()
    {
      begin("Hex");
      if (nextIn('0','9')) return accept();
      if (nextIn('a','f')) return accept();
      if (nextIn('A','F')) return accept();
      return reject();
    }
  
  //=====================================================================
  //  Binary = [0-1] ;
  //=====================================================================
  private boolean Binary()
    {
      begin("Binary");
      if (!nextIn('0','1')) return reject();
      return accept();
    }
  
  //=====================================================================
  //  Octal = [0-7] ;
  //=====================================================================
  private boolean Octal()
    {
      begin("Octal");
      if (!nextIn('0','7')) return reject();
      return accept();
    }
  
  //=====================================================================
  //  Bool = ("true" / "false") {bool} ;
  //=====================================================================
  private boolean Bool()
    {
      begin("Bool");
      if (!next("true")
       && !next("false")
         ) return reject();
      sem.bool();
      return accept();
    }
  
  //=====================================================================
  //  SpcCom = (AnySpace / Comment)* ;
  //=====================================================================
  private boolean SpcCom()
    {
      begin("SpcCom");
      while (SpcCom_0());
      return accept();
    }
  
  //-------------------------------------------------------------------
  //  SpcCom_0 = AnySpace / Comment
  //-------------------------------------------------------------------
  private boolean SpcCom_0()
    {
      begin("");
      if (AnySpace()) return acceptInner();
      if (Comment()) return acceptInner();
      return rejectInner();
    }
  
  //=====================================================================
  //  AnySpace = [ \t\r\n]+ ;
  //=====================================================================
  private boolean AnySpace()
    {
      begin("AnySpace");
      if (!nextIn(" \t\r\n")) return reject();
      while (nextIn(" \t\r\n"));
      return accept();
    }
  
  //=====================================================================
  //  Comment = "//" _*+ EOL / "/*" _*+ "*/" ;
  //=====================================================================
  private boolean Comment()
    {
      begin("Comment");
      if (Comment_0()) return accept();
      if (Comment_1()) return accept();
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  Comment_0 = "//" _*+ EOL
  //-------------------------------------------------------------------
  private boolean Comment_0()
    {
      begin("");
      if (!next("//")) return rejectInner();
      while (!EOL())
        if (!next()) return rejectInner();
      return acceptInner();
    }
  
  //-------------------------------------------------------------------
  //  Comment_1 = "/*" _*+ "*/"
  //-------------------------------------------------------------------
  private boolean Comment_1()
    {
      begin("");
      if (!next("/*")) return rejectInner();
      while (!next("*/"))
        if (!next()) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  Spc = ASpc? ;
  //=====================================================================
  private boolean Spc()
    {
      begin("Spc");
      ASpc();
      return accept();
    }
  
  //=====================================================================
  //  ASpc = [ \t]+ ;
  //=====================================================================
  private boolean ASpc()
    {
      begin("ASpc");
      if (!nextIn(" \t")) return reject();
      while (nextIn(" \t"));
      return accept();
    }
  
  //=====================================================================
  //  EOL = [\r]? [\n] / EOF ;
  //=====================================================================
  private boolean EOL()
    {
      begin("EOL");
      if (EOL_0()) return accept();
      if (EOF()) return accept();
      return reject();
    }
  
  //-------------------------------------------------------------------
  //  EOL_0 = [\r]? [\n]
  //-------------------------------------------------------------------
  private boolean EOL_0()
    {
      begin("");
      next('\r');
      if (!next('\n')) return rejectInner();
      return acceptInner();
    }
  
  //=====================================================================
  //  EOF = !_ ;
  //=====================================================================
  private boolean EOF()
    {
      begin("EOF");
      if (!aheadNot()) return reject();
      return accept();
    }
  
}
