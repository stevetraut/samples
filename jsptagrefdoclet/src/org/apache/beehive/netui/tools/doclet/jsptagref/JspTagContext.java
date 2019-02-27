package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Tag;
import org.apache.beehive.netui.tools.doclet.schema.JavadocTagType;
import org.apache.beehive.netui.tools.doclet.taglets.XmlTagletManager;

import java.util.HashMap;

/**
 * Represents information about the current JSP tag. An instance of this class 
 * is passed to taglets (along with the Javadoc Tag to be rendered) so that the 
 * taglet has information it can use to create links, get information 
 * about other supported Javadoc tags, and report errors during the Javadoc run.
 */
public final class JspTagContext
{
    private JspTagDetail m_currentTagDetail;
    private HashMap m_tagMap;
    private HashMap m_handlerMap;
    private XmlTagletManager m_tagletManager;
    private Linker m_linker;
    private DocErrorReporter m_jvdErrorReporter;
    private ConfigurationJspTagDoc m_configuration;

    /**
     * Constructs a new instance of this class using information about the
     * current JSP tag, other JSP tags in the current run, supported Javadoc
     * tags, and objects for making external links and reporting errors.
     *
     * @param currentDetail Information about the JSP tag for which the doclet
     *                      is currently generating XML.
     * @param tagMap        A JSP tag-to-tag-detail map in which the key is the tag's
     *                      name and the value is the JspTagDetail instance that 
     * 				       describes the tag.
     * @param handlerMap    A JSP tag handler-to-tag-detail map in which the key
     *                      is the handler class's fully-qualified name and the value is the 
     *                      JspTagDetail instance that describes the tag corresponding 
     *                      to the handler.
     * @param configuration Information about user-specified Javadoc options.
     */
    public JspTagContext(JspTagDetail currentDetail,
                         HashMap tagMap, HashMap handlerMap,
                         ConfigurationJspTagDoc configuration)
    {
        m_currentTagDetail = currentDetail;
        m_tagMap = tagMap;
        m_handlerMap = handlerMap;
        m_tagletManager = configuration.getTagletManager();
        m_linker = configuration.getLinker();
        m_jvdErrorReporter = configuration.getErrorReporter();
        m_configuration = configuration;
    }

    /**
     * Returns a string representation of an instance of this class. If this
     * instance represented a <foo:bar> JSP tag, where "foo" is the namespace
     * URI "http://openuri.org", this method would return:
     * <pre>
     * foo@http://openuri.org
     * </pre>
     * 
     * @return The string representation.
     */
    public String toString()
    {
        return m_currentTagDetail.getTagName() + '@' + m_currentTagDetail.getTagLibUri();
    }

    /**
     * Gets a JspTagDetail instance corresponding to the current JSP tag.
     * The JspTagDetail instance contains information such as the
     * tag name, attributes, handler class, and the URI for the
     * tag library of which it's a part.
     *
     * @return A JspTagDetail instance containing information about
     *         the current JSP tag.
     */
    public JspTagDetail getCurrentTagDetail()
    {
        return m_currentTagDetail;
    }

    /**
     * Gets the JspTagDetail instance corresponding to the JSP tag
     * specified by <em>tagName</em> and <em>tagLibUri</em>. The tag must
     * be in the current Javadoc run.
     *
     * @param tagName   The JSP tag for which a JspTagDetail instance
     *                  is being requested.
     * @param tagLibUri The taglib URI for the <em>tagName</em>
     *                  JSP tag.
     * @return A JspTagDetail instance corresponding to <em>tagName</em>.
     */
    public JspTagDetail getTagDetailByTag(String tagName, String tagLibUri)
    {
        return (JspTagDetail) m_tagMap.get(tagName + '@' + tagLibUri);
    }

    /**
     * Gets the JspTagDetail instance corresponding to the tag handler class
     * specified by <em>handlerName</em>. The tag must be in the current
     * Javadoc run.
     *
     * @param handlerName The fully-qualified name of a tag handler class
     *                    in the scope of the current Javadoc run.
     * @return A JspTagDetail instance containing information about the
     *         JSP tag handled by <em>handlerName</em>.
     */
    public JspTagDetail getTagDetailByHandler(String handlerName)
    {
        return (JspTagDetail) m_handlerMap.get(handlerName);
    }

    /**
     * Gets an error reporter that taglets can use to print notices, warnings,
     * and errors to the console.
     *
     * @return The error reporter.
     */
    public DocErrorReporter getErrorReporter()
    {
        return m_jvdErrorReporter;
    }

    /**
     * Gets the configuration instance for this run. The configuration
     * object contains information specified by the user as command-line
     * options.
     *
     * @return The configuration.
     */
    public ConfigurationJspTagDoc getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Gets a ClassDoc instance representing the "tag class" specified 
     * by the tag's TLD.
     *
     * @return The handler class as a Javadoc type.
     */
    public ClassDoc getHandlerClass()
    {
        return m_currentTagDetail.getTagClassDoc();
    }

    /**
     * Gets a ClassDoc representing the "tei class" specified by the tag's TLD.
     *
     * @return The handler class as a Javadoc type.
     */
    public ClassDoc getTeiClass()
    {
        return m_currentTagDetail.getTeiClassDoc() != null ?
               m_currentTagDetail.getTeiClassDoc() : null;
    }

    /**
     * Determines whether the Java package or JSP tag library specified by
     * <em>item</em> is an external resource. In essence, this method
     * asks the {@link Linker} class to find out if the package name or library
     * URI is among those documented externally and referenced via the -link,
     * -linkoffline, -taglink, or -taglinkoffline command-line arguments.
     *
     * @param item A Java package name or JSP tag library URI.
     * @return <code>true</code> if external documentation for <em>item</em>
     *         is known; otherwise, <code>false</code>.
     */
    public boolean isItemExternal(String item)
    {
        return m_linker.isNamespaceExternal(item);
    }

    /**
     * Determines whether the package or taglib URI <em>item</em>
     * provides a link path that is absolute (such as an absolute
     * file path or a URL). Relative paths are prepended with a "../"
     * sequence that makes them relative paths to the root of the Javadoc
     * output before being inserted as a value for linking.
     *
     * @param item A package name or tag library URI.
     * @return <code>true</code> if the path is absolute; otherwise,
     *         <code>false</code>.
     */
    public boolean isItemAbsolute(String item)
    {
        return m_linker.isNamespaceAbsolute(item);
    }

    /**
     * Gets the path to a topic that is available outside the current Javadoc
     * run. The list of available items is constructed from the contents
     * of list files found via the -link and -linkoffline arguments. The return
     * value will be inserted into the XML for use in building a link URL. The
     * form for the return value will be <path-to-external-doc-root>/<package-path>.
     * The first portion is specified by the -link argument's value, or the -linkoffline
     * argument's second value.
     * <p/>
     * So for a java.util.ArrayList topic on the Sun web site, the path
     * might be http://java.sun.com/j2se/1.4.2/docs/api/java/util/ArrayList.
     *
     * @param pkgName The containing package for the item being linked to.
     * @return The full path.
     */
    public String getExternalLinkPath(String pkgName)
    {
        return m_linker.getExternalLinkPath(pkgName);
    }

    /**
     * Generates XML for <em>jvdTag</em>, returning the XML
     * in an XMLBeans type instance. This method uses the taglet associated
     * with the Javadoc tag. See {@link org.apache.beehive.netui.tools.doclet.taglets.XmlTagletManager},
     * for information on how taglets are assigned to tags.
     *
     * @param jvdTag  The Javadoc tag for which XML should be generated.
     * @param context Information about the current Javadoc run.
     * @return XML representing the <em>jvdTag</em>.
     */
    public JavadocTagType processJavadocTag(Tag jvdTag, JspTagContext context)
    {
        return m_tagletManager.processJavadocTag(jvdTag, context);
    }
}