package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.BaseOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.SemanticConnectionsData;
import org.flowdev.parser.data.ParseResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.flowdev.parser.util.ParserUtil.isOk;

public class CreateConnections extends BaseOp<NoConfig> {
    private Port<MainData> inPort = this::createConns;
    private Port<MainData> outPort;
    private Port<SemanticConnectionsData> chainInPort = data -> this.dataFromChain = data;
    private Port<SemanticConnectionsData> chainOutPort;

    private SemanticConnectionsData dataFromChain;


    /**
     * text input:
     * ( optInPort  [OptDataType]-> optInPort )? opName(OpType) optOutPort
     * ( [OptDataType]-> optInPort opName(OpType) optOutPort )*
     * ( [OptDataType]-> optOutPort )?
     * <p>
     * semantic input:
     * List(multiple1)[
     * List(all)[
     * List(chainBeg)[Connection, Operation], List(multiple0)[ List(chainMid)[arrow, Operation] ], Connection ]
     * ]
     * ]
     *
     * @param data the full main flow data including the parser data.
     */
    @SuppressWarnings("unchecked")
    private void createConns(MainData data) {
        SemanticConnectionsData connsData = new SemanticConnectionsData().mainData(data).ops(new LinkedHashMap<>(256))
                .conns(new ArrayList<>(256));

        for (ParseResult subResult : data.parserData().subResults()) {
            List<Object> chain = (List<Object>) subResult.value();
            connsData.addOpResult(new AddOpResult())
                    .chainBeg((List<Object>) chain.get(0))
                    .chainMids((List<Object>) chain.get(1))
                    .chainEnd((Connection) chain.get(2));

            chainOutPort.send(connsData);
            connsData = dataFromChain;
        }
        data.parserData().result().value(null);

        if (isOk(data.parserData().result())) {
            Flow result = new Flow().connections(new ArrayList<>(connsData.conns()))
                    .operations(new ArrayList<>(connsData.ops().values()));

            data.parserData().result().value(result);
        }
        outPort.send(data);
    }

    public Port<SemanticConnectionsData> getChainInPort() {
        return chainInPort;
    }

    public void setChainOutPort(Port<SemanticConnectionsData> chainOutPort) {
        this.chainOutPort = chainOutPort;
    }

    public Port<MainData> getInPort() {
        return inPort;
    }

    public void setOutPort(Port<MainData> outPort) {
        this.outPort = outPort;
    }

}
