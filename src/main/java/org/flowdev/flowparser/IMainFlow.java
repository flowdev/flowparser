package org.flowdev.flowparser;

import org.flowdev.base.Port;

public interface IMainFlow {
    public Port<MainData> getIn();

    public Port<MainConfig> getConfig();
}
