package io.hawt.examples.app.mbeans;

import java.util.List;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import io.hawt.util.MBeanSupport;

/**
 * Deny-all JMXSecurityMBean implementation.
 */
public class JMXSecurity extends MBeanSupport implements JMXSecurityMBean {

    @Override
    protected String getDefaultObjectName() {
        return "io.hawt.examples:type=security,area=jmx,name=DenyAllJMXSecurity";
    }

    @Override
    public boolean canInvoke(String objectName) {
        return false;
    }

    @Override
    public boolean canInvoke(String objectName, String methodName) {
        return false;
    }

    @Override
    public boolean canInvoke(String objectName, String methodName, String[] argumentTypes) {
        return false;
    }

    @Override
    public TabularData canInvoke(Map<String, List<String>> bulkQuery) throws Exception {
        TabularData table = new TabularDataSupport(CAN_INVOKE_TABULAR_TYPE);

        for (Map.Entry<String, List<String>> entry : bulkQuery.entrySet()) {
            String objectName = entry.getKey();
            List<String> methods = entry.getValue();
            if (methods.size() == 0) {
                CompositeData data = new CompositeDataSupport(CAN_INVOKE_RESULT_ROW_TYPE,
                    CAN_INVOKE_RESULT_COLUMNS,
                    new Object[]{objectName, "", false});
                table.put(data);
            } else {
                for (String method : methods) {
                    CompositeData data = new CompositeDataSupport(CAN_INVOKE_RESULT_ROW_TYPE,
                        CAN_INVOKE_RESULT_COLUMNS,
                        new Object[]{objectName, method, false});
                    table.put(data);
                }
            }
        }

        return table;
    }
}
