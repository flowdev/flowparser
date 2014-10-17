package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.flowparser.data.MainData;

public interface IMainFlow {
    Port<MainData> getInPort();

    Port<MainFlow.MainFlowConfig> getConfigPort();

    void setErrorPort(Port<Throwable> port);
}
