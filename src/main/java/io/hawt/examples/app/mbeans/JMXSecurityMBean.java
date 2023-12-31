package io.hawt.examples.app.mbeans;

import java.util.List;
import java.util.Map;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

public interface JMXSecurityMBean {

    TabularType CAN_INVOKE_TABULAR_TYPE = SecurityMBeanOpenTypeInitializer.TABULAR_TYPE;
    CompositeType CAN_INVOKE_RESULT_ROW_TYPE = SecurityMBeanOpenTypeInitializer.ROW_TYPE;
    String[] CAN_INVOKE_RESULT_COLUMNS = SecurityMBeanOpenTypeInitializer.COLUMNS;

    boolean canInvoke(String objectName) throws Exception;

    boolean canInvoke(String objectName, String methodName) throws Exception;

    boolean canInvoke(String objectName, String methodName, String[] argumentTypes) throws Exception;

    TabularData canInvoke(Map<String, List<String>> bulkQuery) throws Exception;

    class SecurityMBeanOpenTypeInitializer {
        private static final String[] COLUMNS = new String[]{"ObjectName", "Method", "CanInvoke"};
        private static final CompositeType ROW_TYPE;

        static {
            try {
                ROW_TYPE = new CompositeType("CanInvokeRowType",
                    "The rows of a CanInvokeTabularType table.",
                    COLUMNS,
                    new String[]{
                        "The ObjectName of the MBean checked",
                        "The Method to checked. This can either be a bare method name which means 'any method with this name' " +
                            "or a specific overload such as foo(java.lang.String). If an empty String is returned this means 'any' method.",
                        "true if the method or mbean can potentially be invoked by the current user."},
                    new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.BOOLEAN}
                );
            } catch (OpenDataException e) {
                throw new RuntimeException(e);
            }
        }

        private static final TabularType TABULAR_TYPE;

        static {
            try {
                TABULAR_TYPE = new TabularType("CanInvokeTabularType", "Result of canInvoke() bulk operation", ROW_TYPE,
                    new String[]{"ObjectName", "Method"});
            } catch (OpenDataException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
