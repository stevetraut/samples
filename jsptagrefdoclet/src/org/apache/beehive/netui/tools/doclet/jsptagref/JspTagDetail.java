package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import java.util.HashMap;

/**
 * Represents information about a JSP tag, including the tag's name, attributes,
 * and handler class, as well as its library's URI and prefix (the TLD
 * shortname). Instances of this class are available to taglets via the 
 * {@link JspTagContext} class. This doclet creates an instance of this 
 * class for each JSP tag in the Javadoc run.
 */
public final class JspTagDetail
{
    // A map in which the key is the attribute's name and the value is a corresponding
    // JspTagAttribute instance.
    private HashMap m_attributeMap = new HashMap();
    // The name of the JSP tag this instance represents.
    private final String m_tagName;
    // The URI for the library containing this JSP tag.
    private final String m_tagLibUri;
    // The tag's prefix (i.e., the tag library's short name.)
    private final String m_tagPrefix;
    // Javadoc's view of the handler class specified for this JSP tag.
    private final ClassDoc m_jvdTagClass;
    // Javadoc's view of the handler class specified for this JSP tag.
    private final ClassDoc m_jvdTeiClass;

    /**
     * Constructs a new instance of this class using information collected
     * from the tag's library's TLD file and information about the handler
     * class collected by Javadoc.
     *
     * @param name         The name of the JSP tag.
     * @param uri          The URI for the library to which the tag belongs.
     * @param prefix       The prefix (usually the TLD shortname) corresponding to
     *                     the library to which the tag belongs.
     * @param jvdTagClass  The handler class (TLD tag-class) for this tag.
     * @param attributeMap A map that associates an attribute's name with its
     *                     accessors.
     */
    public JspTagDetail(String name, String uri, String prefix, ClassDoc jvdTagClass,
                        ClassDoc jvdTeiClass, HashMap attributeMap)
    {
        m_tagName = name;
        m_tagLibUri = uri;
        m_tagPrefix = prefix;
        m_jvdTagClass = jvdTagClass;
        m_jvdTeiClass = jvdTeiClass;
        m_attributeMap = attributeMap;
    }

    /**
     * Gets an attribute by name.
     *
     * @param attributeName The name of the attribute to get.
     * @return Information about the attribute.
     */
    public JspTagAttribute getAttribute(String attributeName)
    {
        return (JspTagAttribute) m_attributeMap.get(attributeName);
    }

    /**
     * Gets all the attributes for this JSP tag.
     * 
     * @return Information about the attributes.
     */
    public JspTagAttribute[] getAttributes()
    {
        return (JspTagAttribute[]) m_attributeMap.values().toArray(new JspTagAttribute[m_attributeMap.size()]);
    }

    /**
     * Gets the handler class for the JSP tag this detail represents.
     *
     * @return A Javadoc representation of the handler class.
     */
    public ClassDoc getTagClassDoc()
    {
        return m_jvdTagClass;
    }

    /**
     * Gets the handler class for the JSP tag this detail represents.
     *
     * @return A Javadoc representation of the handler class.
     */
    public ClassDoc getTeiClassDoc()
    {
        return m_jvdTeiClass;
    }

    /**
     * Gets the tag library URI for the JSP tag this detail represents.
     *
     * @return The URI as a string.
     */
    public String getTagLibUri()
    {
        return m_tagLibUri;
    }

    /**
     * Gets the prefix for the library containing the JSP tag this
     * detail represents; this corresponds to the TLD shortname. Wherever a
     * link to this tag is created, the prefix can be included to disambiguate
     * from other JSP tag's with the same name. The prefix here maps to the tag
     * library containing the tag; the library's URI might have been used, but
     * it is typically much longer.
     *
     * @return The prefix.
     */
    public String getTagPrefix()
    {
        return m_tagPrefix;
    }

    /**
     * Gets the name of the JSP tag this detail represents.
     *
     * @return The tag's name.
     */
    public String getTagName()
    {
        return m_tagName;
    }

    /**
     * Represents a JSP tag attribute, including the attribute's name
     * and corresponding accessor methods.
     */
    public static final class JspTagAttribute
    {
        // The attribute's name.
        private final String m_attributeName;
        // The attribute's setter.
        private final MethodDoc m_jvdSetter;
        // The attribute's getter.
        private final MethodDoc m_jvdGetter;

        /**
         * Constructs a new instance of this class using the attribute's
         * name and Javadoc representations of its accessors.
         *
         * @param name      The attribute's name.
         * @param jvdSetter A Javadoc representation of the "set" method.
         * @param jvdGetter A Javadoc representation of the "get" method.
         */
        public JspTagAttribute(String name, MethodDoc jvdSetter, MethodDoc jvdGetter)
        {
            m_attributeName = name;
            m_jvdSetter = jvdSetter;
            m_jvdGetter = jvdGetter;
        }

        /**
         * Gets the name of the attribute this instance represents.
         *
         * @return The attribute's name.
         */
        public String getName()
        {
            return m_attributeName;
        }

        /**
         * Gets Javadoc's view of the "getter" accessor method for this attribute.
         * Note that this method could take any one of the following forms:
         * "getAttributeName", "isAttributeName", or "hasAttributeName".
         *
         * @return The getter.
         */
        public MethodDoc getGetter()
        {
            return m_jvdGetter;
        }

        /**
         * Gets Javadoc's view of the "setter" accessor method for this attribute.
         *
         * @return The setter.
         */
        public MethodDoc getSetter()
        {
            return m_jvdSetter;
        }

        /**
         * Returns the name of the attribute this instance represents.
         * Overridden for debugging.
         *
         * @return The name of the attribute.
         */
        public String toString()
        {
            return m_attributeName;
        }
    }

    /**
     * Returns the name and URI for the JSP tag this instance represents.
     *
     * @return A string in the form "tagName@tagUri"
     */
    public String toString()
    {
        return m_tagName + '@' + m_tagLibUri;
    }
}
