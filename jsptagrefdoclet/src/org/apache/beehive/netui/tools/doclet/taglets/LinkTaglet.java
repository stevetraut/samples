package org.apache.beehive.netui.tools.doclet.taglets;

import com.sun.tools.doclets.Taglet;

import java.util.Map;

/**
 * Represents doclet support for the @link Javadoc tag. When the @link tag points
 * to a Java class, this doclet generates XML through which XSLT can create a
 * link to Javadoc output for the class (if the location of the output is known).
 * If the class happens to be a JSP tag handler class, then the resulting XML
 * will also include information through which XSLT can create a link to the
 * custom JSP tag that the handler class provides logic for.
 * <p/>
 * In XML output, the @link Javadoc tag (an inline tag) becomes a &lt;javadoc-tag>
 * element that is usually embedded as a part of mixed content. For example,
 * consider the following example of a description in Javadoc:
 * <p>
 *     This text describes a class similar to another class. The {&#64;link Foo} class.
 * </p>
 * The XML output would be something like this:
 * <pre>
 * &lt;description>
 *     &lt;lead>This text describes a class similar to another class.&lt;lead>
 *     &lt;detail>This text describes a class similar to another class. The 
 *         &lt;javadoc-tag name="link">
 *             &lt;target inPackage="my.package" typeName="Foo" packagePath="http://mydomain.com/mydoc/my/package/">Foo&lt;/target>
 *         &lt;/javadoc-tag>
 *         class.
 *     &lt;detail>
 * &lt;/description>
 * </pre>
 * Where the class linked to is also a JSP tag handler class, JSP tag-specific attributes 
 * will be included on the &lt;target> element.
 * <p/>
 * Contrast handling of the @link tag with handling of the custom @jsptagref.taglink
 * tag supported by this doclet. See {@link TagLinkTaglet}
 * for more information.
 */
public final class LinkTaglet extends SeeTaglet
{
    /**
     * Called by Javadoc to register this taglet, associating
     * the taglet with the tag it represents. This method will only be called
     * by the standard doclet (if the taglet is used with it).
     * 
     * @param tagletMap A map that associates the tag with its handler.
     */
    public static void register(Map tagletMap)
    {
        LinkTaglet taglet = new LinkTaglet();
        Taglet t = (Taglet) tagletMap.get(taglet.getName());
        if (t != null)
        {
            tagletMap.remove(taglet.getName());
        }
        tagletMap.put(taglet.getName(), taglet);
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
