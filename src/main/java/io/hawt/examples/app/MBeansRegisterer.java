package io.hawt.examples.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.hawt.examples.app.mbeans.JMXSecurity;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MBeansRegisterer {

    private static final Logger LOG = LoggerFactory.getLogger(MBeansRegisterer.class);

    private final JMXSecurity jmxSecurity;

    MBeansRegisterer() {
        jmxSecurity = new JMXSecurity();
    }

    void onStart(@Observes StartupEvent event) {
        LOG.info("Register RBAC MBeans");
        try {
            jmxSecurity.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void onStop(@Observes ShutdownEvent event) {
        LOG.info("Deregister RBAC MBeans");
        try {
            jmxSecurity.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
