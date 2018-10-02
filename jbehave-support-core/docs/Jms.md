[Contents](../README.md)

## JMS steps

```
Given [$typeAlias] data for JMS broker [$broker]: $data
```
User can enter object paths and values in data ExamplesTable which are used to create a new object.
It's also possible to enter JMS headers using _@header._ prefix
* typeAlias - simple class name, object type we want to construct
* broker - name of the container we want to target. It is qualifier used to resolve JmsHandler, which implements individual steps and provides broker specific customization. This needs to be defined in your configuration.
```
When [$typeAlias] is sent to destination [$destinationName] on JMS broker [$broker]
```
New object is created, marshaled into XML string and JMS message is sent to given destination.

* destinationName - JMS queue or topic

## Send JMS messages to destinations located at Weblogic JMS broker
If you want to send JMS messages to destinations located at Weblogic JMS broker, follow these steps:

Add dependency to your project:
```$xml
<dependency>
	<groupId>com.oracle</groupId>
	<artifactId>wlthint3client</artifactId>
	<version>10.3.6</version>
</dependency>
```

In your `@Configuration`, add following beans and change `jndiEnvironment` values:

```$java
@Bean
JndiTemplate jndiTemplate() {
    Properties jndiEnvironment = new Properties();
    jndiEnvironment.put(javax.naming.Context.PROVIDER_URL, "http://localhost:7001");
    jndiEnvironment.put(javax.naming.Context.SECURITY_PRINCIPAL, "weblogic");
    jndiEnvironment.put(javax.naming.Context.SECURITY_CREDENTIALS, "welcome1");
    jndiEnvironment.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
    jndiEnvironment.put(javax.naming.Context.URL_PKG_PREFIXES, "weblogic.jndi.factories");

    return new JndiTemplate(jndiEnvironment);
}
```

```$java
@Bean
ConnectionFactory connectionFactory() throws NamingException {
    return jndiTemplate().lookup("connectionFactoryName", ConnectionFactory.class);
}
```

```$java
@Bean
public JmsJaxbHandler jmsJaxbHandler() throws NamingException {
    Class[] classesToBeBound = {NameRequest.class};
    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());

    JndiDestinationResolver jndiDestinationResolver = new JndiDestinationResolver();
    jndiDestinationResolver.setResourceRef(true);
    jndiDestinationResolver.setJndiEnvironment(jndiTemplate().getEnvironment());
    jmsTemplate.setDestinationResolver(jndiDestinationResolver);

    return new JmsJaxbHandler(jmsTemplate, classesToBeBound);
}
```
