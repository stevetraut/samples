package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.DocErrorReporter;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Handles transformation of doclet-generated XML into HTML. Methods 
 * of this class aren't used if the user doesn't specify 
 * an HTML output directory with the -d or -destdir Javadoc option.
 */
public class JspTagDocTransformer
{
    private static String m_tagLibPath;
    private static String m_tagHtmlName;
    private static String m_uriValue;
    private static final String xsltDir = "xslt/";
    private static ConfigurationJspTagDoc m_configuration;

    /**
     * Receives an array of topic XML documents, sets up transformation by setting
     * XSLT parameters, then loops through the array to transform the XML into
     * HTML.
     * 
     * @param xmlTopicDocArray The array of topic XML documents.
     * @param configuration Information received by the user, such as the 
     * target destination for HTML output.
     * @param jvdErrorReporter A Javadoc error reporter to report exceptions.
     */
    public static void transformTopicsXmlToHtml(XmlObject[] xmlTopicDocArray,
                                         ConfigurationJspTagDoc configuration, 
                                         DocErrorReporter jvdErrorReporter)
    {
        m_configuration = configuration;
        Transformer transformer = null;
        File xsltFile = null;
        try
        {
            StreamSource xsltSource = null;
            InputStream xsltFileStream =
                    JspTagDocTransformer.class.getResourceAsStream('/' + xsltDir + "tag_topics.xslt");
            xsltSource = new StreamSource(xsltFileStream);
            // Handle the case where the code is running outside a JAR.
            if (xsltFileStream == null)
            {
                xsltFile = new File(xsltDir + "tag_topics.xslt");
                xsltFileStream = new FileInputStream(xsltFile);
                xsltSource = new StreamSource(xsltFileStream);
                xsltSource.setSystemId(xsltFile);
            }
            // Set up the transformer. It's the same stylesheet for every topic.
            TransformerFactory factory = TransformerFactory.newInstance();
            transformer = factory.newTransformer(xsltSource);
            transformer.setParameter("jsptagref", String.valueOf(m_configuration.useJspTagRefContent()));
            transformer.setParameter("tld", String.valueOf(m_configuration.useTldContent()));
            transformer.setParameter("standard", String.valueOf(m_configuration.useStandardContent()));
            transformer.setParameter("windowTitle", m_configuration.getWindowTitle());
            transformer.setParameter("docTitle", m_configuration.getDocTitle());
            transformer.setParameter("header", m_configuration.getHeader());
            transformer.setParameter("footer", m_configuration.getFooter());
            transformer.setParameter("author", m_configuration.getAuthor());
            transformer.setParameter("noNavbar", m_configuration.getNoNavbar());
            transformer.setParameter("noIndex", m_configuration.getNoIndex());
            transformer.setParameter("keywords", m_configuration.getKeywords());
            transformer.setParameter("bottom", m_configuration.getBottom());
            transformer.setParameter("noTagInfo", m_configuration.getNoTagInfo());
        } catch (TransformerConfigurationException tce)
        {
            jvdErrorReporter.printError("Error while creating a transformer to generate HTML files. \n" +
                    tce.getLocalizedMessage());
        } catch (FileNotFoundException fnfe)
        {
            jvdErrorReporter.printError(xsltFile.getPath() + "Error while reading this XSLT file: \n" +
                    fnfe.getLocalizedMessage());
        }
        // Loop through the documents in the array, passing each to a method that
        // will do the actual transformation.
        for (int i = 0; i < xmlTopicDocArray.length; i++)
        {
            applyTopicXslt(xmlTopicDocArray[i], m_configuration.getDestDir(), jvdErrorReporter, transformer);
        }
    }

    /**
     * Applies XSLT to <em>xmlTopic</em>, saving the resulting HTML
     * to the destination specified by the user.
     * 
     * @param xmlTopic The XML topic document to transform.
     * @param htmlDir The user-specified HTML destination directory.
     * @param jvdErrorReporter An instance to report errors, if any.
     * @param transformer A transformer configured with the XSLT and 
     * parameters for transformation.
     */
    private static void applyTopicXslt(XmlObject xmlTopic, String htmlDir,
                                       DocErrorReporter jvdErrorReporter, 
                                       Transformer transformer)
    {
        File outputFile = null;
        StreamSource source = null;
        try
        {
            XmlCursor cursor = xmlTopic.newCursor();
            cursor.toFirstChild();
            XmlObject rootObject = cursor.getObject();
            // Get the name of this JSP tag so it can be used as an HTML file name.
            String query = "declare namespace ref='http://apache.org/beehive/netui/tools/doclet/schema'" +
                    "$this/ref:name";
            XmlObject[] results = rootObject.selectPath(query);
            if (results != null)
            {
                m_tagHtmlName =
                        ((XmlString) results[0]).getStringValue() + ".html";
            }
            // Get the taglib URI so it can be used to build a directory
            // hierarchy leading to the output HTML file.
            query = "declare namespace ref='http://apache.org/beehive/netui/tools/doclet/schema'" +
                    "$this/ref:uri";
            results = rootObject.selectPath(query);
            if (results != null)
            {
                m_uriValue = ((XmlAnyURI) results[0]).getStringValue();
            }
            URI uri = new URI(m_uriValue);
            m_tagLibPath = uri.getPath() + '/';
            // Sometimes the URI doesn't include a host.
            if (uri.getHost() != null)
            {
                m_tagLibPath = uri.getHost() + m_tagLibPath;
            }
            // Create the directory hierarchy and transform the XML.
            String fullTagRefPath = htmlDir + '/' + m_tagLibPath;
            File pathFile = new File(fullTagRefPath);
            pathFile.mkdirs();
            outputFile = new File(pathFile.getPath() + '/' + m_tagHtmlName);
            source = new StreamSource(xmlTopic.newInputStream());
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
            StreamResult result = new StreamResult(out);
            jvdErrorReporter.printNotice("Saving " + outputFile.getPath());
            transformer.transform(source, result);
        } catch (IOException ioe)
        {
            jvdErrorReporter.printError(outputFile.getPath() + ": Error while writing the file. \n" +
                    ioe.getLocalizedMessage());
        } catch (URISyntaxException use)
        {
            jvdErrorReporter.printError(m_uriValue + ": Error while preparing this URI. \n" +
                    use.getLocalizedMessage());
        } catch (TransformerException te)
        {
            jvdErrorReporter.printError("Error while transforming XML into an HTML file. \n" +
                    te.getLocalizedMessage());
        }
    }

    /**
     * Receives the overview XML document and handles its transformation
     * into multiple HTML files needed to make up the various
     * navigation topics included in the HTML frameset.
     * 
     * @param xmlOverview The overview to transform.
     * @param configuration User-specified Javadoc options.
     * @param jvdErrorReporter An instance to report errors.
     */
    public static void transformOverviewXmlToHtml(XmlObject xmlOverview,
                                            ConfigurationJspTagDoc configuration,
                                            DocErrorReporter jvdErrorReporter)
    {
        m_configuration = configuration;
        File htmlDirFile = new File(m_configuration.getDestDir());
        // Apply a different XSLT depending on the kind of nav topic needed.
        applyOverviewXslt(xmlOverview, htmlDirFile, "taglib-overview-frame",
                "taglib-overview-frame", jvdErrorReporter);
        applyOverviewXslt(xmlOverview, htmlDirFile, "alltaglibs-frame",
                "alltaglibs-frame", jvdErrorReporter);
        applyOverviewXslt(xmlOverview, htmlDirFile, "taglib-overview-summary",
                "taglib-overview-summary", jvdErrorReporter);
        applyOverviewXslt(xmlOverview, htmlDirFile, "taglib-frame-ignore",
                "taglib-frame", jvdErrorReporter);
        applyOverviewXslt(xmlOverview, htmlDirFile, "taglib-summary-ignore",
                "taglib-summary", jvdErrorReporter);
        if (m_configuration.getNoIndex().equals("false"))
        {
            applyOverviewXslt(xmlOverview, htmlDirFile, "index-all",
                    "taglib-index", jvdErrorReporter);
        }
    }

    /**
     * Applies the <em>xsltFileName</em> XSLT to the <em>xmlOverview</em>
     * overview XML document. This method is called several times each
     * with a different XSLT file for different transformation of the 
     * overview XML.
     * 
     * @param xmlOverview The overview XML to transform.
     * @param htmlDirFile The HTML destination directory specified by the user.
     * @param htmlFileName The name the resulting HTML file should have.
     * @param xsltFileName The name of the XSLT file to apply.
     * @param jvdErrorReporter An instance for reporting errors.
     */
    private static void applyOverviewXslt(XmlObject xmlOverview,
                                          File htmlDirFile, String htmlFileName,
                                          String xsltFileName,
                                          DocErrorReporter jvdErrorReporter)
    {
        try
        {
            StreamSource xsltSource = null;
            InputStream xsltFileStream =
                    JspTagDocTransformer.class.getResourceAsStream('/' + xsltDir +
                    xsltFileName + ".xslt");
            xsltSource = new StreamSource(xsltFileStream);
            // Handle the case in which the code isn't running from inside a JAR.
            if (xsltFileStream == null)
            {
                File xsltFile = new File(xsltDir + xsltFileName + ".xslt");
                xsltFileStream = new FileInputStream(xsltFile);
                xsltSource = new StreamSource(xsltFileStream);
                xsltSource.setSystemId(xsltFile);
            }
            // Create the transformer.
            File outputFile = new File(htmlDirFile.getAbsolutePath() + '/' + htmlFileName +
                    ".html");
            StreamSource xmlSource = new StreamSource(xmlOverview.newInputStream());
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
            StreamResult result = new StreamResult(out);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(xsltSource);

            // Set parameters that will be passed to the xslt stylesheet.
            transformer.setParameter("taglibLocation", htmlDirFile.getAbsolutePath());
            transformer.setParameter("jsptagref", String.valueOf(m_configuration.useJspTagRefContent()));
            transformer.setParameter("tld", String.valueOf(m_configuration.useTldContent()));
            transformer.setParameter("standard", String.valueOf(m_configuration.useStandardContent()));
            transformer.setParameter("windowTitle", m_configuration.getWindowTitle());
            transformer.setParameter("docTitle", m_configuration.getDocTitle());
            transformer.setParameter("noIndex", m_configuration.getNoIndex());
            transformer.setParameter("noNavbar", m_configuration.getNoNavbar());
            transformer.setParameter("header", m_configuration.getHeader());
            transformer.setParameter("footer", m_configuration.getFooter());
            transformer.setParameter("keywords", m_configuration.getKeywords());
            transformer.setParameter("footer", m_configuration.getFooter());
            transformer.setParameter("bottom", m_configuration.getBottom());
            jvdErrorReporter.printNotice("Saving " + outputFile.getPath());
            transformer.transform(xmlSource, result);
        } catch (TransformerConfigurationException tce)
        {
            jvdErrorReporter.printError("Exception while writing an HTML file: " +
                    tce.getLocalizedMessage());
            return;
        } catch (TransformerException te)
        {
            jvdErrorReporter.printError("Exception while writing an HTML file: " +
                    te.getLocalizedMessage());
            return;
        } catch (IllegalArgumentException iae)
        {
            jvdErrorReporter.printError("Exception while writing an HTML file: " +
                    iae.getLocalizedMessage());
            return;
        } catch (IOException ioe)
        {
            jvdErrorReporter.printError("Exception while writing an HTML file: " +
                    ioe.getLocalizedMessage());
            return;
        }
    }
}
