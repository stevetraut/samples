package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.*;
import org.apache.beehive.netui.tools.doclet.schema.*;
import org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary12.TaglibDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import javax.xml.namespace.QName;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * JspTagDoc is a doclet that produces XML and HTML for custom JSP tag
 * reference topics. This doclet uses the content of both
 * the custom tag library's TLD file and its Java source files. It produces 
 * one XML file for each custom tag and function, along with an overview 
 * XML file for creating summaries and indexes.
 * <p/>
 * The HTML output of this doclet is a set of JSP tag reference topics 
 * that focus on providing information about the tags as used in 
 * a JSP file, including the tag's attributes and their use. In contrast,
 * Javadoc for the handler classes includes reference information for methods
 * (such as attribute getters and setters and other methods) that are not
 * visible to tag users, and needn't be of interest to the tag's user. JSP 
 * tags are designed to provide a declarative way to execute the underlying Java code;
 * JSP tag <em>references</em> are designed to describe the tags in that
 * context.
 * <p/>
 * This doclet supports many of the Javadoc 1.5 (including standard doclet)
 * features, but not all. Some features are not supported because they don't
 * seem to make much sense in a doclet that renders JSP tag documentation rather
 * than Java class documentation. Some aren't supported simply because support
 * hasn't been implemented yet (such as the -docfilessubdirs option).
 */
public final class JspTagDoc extends Doclet
{
    private static final XmlOptions m_xmlOptions = getXmlOptions();
    private static ConfigurationJspTagDoc m_configuration;
    private static DocErrorReporter m_jvdErrorReporter;
    protected static String REF_NAMESPACE = "http://apache.org/beehive/netui/tools/doclet/schema";

    /**
     * Called by Javadoc to begin processing with this doclet. The
     * <em>jvdRootDoc</em> parameter contains all of the information provided by
     * Javadoc for the packages specified for this Javadoc run. This includes
     * arrays of interfaces, classes, methods, and includes the doclet-specific
     * options specified by the user at the command-line.
     *
     * @param jvdRootDoc The information collected by Javadoc for this run.
     * @return <code>true</code> if the run was successful; otherwise
     *         <code>false</code>.
     */
    public static boolean start(RootDoc jvdRootDoc)
    {
        m_configuration = new ConfigurationJspTagDoc(jvdRootDoc);
        try
        {
            generateDocs(jvdRootDoc);
            return true;
        } catch (Exception e)
        {
            m_jvdErrorReporter.printError("Exception while "
                    + "generating JSP tag reference documentation: "
                    + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Called by Javadoc to retrieve the supported option length for the
     * specified command-line <em>option</em>. See
     * {@link ConfigurationJspTagDoc#optionLength(String)} for more information.
     *
     * @param option The Javadoc option whose length is being requested.
     * @return The sum of space-separated option parts, including the option
     *         name.
     */
    public static int optionLength(String option)
    {
        return ConfigurationJspTagDoc.optionLength(option);
    }

    /**
     * Called by Javadoc to determine if the command-line options specified by
     * the user are valid for the doclet. See
     * {@link ConfigurationJspTagDoc#validOptions(String[][], com.sun.javadoc.DocErrorReporter)}
     * for more information.
     *
     * @param options  The command-line options specified by the user.
     * @param reporter An error reporter for printing messages to the user.
     * @return <code>true</code> if the options are valid; otherwise,
     *         <code>false</code>.
     */
    public static boolean validOptions(String[][] options,
                                       DocErrorReporter reporter)
    {
        // Save the error reporter for use later.
        m_jvdErrorReporter = reporter;
        return ConfigurationJspTagDoc.validOptions(options, reporter);
    }

    /**
     * Generates tag reference XML (and HTML, if requested). This method
     * locates the TLD files specified with the -tldpath command-line option,
     * then binds the files to XMLBeans types. Most of the information
     * in the TLDs is then copied into another XML shape that is 
     * designed to hold both TLD and Javadoc information. The new
     * XML is then handed to the {@link JavadocHandler} class, where
     * it receives information from corresponding tag and function classes in 
     * the current run. From the combined information, an overview XML file
     * is created.
     *
     * @param jvdRootDoc An object containing all of the information that Javadoc has
     *                   collected from the classes it looked at.
     */
    private static void generateDocs(RootDoc jvdRootDoc)
    {
        ArrayList xmlJspTagDocList = new ArrayList();
        ArrayList xmlFunctionDocList = new ArrayList();

        if (jvdRootDoc.classes().length == 0)
        {
            m_jvdErrorReporter.printNotice("There are no public classes to document. "
                    + "This run will use TLD content only.");
        }
        // Get the TLD files specified by the user.
        File[] tldFiles = m_configuration.getTldfiles();

        // XMLBeans variables to hold the XML we assemble.
        TaglibSummariesDocument xmlOverviewDoc =
                TaglibSummariesDocument.Factory.newInstance();
        TaglibSummariesDocument.TaglibSummaries xmlOverview =
                xmlOverviewDoc.addNewTaglibSummaries();
        JspTagDocument[] xmlJspTagDocArray = null;
        FunctionDocument[] xmlFunctionDocArray = null;

        // For each TLD file, determine which TLD shape (i.e., schema) it conforms
        // to. Then bind the TLD to the appropriate XMLBeans types, add the info
        // about its tags and functions to the doc XML we're generating.
        for (int i = 0; i < tldFiles.length; i++)
        {
            m_jvdErrorReporter.printNotice("Collecting information from " +
                    tldFiles[i].getPath());

            // Handle a TLD for 2.0 tags.
            if (isInNamespace(tldFiles[i], "http://java.sun.com/xml/ns/j2ee"))
            {
                com.sun.java.xml.ns.j2Ee.TaglibDocument tldTaglibDoc = null;
                try
                {
                    // Parse the TLD into an XMLBeans type.
                    tldTaglibDoc = com.sun.java.xml.ns.j2Ee.TaglibDocument.Factory
                            .parse(tldFiles[i]);
                    TaglibSummaryType xmlSummary = TaglibHandler20
                            .addLibraryToSummary(tldTaglibDoc, xmlOverview.addNewTaglibSummary());

                    if (tldTaglibDoc.getTaglib().sizeOfTagArray() > 0)
                    {
                        xmlJspTagDocArray = TaglibHandler20
                                .collectTagInfo(tldTaglibDoc);
                        xmlJspTagDocList.addAll(Arrays
                                .asList(xmlJspTagDocArray));
                    }
                    if (tldTaglibDoc.getTaglib().sizeOfFunctionArray() > 0)
                    {
                        xmlFunctionDocArray = TaglibHandler20.collectFunctionInfo(tldTaglibDoc);
                        xmlFunctionDocList.addAll(Arrays.asList(xmlFunctionDocArray));
                    }
                } catch (XmlException xmle)
                {
                    m_jvdErrorReporter.printError(tldFiles[i].getPath() + ": Error while " +
                            "parsing the file into XMLBeans. " + xmle.getLocalizedMessage());
                } catch (IOException ioe)
                {
                    m_jvdErrorReporter.printError(tldFiles[i].getPath() + ": Error while " +
                            "parsing the file into XMLBeans. " + ioe.getLocalizedMessage());
                }

            // Handle a TLD for 1.2 tags.
            } else if (isInNamespace(tldFiles[i],
                    "http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd"))
            {
                // Insert the namespace URI needed for binding the TLD to
                // XMLBeans types, and get back the altered TLD as a string.
                String tldAsString = insertNamespaceForTld(tldFiles[i],
                        "http://apache.org/beehive/netui/tools/doclet/schema/web-jsptaglibrary_1_2");
                org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary12.TaglibDocument tldTaglibDoc = null;
                try
                {
                    tldTaglibDoc = TaglibDocument.Factory.parse(tldAsString);
                    TaglibSummaryType xmlSummary = TaglibHandler12
                            .addLibraryToSummary(tldTaglibDoc, xmlOverview
                            .addNewTaglibSummary());
                    if (tldTaglibDoc.getTaglib().sizeOfTagArray() > 0)
                    {
                        xmlJspTagDocArray = TaglibHandler12.collectTagInfo(tldTaglibDoc);
                        xmlJspTagDocList.addAll(Arrays.asList(xmlJspTagDocArray));
                    }
                } catch (XmlException xmle)
                {
                    m_jvdErrorReporter.printError(tldFiles[i].getPath() + ": Error while " +
                            "parsing the file into XMLBeans. " + xmle.getLocalizedMessage());
                }
                // Handle a TLD for 1.1 tags.
            } else if (isInNamespace(tldFiles[i],
                    "http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_1.dtd"))
            {
                // Insert the namespace URI needed for binding the TLD to
                // XMLBeans types, and get back the altered TLD as a string.
                String tldAsString = insertNamespaceForTld(tldFiles[i],
                        "http://apache.org/beehive/netui/tools/doclet/schema/web-jsptaglibrary_1_1");
                org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.TaglibDocument tldTaglibDoc = null;
                try
                {
                    tldTaglibDoc = org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.TaglibDocument.Factory
                            .parse(tldAsString);
                    TaglibSummaryType xmlSummary = TaglibHandler11
                            .addLibraryToSummary(tldTaglibDoc, xmlOverview
                            .addNewTaglibSummary());
                    if (tldTaglibDoc.getTaglib().sizeOfTagArray() > 0)
                    {
                        xmlJspTagDocArray = TaglibHandler11.collectTagInfo(tldTaglibDoc);
                        xmlJspTagDocList.addAll(Arrays.asList(xmlJspTagDocArray));
                    }
                } catch (XmlException xmle)
                {
                    m_jvdErrorReporter.printError(tldFiles[i].getPath() + ": Error while " +
                            "parsing the file into XMLBeans. " + xmle.getLocalizedMessage());
                }
            }
        }

        // Initialize the arrays of XMLBeans types with the 
        // XML collected from the TLD files.
        xmlJspTagDocArray = (JspTagDocument[]) xmlJspTagDocList
                .toArray(new JspTagDocument[xmlJspTagDocList.size()]);
        xmlFunctionDocArray = (FunctionDocument[]) xmlFunctionDocList
                .toArray(new FunctionDocument[xmlFunctionDocList.size()]);

        // If we got anything from the TLDs, add Javadoc to the new XML.
        if (xmlJspTagDocArray.length > 0)
        {
            generateJspTagDocXml(xmlJspTagDocArray, jvdRootDoc, xmlOverview);
        }
        if (xmlFunctionDocArray.length > 0)
        {
            generateFunctionDocXml(xmlFunctionDocArray, jvdRootDoc, xmlOverview);
        }

        // 
        if (m_configuration.getXmlDir() != null)
        {
            saveSummaryXml(xmlOverviewDoc);
        }

        //
        UriListWriter.writeUriList(xmlOverviewDoc, m_configuration);
        if (m_configuration.getDestDir() != null)
        {
            generateHtmlOutput(xmlJspTagDocArray, xmlFunctionDocArray,
                    xmlOverviewDoc);            
        }
    }

    /**
     * Receives XML containing TLD information and adds Javadoc 
     * information, if any, before writing out the XML (if requested).
     * 
     * @param xmlJspTagDocArray XML for JSP tags with TLD info.
     * @param jvdRootDoc Information about handler classes from Javadoc.
     * @param xmlOverview Overview XML to which tag XML will be added.
     */
    private static void generateJspTagDocXml(JspTagDocument[] xmlJspTagDocArray,
            RootDoc jvdRootDoc, TaglibSummariesDocument.TaglibSummaries xmlOverview)
    {
        // A map of the tag handler class names to JspTagDetail instances that
        // contain information about their corresponding tags. This is used to
        // generate links to tag references where handler Javadoc comments
        // specify links to other handler classes.
        HashMap handlerMap = new HashMap();

        // A map of the JSP tag names to JspTagDetail instances that hold
        // information about the tag.
        HashMap tagMap = new HashMap();

        if (xmlJspTagDocArray != null)
        {
            // Loop through the tags once to complete the maps through which
            // the doclet creates links from one tag to another.
            if ((m_configuration.useJspTagRefContent() || m_configuration.useStandardContent()))
            {
                for (int j = 0; j < xmlJspTagDocArray.length; j++)
                {
                    JspTagDocument xmlJspTagDoc = xmlJspTagDocArray[j];
                    if (jvdRootDoc.classNamed(xmlJspTagDoc.getJspTag().getHandlerClass().getFullName()) != null)
                    {
                        updateHandlerMaps(handlerMap, tagMap, xmlJspTagDoc, jvdRootDoc);
                    }
                }
            }

            // Loop through the information collected from TLDs, adding information
            // from Javadoc.
            for (int j = 0; j < xmlJspTagDocArray.length; j++)
            {
                JspTagDocument xmlJspTagDoc = xmlJspTagDocArray[j];
                if (xmlJspTagDoc != null && (m_configuration.useJspTagRefContent() || m_configuration.useStandardContent()) &&
                        (jvdRootDoc.classNamed(xmlJspTagDoc.getJspTag().getHandlerClass().getFullName()) != null))
                {
                        JspTagDocument.JspTag xmlJspTag = xmlJspTagDoc.getJspTag();
                        // Create a context object to pass to taglets.
                        JspTagContext context = new JspTagContext((JspTagDetail) tagMap
                                .get(xmlJspTag.getName() + '@' + xmlJspTag.getUri()),
                                tagMap, handlerMap, m_configuration);
                        xmlJspTagDoc =
                                JavadocHandler.addJavadocToJspTagXml(jvdRootDoc, xmlJspTagDoc, context);
                }

                TaglibSummaryType[] xmlTaglibSummaries =
                        xmlOverview.getTaglibSummaryArray();

                String jspTagUri = xmlJspTagDoc.getJspTag().getUri();

                for (int k = 0; k < xmlTaglibSummaries.length; k++)
                {
                    TaglibSummaryType xmlTaglibSummary = xmlTaglibSummaries[k];
                    if (xmlTaglibSummary.getUri().getStringValue().equals(jspTagUri))
                    {
                        addJspTagToSummary(xmlJspTagDoc, xmlTaglibSummary);
                    }
                }

                // Output XML files, if requested.
                if (m_configuration.getXmlDir() != null)
                {
                    // Get the tag ref's URI so it can be used to create
                    // directories that will contain the generated XML files.
                    String xmlTaglibUri = xmlJspTagDoc.getJspTag().getUri();
                    URI uri = null;
                    try
                    {
                        uri = new URI(xmlTaglibUri);
                    } catch (URISyntaxException urise)
                    {
                        m_jvdErrorReporter.printError(xmlTaglibUri + ": Error while " +
                                "handling this URI. " + urise.getLocalizedMessage());
                    }
                    String tagLibPath = uri.getPath() + '/';
                    // Sometimes the URI doesn't include a host.
                    if (uri.getHost() != null)
                    {
                        tagLibPath = uri.getHost() + tagLibPath;
                    }

                    // Save the generated XML.
                    saveJspTagXml(xmlJspTagDoc, tagLibPath);
                }
            }
        }            
    }
    
    /**
     * Receives XML containing TLD information and adds Javadoc 
     * information, if any, before writing out the XML (if requested).
     * 
     * @param xmlFunctionDocArray XML for functions with TLD info.
     * @param jvdRootDoc Information about handler classes from Javadoc.
     * @param xmlOverview Overview XML to which function XML will be added.
     */
    private static void generateFunctionDocXml(FunctionDocument[] xmlFunctionDocArray,
            RootDoc jvdRootDoc, TaglibSummariesDocument.TaglibSummaries xmlOverview)
    {
        if (xmlFunctionDocArray != null)
        {
            for (int j = 0; j < xmlFunctionDocArray.length; j++)
            {
                FunctionDocument xmlFunctionDoc = xmlFunctionDocArray[j];
                
                if ((m_configuration.useJspTagRefContent() || m_configuration.useStandardContent()) &&
                        (jvdRootDoc.classNamed(xmlFunctionDoc.getFunction().getFunctionClass().getFullName()) != null))
                {
                    xmlFunctionDoc = 
                        JavadocHandler.addJavadocToFunctionXml(xmlFunctionDoc, jvdRootDoc);
                }

                TaglibSummaryType[] xmlTaglibSummaries =
                        xmlOverview.getTaglibSummaryArray();

                String functionUri = xmlFunctionDoc.getFunction().getUri();

                for (int k = 0; k < xmlTaglibSummaries.length; k++)
                {
                    TaglibSummaryType xmlTaglibSummary = xmlTaglibSummaries[k];
                    if (xmlTaglibSummary.getUri().getStringValue().equals(functionUri))
                    {
                        addFunctionToSummary(xmlFunctionDoc, xmlTaglibSummary);
                    }
                }
                
                if (m_configuration.getXmlDir() != null)
                {
                    String xmlTaglibUri = xmlFunctionDoc.getFunction().getUri();
                    URI uri = null;
                    try
                    {
                        uri = new URI(xmlTaglibUri);
                    } catch (URISyntaxException urise)
                    {
                        m_jvdErrorReporter.printError(xmlTaglibUri + ": Error while " +
                                "handling this URI. " + urise.getLocalizedMessage());
                    }
                    String tagLibPath = uri.getPath() + '/';
                    // Sometimes the URI doesn't include a host.
                    if (uri.getHost() != null)
                    {
                        tagLibPath = uri.getHost() + tagLibPath;
                    }
                    saveFunctionXml(xmlFunctionDocArray[j], tagLibPath);
                }
            }
        }            
    }

    /**
     * Generates the HTML output by transforming doclet-created XML.
     * 
     * @param xmlJspTagDocArray XML for JSP tags in the current run.
     * @param xmlFunctionDocArray XML for functions in the current run.
     * @param xmlOverviewDoc XML for the overview.
     */
    private static void generateHtmlOutput(JspTagDocument[] xmlJspTagDocArray,
            FunctionDocument[] xmlFunctionDocArray,
            TaglibSummariesDocument xmlOverviewDoc)
    {
        if (xmlJspTagDocArray != null)
        {
            JspTagDocTransformer.transformTopicsXmlToHtml(xmlJspTagDocArray,
                    m_configuration, m_jvdErrorReporter);
        }
        if (xmlFunctionDocArray != null)
        {
            JspTagDocTransformer.transformTopicsXmlToHtml(xmlFunctionDocArray,
                    m_configuration, m_jvdErrorReporter);
        }
        JspTagDocTransformer.transformOverviewXmlToHtml(xmlOverviewDoc,
                m_configuration, m_jvdErrorReporter);
        copyHtmlResourceToDestination("index.html", m_configuration
                .getDestDir());
        copyHtmlResourceToDestination("stylesheet.css", m_configuration
                .getDestDir());        
    }

    /**
     * Adds to <em>handlerMap</em> and <em>tagMap</em> pairs that map
     * JSP tags with their handler classes, and vice versa. These pairs
     * are used to locate information about a JSP tag or locate Javadoc 
     * for a handler class in order to, say, create a link from one
     * tag to another.
     * 
     * @param handlerMap A map in which the key is the name of a tag class,
     * and the value is a JspContext instance corresponding to the JSP
     * tag that uses the tag class.
     * @param tagMap A map in which the key is the name of a JSP tag,
     * and the value is a JspContext instance corresponding to the tag.
     * @param xmlJspTagDoc XML this doclet is generating for the JSP tag.
     * @param jvdRootDoc Information received from Javadoc about tag
     * classes.
     */
    private static void updateHandlerMaps(HashMap handlerMap, HashMap tagMap,
                                          JspTagDocument xmlJspTagDoc, RootDoc jvdRootDoc)
    {
        ClassDoc jvdTagClass = null;
        ClassDoc jvdTeiClass = null;
        JspTagDocument.JspTag xmlJspTag = xmlJspTagDoc.getJspTag();
        String xmlHandlerClassName = xmlJspTag.getHandlerClass().getFullName();
        jvdTagClass = JavadocHandler.findJavadocForClass(jvdRootDoc, xmlHandlerClassName);
        TypeInfoType xmlTeiClass = xmlJspTag.getTeiClass();
        // If the we got TEI class info from the TLD, look for the Javadoc
        // for its class.
        if (xmlTeiClass != null)
        {
            jvdTeiClass = JavadocHandler.findJavadocForClass(jvdRootDoc, xmlTeiClass
                    .getFullName());
        } else if (xmlTeiClass == null)
        {
            jvdTeiClass = null;
        }
        // If we have Javadoc about the tag class mentioned in the TLD,
        // load up the map with the basics from it, including the Javadoc
        // types for attribute accessors.
        if (jvdTagClass != null)
        {
            String xmlTagClassName = jvdTagClass.containingPackage() + "."
                    + jvdTagClass.name();
            HashMap attributeMap = null;
            if (xmlJspTag.getAttributes() != null)
            {
                attributeMap = buildAttributeMap(xmlJspTag.getAttributes(),
                        jvdTagClass);
            }
            // Now create a "detail" instance for the tag, handing it
            // all the info we've collected.
            JspTagDetail jspTagDetail = new JspTagDetail(xmlJspTag.getName(),
                    xmlJspTag.getUri(), xmlJspTag.getPrefix(), jvdTagClass,
                    jvdTeiClass, attributeMap);
            // Update the maps.
            tagMap.put(xmlJspTag.getName() + '@' + xmlJspTag.getUri(),
                    jspTagDetail);
            handlerMap.put(xmlTagClassName, jspTagDetail);
        }
    }

    /**
     * Assembles a map in which keys are attribute names and values are
     * {@link JspTagDetail.JspTagAttribute} instances containing the attribute
     * name and corresponding accessors from a handler class.
     *
     * @param xmlAttributes The JSP tag's attributes.
     * @param jvdTagClass   The handler class for the tag as declared in the TLD.
     * @return A map that associates attribute name with accessor information.
     */
    private static HashMap buildAttributeMap(AttributesType xmlAttributes,
                                             ClassDoc jvdTagClass)
    {
        AttributeType[] xmlAttributeArray = xmlAttributes.getAttributeArray();
        HashMap attributeMap = new HashMap();
        for (int k = 0; k < xmlAttributeArray.length; k++)
        {
            AttributeType xmlAttribute = xmlAttributeArray[k];
            MethodDoc jvdSetter = null;
            MethodDoc jvdGetter = null;
            String xmlAttributeName = xmlAttribute.getName();
            HashMap accessors = buildAccessorsMap(jvdTagClass, xmlAttribute);
            if (accessors != null)
            {
                jvdSetter = (MethodDoc) accessors.get("setter");
                jvdGetter = (MethodDoc) accessors.get("getter");
            }
            JspTagDetail.JspTagAttribute attributeDetails = new JspTagDetail.JspTagAttribute(xmlAttributeName, jvdSetter, jvdGetter);
            attributeMap.put(xmlAttributeName, attributeDetails);
        }
        return attributeMap;
    }

    /**
     * Finds the accessor methods corresponding to the JSP tag attribute
     * specified by <em>xmlAttribute</em>. The methods are often contained by
     * the tag's handler class, but may instead be in a superclass. This method
     * is first called with the tag class as <em>jvdTagClass</em>; if
     * the accessors are not found there, this method will call itself, passing
     * the super, until the accessors are found.
     *
     * @param jvdTagClass  A class in which the accessors might be found.
     * @param xmlAttribute The JSP tag attribute for which accessors are sought.
     * @return A HashMap in which the key is "getter" or "setter" and the value
     *         is a MethodDoc instance from Javadoc representing the method.
     */
    private static HashMap buildAccessorsMap(ClassDoc jvdTagClass,
                                         AttributeType xmlAttribute)
    {
        // A map to hold the accessors found.
        HashMap accessorMethodMap = null;
        // The handler class, when it's found.
        // The name of the attribute whose accessors we're looking for.
        String xmlAttributeName = xmlAttribute.getName();

        // Accessors can have a variety of names. These are defined by JavaBeans
        // conventions. We search potential handler classes for these.
        String setterMethName = "set" + xmlAttributeName;
        String getterMethName = "get" + xmlAttributeName;
        String isMethName = "is" + xmlAttributeName;
        String hasMethName = "has" + xmlAttributeName;

        // Check the current class for methods that match the accessor names
        // defined above.
        if (jvdTagClass != null)
        {
            MethodDoc[] jvdMethodArray = jvdTagClass.methods();
            if (jvdMethodArray.length > 0)
            {
                MethodDoc jvdSetter = null;
                for (int i = 0; i < jvdMethodArray.length; i++)
                {
                    MethodDoc jvdMethod = jvdMethodArray[i];
                    if (jvdMethod.name().equalsIgnoreCase(setterMethName))
                    {
                        jvdSetter = jvdMethod;
                        accessorMethodMap = new HashMap();
                        accessorMethodMap.put("setter", jvdSetter);
                        break;
                    }
                }
                // If we found a setter method, let's look for a getter in the
                // same class.
                if (jvdSetter != null)
                {
                    for (int j = 0; j < jvdMethodArray.length; j++)
                    {
                        MethodDoc jvdGetter = jvdMethodArray[j];

                        // If there's a method whose name is the same as one of
                        // the accessor names created from the attribute name,
                        // assume we have a match. Put the matching method into
                        // the map.
                        if (jvdGetter.name().equalsIgnoreCase(getterMethName)
                                || jvdGetter.name().equalsIgnoreCase(isMethName)
                                || jvdGetter.name().equalsIgnoreCase(hasMethName))
                        {
                            accessorMethodMap.put("getter", jvdGetter);
                            break;
                        }
                    }
                    return accessorMethodMap;
                }
                // If the setter couldn't be found in the current class, check
                // the super.
                else if (jvdSetter == null)
                {
                    // If the current class has a super, look there for the
                    // accessors.
                    if (jvdTagClass.superclass() != null)
                    {
                        return buildAccessorsMap(jvdTagClass.superclass(),
                                xmlAttribute);
                    }
                    // If no super, just give up.
                    else
                    {
                        return null;
                    }
                }
            }
        } else
        {
            return null;
        }
        return accessorMethodMap;
    }

    /**
     * Returns <code>true</code> if the TLD in <em>tldFile</em> is
     * in the <em>namespaceUri</em> namespace. This method is called
     * in order to find out which XMLBeans types the TLD should be
     * bound to.
     * 
     * Note: Just checking for the string is pretty lame. Probably
     * a better way using XMLBeans.
     * 
     * @param tldFile The TLD who namespace should be checked.
     * @param namespaceUri The URI for the namespace.
     * @return <code>true</code> if the TLD is in the specified namespace;
     * otherwise, <code>false</code>.
     */
    private static boolean isInNamespace(File tldFile, String namespaceUri)
    {
        boolean isInNamespace = false;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(tldFile));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if (line.indexOf(namespaceUri) != -1)
                {
                    isInNamespace = true;
                    break;
                }
            }
            reader.close();
        } catch (FileNotFoundException fnfe)
        {
            m_jvdErrorReporter.printError(tldFile.getPath() + ": Unable to find " +
                    "the file in order to check its namespace URI. " + fnfe.getLocalizedMessage());
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError(tldFile.getPath() + ": Unable to read " +
                    "the file in order to check its namespace URI. " + ioe.getLocalizedMessage());
        }
        return isInNamespace;
    }

    /**
     * Adds the <em>xmlJspTagDoc</em> JSP tag XML to the overview XML
     * bound to <em>xmlTaglibSummary</em>. Note that only a subset of 
     * the tag XML's values are added to the summary.
     * 
     * @param xmlJspTagDoc The JSP tag XML to add.
     * @param xmlTaglibSummary The summary XML.
     */
    public static void addJspTagToSummary(JspTagDocument xmlJspTagDoc,
                                           TaglibSummaryType xmlTaglibSummary)
    {
        JspTagDocument.JspTag xmlJspTag = xmlJspTagDoc.getJspTag();
        JspTagSummaryType xmlJspTagSummary = xmlTaglibSummary
                .addNewJspTagSummary();
        xmlJspTagSummary.setName(xmlJspTag.getName());
        if (xmlJspTag.getHandlerClass() != null)
        {
            if (xmlJspTag.getHandlerClass().getDescription() != null)
            {
                xmlJspTagSummary.setTagClassDescription(xmlJspTag
                        .getHandlerClass().getDescription());
            }
            XmlObject[] xmlTagClassJavadocTags = xmlJspTag
                    .selectPath("declare namespace ref='http://apache.org/beehive/netui/tools/doclet/schema'"
                    + "$this/ref:handler-class/ref:javadoc-tags/ref:javadoc-tag");
            if (xmlTagClassJavadocTags.length > 0)
            {
                for (int k = 0; k < xmlTagClassJavadocTags.length; k++)
                {
                    JavadocTagType xmlTagClassJavadocTag = (JavadocTagType) xmlTagClassJavadocTags[k];
                    String prefix = xmlTagClassJavadocTag.getPrefix();
                    if (prefix != null)
                    {
                        if (xmlTagClassJavadocTag.getName().equals("tagdescription")
                                && xmlTagClassJavadocTag.getPrefix().equals("jsptagref"))
                        {
                            XmlObject[] descriptions = xmlTagClassJavadocTag
                                    .selectChildren(new QName(REF_NAMESPACE,
                                            "description"));
                            if (descriptions.length > 0)
                            {
                                TagDescriptionType xmlTagSummaryDescription = xmlJspTagSummary
                                        .addNewTagdescription();
                                xmlTagSummaryDescription.set(descriptions[0]);
                                xmlJspTagSummary
                                        .setTagdescription(xmlTagSummaryDescription);
                            }
                        }
                    }
                }
            }
        }
        String[] xmlTldDescArray = xmlJspTag.getTldDescriptionArray();
        if (xmlTldDescArray != null)
        {
            for (int i = 0; i < xmlTldDescArray.length; i++)
            {
                xmlJspTagSummary.addTldDescription(xmlTldDescArray[i]);
            }
        }
        AttributesType xmlAttributes = xmlJspTag.getAttributes();
        if (xmlAttributes != null)
        {
            JspTagSummaryType.AttributeSummaries xmlAttributeSummaries = xmlJspTagSummary
                    .addNewAttributeSummaries();
            AttributeType[] xmlAttributeArray = xmlAttributes
                    .getAttributeArray();
            XmlObject[] xmlAccessorJavadocTags = null;
            AttributeType xmlAttribute = null;
            for (int i = 0; i < xmlAttributeArray.length; i++)
            {
                xmlAttribute = xmlAttributeArray[i];
                AttributeSummaryType xmlAttributeSummary = xmlAttributeSummaries
                        .addNewAttributeSummary();
                xmlAttributeSummary.setName(xmlAttribute.getName());
                xmlAccessorJavadocTags =
                        xmlAttribute
                        .selectPath("declare namespace ref='http://apache.org/beehive/netui/tools/doclet/schema'"
                        + "$this/ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag");
                if (xmlAccessorJavadocTags.length > 0)
                {
                    for (int j = 0; j < xmlAccessorJavadocTags.length; j++)
                    {
                        JavadocTagType xmlJavadocTag = (JavadocTagType) xmlAccessorJavadocTags[j];
                        String prefix = xmlJavadocTag.getPrefix();
                        if (prefix != null)
                        {
                            if (xmlJavadocTag.getName().equals("attributedescription")
                                    && xmlJavadocTag.getPrefix().equals("jsptagref"))
                            {
                                XmlObject[] descriptions = xmlJavadocTag
                                        .selectChildren(new QName(REF_NAMESPACE, "description"));
                                if (descriptions.length > 0)
                                {
                                    AttributeDescriptionType xmlAttributeSummaryDescription = xmlAttributeSummary
                                            .addNewAttributedescription();
                                    xmlAttributeSummaryDescription
                                            .set(descriptions[0]);
                                    xmlAttributeSummary
                                            .setAttributedescription(xmlAttributeSummaryDescription);
                                }
                            }
                        }
                    }
                }
                String xmlTldDescription = xmlAttribute.getTldDescription();
                if (xmlTldDescription != null)
                {
                    xmlAttributeSummary.setTldDescription(xmlTldDescription);
                }
                XmlObject[] xmlAccessors = xmlAttribute
                        .selectPath("declare namespace ref='http://apache.org/beehive/netui/tools/doclet/schema'"
                        + "$this/ref:accessors/ref:method");
                if (xmlAccessors.length > 0)
                {
                    for (int k = 0; k < xmlAccessors.length; k++)
                    {
                        MethodType xmlAccessor = (MethodType) xmlAccessors[k];
                        if (xmlAccessor.getName().startsWith("set"))
                        {
                            DescriptionType xmlAccessorDescription =
                                    xmlAccessor.getDescription();
                            if (xmlAccessorDescription != null)
                            {
                                DescriptionType xmlAttributeSetterDescription =
                                        xmlAttributeSummary.addNewSetterDescription();
                                xmlAttributeSetterDescription.set(xmlAccessorDescription);
                                xmlAttributeSummary.setSetterDescription(xmlAttributeSetterDescription);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds the <em>xmlFunctionDoc</em> function XML to the overview XML 
     * bound to <em>xmlTaglibSummary</em>. Note that only a subset of
     * the function XML's values are added to the summary.
     * 
     * @param xmlFunctionDoc The function XML to add.
     * @param xmlTaglibSummary The summary XML.
     */
    public static void addFunctionToSummary(FunctionDocument xmlFunctionDoc,
            TaglibSummaryType xmlTaglibSummary)
    {
            FunctionDocument.Function xmlFunction = xmlFunctionDoc.getFunction();
            FunctionSummaryType xmlFunctionSummary = xmlTaglibSummary.addNewFunctionSummary();
            xmlFunctionSummary.setName(xmlFunction.getName());
            xmlFunctionSummary.setReturn(xmlFunction.getFunctionSupportMethod().getReturns().getFullName());
            
            StringBuffer functionSigBuffer = new StringBuffer();
            functionSigBuffer.append(xmlFunction.getName() + '(');
            MethodType xmlFunctionSupportMethod = xmlFunction.getFunctionSupportMethod();
            if (xmlFunctionSupportMethod.getMod() != null)
            {
                if (xmlFunctionSupportMethod.sizeOfParameterArray() > 0)
                {
                    ParameterType[] xmlParams = xmlFunctionSupportMethod.getParameterArray();
                    for (int j = 0; j < xmlParams.length; j++)
                    {
                        functionSigBuffer.append(xmlParams[j].getTypeName());
                        if (j < (xmlParams.length - 1))
                        {
                            functionSigBuffer.append(", ");
                        }
                    }
                }
                functionSigBuffer.append(')');
                xmlFunctionSummary.setSignature(functionSigBuffer.toString());
            }
            else 
            {
                xmlFunctionSummary.setSignature(xmlFunction.getFunctionSupportMethod().getSignature());
            }
            if (xmlFunction.getTldDescriptionArray() != null)
            {
                xmlFunctionSummary.setTldDescriptionArray(xmlFunction.getTldDescriptionArray());
            }
    }

    /**
     * Inserts into the specified TLD an xmlns attribute with 
     * <em>namespaceUri</em> as a value. This is done so that
     * the TLD will bind to the XMLBeans types used to access it.
     * 
     * @param tldFile The TLD into which the attribute should be inserted.
     * @param namespaceUri The namespace URI to use as a value for
     * the xmlns attribute.
     * @return The TLD as a string.
     */
    private static String insertNamespaceForTld(File tldFile,
                                                String namespaceUri)
    {
        StringBuffer buffer = new StringBuffer();
        String tldAsString = null;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(tldFile));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if (line.indexOf("<taglib>") != -1)
                {
                    line = line.replaceFirst("<taglib>", "<taglib xmlns='"
                            + namespaceUri + "'>");
                }
                buffer.append(line);
                buffer.append('\n');
            }
            reader.close();
            tldAsString = buffer.toString();
        } catch (FileNotFoundException fnfe)
        {
            m_jvdErrorReporter.printError(tldFile.getPath() + ": Unable to find the file " +
                    "in order to prepare it for use with the doclet: " + fnfe.getLocalizedMessage());
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError(tldFile.getPath() + ": Unable to read the file " +
                    "in order to prepare it for use with the doclet: " + ioe.getLocalizedMessage());
        }
        return tldAsString;
    }

    /**
     * Saves the generated XML to a path constructed from the tag library URI.
     * This method is called only if an -xmldir option was
     * specified at the command line.
     *
     * @param xmlJspTagDoc The newly generated tag reference XML.
     * @param tagLibPath   The output path.
     */
    private static void saveJspTagXml(JspTagDocument xmlJspTagDoc,
                                      String tagLibPath)
    {
        String filePath = tagLibPath + xmlJspTagDoc.getJspTag().getName()
                + ".xml";
        File xmlFile = new File(m_configuration.getXmlDir(), filePath);
        m_jvdErrorReporter.printNotice("Saving " + xmlFile.getPath());
        xmlFile.getParentFile().mkdirs();
        try
        {
            xmlJspTagDoc.save(xmlFile, m_xmlOptions);
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError("Error while saving "
                    + "the reference XML file " + xmlFile.getPath() + ": "
                    + ioe.getLocalizedMessage());
        }
    }

    /**
     * Saves the XML representing generated doc on a function. This method
     * is called only if the user specified the -xmldir command line
     * option.
     *
     * @param xmlFunctionDoc The XML to save.
     * @param tagLibPath     A path consisting primarily of the URI 
     * 					    for the library
     *                       containing the function.
     */
    private static void saveFunctionXml(FunctionDocument xmlFunctionDoc,
                                        String tagLibPath)
    {
        String filePath = tagLibPath + xmlFunctionDoc.getFunction().getName()
                + ".xml";
        File xmlFile = new File(m_configuration.getXmlDir(), filePath);
        m_jvdErrorReporter.printNotice("Saving " + xmlFile.getPath());
        xmlFile.getParentFile().mkdirs();
        try
        {
            xmlFunctionDoc.save(xmlFile, m_xmlOptions);
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError("Error while saving "
                    + "the reference XML file " + xmlFile.getPath() + ": "
                    + ioe.getLocalizedMessage());
        }
    }

    /**
     * Saves the overview.xml file that includes basic information 
     * about all the tag libraries included in the currect Javadoc run.
     * This method is called only if the user specified the -xmldir
     * command line option.
     *
     * @param xmlOverview The XML to save.
     */
    private static void saveSummaryXml(TaglibSummariesDocument xmlOverview)
    {
        File overviewFile = new File(m_configuration.getXmlDir(),
                "overview.xml");
        m_jvdErrorReporter.printNotice("Saving " + overviewFile.getPath());
        overviewFile.getParentFile().mkdirs();
        try
        {
            xmlOverview.save(overviewFile, m_xmlOptions);
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError("Error while saving "
                    + "the summary XML file " + overviewFile.getPath() + ": "
                    + ioe.getLocalizedMessage());
        }
    }

    /**
     * Copies a resource needed for HTML output from the resources directory to
     * the HTML destination directory. Resources include CSS and HTML 
     * files.
     *
     * @param resourceName The file to copy.
     * @param destination  The path to the destination directory.
     */
    private static void copyHtmlResourceToDestination(String resourceName,
                                                     String destination)
    {
        File destinationFile = new File(destination, resourceName);
        try
        {
            InputStream inputFile = JspTagDoc.class
                    .getResourceAsStream("/resources/" + resourceName);
            if (inputFile == null)
            {
                inputFile = new FileInputStream(new File("resources/"
                        + resourceName));
            }
            if (inputFile == null)
            {
                m_jvdErrorReporter.printError(resourceName + ": Unable to find this file.");
                return;
            }
            if (resourceName.equals("index.html"))
            {
                inputFile = insertTitleValue(m_configuration.getWindowTitle(),
                        inputFile);
            }
            FileOutputStream outputFile = new FileOutputStream(destinationFile);
            copyFile(inputFile, outputFile);
        } catch (FileNotFoundException fnfe)
        {
            m_jvdErrorReporter.printError("Exception while writing resource "
                    + "files to " + m_configuration.getXmlDir() + ": "
                    + fnfe.getLocalizedMessage());
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError("Exception while writing resource "
                    + "files to " + m_configuration.getXmlDir() + ": "
                    + ioe.getLocalizedMessage());
        }
    }

    /**
     * Adds <em>title</em> as the value for the &lt;title> tag in the <em>htmlFile</em>
     * HTML file. This is used to add the value of the
     * -windowtitle option specified by the user.
     *
     * @param title    The value to use for the <tktle>tag.
     * @param htmlFile The HTML file into which the value should be inserted.
     * @return The updated HTML file.
     */
    private static InputStream insertTitleValue(String title,
                                               InputStream htmlFile)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(htmlFile));
        StringBuffer buffer = new StringBuffer();
        try
        {
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if (line.indexOf("<title>") != -1)
                {
                    line = line.replaceFirst("<title>", "<title>" + title);
                }
                buffer.append(line);
                buffer.append('\n');
            }
            reader.close();
        } catch (FileNotFoundException e)
        {
            m_jvdErrorReporter.printError("Unable to find an HTML file in " +
                    "order to update its title. " + e.getLocalizedMessage());
        } catch (IOException e)
        {
            m_jvdErrorReporter.printError("Unable to read an HTML file in " +
                    "order to update its title. " + e.getLocalizedMessage());
        }
        return new ByteArrayInputStream(buffer.toString().getBytes());
    }

    /**
     * Copies <em>inputFile</em> to <em>outputFile</em>.
     *
     * @param inputFile  The source file.
     * @param outputFile The destination file.
     * @throws IOException If an error occurs while reading the source file or writing
     *                     the destination file.
     */
    private static void copyFile(InputStream inputFile,
                                FileOutputStream outputFile)
            throws IOException
    {
        byte[] fileByteArray = new byte[512];
        int arrayLength = 0;
        try
        {
            while ((arrayLength = inputFile.read(fileByteArray)) != -1)
            {
                outputFile.write(fileByteArray, 0, arrayLength);
            }
        } catch (FileNotFoundException fnfe)
        {
            m_jvdErrorReporter.printError("Exception while writing resource "
                    + "files to " + m_configuration.getDestDir() + ": "
                    + fnfe.getLocalizedMessage());
        } finally
        {
            inputFile.close();
            outputFile.close();
        }
    }

    /**
     * Gets the options set for the XML generated by this doclet. Currently, 
     * the only option used is to pretty print the output, with indent set 
     * to four spaces.
     *
     * @return The options.
     */
    private static XmlOptions getXmlOptions()
    {
        XmlOptions opts = new XmlOptions();
        opts.setSavePrettyPrint();
        opts.setSavePrettyPrintIndent(4);
        return opts;
    }
}