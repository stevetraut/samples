package org.apache.beehive.netui.tools.doclet.jsptagref;

import org.apache.beehive.netui.tools.doclet.schema.*;
import org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.TagType;
import org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.TaglibDocument;
import org.apache.beehive.netui.tools.doclet.taglets.SeeTaglet;

/**
 * Handler for tag libraries in the http://java.sun.com/dtd/web-jsptaglibrary_1_1.dtd
 * namespace. The doclet binds a TLD file with libraries in this namespace to an instance
 * of org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.TaglibDocument. 
 * It then passes the instance to methods of this class, which copy parts of the 
 * TLD content into new XML shapes designed to hold both TLD and Javadoc data. 
 * <p/>
 * The newly created XML is in the http://apache.org/beehive/netui/tools/doclet/schema
 * namespace. This namespace includes one document per JSP tag, as well as
 * an overview document. These are bound to XMLBeans types in the 
 * org.apache.beehive.netui.tools.doclet.schema package (types generated from schema).
 * <p/>
 * Note that the TLD schema was generated from a DTD solely for the purposes of
 * this doclet. It is not an official description of the TLD shape.
 */
public class TaglibHandler11
{
    /**
     * Collects tag library information from the <em>tldTaglibDoc</em>
     * TLD, copying the information into <code>JspTagDocument</code>
     * array returned by the method. A <code>JspTagDocument</code> instance
     * represents an XML instance created by the doclet for a single JSP tag.
     * Each JSP tag described in <em>tldTaglibDoc</em> gets its own 
     * JspTagDocument instance. Information scoped to the tag's containing
     * library (such as the taglib URI) is also added to each JspTagDocument 
     * instance.
     * <p/>
     * 
     * @param tldTaglibDoc An XMLBeans instance to which the TLD is bound.
     * @return An array of &lt;jsp-tag> elements bound to XMLBeans types.
     */
    public static JspTagDocument[] collectTagInfo(TaglibDocument tldTaglibDoc)
    {
        TaglibDocument.Taglib tldTaglib = tldTaglibDoc.getTaglib();
        JspTagDocument[] xmlJspTagDocArray = new JspTagDocument[tldTaglib.sizeOfTagArray()];

        // Get the tag info.
        TagType[] tldJspTagArray = tldTaglib.getTagArray();
        for (int i = 0; i < tldJspTagArray.length; i++)
        {
            JspTagDocument xmlJspTagDoc = JspTagDocument.Factory.newInstance();
            JspTagDocument.JspTag xmlJspTag = xmlJspTagDoc.addNewJspTag();
            xmlJspTag.setPrefix(tldTaglib.getShortname().getStringValue());
            String tldUri = tldTaglib.getUri().getStringValue();
            xmlJspTag.setUri(tldUri);
            xmlJspTag.setDocRoot(JavadocHandler.buildDocRoot(tldUri));
            if (tldTaglib.getTlibversion() != null)
            {
                xmlJspTag.setTaglibVersion(tldTaglib.getTlibversion().getStringValue());
            }
            TagType tldJspTag = tldJspTagArray[i];
            if (tldJspTag.getBodycontent() != null)
            {
                xmlJspTag.setBodyContent(tldJspTag.getBodycontent().getStringValue());
            }
            if (tldJspTag.getBodycontent() == null)
            {
                xmlJspTag.setBodyContent("JSP");
            }
            if (tldJspTag.getName() != null)
            {
                xmlJspTag.setName(tldJspTag.getName().getStringValue());
            }
            // need to retrieve the rest of the tag info here, as with handler classes.
            if (tldJspTag.getTeiclass() != null)
            {
                TypeInfoType xmlTeiClass = xmlJspTag.addNewTeiClass();
                xmlTeiClass.setFullName(tldJspTag.getTeiclass().getStringValue());
            }

            String tldTagClassName = tldJspTag.getTagclass().getStringValue();
            if (tldTagClassName != null)
            {
                TypeInfoType xmlHandlerClass = xmlJspTag.addNewHandlerClass();
                xmlHandlerClass.setFullName(tldTagClassName);
            }
            if (tldJspTag.getInfo() != null)
            {
                String tldTagDescription = tldJspTag.getInfo().getStringValue();
                xmlJspTag.addTldDescription(tldTagDescription);
            }
            org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.AttributeType[] tldAttributeArray =
                    tldJspTag.getAttributeArray();
            if (tldAttributeArray.length > 0)
            {
                AttributesType attributes = xmlJspTag.addNewAttributes();
                for (int j = 0; j < tldAttributeArray.length; j++)
                {
                    org.apache.beehive.netui.tools.doclet.schema.webJsptaglibrary11.AttributeType tldAttribute =
                            tldAttributeArray[j];
                    AttributeType xmlAttribute = attributes.addNewAttribute();
                    //                attribute.getName().getId();
                    if (tldAttribute.getName() != null)
                    {
                        xmlAttribute.setName(tldAttribute.getName().getStringValue());
                    }
                    if (tldAttribute.getRequired() != null)
                    {
                        xmlAttribute.setRequired(tldAttribute.getRequired().getStringValue());
                    }
                    if (tldAttribute.getRtexprvalue() != null)
                    {
                        xmlAttribute.setRtexprvalue(tldAttribute.getRtexprvalue().getStringValue());
                    } else if (tldAttribute.getRtexprvalue() == null)
                    {
                        xmlAttribute.setRtexprvalue("false");
                    }
                    TypeInfoType xmlAttributeType = xmlAttribute.addNewType();
                    xmlAttributeType.setFullName("java.lang.String");
                }
            }
            xmlJspTagDocArray[i] = xmlJspTagDoc;
        }
        return xmlJspTagDocArray;
    }

    /**
     * Adds library information from <em>tldTaglibDoc</em>, a TLD in the current Javadoc
     * run, to the generated summary XML represented by <em>xmlTaglibSummary</em>,
     * returning the updated summary XML. This method copies only library-scope
     * information into the summary -- it does not copy information about 
     * tags or functions in the library.
     *
     * @param tldTaglibDoc     The TLD with tag library info to add to the summary.
     * @param xmlTaglibSummary The generated summary to copy the info into.
     * @return The updated summary XML.
     */
    public static TaglibSummaryType addLibraryToSummary(TaglibDocument tldTaglibDoc,
                                                        TaglibSummaryType xmlTaglibSummary)
    {
        TaglibDocument.Taglib tldTaglib = tldTaglibDoc.getTaglib();
        String tldUri = tldTaglib.getUri().getStringValue();
        String tldShortName = tldTaglib.getShortname().getStringValue();
        SummaryUriType xmlSummaryUri = xmlTaglibSummary.addNewUri();
        xmlSummaryUri.setStringValue(tldUri);
        xmlSummaryUri.setUriPath(SeeTaglet.removeProtocolPrefix(tldUri) + '/');
        xmlTaglibSummary.setDocRoot(JavadocHandler.buildDocRoot(tldUri));
        xmlTaglibSummary.setPrefix(tldShortName);
        if (tldTaglib.getTlibversion() != null)
        {
            xmlTaglibSummary.setTaglibVersion(tldTaglib.getTlibversion().getStringValue());
        }
        xmlTaglibSummary.setUri(xmlSummaryUri);
        return xmlTaglibSummary;
    }
}
