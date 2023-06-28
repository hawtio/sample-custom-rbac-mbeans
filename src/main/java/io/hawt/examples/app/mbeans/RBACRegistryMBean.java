package io.hawt.examples.app.mbeans;

import java.util.Map;

public interface RBACRegistryMBean {
    Map<String, Object> list() throws Exception;
}
