package org.flowdev.flowparser;

import org.flowdev.base.Port;

@SuppressWarnings("WeakerAccess")
public interface IMainFlow {
    public Port<MainData> getInPort();

    public Port<MainConfig> getConfigPort();
}
