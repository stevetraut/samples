package org.apache.beehive.netui.tools.doclet.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import org.apache.beehive.netui.tools.doclet.jsptagref.JspTagContext;
import org.apache.beehive.netui.tools.doclet.jsptagref.JspTagDetail;
import org.apache.beehive.netui.tools.doclet.schema.TagLinkType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Represents doclet support for the @jsptagref.taglink Javadoc tag. 
 * This tag is designed to provide a way to link directly to the
 * topic for a JSP tag. Note that this tag is not supported by the standard
 * doclet, and so it should only be used within jsptagref-prefixed Javadoc tags
 * such as @jsptagref.tagdescription, which will not be seen by the standard
 * doclet unless explicit support is added.
 * <p/>
 * In XML output, the @jsptagref.taglink Javadoc tag (an inline tag) becomes 
 * a &lt;javadoc-tag> element that is usually embedded as a part of mixed content. 
 * For example, consider the following example of a JSP tag description in Javadoc:
 * <p>
 *     &#64;jsptagref.tagdescription This text describes a JSP tag similar to 
 * another tag. The {&#64;jsptagref.taglink foo@http://openuri.org/tags} tag.
 * </p>
 * If the &lt;foo> tag was among the tags in the current Javadoc run,
 * the XML output would be something like this:
 * <pre>
 * &lt;javadoc-tag name="tagdescription" prefix="jsptagref" head="Description">
 *     &lt;description>
 *         &lt;lead>This text describes a JSP tag similar to another tag.&lt;lead>
 *         &lt;detail>This text describes a JSP tag similar to another tag. The 
 *             &lt;javadoc-tag name="taglink" prefix="jsptagref">
 *                 &lt;target tagName="foo" tagPrefix="bar" tagUri="http://openuri.org/tags" uriPath="../../../openuri.org/tags/">Foo&lt;/target>
 *             &lt;/javadoc-tag>
 *             tag.
 *         &lt;detail>
 *     &lt;/description>
 * &lt;/javadoc-tag>
 * </pre>
 * Note that when the tag library is not in the current run, but available
 * through an external uri-path file, the prefix attribute will be omitted
 * because the TLD file is not available.
 */
public class TagLinkTaglet extends PrefixedTaglet
{
    /**
     * Called by Javadoc to register this taglet, associating
     * the taglet with the tag it represents.
     *
     * @param tagletMap A map that associates tag
     */
    public static void register(Map tagletMap)
    {
        TagLinkTaglet taglet = new TagLinkTaglet();
        Taglet t = (Taglet) tagletMap.get(taglet.getName());
        if (t != null)
        {
            tagletMap.remove(taglet.getName());
        }
        tagletMap.put(taglet.getName(), taglet);
    }

    /**
     * Generates a container for the &lt;target> element that describes 
     * the destination this Javadoc tag links to. The {@link #generateTargetXml(Tag)}
     * method generates the rest of the element.
     * 
     * @param jvdTag The &#64;jsptagref.taglink Javadoc tag.
     * @return The &lt;target> element.
     */
    protected XmlObject generateContentXml(Tag jvdTag)
    {
        XmlObject xmlContent = XmlObject.Factory.newInstance();
        XmlCursor contentCursor = xmlContent.newCursor();
        contentCursor.toNextToken();
        contentCursor.beginElement(new QName(REF_NAMESPACE, "target"));
        contentCursor.toParent();
        XmlObject xmlTarget = contentCursor.getObject();
        TagLinkType xmlTargetContent = generateTargetXml(jvdTag);
        xmlTarget.set(xmlTargetContent);
        return xmlContent;
    }

    /**
     * Generates the &lt;target> element that describes the destination 
     * this Javadoc tag links to. The {@link #generateContentXml(Tag)}
     * method generates a container for the element.
     * 
     * @param jvdTag The &#64;jsptagref.taglink Javadoc tag.
     * @return The contained portion of &lt;target> element.
     */
    protected TagLinkType generateTargetXml(Tag jvdTag)
    {
        JspTagContext context = getContext();
        TagLinkType xmlTarget = TagLinkType.Factory.newInstance();
        // Parse the @jsptagref.taglink tag's text. 
        String jspTagName = jvdTag.text().substring(0, jvdTag.text().indexOf("@"));
        String tagLibUri = jvdTag.text().substring(jvdTag.text().indexOf("@") + 1, jvdTag.text().length());
        // Get information about the JSP tag this link points to.
        JspTagDetail detail = context.getTagDetailByTag(jspTagName, tagLibUri);
        // Fill in the value's for the XML we're building.
        xmlTarget.setTagUri(tagLibUri);
        xmlTarget.setTagName(jspTagName);
        // If there's a JspTagDetail instance, the tag is in the scope of the current run.
        if (detail != null)
        {
            String cleanPath = SeeTaglet.removeProtocolPrefix(tagLibUri);
            xmlTarget.setUriPath("../" + buildDocRoot(tagLibUri) +
                    '/' + cleanPath + '/');
            xmlTarget.setTagPrefix(detail.getTagPrefix());
        }
        // If the tag's URI is among the external references, treat it as an external link.
        else if (context.isItemExternal(tagLibUri))
        {
            String externalLink = context.getExternalLinkPath(tagLibUri);
            if (externalLink != null)
            {
                if (!context.isItemAbsolute(tagLibUri))
                {
                  externalLink = "../" + buildDocRoot(tagLibUri) +
                  '/' + externalLink + '/';
                }
                xmlTarget.setUriPath(externalLink);
            }
        }
        return xmlTarget;
    }

    /**
     * <code>true</code> if this tag may occur inline with text;
     * otherwise, <code>false</code>.
     * <p/>
     * Part of the Taglet interface.
     */
    public boolean isInlineTag()
    {
        return true;
    }
}
