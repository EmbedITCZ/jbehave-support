package org.jbehavesupport.core.test.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean(name = "name")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("Name");
        wsdl.setLocationUri("/nameService/");
        wsdl.setTargetNamespace("http://jbehavesupport.org/definitions");
        wsdl.setSchema(countriesSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema getSchema() {
        return new SimpleXsdSchema(new ClassPathResource("name.xsd"));
    }
}
