package org.apache.beehive.netui.tools.doclet.taglets;

import com.sun.javadoc.Tag;
import org.apache.beehive.netui.tools.doclet.jsptagref.ConfigurationJspTagDoc;
import org.apache.beehive.netui.tools.doclet.jsptagref.JspTagContext;
import org.apache.beehive.netui.tools.doclet.schema.JavadocTagType;

import java.util.HashMap;

/**
 * A class to manage support for Javadoc tags. Support for a given Javadoc tag
 * is implemented as a taglet -- for this doclet, the taglet implements the
 * {@link XmlTaglet} interface. The default set of supported tags is initialized
 * when an instance of this class is created. Although this doclet doesn't fully
 * support it yet, this manager provides a way to add support for additional
 * tags by adding taglets that implement XmlTaglet. This support will be more
 * complete when the doclet knows how to interpret the -taglet and -tagletpath
 * command-line options.
 * <p/>
 * For a list of the Javadoc tags supported by this doclet by default, see
 * {@link #initBuiltInTags(ConfigurationJspTagDoc)}.
 */
public final class XmlTagletManager
{
    /**
     * A map to hold taglets known to this Javadoc run. The key is the Javadoc
     * tag's name; the value is the {@link XmlTaglet} instance corresponding to the tag.
     */
    private final HashMap m_tagletMap;

    /**
     * Constructs a new instance of this class by creating a HashMap to hold
     * tag-name-to-taglet-instance mappings, and by adding to the map taglet
     * instances that will handle Javadoc tags for which this doclet provides
     * default support.
     */
    public XmlTagletManager(ConfigurationJspTagDoc config)
    {
        m_tagletMap = new HashMap();
        initBuiltInTags(config);
    }

    /**
     * Retrieves the XML for a Javadoc tag by getting its corresponding taglet
     * instance from the taglet map and calling that taglet's toXmlObject method.
     *
     * @param jvdTag  The Javadoc tag to be processed.
     * @param context A JspTagContext object containing context information
     *                for the current JSP tag in the current Javadoc run.
     * @return XML corresponding to the Javadoc tag being processed.
     */
    public JavadocTagType processJavadocTag(Tag jvdTag, JspTagContext context)
    {
        JavadocTagType xmlJavadocTag = null;
        String tagName = removeAtSign(jvdTag.name());
        XmlTaglet taglet = (XmlTaglet) m_tagletMap.get(tagName);
        if (taglet != null)
        {
            taglet.setContext(context);
            xmlJavadocTag = taglet.toXmlObject(jvdTag);
        }
        return xmlJavadocTag;
    }

    /**
     * Adds a taglet to the set of taglets managed by this class.
     *
     * @param xmlCustomTag The taglet to add.
     */
    public void addCustomTag(XmlTaglet xmlCustomTag)
    {
        if (xmlCustomTag != null)
        {
            String name = xmlCustomTag.getName();
            if (m_tagletMap.containsKey(name))
            {
                m_tagletMap.remove(name);
            }
            m_tagletMap.put(name, xmlCustomTag);
        }
    }

    /**
     * Adds a taglet to the set of taglets managed by this class.
     */
    public void addCustomTag(String tagName, String tagScope, String tagHeader)
    {
        XmlTaglet taglet = assignTaglet(tagName, tagScope, tagHeader);
        addCustomTag(taglet);
    }

    /**
     * Assigns one of this doclet's taglets to handle the tag whose <em>tagName</em> was
     * specified by the -tag argument. The <em>tagScope</em> argument is not
     * currently used, but may eventually be used for the kind of error checking
     * the standard doclet does.
     *
     * @param tagName   The name of the Javadoc tag as it will appear in Java code,
     *                  without the @ sign.
     * @param tagScope  A string indicating the tag's scope of use -- whether it
     *                  may be used on methods, on fields, etc.
     * @param tagHeader "header" text that the tag's user might want to have
     *                  show up in the XML.
     * @return An XmlTaglet that will represent the tag in this doclet.
     */
    private static XmlTaglet assignTaglet(String tagName, String tagScope, String tagHeader)
    {
        XmlTaglet taglet;

        // Add support for @see.
        if (tagName.equals("see"))
        {
            taglet = new SeeTaglet();
            ((SeeTaglet) taglet).setName(tagName);
            ((SeeTaglet) taglet).setTagHead(tagHeader);

            // Adds support for @link.
        } else if (tagName.equals("link"))
        {
            taglet = new LinkTaglet();
            ((LinkTaglet) taglet).setName(tagName);
            ((LinkTaglet) taglet).setTagHead(tagHeader);

            // Add support for @linkplain.
        } else if (tagName.equals("linkplain"))
        {
            taglet = new LinkTaglet();
            ((LinkTaglet) taglet).setName(tagName);
            ((LinkTaglet) taglet).setTagHead(tagHeader);

            // Add support for @jsptagref.tagdescription.
        } else if (tagName.equals("jsptagref.tagdescription"))
        {
            taglet = new PrefixedDescriptionTaglet();
            ((PrefixedDescriptionTaglet) taglet).setName(tagName);
            ((PrefixedDescriptionTaglet) taglet).setTagHead(tagHeader);

            // Add support for @jsptagref.attributedescription.
        } else if (tagName.equals("jsptagref.attributedescription"))
        {
            taglet = new PrefixedDescriptionTaglet();
            ((PrefixedDescriptionTaglet) taglet).setName(tagName);
            ((PrefixedDescriptionTaglet) taglet).setTagHead(tagHeader);

// Add support for @jsptagref.taglink.
        } else if (tagName.equals("jsptagref.taglink"))
        {
            taglet = new TagLinkTaglet();
            ((TagLinkTaglet) taglet).setName(tagName);
            ((TagLinkTaglet) taglet).setTagHead(tagHeader);

// Add support for @jsptagref.databindable.
        } else if (tagName.equals("jsptagref.databindable"))
        {
            taglet = new PrefixedTaglet();
            ((PrefixedTaglet) taglet).setName(tagName);
            ((PrefixedTaglet) taglet).setTagHead(tagHeader);

            // Add support for @jsptagref.attributesyntaxvalue.
        } else if (tagName.equals("jsptagref.attributesyntaxvalue"))
        {
            taglet = new PrefixedTaglet();
            ((PrefixedTaglet) taglet).setName(tagName);
            ((PrefixedTaglet) taglet).setTagHead(tagHeader);

            // Add support for @jsptagref.see.
        } else if (tagName.equals("jsptagref.see"))
        {
            taglet = new PrefixedTaglet();
            ((PrefixedTaglet) taglet).setName(tagName);
            ((PrefixedTaglet) taglet).setTagHead(tagHeader);

            // Add support for @jsptagref.author.
        } else if (tagName.equals("jsptagref.author"))
        {
            taglet = new PrefixedTaglet();
            ((PrefixedTaglet) taglet).setName(tagName);
            ((PrefixedTaglet) taglet).setTagHead(tagHeader);

            // Add support for other tags with "." or ":" in their name.
        } else if (tagName.indexOf(".") > 0)
        {
            taglet = new PrefixedTaglet();
            ((PrefixedTaglet) taglet).setName(tagName);
            ((PrefixedTaglet) taglet).setTagHead(tagHeader);
        } else if (tagName.indexOf(":") > 0)
        {
            taglet = new PrefixedTaglet();
            ((PrefixedTaglet) taglet).setName(tagName);
            ((PrefixedTaglet) taglet).setTagHead(tagHeader);

            // Add support for other tags.
        } else
        {
            taglet = new SimpleXmlTaglet();
            ((SimpleXmlTaglet) taglet).setName(tagName);
            ((SimpleXmlTaglet) taglet).setTagHead(tagHeader);
        }
        return taglet;
    }

    /**
     * Adds taglet support for the set of Javadoc tags that this doclet
     * supports by default. These tags include:
     * <ul>
     * <li>@author</li>
     * <li>@example</li>
     * <li>@since</li>
     * <li>@version</li>
     * <li>@see</li>
     * <li>@link</li>
     * <li>@linkplain</li>
     * <li>@jsptagref.tagdescription</li>
     * <li>@jsptagref.taglink</li>
     * <li>@jsptagref.attributedescription</li>
     * <li>@jsptagref.databindable</li>
     * <li>@jsptagref.attributesyntaxvalue</li>
     * </ul>
     */
    private void initBuiltInTags(ConfigurationJspTagDoc config)
    {
        // Create taglet instances to use for each of the supported Javadoc tags.
        SimpleXmlTaglet simpleTaglet = null;
        SeeTaglet seeTaglet = null;
        LinkTaglet linkTaglet = null;
        PrefixedTaglet prefixedTaglet = null;
        PrefixedDescriptionTaglet prefixedDescriptionTaglet = null;
        TagLinkTaglet tagLinkTaglet = null;
        DocRootTaglet docRootTaglet = null;

        // The following tags are supported in both Javadoc cases
        // because they're used in both standard and jsptagref tags.

        // Add support for @linkplain
        linkTaglet = new LinkTaglet();
        linkTaglet.setName("linkplain");
        addCustomTag(linkTaglet);

        // Adds support for @link.
        linkTaglet = new LinkTaglet();
        linkTaglet.setName("link");
        addCustomTag(linkTaglet);

        // Add support for @docRoot.
        docRootTaglet = new DocRootTaglet();
        docRootTaglet.setName("docRoot");
        addCustomTag(docRootTaglet);

        // If the user wants standard Javadoc, add support for those tags.
        if (config.useStandardContent())
        {
            // Add support for @author.
            simpleTaglet = new SimpleXmlTaglet();
            simpleTaglet.setName("author");
            simpleTaglet.setTagHead("Author");
            addCustomTag(simpleTaglet);

            // Add support for @deprecated.
            simpleTaglet = new SimpleXmlTaglet();
            simpleTaglet.setName("deprecated");
            simpleTaglet.setTagHead("Deprecated");
            addCustomTag(simpleTaglet);

            // Add support for @example.
            simpleTaglet = new SimpleXmlTaglet();
            simpleTaglet.setName("example");
            simpleTaglet.setTagHead("Example");
            addCustomTag(simpleTaglet);

            // Add support for @since.
            simpleTaglet = new SimpleXmlTaglet();
            simpleTaglet.setName("since");
            simpleTaglet.setTagHead("Since");
            addCustomTag(simpleTaglet);

            // Add support for @version.
            simpleTaglet = new SimpleXmlTaglet();
            simpleTaglet.setName("version");
            simpleTaglet.setTagHead("Version");
            addCustomTag(simpleTaglet);

            // Add support for @see.
            seeTaglet = new SeeTaglet();
            seeTaglet.setName("see");
            seeTaglet.setTagHead("See Also");
            addCustomTag(seeTaglet);
        }
        // If the user wants jsptagref tags, add support for them.
        if (config.useJspTagRefContent())
        {
            // Add support for @jsptagref.tagdescription.
            prefixedDescriptionTaglet = new PrefixedDescriptionTaglet();
            prefixedDescriptionTaglet.setName("jsptagref.tagdescription");
            prefixedDescriptionTaglet.setTagHead("Description");
            addCustomTag(prefixedDescriptionTaglet);

            // Add support for @jsptagref.attributedescription.
            prefixedDescriptionTaglet = new PrefixedDescriptionTaglet();
            prefixedDescriptionTaglet.setName("jsptagref.attributedescription");
            prefixedDescriptionTaglet.setTagHead("Description");
            addCustomTag(prefixedDescriptionTaglet);

            // Add support for @jsptagref.taglink.
            tagLinkTaglet = new TagLinkTaglet();
            tagLinkTaglet.setName("jsptagref.taglink");
            addCustomTag(tagLinkTaglet);

            // Add support for @jsptagref.databindable.
            prefixedTaglet = new PrefixedTaglet();
            prefixedTaglet.setName("jsptagref.databindable");
            prefixedTaglet.setTagHead("Data bindable");
            addCustomTag(prefixedTaglet);

            // Add support for @jsptagref.attributesyntaxvalue.
            prefixedTaglet = new PrefixedTaglet();
            prefixedTaglet.setName("jsptagref.attributesyntaxvalue");
            prefixedTaglet.setTagHead("Attribute Value");
            addCustomTag(prefixedTaglet);

            // Add support for @jsptagref.see.
            prefixedTaglet = new PrefixedTaglet();
            prefixedTaglet.setName("jsptagref.see");
            prefixedTaglet.setTagHead("See Also");
            addCustomTag(prefixedTaglet);
        }
    }

    /**
     * Removes the @ sign prepending a Javadoc tag name.
     *
     * @param javadocTagName The name of the tag whose @ sign should
     *                       be removed.
     * @return The tag name without an @ aign.
     */
    private static String removeAtSign(String javadocTagName)
    {
        if (javadocTagName.charAt(0) == '@')
        {
            javadocTagName = javadocTagName.substring(1);
        }
        return javadocTagName;
    }

    /**
     * Gets the HashMap that maps the names of Javadoc tags with the taglets
     * that render their XML output.
     *
     * @return A map that associates supported Javadoc tags with taglet instances
     *         that generate XML for the tags.
     */
    public HashMap getTagletMap()
    {
        return m_tagletMap;
    }
}