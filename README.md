# Custom RBAC MBeans Sample for Hawtio

Hawtio has the [RBAC](https://github.com/hawtio/hawtio-next/tree/main/packages/hawtio/src/plugins/rbac) feature, which is provided through the [RBAC MBeans](#hawtio-rbac). This sample project demonstrates how an application/platform can provide custom RBAC MBeans for Hawtio.

## Key components

Currently this sample provides a deny-all implementation of `JMXSecurityMBean`, so by running the application and connecting the Hawtio console to it you can test how RBAC works on Hawtio in an opposite way, i.e. how it looks when every operation on the console is disallowed.

The key components to look at in this sample are as follows:

| File | Description |
| ---- | ----------- |
| [JMXSecurity.java](./src/main/java/io/hawt/examples/app/mbeans/JMXSecurity.java) | Deny-all implementation of JMXSecurityMBean, which returns `false` to any `canInvoke` request.  |
| [RBACRegistry.java](./src/main/java/io/hawt/examples/app/mbeans/RBACRegistry.java) | [TODO] |
| RBACDecorator.java | [TODO] |

## How to run the application

### Dev mode

You can run your application in dev mode that enables live coding using:

```console
mvn compile quarkus:dev
```

### Packaging and running

The application can be packaged using:

```console
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory. The application is now runnable with Jolokia JVM agent using:

```console
java -javaagent:./target/dependency/jolokia-jvm.jar -jar target/quarkus-app/quarkus-run.jar
```

### Connecting from Hawtio console

Once you have run the application, you can connect the Hawtio console to it at the Jolokia endpoint:
<http://localhost:8778/jolokia>

## Hawtio RBAC

There are three types of MBeans that enable Hawtio RBAC together.

- **ACL MBean** - An implementation of [JMXSecurityMBean](https://github.com/hawtio/hawtio/blob/3.x/hawtio-system/src/main/java/io/hawt/jmx/JMXSecurityMBean.java). This is the core RBAC API that a platform should provide to support RBAC for Hawtio. A platform can provide information about which JMX operation can be invoked by the authenticated subject through the interface.
- **RBACRegistry** - Hawtio relies on Jolokia [LIST](https://jolokia.org/reference/html/protocol.html#list) operation to fetch the entire MBean list from the JVM. However, Java frameworks/libraries such as Apache Camel or Artemis can have hundreds or even thousands of MBeans in the same shapes (e.g. Camel routes, Artemis destination queues). In such cases, a lot of MBean information are duplicated, leading to an increasing amount of network load. [RBACRegistry](https://github.com/hawtio/hawtio/blob/3.x/hawtio-system/src/main/java/io/hawt/jmx/RBACRegistry.java) is a Hawtio MBean that provides the optimised list operation for Jolokia. The idea is that the MBean list operation groups MBeans in the same shape and share the MBean information in the cache, and makes each MBean with the same shape referring to the cached information instead of each duplicating the same one.
- **RBACDecorator** - This is yet another optimisation MBean Hawtio may provide. Basically, fetching RBAC information takes two steps in Hawtio. At the first step Hawtio fetches the MBean list utilising Jolokia `LIST` or `RBACRegistry`, then at the second step it acquires RBAC information on the list of MBeans. The second step uses a bulk Jolokia request, so it only costs one HTTP request. But it's still bulky and can consume network traffic. You can optimise this process by combining the steps into one at the server side, and it is what [RBACDecorator](https://github.com/hawtio/hawtio/blob/eab4992addfe3da0d72ef3c123e35703cc6a4428/platforms/hawtio-osgi-jmx/src/main/java/io/hawt/osgi/jmx/RBACDecorator.java) provides. Theoretically every platform can provide the `RBACDecorator` MBean, but currently only Karaf and Hawtio Online provide the implementations.

The object names for the RBAC MBeans:

- **ACL MBean:** `*:type=security,area=jmx,*`
- **RBACRegistry:** `hawtio:type=security,name=RBACRegistry`
- **RBACDecorator:** `hawtio:type=security,area=jolokia,name=RBACDecorator`

### Matrix of supported RBAC MBeans per platform

| Platform           | ACL MBean | RBACRegistry | RBACDecorator |
|--------------------|-----------|--------------|---------------|
| Karaf              | &check;   | &check;      | &check;       |
| WildFly            | - [^1]    | &check;      | -             |
| Spring Boot        | - [^1]    | &check;      | -             |
| Artemis            | &check;   | &check;      | -             |
| Jolokia            | -         | -            | -             |
| Hawtio Online [^2] | &check;   | &check;      | &check;       |

[^1]: Dummy JMXSecurity implementation: <https://github.com/hawtio/hawtio/blob/3.x/hawtio-system/src/main/java/io/hawt/jmx/JMXSecurity.java>
[^2]: Through Nginx JS: <https://github.com/hawtio/hawtio-online/blob/main/docker/rbac.js>
