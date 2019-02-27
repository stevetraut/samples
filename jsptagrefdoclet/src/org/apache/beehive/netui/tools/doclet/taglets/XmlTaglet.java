package org.apache.beehive.netui.tools.doclet.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import org.apache.beehive.netui.tools.doclet.jsptagref.JspTagContext;
import org.apache.beehive.netui.tools.doclet.schema.JavadocTagType;

/**
 * An interface implemented by taglets used with the JspTagDoc doclet.
 * <p/>
 * This interface extends Javadoc's Taglet interface in part because some of that
 * interface's methods are useful to this doclet. But this also makes it more convenient
 * to write a taglet that is useful for both this doclet and the standard doclet.
 * Taglet methods have default implementations in the {@link SimpleXmlTaglet} class, so
 * that class should be extended for simple cases.
 */
public interface XmlTaglet extends Taglet
{
    /**
     * Called by the JspTagDoc doclet to retrieve the XML representing the Javadoc
     * tag <em>tag</em>.
     *
     * @param jvdTag  The Javadoc tag for which XML should be generated.
     * @return The XML as an XMLBeans type.
     */
    JavadocTagType toXmlObject(Tag jvdTag);

    /**
     * Called by the JspTagDoc doclet to retrieve the XML representing an array of
     * the Javadoc tag <em>tag</em>.
     * <p/>
     * This method is not currently used by the doclet.
     *
     * @param jvdTagArray An array of the Javadoc tag for which XML should be generated.
     * @return The XML as an XMLBeans type.
     */
    JavadocTagType toXmlObject(Tag[] jvdTagArray);

    /**
     * Called by the {@link XmlTagletManager} to set a context
     * object that provides information about the current JSP tag
     * and the Javadoc run.
     * 
     * @param context The context instance.
     */
    void setContext(JspTagContext context);
}