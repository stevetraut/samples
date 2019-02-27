package org.apache.beehive.netui.tools.doclet.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import org.apache.beehive.netui.tools.doclet.schema.JavadocTagType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.util.Map;

/**
 * Represents support for Javadoc tags whose name contains a colon or dot separator;
 * this taglet renders XML in which the text preceding the last colon
 * or dot is used as a "prefix" attribute value, while the rest of the tag name
 * is used as the element name. This class is the default taglet for Javadoc 
 * tags that have a colon or dot in their name.
 * <p/>
 * Consider a tag such as the following:
 * <p/>
 * &#64;foo.bar Some text.
 * <p/>
 * The XML output will be something like the following:
 * <pre>
 * &lt;sch:javadoc-tag name="bar" prefix="bar">
 *     &lt;sch:content>Some text.&lt;/sch:content>
 * &lt;/sch:javadoc-tag>
 * </pre>
 */
public class PrefixedTaglet extends SimpleXmlTaglet
{
    protected String m_prefix = "";

    /**
     * Called by Javadoc to register this taglet, associating
     * the taglet with the tag it represents. This method will only be called
     * by the standard doclet (if the taglet is used with it).
     *
     * @param tagletMap A map that associates the tag with its handler.
     */
    public static void register(Map tagletMap)
    {
        PrefixedTaglet taglet = new PrefixedTaglet();
        Taglet t = (Taglet) tagletMap.get(taglet.getName());
        if (t != null)
        {
            tagletMap.remove(taglet.getName());
        }
        tagletMap.put(taglet.getName(), taglet);
    }

    /**
     * Generates XML representing the <em>jvdTag</em> Javadoc
     * tag.
     *
     * @param jvdTag  The Javadoc tag.
     * @return XML representing <em>tag</em> as an XMLBeans type.
     */
    public JavadocTagType toXmlObject(Tag jvdTag)
    {
        JavadocTagType xmlJavadocTag = JavadocTagType.Factory.newInstance();
        xmlJavadocTag.setName(m_tagElementName);
        xmlJavadocTag.setPrefix(m_prefix);
        if (!m_tagHead.equals(""))
        {
            xmlJavadocTag.setHead(m_tagHead);
        }
        if (!jvdTag.text().equals(""))
        {
            XmlObject xmlContent = generateContentXml(jvdTag);
            XmlCursor tagCursor = xmlJavadocTag.newCursor();
            tagCursor.toFirstContentToken();
            XmlCursor contentCursor = xmlContent.newCursor();
            contentCursor.toNextToken();
            contentCursor.copyXml(tagCursor);
        }
        return xmlJavadocTag;
    }

    /**
     * Sets the name of the Javadoc tag that this taglet instance will
     * handle. This method is called from the {@link XmlTagletManager} when
     * it is creating taglet instances to use during the Javadoc run. Using
     * <em>name</em>, this method generates values for the Javadoc tag's
     * name (after removing the prefix), the prefix, and the name
     * of the "name" attribute for the &lt;javadoc-tag> element that
     * the taglet generates.
     * 
     * @param name The name of the Javadoc tag.
     */
    public void setName(String name)
    {
        m_tagName = removeAtSign(name);
        m_tagElementName = generateElementName(name);
        m_prefix = generatePrefix(name);
    }

    /**
     * Gets the prefix for the Javadoc tag for which this taglet 
     * generates XML.
     * 
     * @return The prefix.
     */
    protected String getPrefix()
    {
        return m_prefix;
    }

    /**
     * Generates a value for the "name" attribute of the 
     * &lt;javadoc-tag> element this taglet generates.
     * 
     * @param fullName The full name of the Javadoc tag, such as
     * "@foo.bar".
     * @return The attribute value, such as "bar".
     */
    protected static String generateElementName(String fullName)
    {
        String tagFullName = removeAtSign(fullName);
        String tagName = "";
        if (tagFullName.indexOf(":") > 0)
        {
            tagName = getTagNameAfterColon(tagFullName);
        } else if (tagFullName.indexOf(".") > 0)
        {
            tagName = getTagNameAfterDot(tagFullName);
        }
        return tagName;
    }

    /**
     * Generates a value for the "prefix" attribute of the 
     * &lt;javadoc-tag> element this taglet generates.
     * 
     * @param fullName The full name of the Javadoc tag, such as
     * "@foo.bar".
     * @return The attribute value, such as "foo".
     */
    protected static String generatePrefix(String fullName)
    {
        fullName = removeAtSign(fullName);
        String tagPrefix = "";
        if (fullName.indexOf(":") > 0)
        {
            tagPrefix = getPrefixBeforeColon(fullName);
        } else if (fullName.indexOf(".") > 0)
        {
            tagPrefix = getPrefixBeforeDot(fullName);
        }
        return tagPrefix;
    }

    /**
     * Gets the string that precedes the last colon in <em>tagName</em>.
     * 
     * @param tagName The Javadoc tag's name, such as "foo:bar".
     * @return The prefix value, such as "foo".
     */
    protected static String getPrefixBeforeColon(String tagName)
    {
        int firstColon = tagName.lastIndexOf(":");
        String prefix = tagName.substring(0, firstColon);
        return prefix;
    }

    /**
     * Gets the string that follows the last colon in <em>tagName</em>.
     * 
     * @param tagName The Javadoc tag's name, such as "foo:bar".
     * @return The name value, such as "bar".
     */
    protected static String getTagNameAfterColon(String tagName)
    {
        int firstColon = tagName.lastIndexOf(":");
        String tagNameString = tagName.substring(++firstColon, tagName.length());
        return tagNameString;
    }

    /**
     * Gets the string that precedes the last dot in <em>tagName</em>.
     * 
     * @param tagName The Javadoc tag's name, such as "foo.bar".
     * @return The prefix value, such as "foo".
     */
    protected static String getPrefixBeforeDot(String tagName)
    {
        int lastDot = tagName.lastIndexOf(".");
        String prefix = tagName.substring(0, lastDot);
        return prefix;
    }

    /**
     * Gets the string that follows the last dot in <em>tagName</em>.
     * 
     * @param tagName The Javadoc tag's name, such as "foo.bar".
     * @return The name value, such as "bar".
     */
    protected static String getTagNameAfterDot(String tagName)
    {
        int lastDot = tagName.lastIndexOf(".");
        String tagNameString = tagName.substring(++lastDot, tagName.length());
        return tagNameString;
    }
}
