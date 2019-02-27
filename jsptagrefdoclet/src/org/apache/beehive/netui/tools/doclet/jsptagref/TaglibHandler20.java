package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.java.xml.ns.j2Ee.DescriptionType;
import com.sun.java.xml.ns.j2Ee.*;
import org.apache.beehive.netui.tools.doclet.schema.*;
import org.apache.beehive.netui.tools.doclet.schema.DisplayNameType;
import org.apache.beehive.netui.tools.doclet.taglets.SeeTaglet;

import java.lang.String;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Handler for tag libraries in the http://java.sun.com/xml/ns/j2ee namespace.
 * The doclet binds a TLD file with libraries in this namespace to an instance
 * of com.sun.java.xml.ns.j2Ee.TaglibDocument. It then passes the instance to
 * methods of this class, which copy parts of the TLD content into new
 * XML shapes designed to hold both TLD and Javadoc data. 
 * <p/>
 * The newly created XML is in the http://apache.org/beehive/netui/tools/doclet/schema
 * namespace. This namespace includes one document per JSP tag and function, as well as
 * an overview document. These are bound to XMLBeans types in the 
 * org.apache.beehive.netui.tools.doclet.schema package (types generated from schema).
 */
public class TaglibHandler20
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
        TldTaglibType tldTaglib = tldTaglibDoc.getTaglib();
        JspTagDocument[] xmlJspTagDocArray = new JspTagDocument[tldTaglib.sizeOfTagArray()];
        TagType[] tldJspTagArray = tldTaglib.getTagArray();
        // Loop through the tags defined in the TLD, creating for each a new
        // XML document to hold topic information, then copying info from the
        // TLD into the new XML.
        for (int i = 0; i < tldJspTagArray.length; i++)
        {
            JspTagDocument xmlJspTagDoc = JspTagDocument.Factory.newInstance();
            JspTagDocument.JspTag xmlJspTag = xmlJspTagDoc.addNewJspTag();
            xmlJspTag.setPrefix(tldTaglib.getShortName().getStringValue());
            String tldUri = tldTaglib.getUri().getStringValue();
            xmlJspTag.setUri(tldUri);
            xmlJspTag.setDocRoot(JavadocHandler.buildDocRoot(tldUri));
            if (tldTaglib.getDisplayNameArray() != null)
            {
                if (tldTaglib.getDisplayNameArray().length > 0)
                {
                    DisplayNameType xmlJspTagDisplayName = DisplayNameType.Factory.newInstance();
                    xmlJspTagDisplayName.setStringValue(tldTaglib.getDisplayNameArray(0).getStringValue());
                    xmlJspTag.setDisplayName(xmlJspTagDisplayName);
                }
            }
            xmlJspTag.setUri(tldTaglib.getUri().getStringValue());
            if (tldTaglib.getTlibVersion() != null)
            {
                xmlJspTag.setTaglibVersion(tldTaglib.getTlibVersion().toString());
            }
            TagType tldJspTag = tldJspTagArray[i];
            if (tldJspTag.getBodyContent() != null)
            {
                xmlJspTag.setBodyContent(tldJspTag.getBodyContent().getStringValue());
            }
            if (tldJspTag.getBodyContent() == null)
            {
                xmlJspTag.setBodyContent("JSP");
            }
            if (tldJspTag.getExample() != null)
            {
                xmlJspTag.setTldExample(tldJspTag.getExample().getStringValue());
            }
            if (tldJspTag.getName() != null)
            {
                xmlJspTag.setName(tldJspTag.getName().getStringValue());
            }
            if (tldJspTag.getTeiClass() != null)
            {
                TypeInfoType xmlTeiClass = xmlJspTag.addNewTeiClass();
                xmlTeiClass.setFullName(tldJspTag.getTeiClass().getStringValue());
            }
            com.sun.java.xml.ns.j2Ee.DisplayNameType[] tldDisplayNameArray = tldJspTag.getDisplayNameArray();
            for (int l = 0; l < tldDisplayNameArray.length; l++)
            {
                com.sun.java.xml.ns.j2Ee.DisplayNameType tldDisplayName = tldDisplayNameArray[l];
                org.apache.beehive.netui.tools.doclet.schema.DisplayNameType xmlDisplayName =
                        xmlJspTag.addNewDisplayName();
                xmlDisplayName.setLang(tldDisplayName.getLang());
                xmlDisplayName.setStringValue(tldDisplayName.getStringValue());
            }
            if (tldJspTag.getDynamicAttributes() != null)
            {
                xmlJspTag.setDynamicAttributes(Boolean.valueOf(tldJspTag.getDynamicAttributes().getStringValue()).booleanValue());
            }
            com.sun.java.xml.ns.j2Ee.IconType[] iconArray = tldJspTag.getIconArray();
            for (int m = 0; m < iconArray.length; m++)
            {
                com.sun.java.xml.ns.j2Ee.IconType icon = iconArray[m];
                xmlJspTag.setLargeIcon(icon.getLargeIcon().getStringValue());
                icon.getLang();
                icon.getLargeIcon().getStringValue();
                icon.getSmallIcon().getStringValue();
            }
            String tldTagClassName = tldJspTag.getTagClass().getStringValue();
            if (tldTagClassName != null)
            {
                TypeInfoType xmlHandlerClass = xmlJspTag.addNewHandlerClass();
                xmlHandlerClass.setFullName(tldTagClassName);
            }
            DescriptionType[] tldTagDescriptionArray = tldJspTag.getDescriptionArray();
            for (int k = 0; k < tldTagDescriptionArray.length; k++)
            {
                DescriptionType tldTagDescription = tldTagDescriptionArray[k];
                xmlJspTag.addTldDescription(tldTagDescription.getStringValue());
            }
            // Copy the attribute information from the TLD into the new XML.
            TldAttributeType[] tldAttributeArray = tldJspTag.getAttributeArray();
            if (tldAttributeArray.length > 0)
            {
                AttributesType xmlAttributes = xmlJspTag.addNewAttributes();
                for (int j = 0; j < tldAttributeArray.length; j++)
                {
                    TldAttributeType tldAttribute = tldAttributeArray[j];
                    AttributeType xmlAttribute = xmlAttributes.addNewAttribute();

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
                    FullyQualifiedClassType tldAttributeType = tldAttribute.getType();
                    TypeInfoType xmlAttributeType = xmlAttribute.addNewType();
                    if (tldAttributeType != null)
                    {
                        xmlAttributeType.setFullName(tldAttributeType.getStringValue());
                    }
                    if (tldAttribute.getType() == null)
                    {
                        xmlAttributeType.setFullName("java.lang.String");
                    }
                    if (tldAttribute.getFragment() != null)
                    {
                        xmlAttribute.setFragment(Boolean.valueOf(tldAttribute.getFragment().getStringValue()).booleanValue());
                    }
                    DescriptionType[] tldAttDescriptionArray = tldAttribute.getDescriptionArray();
                    for (int k = 0; k < tldAttDescriptionArray.length; k++)
                    {
                        DescriptionType attDescription = tldAttDescriptionArray[k];
                        xmlAttribute.setTldDescription(attDescription.getStringValue());
                    }
                }
            }
            xmlJspTagDocArray[i] = xmlJspTagDoc;
        }
        return xmlJspTagDocArray;
    }

    /**
     * Collects taglib function information from the <em>tldTaglibDoc</em> TLD, 
     * copying the information into the FunctionDocument returned by the
     * method. The XML bound to FunctionDocument is the beginning of what
     * will be output by the doclet for the function.
     *
     * @param tldTaglibDoc The TLD as an XMLBeans type.
     * @return An array of &lt;function&gt; elements bound to XMLBeans types.
     */
    public static FunctionDocument[] collectFunctionInfo(TaglibDocument tldTaglibDoc)
    {
        TldTaglibType tldTaglib = tldTaglibDoc.getTaglib();
        FunctionDocument[] xmlFunctionDocArray = new FunctionDocument[tldTaglib.sizeOfFunctionArray()];
        FunctionType[] tldFunctionArray = tldTaglib.getFunctionArray();
        for (int i = 0; i < tldFunctionArray.length; i++)
        {
            FunctionType tldFunction = tldFunctionArray[i];
            FunctionDocument xmlFunctionDoc = FunctionDocument.Factory.newInstance();
            FunctionDocument.Function xmlFunction = xmlFunctionDoc.addNewFunction();
            xmlFunction.setName(tldFunction.getName().getStringValue());
            String tldUri = tldTaglib.getUri().getStringValue();
            xmlFunction.setUri(tldUri);
            xmlFunction.setDocRoot(JavadocHandler.buildDocRoot(tldUri));
            xmlFunction.setPrefix(tldTaglib.getShortName().getStringValue());
            if (tldTaglib.getDisplayNameArray() != null)
            {
                if (tldTaglib.getDisplayNameArray().length > 0)
                {
                    DisplayNameType xmlFunctionDisplayName = DisplayNameType.Factory.newInstance();
                    xmlFunctionDisplayName.setStringValue(tldTaglib.getDisplayNameArray(0).getStringValue());
                    xmlFunction.setDisplayName(xmlFunctionDisplayName);
                }
            }
            if (tldTaglib.getTlibVersion() != null)
            {
                xmlFunction.setTaglibVersion(tldTaglib.getTlibVersion().toString());
            }
            TypeInfoType xmlFunctionClass = xmlFunction.addNewFunctionClass();
            xmlFunctionClass.setFullName(tldFunction.getFunctionClass().getStringValue());
            xmlFunction.setFunctionSignature(tldFunction.getFunctionSignature().getStringValue());
            if (tldFunction.getExample() != null)
            {
                xmlFunction.setTldExample(tldFunction.getExample().getStringValue());
            }
            if (tldFunction.sizeOfDescriptionArray() > 0)
            {
                for (int j = 0; j < tldFunction.sizeOfDescriptionArray(); j++)
                {
                    xmlFunction.addTldDescription(tldFunction.getDescriptionArray(j).getStringValue());
                }
            }
            MethodType xmlFunctionMethod;
            if (tldFunction.getFunctionSignature() != null)
            {
                HashMap signatureMap =
                        parseFunctionSignature(tldFunction.getFunctionSignature().getStringValue());
                xmlFunctionMethod = xmlFunction.addNewFunctionSupportMethod();
                xmlFunctionMethod.setName((String) signatureMap.get("name"));
                xmlFunctionMethod.setSignature((String) signatureMap.get("signature"));
                xmlFunctionMethod.addNewReturns().setFullName((String) signatureMap.get("return"));
            }
            xmlFunctionDocArray[i] = xmlFunctionDoc;
        }
        return xmlFunctionDocArray;
    }

    /**
     * Parses a function signature into its parts, returning a HashMap with 
     * the parts. The parts are reassembled as a method
     * signature in generated XML so that the function's underlying Java method
     * can be found among those known to Javadoc. 
     * <p/>
     * <em>functionSignature</em> is in the form:
     * <pre>
     *     returntype functionName(arg1, arg2)
     * </pre>
     * The signature used to find the corresponding method in Javadoc is
     * of the form:
     * <pre>
     *     functionName(arg1, arg2)
     * </pre>
     * The syntax parts are separately represented in the XML, so the entire
     * function signature is divided, rather than simply removing the return
     * type.
     *
     * @param functionSignature The signature from the TLD file.
     * @return A map in which the keys are "name", "return", "parameterTypes"
     *         (a String array in which each member is a Java type), and "signature"
     *         (the function name and parameters without return type preceding).
     */
    private static HashMap parseFunctionSignature(String functionSignature)
    {
        HashMap signatureMap = new HashMap();
        String functionTypeAndName = functionSignature.substring(0, functionSignature.indexOf("("));
        StringTokenizer tokenizer = new StringTokenizer(functionTypeAndName, " ", false);
        String functionReturnType = tokenizer.nextToken().trim();
        String functionName = tokenizer.nextToken().trim();
        String signature = functionName + '(';
        String paramTypes = functionSignature.substring(functionSignature.indexOf("(") + 1,
                functionSignature.indexOf(")"));
        tokenizer = new StringTokenizer(paramTypes, " ", false);
        String[] params = new String[tokenizer.countTokens()];
        int i = 0;
        // Build the signature from the parts.
        while (tokenizer.hasMoreTokens())
        {
            params[i] = tokenizer.nextToken().trim();
            // In case there's no comma between arguments.
            String param = params[i].endsWith(",") ? 
                    params[i] + " " : params[i] + ", ";
            signature += (param);
            i++;
        }
        signature = signature.substring(0, signature.lastIndexOf(",")) + ')';
        signatureMap.put("name", functionName);
        signatureMap.put("return", functionReturnType);
        signatureMap.put("parameterTypes", params);
        signatureMap.put("signature", signature);
        return signatureMap;
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
        TldTaglibType tldTaglib = tldTaglibDoc.getTaglib();
        String tldUri = tldTaglib.getUri().getStringValue();
        String tldShortName = tldTaglib.getShortName().getStringValue();
        if (tldTaglib.sizeOfDisplayNameArray() > 0)
        {
            String tldDisplayName = tldTaglib.getDisplayNameArray(0).getStringValue();
            if (tldDisplayName != null)
            {
                xmlTaglibSummary.setDisplayName(tldDisplayName);
            }
        }
        SummaryUriType xmlSummaryUri = xmlTaglibSummary.addNewUri();
        xmlSummaryUri.setStringValue(tldUri);
        xmlSummaryUri.setUriPath(SeeTaglet.removeProtocolPrefix(tldUri) + '/');
        xmlTaglibSummary.setDocRoot(JavadocHandler.buildDocRoot(tldUri));
        xmlTaglibSummary.setPrefix(tldShortName);
        xmlTaglibSummary.setTaglibVersion(tldTaglib.getTlibVersion().toString());
        DescriptionType[] tldTaglibDescArray = tldTaglib.getDescriptionArray();
        if (tldTaglibDescArray != null)
        {
            for (int i = 0; i < tldTaglibDescArray.length; i++)
            {
                xmlTaglibSummary.addTldDescription(tldTaglibDescArray[i].getStringValue());
            }
        }
        if (tldTaglib.getValidator() != null)
        {
            com.sun.java.xml.ns.j2Ee.ValidatorType tldValidator = tldTaglib.getValidator();
            org.apache.beehive.netui.tools.doclet.schema.ValidatorType xmlValidator =
                    xmlTaglibSummary.addNewValidator();
            TypeInfoType xmlValidatorClass = xmlValidator.addNewValidatorClass();
            xmlValidatorClass.setFullName(tldValidator.getValidatorClass().getStringValue());
            if (tldValidator.getInitParamArray() != null)
            {
                ParamValueType[] tldInitParams = tldValidator.getInitParamArray();
                for (int i = 0; i < tldInitParams.length; i++)
                {
                    ParamValueType tldInitParam = tldInitParams[i];
                    InitParamType xmlInitParam = xmlValidator.addNewInitParam();
                    xmlInitParam.setParamName(tldInitParam.getParamName().getStringValue());
                    xmlInitParam.setParamValue(tldInitParam.getParamValue().getStringValue());
                    if (tldInitParam.getDescriptionArray() != null)
                    {
                        for (int j = 0; j < tldInitParam.getDescriptionArray().length; j++)
                        {
                            DescriptionType tldParamDesc = tldInitParam.getDescriptionArray(j);
                            xmlInitParam.addTldDescription(tldParamDesc.getStringValue());
                        }
                    }
                }
            }
        }
        return xmlTaglibSummary;
    }
}
