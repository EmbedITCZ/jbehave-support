package org.jbehavesupport.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringEscapeUtils.escapeXml10;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class XmlReporterFactory extends Format {

    private static final String INDEX_FILE = "index.xml";
    private static final String XML_STYLESHEET_HEADER = "%n<?xml-stylesheet type=\"text/xsl\" href=\"%s\"?>";
    private static final StoryReporter NULL_STORY_REPORTER = new NullStoryReporter();
    private final XPath xPath = XPathFactory.newInstance().newXPath();

    @Autowired
    private List<XmlReporterExtension> xmlReporterExtensions;

    @Value("${report.indexTemplate:index.xslt}")
    private Resource indexTemplate;

    @Value("${report.template:report.xslt}")
    private Resource reportTemplate;

    @Value("#{'${report.additionalResources:functions.js}'.split(',')}")
    private List<Resource> additionalResources;

    @Value("${report.directory:reports}")
    private String reportsDirectory;

    private String reportsAbsoluteDirectory;

    public XmlReporterFactory() {
        super("XML-EXT");
    }

    @Override
    public StoryReporter createStoryReporter(FilePrintStreamFactory filePrintStreamFactory, StoryReporterBuilder storyReporterBuilder) {
        FilePrintStreamFactory.ResolveToSimpleName pathResolver = new FilePrintStreamFactory.ResolveToSimpleName();
        FilePrintStreamFactory.FileConfiguration fileConfiguration = new FilePrintStreamFactory.FileConfiguration(reportsDirectory, "xml", pathResolver);
        filePrintStreamFactory.useConfiguration(fileConfiguration);

        // skip xml report generation for BeforeStories and AfterStories
        String fileName = filePrintStreamFactory.getOutputFile().getName();
        reportsAbsoluteDirectory = filePrintStreamFactory.getOutputFile().getParentFile().getAbsolutePath();
        if (!fileName.matches("(Before|After)Stories.xml")) {
            XmlReporter reportXml = new XmlReporter(xmlReporterExtensions, filePrintStreamFactory.createPrintStream(), reportTemplate.getFilename());
            reportXml.doReportFailureTrace(storyReporterBuilder.reportFailureTrace());
            reportXml.doCompressFailureTrace(storyReporterBuilder.compressFailureTrace());

            copyResourceToReportDirectory(reportTemplate);
            for (Resource additionalResource : additionalResources) {
                copyResourceToReportDirectory(additionalResource);
            }
            return reportXml;
        } else {
            return NULL_STORY_REPORTER;
        }
    }

    @PreDestroy
    public void destroy() {
        log.debug("Generating report index file");
        if (nonNull(reportsAbsoluteDirectory) && Paths.get(reportsAbsoluteDirectory).toFile().exists()) {
            IndexContainer container = new IndexContainer();
            List<Path> files = new ArrayList<>();
            try (DirectoryStream<Path> xmlFiles = Files.newDirectoryStream(Paths.get(reportsAbsoluteDirectory),
                path -> path.toString().endsWith(".xml") && !path.toString().endsWith(INDEX_FILE))) {
                xmlFiles.forEach(files::add);
                files.sort(Comparator.comparing(path -> path.toAbsolutePath().toString()));
                files.forEach(path -> {
                    container.addItem(parseFile(path.toFile()));
                    addContainerMetadata(container, path.toFile());
                });
            } catch (IOException e) {
                log.error("Some problem with parsing xml files for index", e);
            }
            storeIndex(container);
        }
    }

    private void storeIndex(IndexContainer container) {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(IndexContainer.class);
            Marshaller marshaller = contextObj.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if (nonNull(indexTemplate)) {
                String header = format(XML_STYLESHEET_HEADER, escapeXml10(indexTemplate.getFilename()));
                setStyleSheetHeader(marshaller, header);
                copyResourceToReportDirectory(indexTemplate);
            }
            marshaller.marshal(container, new FileOutputStream(reportsAbsoluteDirectory + "/" + INDEX_FILE));
        } catch (JAXBException | IOException e) {
            log.error("Some problem with generating index", e);
        }
    }

    private void setStyleSheetHeader(Marshaller marshaller, String header) throws PropertyException {
        // tricky by http://timepassguys.blogspot.cz/2011/12/jaxb-exception-javaxxmlbindpropertyexce.html
        try {
            marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", header);
        } catch (PropertyException pex) {
            marshaller.setProperty("com.sun.xml.bind.xmlHeaders", header);
        }
    }

    private void copyResourceToReportDirectory(Resource resource) {
        copyResourceToReportDirectory(resource, reportsAbsoluteDirectory + "/" + resource.getFilename());
    }

    private void copyResourceToReportDirectory(Resource resource, String targetFilePath) {
        File targetFile = new File(targetFilePath);
        try {
            FileUtils.copyInputStreamToFile(resource.getInputStream(), targetFile);
        } catch (IOException e) {
            log.error("Cannot copy resource file to target", e);
        }
    }

    private IndexItem parseFile(File file) {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // XPath Query for showing all nodes value
            return IndexItem.builder()
                .path(getValue(doc, "/story/@path"))
                .status(getValue(doc, "/story/status"))
                .duration(getValue(doc, "/story/duration"))
                .fileName(file.getName())
                .build();
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            log.error("Error in parsing xml report", e);
        }
        return null;
    }

    private void addContainerMetadata(IndexContainer indexContainer, final File file) {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(file);

            int i = 1;
            while (true) {
                String key = getValue(doc, "/story/environmentInfo/values[" + i + "]/key");
                String value = getValue(doc, "/story/environmentInfo/values[" + i + "]/value");
                if (isNotEmpty(key)) {
                    indexContainer.addEnvironmentInfo(key, value);
                    i++;
                } else {
                    break;
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            log.error("Error in parsing xml report", e);
        }
    }

    private String getValue(Document doc, String path) throws XPathExpressionException {
        XPathExpression expr = xPath.compile(path);
        return (String) expr.evaluate(doc, XPathConstants.STRING);
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement(name = "item")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class IndexItem {

        private String status;
        private String duration;
        private String path;
        private String fileName;
    }

    @NoArgsConstructor
    @XmlRootElement(name = "index")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class IndexContainer {

        @XmlElement(name = "item")
        private List<IndexItem> items = new ArrayList<>();

        @XmlElement(name = "environmentInfo")
        private Map<String, String> environmentInfo;

        public void addItem(IndexItem item) {
            items.add(item);
        }

        public void addEnvironmentInfo(String key, String value) {
            if (environmentInfo == null) {
                environmentInfo = new TreeMap<>();
            }
            environmentInfo.put(key, value);
        }
    }
}
