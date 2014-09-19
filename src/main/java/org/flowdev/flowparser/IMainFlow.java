package org.flowdev.flowparser;

import org.flowdev.base.Port;

public interface IMainFlow {
    Port<MainData> getInPort();

    Port<MainConfig> getConfigPort();

    void setErrorPort(Port<Throwable> port);
}
