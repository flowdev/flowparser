package org.flowdev.flowparser;

import java.util.List;
import java.util.ArrayList;

import mouse.runtime.SemanticsBase;
import mouse.runtime.Phrase;

import org.flowdev.flowparser.rawdata.*;


class FlowParserSemantics extends SemanticsBase {

	private RawFlowFile flowFile;

	public void init() {
		flowFile = new RawFlowFile();
	}

	public RawFlowFile getFlowFile() {
		return flowFile;
	}

	//-------------------------------------------------------------------
	//  start = SpcCom Version Flow+ EOF
	//-------------------------------------------------------------------
	void flowFile() {
		int N = rhsSize() - 1;

		flowFile.version = (RawVersion) rhs(1).get();
		flowFile.flows = new ArrayList<>(N-2);

		for (int i = 2; i < N; i++) {
			flowFile.flows.add((RawFlow) rhs(i).get());
		}
	}
  
	//-------------------------------------------------------------------
	//  Version = "version" ASpc Int "." Int SpcCom
	//-------------------------------------------------------------------
	void version() {
		RawVersion data = new RawVersion();
		data.sourcePosition = lhs().where(0);
		data.political      = ((Long) rhs(2).get()).longValue();
		data.major          = ((Long) rhs(4).get()).longValue();

		lhs().put(data);
	}
 
	//-------------------------------------------------------------------
	//  Flow = FlowStart "{" SpcCom Ports Connections Operations "}"
	//    SpcCom
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	void flow() {
		RawFlow data = new RawFlow();
		data.sourcePosition = lhs().where(0);
		data.name = (String) rhs(0).get();
		data.ports = (List<RawPort>) rhs(3).get();
		data.connections = (List<RawConnectionChain>) rhs(4).get();
		data.operations = (List<RawOperation>) rhs(5).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  FlowStart = "flow" ASpc BigIdent SpcCom
	//-------------------------------------------------------------------
	void flowStart() {
		lhs().put(rhs(2).text());
	}

	//-------------------------------------------------------------------
	//  Ports = "ports" SpcCom "{" SpcCom Port+ "}" SpcCom
	//-------------------------------------------------------------------
	void ports() {
		List<RawPort> data = new ArrayList<>(rhsSize() - 6);
		int N = rhsSize() - 2;

		for (int i = 4; i < N; i++) {
			data.add((RawPort) rhs(i).get());
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  Port = PortType ASpc PortNameColon? SpcCom DataType StmtEnd
	//-------------------------------------------------------------------
	void port() {
		RawPort data = new RawPort();
		data.sourcePosition = lhs().where(0);
		data.type = (RawPortType) rhs(0).get();

		if (rhsSize() > 5) {
			data.name = (String) rhs(2).get();
			data.dataType = (RawDataType) rhs(4).get();
		} else {
			data.name = data.type.toString();
			data.dataType = (RawDataType) rhs(3).get();
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  PortType = ("in"/"out")
	//-------------------------------------------------------------------
	void portType() {
		RawPortType data;

		if ("in".equals(lhs().text())) {
			data = RawPortType.IN;
		} else {
			data = RawPortType.OUT;
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  PortNameColon = PortName ":"
	//-------------------------------------------------------------------
	void portNameColon() {
		lhs().put(rhs(0).get());
	}

	//-------------------------------------------------------------------
	//  Connections = "connections" SpcCom "{" SpcCom ConnectionChain+
	//    "}" SpcCom
	//-------------------------------------------------------------------
	void connections() {
		int n = rhsSize() - 2;
		List<RawConnectionChain> data = new ArrayList<>(n - 3);

		for (int i = 4; i < n; i++) {
			data.add((RawConnectionChain) rhs(i).get());
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  ConnectionChain = ConnectionChainBeg ConnectionChainMid+
	//    ConnectionChainEnd? StmtEnd
	//-------------------------------------------------------------------
	void connectionChain() {
		int n = rhsSize() - 1;
		RawConnectionChain data = new RawConnectionChain();
		data.sourcePosition = lhs().where(0);
		data.parts = new ArrayList<>(n);

		for (int i = 0; i < n; i++) {
			data.parts.add((RawConnectionPart) rhs(i).get());
		}

		if (n > 1) {
			correctConnectionChainBeg(data.parts);
			correctConnectionChainEnd(data.parts);
		}

		lhs().put(data);
	}
	private void correctConnectionChainBeg(List<RawConnectionPart> parts) {
		RawConnectionPart firstPart = parts.get(0);
		if (firstPart.operationName == null) {
			if (firstPart.inPort.name == null) {
				RawConnectionPart secondPart = parts.get(1);
				firstPart.inPort.name = secondPart.inPort.name;
			}
		}

	}
	private void correctConnectionChainEnd(List<RawConnectionPart> parts) {
		RawConnectionPart lastPart = parts.get(parts.size() - 1);
		if (lastPart.operationName == null) {
			if (lastPart.outPort.name == null) {
				RawConnectionPart preLastPart = parts.get(parts.size() - 2);
				lastPart.outPort.name = preLastPart.outPort.name;
			}
		} else {
			if (hasOverkillOutPort(lastPart)) {
				lastPart.outPort = null;
			}
		}

	}
	private boolean hasOverkillOutPort(RawConnectionPart part) {
		return part.operationName != null && part.outPort != null && part.outPort.name != null &&
				part.outPort.name.equals(part.outPort.type.toString());
	}

	//-------------------------------------------------------------------
	//  ConnectionChainBegMax = OperationNameParens OpOutPort
	//-------------------------------------------------------------------
	void connectionChainBegMax() {
		RawConnectionPart data = new RawConnectionPart();
		data.sourcePosition = lhs().where(0);
		data.inPort = null;
		data.operationName = (String) rhs(0).get();
		data.outPort = (RawPort) rhs(1).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  ConnectionChainBegMin = InPort?
	//-------------------------------------------------------------------
	void connectionChainBegMin() {
		RawConnectionPart data = new RawConnectionPart();
		data.sourcePosition = lhs().where(0);
		data.operationName = null;

		if (rhsSize() > 0) {
			data.inPort = (RawPort) rhs(0).get();
		} else {
			data.inPort = new RawPort();
			data.inPort.type = RawPortType.IN;
			data.inPort.sourcePosition = data.sourcePosition;
		}
		data.outPort = data.inPort;

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  ConnectionChainMid = "->" Spc OpInPort OperationNameParens
	//    OpOutPort
	//-------------------------------------------------------------------
	void connectionChainMid() {
		RawConnectionPart data = new RawConnectionPart();
		data.sourcePosition = lhs().where(0);
		data.inPort = (RawPort) rhs(2).get();
		data.operationName = (String) rhs(3).get();
		data.outPort = (RawPort) rhs(4).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  ConnectionChainEnd = "->" Spc OutPort?
	//-------------------------------------------------------------------
	void connectionChainEnd() {
		RawConnectionPart data = new RawConnectionPart();
		data.sourcePosition = lhs().where(0);
		data.operationName = null;

		if (rhsSize() > 2) {
			data.outPort = (RawPort) rhs(2).get();
		} else {
			data.outPort = new RawPort();
			data.outPort.type = RawPortType.OUT;
		}
		data.inPort = data.outPort;

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationNameParens = "(" Spc OperationName ")" Spc
	//-------------------------------------------------------------------
	void operationNameParens() {
		lhs().put(rhs(2).get());
	}

	//-------------------------------------------------------------------
	//  OpInPort = InPort?
	//-------------------------------------------------------------------
	void opInPort() {
		RawPort data;

		if (rhsSize() > 0) {
			data = (RawPort) rhs(0).get();
		} else {
			data = new RawPort();
			data.sourcePosition = lhs().where(0);
			data.type = RawPortType.IN;
			data.name = data.type.toString();
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OpOutPort = OutPort?
	//-------------------------------------------------------------------
	void opOutPort() {
		RawPort data;

		if (rhsSize() > 0) {
			data = (RawPort) rhs(0).get();
		} else {
			data = new RawPort();
			data.sourcePosition = lhs().where(0);
			data.type = RawPortType.OUT;
			data.name = data.type.toString();
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  InPort = PortName "." Spc
	//-------------------------------------------------------------------
	void inPort() {
		RawPort data = new RawPort();
		data.sourcePosition = lhs().where(0);
		data.type = RawPortType.IN;
		data.name = (String) rhs(0).get();
		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OutPort = "." Spc PortName
	//-------------------------------------------------------------------
	void outPort() {
		RawPort data = new RawPort();
		data.sourcePosition = lhs().where(0);
		data.type = RawPortType.OUT;
		data.name = (String) rhs(2).get();
		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  PortName = SmallIdent Spc
	//-------------------------------------------------------------------
	void portName() {
		lhs().put(rhs(0).text());
	}

	//-------------------------------------------------------------------
	//  Operations = "operations" SpcCom "{" SpcCom Operation+ "}"
	//    SpcCom
	//-------------------------------------------------------------------
	void operations() {
		int n = rhsSize() - 2;
		List<RawOperation> data = new ArrayList<>(n - 3);

		for (int i = 4; i < n; i++) {
			data.add((RawOperation) rhs(i).get());
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  Operation = OperationDecl "{" SpcCom OperationPart* "}" SpcCom
	//-------------------------------------------------------------------
	void operation() {
		Object[] decl = (Object[]) rhs(0).get();
		RawOperation data = new RawOperation();
		data.sourcePosition = lhs().where(0);
		data.name = (String) decl[0];
		data.type = (RawDataType) decl[1];

		int n = rhsSize() - 2;
		for (int i = 3; i < n; i++) {
			putOperationPart(rhs(i).get(), data);
		}

		lhs().put(data);
	}
	private void putOperationPart(Object part, RawOperation op) {
		if (part instanceof RawGetter) {
			op.getters.add((RawGetter) part);
		} else if (part instanceof RawSetter) {
			op.setters.add((RawSetter) part);
		} else if (part instanceof RawCreator) {
			op.creators.add((RawCreator) part);
		} else if (part instanceof RawConfig) {
			op.configs.add((RawConfig) part);
		}
	}

	//-------------------------------------------------------------------
	//  OperationDecl = OperationNameColon SpcCom DataType SpcCom
	//-------------------------------------------------------------------
	void operationDecl() {
		Object[] data = new Object[2];
		data[0] = rhs(0).get();
		data[1] = rhs(2).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationNameColon = OperationName ":"
	//-------------------------------------------------------------------
	void operationNameColon() {
		lhs().put(rhs(0).get());
	}

	//-------------------------------------------------------------------
	//  OperationName = SmallIdent Spc
	//-------------------------------------------------------------------
	void operationName() {
		lhs().put(rhs(0).text());
	}

	//-------------------------------------------------------------------
	//  OperationGetter = "getter" OperationPartName OperationGetterImpl
	//    StmtEnd
	//-------------------------------------------------------------------
	void operationGetter() {
		RawGetter data = (RawGetter) rhs(2).get();
		data.sourcePosition = lhs().where(0);
		data.name = (String) rhs(1).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationGetterImpl = "getFrom" Spc "(" OperationDataPath ","
	//    ConstExpr ")"
	//-------------------------------------------------------------------
	void operationGetterGetFrom() {
		RawGetter data = new RawGetter();
		data.type = RawGetterType.COLLECTION;
		data.dataPath = (RawDataPath) rhs(3).get();
		data.constValue = rhs(5).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationGetterImpl = DataPath
	//-------------------------------------------------------------------
	void operationGetterData() {
		RawGetter data = new RawGetter();
		data.type = RawGetterType.PLAIN;
		data.dataPath = (RawDataPath) rhs(0).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationGetterImpl = ConstExpr
	//-------------------------------------------------------------------
	void operationGetterConst() {
		RawGetter data = new RawGetter();
		data.type = RawGetterType.CONST;
		data.constValue = rhs(0).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationSetter = "setter" OperationPartName OperationSetterImpl
	//    StmtEnd
	//-------------------------------------------------------------------
	void operationSetter() {
		RawSetter data = (RawSetter) rhs(2).get();
		data.sourcePosition = lhs().where(0);
		data.name = (String) rhs(1).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationSetterImpl = "addTo" Spc "(" OperationDataPath ")"
	//-------------------------------------------------------------------
	void operationSetterAddTo() {
		RawSetter data = new RawSetter();
		data.type = RawSetterType.APPEND;
		data.dataPath = (RawDataPath) rhs(3).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationSetterImpl = "setTo" Spc "(" OperationDataPath ","
	//    ConstExpr ")"
	//-------------------------------------------------------------------
	void operationSetterSetTo() {
		RawSetter data = new RawSetter();
		data.type = RawSetterType.COLLECTION;
		data.dataPath = (RawDataPath) rhs(3).get();
		data.constValue = rhs(5).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationSetterImpl = DataPath
	//-------------------------------------------------------------------
	void operationSetterData() {
		RawSetter data = new RawSetter();
		data.type = RawSetterType.PLAIN;
		data.dataPath = (RawDataPath) rhs(0).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationCreator = "create" OperationPartName
	//    OperationCreatorImpl StmtEnd
	//-------------------------------------------------------------------
	void operationCreator() {
		RawCreator data = (RawCreator) rhs(2).get();
		data.sourcePosition = lhs().where(0);
		data.name = (String) rhs(1).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationCreatorImpl = "typeOf" Spc "(" SpcCom DataType SpcCom
	//    ")"
	//-------------------------------------------------------------------
	void operationCreatorType() {
		RawCreator data = new RawCreator();
		data.type = RawCreatorType.TYPE;
		data.dataType = (RawDataType) rhs(4).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationCreatorImpl = DataType
	//-------------------------------------------------------------------
	void operationCreatorData() {
		RawCreator data = new RawCreator();
		data.type = RawCreatorType.DATA;
		data.dataType = (RawDataType) rhs(0).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationConfig = "config" OperationPartName ConstExpr StmtEnd
	//-------------------------------------------------------------------
	void operationConfig() {
		RawConfig data = new RawConfig();
		data.sourcePosition = rhs(1).where(0);
		data.name = (String) rhs(1).get();
		data.constValue = rhs(2).get();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  OperationDataPath = SpcCom DataPath SpcCom
	//-------------------------------------------------------------------
	void operationDataPath() {
		lhs().put(rhs(1).get());
	}

	//-------------------------------------------------------------------
	//  OperationPartName = Spc SmallIdent Spc ":" SpcCom
	//-------------------------------------------------------------------
	void operationPartName() {
		lhs().put(rhs(1).text());
	}

	//-------------------------------------------------------------------
	//  DataPath = SmallIdent ( Spc "." Spc SmallIdent )*
	//-------------------------------------------------------------------
	void dataPath() {
		RawDataPath data = new RawDataPath();
		data.sourcePosition = lhs().where(0);
		data.path = new ArrayList<>();

		for (int i = 0; i < rhsSize(); i += 4) {
			data.path.add(rhs(i).text());
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  DataType = DataModule? BigIdent
	//-------------------------------------------------------------------
	void dataType() {
		RawDataType data = new RawDataType();
		data.sourcePosition = lhs().where(0);

		if (rhsSize() > 1) {
			data.path = (String) rhs(0).get();
		} else {
			data.path = "";
		}

		data.type = rhs(rhsSize() - 1).text();

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  DataModule = FilePath "/"
	//-------------------------------------------------------------------
	void dataModule() {
		lhs().put(rhs(0).text());
	}

	//-------------------------------------------------------------------
	//  ConstExpr = SpcCom (String / Int / Bool) SpcCom
	//-------------------------------------------------------------------
	void constExpr() {
		lhs().put(rhs(1));
	}

	//-------------------------------------------------------------------
	//  String = ["] Char*+ ["]
	//-------------------------------------------------------------------
	void unescapeString() {
		int N = rhsSize() - 1;
		StringBuilder sb = new StringBuilder(128);

		for (int i = 1; i < N; i++) {
			appendChar(rhs(i), sb);
		}

		RawStr data = new RawStr();
		data.sourcePosition = lhs().where(0);
		data.value = sb.toString();

		lhs().put(data);
	}
	private void appendChar(Phrase rhs, StringBuilder buf) {
		Object obj = rhs.get();
		if (obj instanceof String) {
			buf.append(obj);
		} else {
			buf.append(rhs.text());
		}
	}

	//-------------------------------------------------------------------
	//  EscapeChar = "\ u" Hex Hex Hex Hex
	//-------------------------------------------------------------------
	void unicodeChar() {
		try {
			String num = rhsText(1, rhsSize());
			int codePoint = Integer.parseInt(num, 16);
			lhs().put(new String(Character.toChars(codePoint)));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);	// should never happen!
		}
	}

	//-------------------------------------------------------------------
	//  EscapeChar = "\t"
	//-------------------------------------------------------------------
	void tabChar() {
		lhs().put("\t");
	}

	//-------------------------------------------------------------------
	//  EscapeChar = "\n"
	//-------------------------------------------------------------------
	void newlineChar() {
		lhs().put("\n");
	}

	//-------------------------------------------------------------------
	//  EscapeChar = "\r"
	//-------------------------------------------------------------------
	void carriagereturnChar() {
		lhs().put("\r");
	}

	//-------------------------------------------------------------------
	//  EscapeChar = !"\ u" "\" _
	//-------------------------------------------------------------------
	void backslashChar() {
		lhs().put(rhs(1).text());
	}

	//-------------------------------------------------------------------
	//  Int = [-+]? Natural
	//-------------------------------------------------------------------
	void integer() {
		RawInt data = new RawInt();
		data.sourcePosition = lhs().where(0);
		data.value = ((Long) rhs(rhsSize() - 1).get()).longValue();

		if ("-".equals(rhs(0).text())) {
			data.value = -data.value;
		}

		lhs().put(data);
	}

	//-------------------------------------------------------------------
	//  Natural = "0x" Hex+ ("_"+ Hex+)*
	//-------------------------------------------------------------------
	void hexInt() {
		try {
			String num = rhsText(1, rhsSize()).replace("_", "");
			lhs().put(Long.parseLong(num, 16));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);	// should never happen!
		}
	}

	//-------------------------------------------------------------------
	//  Natural = "0b" Binary+ ("_"+ Binary+)*
	//-------------------------------------------------------------------
	void binaryInt() {
		try {
			String num = rhsText(1, rhsSize()).replace("_", "");
			lhs().put(Long.parseLong(num, 2));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);	// should never happen!
		}
	}

	//-------------------------------------------------------------------
	//  Natural = "0c" Octal+ ("_"+ Octal+)*
	//-------------------------------------------------------------------
	void octalInt() {
		try {
			String num = rhsText(1, rhsSize()).replace("_", "");
			lhs().put(Long.parseLong(num, 8));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);	// should never happen!
		}
	}

	//-------------------------------------------------------------------
	//  Natural = Decimal+ ("_"+ Decimal+)*
	//-------------------------------------------------------------------
	void decimalInt() {
		try {
			String num = lhs().text().replace("_", "");
			lhs().put(Long.parseLong(num, 10));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);	// should never happen!
		}
	}

	//-------------------------------------------------------------------
	//  Bool = ("true"/"false")
	//-------------------------------------------------------------------
	void bool() {
		RawBool data = new RawBool();
		data.sourcePosition = lhs().where(0);
		data.value = "true".equals(lhs().text());

		lhs().put(data);
	}

}
