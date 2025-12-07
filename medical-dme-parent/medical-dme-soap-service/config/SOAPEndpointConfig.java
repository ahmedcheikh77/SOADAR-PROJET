package com.medical.dme.soap.config;

import com.medical.dme.soap.IMedicalRecordSOAPService;
import com.medical.dme.soap.impl.MedicalRecordSOAPServiceImpl;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import jakarta.xml.ws.Endpoint;

@Configuration
@EnableWs
public class SOAPEndpointConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "medicalRecords")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema medicalRecordSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("MedicalRecordPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://medical.dme.com/soap");
        wsdl11Definition.setSchema(medicalRecordSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema medicalRecordSchema() {
        return new SimpleXsdSchema(new ClassPathResource("medical-record.xsd"));
    }

    // Alternative: Publier l'endpoint SOAP avec JAX-WS
    @Bean
    public Endpoint publishSOAPService() {
        IMedicalRecordSOAPService service = new MedicalRecordSOAPServiceImpl();
        String address = "http://localhost:8081/medical/soap/recordService";
        return Endpoint.publish(address, service);
    }
}
