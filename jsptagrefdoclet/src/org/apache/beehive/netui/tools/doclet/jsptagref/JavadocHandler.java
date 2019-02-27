package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.*;

import org.apache.beehive.netui.tools.doclet.schema.*;
import org.apache.xmlbeans.XmlCursor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

/**
 * Provides methods for rendering information collected by Javadoc into
 * XML and merging this XML with XML collected from TLD files. This class
 * tries to do all of the Javadoc-related work that isn't specific
 * to self-contained Javadoc tags. In particular, this class generates
 * the XML for class descriptions, and methods (along with parameters and 
 * return value). It also knows how to find the taglet corresponding to 
 * a given Javadoc tag so that XML for that tag can be included also.
 * <p/>
 * The division of work for rendering XML from Javadoc wants a little explaining.
 * Taglets included with the doclet generate XML for self-contained tags --
 * that is, tags whose text contains all the information needed to 
 * generate the resulting XML. An example is the @link tag, whose text
 * includes both the descriptive information and the destination to link
 * to. The @param tag, however, includes only the name and description 
 * of the parameter; XML for a parameter includes this information, but also 
 * includes information about the parameter type:
 * <pre>
 * &lt;sch:method mod="public" signature="setOnClick(java.lang.String)" name="setOnClick">
 *     &lt;sch:parameter packagePath="http://java.sun.com/j2se/1.5/docs/api/java/lang/" inPackage="java.lang" dimension="" typeName="String" name="onclick">
 *         &lt;sch:description>
 *             &lt;sch:lead>onclick the onClick event.&lt;/sch:lead>
 *             &lt;sch:detail>the onClick event.&lt;/sch:detail>
 *         &lt;/sch:description>
 *     &lt;/sch:parameter>
 * &lt;method>
 * </pre>
 * In other words, a &lt;javadoc-tag> element is not generated for a few tags, such 
 * as @param, because the tag's information is merged into an XML shape that
 * is designed to be a little easier to read.
 */
final class JavadocHandler
{
    protected static final String REF_NAMESPACE =
        "http://apache.org/beehive/netui/tools/doclet/schema";
    private static JspTagContext m_context;

    /**
     * The "../" sequence that represents a path from the tag topic to the root
     * of the generate topic set.
     */
    private static String m_docRoot;

    private JavadocHandler()
    {
        super();
    }

    /**
     * Generates XML from Javadoc and merges the XML with <em>xmlJspTagDoc</em>, 
     * returning the combined result. Only Javadoc information for the JSP tag 
     * represented by <em>xmlJspTagDoc</em> is collected and merged.
     * <p/>
     * When this method is called, <em>xmlJspTagDoc</em> already contains TLD 
     * information about the JSP tag. This method uses that information to 
     * locate relevant Javadoc included in <em>jvdRootDoc</em>. If no relevant
     * information is found (for example, there's no Javadoc for the tag-class),
     * then the Javadoc isn't included in XML returned by the method.
     * 
     * @param jvdRootDoc   RootDoc with Javadoc information that could be added.
     * @param xmlJspTagDoc JSP tag XML to which Javadoc should be added.
     * @param context      Contextual Information about the JSP tag.
     *
     * @return XML representing the current JSP tag, including Javadoc information,
     *         if any.
     */
    static JspTagDocument addJavadocToJspTagXml(RootDoc jvdRootDoc,
                                                    JspTagDocument xmlJspTagDoc,
                                                    JspTagContext context)
    {
        m_context = context;
        JspTagDocument.JspTag xmlJspTag = xmlJspTagDoc.getJspTag();
        m_docRoot = buildDocRoot(xmlJspTag.getUri());
        AttributesType xmlAttributes = xmlJspTag.getAttributes();
        if (xmlAttributes != null)
        {
            AttributeType[] xmlAttributeArray = xmlAttributes.getAttributeArray();

            // For each of the attributes defined in the TLD, build new XML.
            for (int i = 0; i < xmlAttributeArray.length; i++)
            {
                AttributeType xmlAttribute = xmlAttributeArray[i];
                JspTagDetail.JspTagAttribute attributeDetail =
                        m_context.getCurrentTagDetail().getAttribute(xmlAttribute.getName());

                // Use the getter and setter info to build XML about the attribute.
                MethodDoc jvdSetterMethod = attributeDetail.getSetter();
                MethodDoc jvdGetterMethod = attributeDetail.getGetter();
                if (jvdSetterMethod != null)
                {
                    xmlAttribute.setAccessors(generateAccessorsXml(jvdGetterMethod, jvdSetterMethod));
                }
                // Build XML for the attribute's Java type.
                ClassDoc jvdAttributeType = JavadocHandler.findJavadocForClass(jvdRootDoc,
                        xmlAttribute.getType().getFullName());
                if (jvdAttributeType != null)
                {
                    xmlAttribute.setType(generateTypeInfoXml(jvdAttributeType));
                }
            }
        }
        // Build XML for the tag-class.
        if (m_context.getHandlerClass() != null)
        {
            xmlJspTag.setHandlerClass(generateTypeInfoXml(m_context.getHandlerClass()));
        }
        // Build XML for the tei-class.
        if (m_context.getTeiClass() != null)
        {
            xmlJspTag.setTeiClass(generateTypeInfoXml(m_context.getTeiClass()));
        }
        return xmlJspTagDoc;
    }

    /**
     * Generates XML from Javadoc about attribute accessor methods,
     * returning the XML for use in XML about the JSP tag.
     *
     * @param jvdGetterMethod The attribute's getter as a Javadoc type.
     * @param jvdSetterMethod The attribute's jvdSetter as a Javadoc type.
     * @return The attribute XML as an XMLBeans type.
     */
    private static MethodsType generateAccessorsXml(MethodDoc jvdGetterMethod,
                                                    MethodDoc jvdSetterMethod)
    {
        MethodsType xmlAccessors = null;
        if (jvdSetterMethod != null || jvdGetterMethod != null)
        {
            xmlAccessors = MethodsType.Factory.newInstance();

            // If there's a setter, add XML for it.
            if (jvdSetterMethod != null)
            {
                final MethodType xmlSetterMethod = xmlAccessors.addNewMethod();
                xmlSetterMethod.set(generateSingleMethodXml(jvdSetterMethod));
            }
            // If there's a getter, add XML for it.
            if (jvdGetterMethod != null)
            {
                final MethodType xmlGetterMethod = xmlAccessors.addNewMethod();
                xmlGetterMethod.set(generateSingleMethodXml(jvdGetterMethod));
            }
        }
        // Return the new XML.
        return xmlAccessors;
    }

    /**
     * Returns XML for the Javadoc tag represented by <em>jvdTag</em>.
     * This method uses the {@link JspTagContext#processJavadocTag(Tag, JspTagContext)}
     * method to get the XML corresponding to the tag.
     *
     * @param jvdTag The Javadoc tag to generate XML for.
     * @return The Javadoc tag XML as an XMLBeans type.
     */
    private static JavadocTagType generateJavadocTagXml(Tag jvdTag)
    {
        return m_context.processJavadocTag(jvdTag, m_context);
    }

    /**
     * Generates XML for inline Javadoc tags, such as those found in 
     * a class or method description; "inline tags" includes both
     * the text and Javadoc tags (such as @link) in the description.
     * 
     * @param jvdInlineTags The tags to render XML for.
     * @return The generated XML.
     */
    static TextAndTagsType generateInlineTagsXml(Tag[] jvdInlineTags)
    {
        TextAndTagsType xmlTagContent = TextAndTagsType.Factory.newInstance();
        XmlCursor cursor = xmlTagContent.newCursor();
        XmlCursor tagCursor = null;
        cursor.toNextToken();
        for (int i = 0; i < jvdInlineTags.length; i++)
        {
            Tag jvdTag = jvdInlineTags[i];
            if (jvdTag.kind().equals("Text"))
            {
                String text = jvdTag.text();
                cursor.insertChars(text);
            } else
            {
                JavadocTagType xmlJavadocTagContent = m_context.processJavadocTag(jvdTag, m_context);
                if (xmlJavadocTagContent != null)
                {
                    JavadocTagType xmlJavadocTag = xmlTagContent.addNewJavadocTag();
                    xmlJavadocTag.set(xmlJavadocTagContent);
                    tagCursor = xmlJavadocTag.newCursor();
                    tagCursor.moveXml(cursor);
                } else
                {
                    cursor.insertChars('{' + jvdTag.name() + ' ' + jvdTag.text() + '}');
                }
            }
        }
        if (tagCursor != null)
        {
            tagCursor.dispose();
        }
        return xmlTagContent;
    }

    /**
     * Builds a sequence of "../" for the first part of a relative path up
     * to the top of the Javadoc run (the "doc root"). The sequence is built 
     * using the tag library's URI, such as "http://www.mydomain.com/foo/bartags".
     * Forward slashes are considered path delimiters, and the values
     * between are considered directories and each replaced with "..".
     *
     * @param rawPath The URI from which to build a path up to the doc root.
     * @return A sequence of "../".
     */
    public static String buildDocRoot(String rawPath)
    {
        URI uri = null;
        try
        {
            uri = new URI(rawPath);
        } catch (URISyntaxException urise)
        {
            m_context.getErrorReporter().printError("Error while generating the " +
                    "doc root from a taglib URI: \n" +
                    urise.getLocalizedMessage());
        }
        String tagLibPath = uri.getPath() + '/';
        // Sometimes the URI doesn't include a host.
        if (uri.getHost() != null)
        {
            tagLibPath = uri.getHost() + tagLibPath;
        }
        final StringBuffer delimiters = new StringBuffer();
        final StringBuffer cleanString = new StringBuffer();
        for (int i = 0; i < tagLibPath.length(); i++)
        {
            if (tagLibPath.charAt(i) != '/')
            {
                delimiters.append(tagLibPath.charAt(i));
            }
        }
        final StringTokenizer tokens = new StringTokenizer(tagLibPath, delimiters.toString());
        while (tokens.hasMoreTokens())
        {
            cleanString.append(".." + tokens.nextToken());
        }
        final String path = cleanString.toString();
        return path.substring(0, path.lastIndexOf("/"));
    }

    /**
     * Builds a path from the topic for the current tag to the class represented
     * by <em>jvdDestClass</em>. In the case of a link to an external class
     * (such as something at the Sun web site) this will be a full
     * URL returned by the {@link Linker} class. In the case of an internal
     * link, it will be a relative path -- up to the top of this Javadoc
     * run (via the doc root built by {@link #buildDocRoot}) and down to the
     * destination class via package hierarchy.
     *
     * @param jvdDestClass The class the link points to.
     * @return A URL or relative path.
     */
    private static String getPackagePath(ClassDoc jvdDestClass)
    {
        String packagePath = null;
        String packageName = jvdDestClass.containingPackage().name();
        if (jvdDestClass.containingPackage() != null)
        {
            if (m_context.isItemExternal(packageName))
            {
                packagePath = m_context.getExternalLinkPath(packageName);
                if (!m_context.isItemAbsolute(packageName))
                {
                    packagePath = m_docRoot + '/' + packagePath;
                }
            }
        }
        return packagePath;
    }

    /**
     * Generates XML for the <em>jvdType</em> Java type. The type might
     * be a tag class, a TEI class, a return type, etc. -- in other words, 
     * something that Javadoc has information about.
     * <p/>
     * Note that the returned XML might end up in one several elements, including
     * &lt;handler-class> or &lt;enclosing-type>, depending on how the 
     * caller uses the return value.
     *
     * @param jvdType The Java type for which XML should be generated.
     * @return The generated XML as an XMLBeans type.
     */
    private static TypeInfoType generateTypeInfoXml(Type jvdType)
    {
        TypeInfoType xmlTypeInfo = TypeInfoType.Factory.newInstance();
        xmlTypeInfo.setFullName(jvdType.qualifiedTypeName());
        xmlTypeInfo.setTypeName(jvdType.typeName());
        String dimension = jvdType.dimension();
        if (!dimension.equals(""))
        {
            xmlTypeInfo.setDimension(dimension);
        }
        ClassDoc jvdObjectType = jvdType.asClassDoc();
        if (jvdObjectType != null)
        {
	        xmlTypeInfo.setFullName(jvdObjectType.containingPackage().name() + '.' +
	                jvdObjectType.name());	
	        if (getPackagePath(jvdObjectType) != null)
	        {
	            xmlTypeInfo.setPackagePath(getPackagePath(jvdObjectType));
	        }
	        xmlTypeInfo.setInPackage(jvdObjectType.containingPackage().name());
	        xmlTypeInfo.setTypeName(jvdObjectType.name());
	        if (jvdObjectType.inlineTags().length > 0)
	        {
	            Tag[] inlineTags = jvdObjectType.inlineTags();
	
	            // Add a new <description> element to the newly generated XML,
	            // building its content (<lead> and <detail> elements) from
	            // the inline tags.
	            DescriptionType xmlDescription = xmlTypeInfo.addNewDescription();
	            TextAndTagsType xmlDetail = xmlDescription.addNewDetail();
	            xmlDetail.set(generateInlineTagsXml(inlineTags));
	            inlineTags = jvdObjectType.firstSentenceTags();
	            TextAndTagsType xmlLead = xmlDescription.addNewLead();
	            xmlLead.set(generateInlineTagsXml(inlineTags));
	        }
	        if (jvdObjectType.tags().length > 0)
	        {
	            JavadocTagsType xmlJavadocTags = xmlTypeInfo.addNewJavadocTags();
	            for (int i = 0; i < jvdObjectType.tags().length; i++)
	            {
	                Tag jvdTag = jvdObjectType.tags()[i];
	                JavadocTagType xmlJavadocTagContent = generateJavadocTagXml(jvdTag);
	                if (xmlJavadocTagContent != null)
	                {
	                    JavadocTagType xmlJavadocTag = xmlJavadocTags.addNewJavadocTag();
	                    xmlJavadocTag.set(xmlJavadocTagContent);
	                }
	            }
	            // If there were Javadoc tags, but none resulted in XML,
	            // remove the <javadoc-tags> element.
	            if (xmlJavadocTags.sizeOfJavadocTagArray() == 0)
	            {
	                xmlTypeInfo.unsetJavadocTags();
	            }
	        }
        }
        return xmlTypeInfo;
    }

    /**
     * Generates &lt;method> element XML representing <em>jvdMethod</em>. This is
     * used to generate the &lt;method> elements containing information about an
     * attribute accessor. This method is called for each attribute accessor found.
     * The doclet generates XML only for methods corresponding to JSP tag attributes.
     *
     * @param jvdMethod The Javadoc type representing the method to be described
     *                  by the XML.
     * @return The &lt;method> element as an XMLBeans type.
     */
    private static MethodType generateSingleMethodXml(MethodDoc jvdMethod)
    {
        MethodType xmlMethod = MethodType.Factory.newInstance();
        if (jvdMethod != null)
        {
            // Add XML about inline tags, such as the method's description.
            if (jvdMethod.inlineTags().length > 0)
            {
                DescriptionType xmlDescription = xmlMethod.addNewDescription();

                // Generate XML for the <lead> and <detail> elements.
                Tag[] firstSentenceTags = jvdMethod.firstSentenceTags();
                TextAndTagsType xmlLead = xmlDescription.addNewLead();
                xmlLead.set(generateInlineTagsXml(firstSentenceTags));
                Tag[] inlineTags = jvdMethod.inlineTags();
                TextAndTagsType xmlDetail = xmlDescription.addNewDetail();
                xmlDetail.set(generateInlineTagsXml(inlineTags));
            }
            // Set attribute values.
            xmlMethod.setMod(jvdMethod.modifiers());
            xmlMethod.setSignature(jvdMethod.name() + jvdMethod.signature());
            xmlMethod.setName(jvdMethod.name());

            // Generate XML for the <enclosing-type> element.
            if (jvdMethod.containingClass() != null)
            {
                EnclosureType containingType = xmlMethod.addNewEnclosingType();
                containingType.setInPackage(jvdMethod.containingClass().containingPackage().name());
                containingType.setMod(jvdMethod.containingClass().modifiers());
                containingType.setTypeName(jvdMethod.containingClass().name());
                containingType.setTypeKind(jvdMethod.containingClass().isInterface() ? "interface" : "class");
                if (getPackagePath(jvdMethod.containingClass()) != null)
                {
                    containingType.setPackagePath(getPackagePath(jvdMethod.containingClass()));
                }
            }

            // Generate XML for the <returns> element (including @return).
            TypeInfoType xmlReturns = xmlMethod.addNewReturns();
            Type jvdReturnType = jvdMethod.returnType();
            Tag returnTag = jvdMethod.tags("@return").length > 0 ? jvdMethod.tags("@return")[0] : null;
            xmlReturns.set(generateReturnsXml(jvdReturnType, returnTag));

            // Generate XML for <parameter> elements (including @param tags), if any.
            if (jvdMethod.parameters().length > 0)
            {
                Parameter[] jvdParamArray = jvdMethod.parameters();
                for (int j = 0; j < jvdParamArray.length; j++)
                {
                    ParameterType xmlParameter = xmlMethod.addNewParameter();
                    xmlParameter.set(generateParamXml(jvdParamArray[j], jvdMethod.paramTags()));
                }
            }
            // Generate XML for Javadoc tags (except @param, @return, and @throws,
            // whose information is stored elsewhere in the XML. See doc for this
            // class).
            Tag[] tags = jvdMethod.tags();
            if (tags.length > 0)
            {
                JavadocTagsType xmlJavadocTags = xmlMethod.addNewJavadocTags();
                for (int i = 0; i < tags.length; i++)
                {
                    Tag jvdTag = tags[i];
                    JavadocTagType xmlContent = generateJavadocTagXml(jvdTag);
                    if (xmlContent != null)
                    {
                        JavadocTagType xmlJavadocTag = xmlJavadocTags.addNewJavadocTag();
                        xmlJavadocTag.set(xmlContent);
                    }
                }
                if (xmlJavadocTags.sizeOfJavadocTagArray() == 0)
                {
                    xmlMethod.unsetJavadocTags();
                }
            }
            // Generate XML for the <throws> element (included @throws).
            if (jvdMethod.thrownExceptions().length > 0)
            {
                ClassDoc[] jvdExceptionArray = jvdMethod.thrownExceptions();
                for (int j = 0; j < jvdExceptionArray.length; j++)
                {
                    ClassDoc jvdException = jvdExceptionArray[j];
                    ThrowsType xmlThrows = generateThrowsXml(jvdException, jvdMethod.throwsTags());
                    if (xmlThrows != null)
                    {
                        ThrowsType xmlMethThrows = xmlMethod.addNewThrows();
                        xmlMethThrows.set(xmlThrows);
                    }
                }
            }
        }
        return xmlMethod;
    }
    
    /**
     * Generates XML for a method's return type.
     * 
     * @param jvdReturnType The method's return type.
     * @param returnTag The @return tag, if any; null if none.
     * @return A &lt;returns> element representing the return type.
     */
    private static TypeInfoType generateReturnsXml(Type jvdReturnType,
            Tag returnTag)
    {
            TypeInfoType xmlReturns = generateTypeInfoXml(jvdReturnType);
        if (returnTag != null)
        {
            DescriptionType xmlDescription = xmlReturns.addNewDescription();
            xmlDescription.set(generateJavadocTagDescription(returnTag));
        }
        return xmlReturns;
    }

    /**
     * Generates XML for the &lt;parameter> element, merging information about
     * the parameter type with information from the corresponding @param tag.
     * 
     * @param jvdParameter The parameter to generate XML for.
     * @param paramTags The @param tags on the method.
     * @return A &lt;parameter> element representing <em>jvdParameter</em>.
     */
    private static ParameterType generateParamXml(Parameter jvdParameter, ParamTag[] paramTags)
    {
        ParameterType xmlParameter = ParameterType.Factory.newInstance();
        String inPackageName;
        if (jvdParameter.type().asClassDoc() != null)
        {
            inPackageName =
                    jvdParameter.type().asClassDoc().containingPackage().name();
            ClassDoc jvdParameterType = jvdParameter.type().asClassDoc();
            String packagePath = getPackagePath(jvdParameterType == null ? jvdParameter.type().asClassDoc() : jvdParameterType);
            if (packagePath != null)
            {
                xmlParameter.setPackagePath(packagePath);
            }
            xmlParameter.setInPackage(inPackageName);
        }
        String dimension = jvdParameter.type().dimension();
        if (!dimension.equals(""))
        {
            xmlParameter.setDimension(dimension);
        }
        xmlParameter.setTypeName(jvdParameter.type().typeName());
        xmlParameter.setName(jvdParameter.name());
        for (int i = 0; i < paramTags.length; i++)
        {
            ParamTag paramTag = paramTags[i];
            if (xmlParameter.getName().equals(paramTag.parameterName()))
            {
                DescriptionType xmlDescription = xmlParameter.addNewDescription();
                XmlCursor valueCursor = xmlDescription.addNewLead().newCursor();
                valueCursor.toEndToken();
                valueCursor.insertChars(paramTag.parameterComment());
                valueCursor = xmlDescription.addNewDetail().newCursor();
                valueCursor.toEndToken();
                valueCursor.insertChars(paramTag.parameterComment());
                valueCursor.dispose();
            }
        }
        return xmlParameter;
    }
    
    /**
     * Generates XML for a &lt;throws> element.
     *
     * @param jvdException      The exception thrown.
     * @param jvdThrowsTagArray An array of @throws tags from the method.
     * @return The &lt;throws> element as an XMLBeans type.
     */
    private static ThrowsType generateThrowsXml(ClassDoc jvdException,
                                                ThrowsTag[] jvdThrowsTagArray)
    {
        ThrowsType xmlThrows = ThrowsType.Factory.newInstance();
        if (jvdException.asClassDoc() != null)
        {
            String packagePath = getPackagePath(jvdException.asClassDoc());
            if (packagePath != null)
            {
                xmlThrows.setPackagePath(packagePath);
            }
        }
        xmlThrows.setInPackage(jvdException.containingPackage().name());
        xmlThrows.setTypeName(jvdException.typeName());
        for (int i = 0; i < jvdThrowsTagArray.length; i++)
        {
            if (jvdThrowsTagArray[i].exceptionName().equals(jvdException.name()))
            {
                Tag[] jvdInlineTagArray = jvdThrowsTagArray[i].inlineTags();
                xmlThrows =
                        (ThrowsType) generateInlineTagsXml(jvdInlineTagArray);
            }
        }
        return xmlThrows;
    }

    /**
     * Generates XML for a function listed in a TLD.
     *
     * @param xmlFunctionDoc The function XML with information collected from the
     *                       TLD.
     * @param jvdRootDoc     The root doc received from Javadoc.
     * @return A &lt;function> element representing <em>xmlFunctionDoc</em>.
     */
    static FunctionDocument addJavadocToFunctionXml(FunctionDocument xmlFunctionDoc,
                                                        RootDoc jvdRootDoc)
    {
        FunctionDocument.Function xmlFunction = xmlFunctionDoc.getFunction();
        m_docRoot = buildDocRoot(xmlFunction.getUri());
        ClassDoc jvdFunctionClass =
                jvdRootDoc.classNamed(xmlFunction.getFunctionClass().getFullName());
        if (jvdFunctionClass != null)
        {
            xmlFunction.setFunctionClass(generateTypeInfoXml(jvdFunctionClass));
            MethodType xmlFunctionMethod = xmlFunction.getFunctionSupportMethod();
            String xmlFunctionMethodSig = xmlFunctionMethod.getSignature();
            MethodDoc jvdFunctionMethod = findMethodInType(xmlFunctionMethodSig,
                    jvdFunctionClass);
            if (jvdFunctionMethod != null)
            {
                xmlFunctionMethod.set(generateSingleMethodXml(jvdFunctionMethod));
            }
        }
        return xmlFunctionDoc;
    }

   /**
    * Generates the contents of a &lt;description element from the 
    * description content of the <em>jvdTag</em> Javadoc tag.
    *
    * @param jvdTag The tag whose text should be rendered into a &lt;description>
    *               element.
    * @return A &lt;description> element representing the <em>jvdTag</em> tag's
    * description content.
    */
   private static DescriptionType generateJavadocTagDescription(Tag jvdTag)
   {
       DescriptionType xmlDescription = DescriptionType.Factory.newInstance();
       Tag[] jvdInlineTagArray = jvdTag.inlineTags();
       if (jvdInlineTagArray.length > 0)
       {
           Tag[] firstSentenceTags = jvdTag.firstSentenceTags();
           TextAndTagsType xmlLead = xmlDescription.addNewLead();
           xmlLead.set(generateInlineTagsXml(firstSentenceTags));
           TextAndTagsType xmlDetail = xmlDescription.addNewDetail();
           xmlDetail.set(generateInlineTagsXml(jvdInlineTagArray));
       }
       return xmlDescription;
   }

   /**
     * Finds the <em>xmlMethodSignature</em> method in the <em>jvdClass</em>
     * class.
     *
     * @param xmlMethodSignature The method to look for.
     * @param jvdClass           The class to look in.
     * @return The method in Javadoc terms.
     */
    private static MethodDoc findMethodInType(String xmlMethodSignature, ClassDoc jvdClass)
    {
        final MethodDoc[] jvdMethods = jvdClass.methods();
        MethodDoc jvdMethod = null;
        for (int i = 0; i < jvdMethods.length; i++)
        {
            final String jvdMethodSignature = jvdMethods[i].name() + jvdMethods[i].signature();
            if (jvdMethodSignature.equals(xmlMethodSignature))
            {
                jvdMethod = jvdMethods[i];
                break;
            }
        }
        return jvdMethod;
    }

	/**
	 * Locates the specified tag handler class in the information collected by
	 * Javadoc.
	 *
	 * @param jvdRootDoc The information collected by Javadoc for this run.
	 * @param className The class to look for.
	 * @return Javadoc information about the specified class; <code>null</code>
	 * if the class isn't in scope for this run.
	 */
	static ClassDoc findJavadocForClass(RootDoc jvdRootDoc, String className)
	{
	    ClassDoc jvdClass = jvdRootDoc.classNamed(className);
	    if (jvdClass != null)
	    {
	        return jvdClass;
	    } else
	    {
	        return null;
	    }
	}
}