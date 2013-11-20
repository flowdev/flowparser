package org.flowdev.flowparser;

import org.flowdev.base.Port;

public interface IMainFlow {
    public Port<MainData> getInPort();

    public Port<MainConfig> getConfigPort();
}
