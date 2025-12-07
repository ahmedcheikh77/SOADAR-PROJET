package com.medical.dme.common.xml;

import com.medical.dme.common.model.MedicalRecord;
import jakarta.xml.bind.*;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MedicalRecordXMLProcessor {

    public MedicalRecord unmarshalFromXML(String xmlFilePath) throws Exception {
        JAXBContext context = JAXBContext.newInstance(MedicalRecord.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Validation avec XSD
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new StreamSource(
                getClass().getResourceAsStream("/medical-record.xsd")
        ));
        unmarshaller.setSchema(schema);

        try (InputStream is = Files.newInputStream(Paths.get(xmlFilePath))) {
            return (MedicalRecord) unmarshaller.unmarshal(is);
        }
    }

    public void marshalToXML(MedicalRecord record, String outputPath) throws Exception {
        JAXBContext context = JAXBContext.newInstance(MedicalRecord.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        try (FileWriter writer = new FileWriter(outputPath)) {
            marshaller.marshal(record, writer);
        }
    }

    public String evaluateXPath(String xmlContent, String xpathExpression) throws Exception {
        // Implémentation XPath pour extraire des données spécifiques
        javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory.newInstance();
        javax.xml.xpath.XPath xpath = xpathFactory.newXPath();

        InputSource source = new InputSource(new StringReader(xmlContent));
        return xpath.evaluate(xpathExpression, source);
    }
}