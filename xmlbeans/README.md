# XMLBeans

This directory contains samples from the [XMLBeans project](https://xmlbeans.apache.org/index.html). 

XMLBeans is a Java-based technology for accessing XML structure and data. Using XMLBeans, you can compile an XML schema to generate Java classes that have accessors for working with an XML instance bound to the generated classes. For access to XML without schema, XMLBeans provides a cursor for traversing an XML instance.

XMLBeans was created by engineers at BEA Systems. I wrote samples and [documentation](https://xmlbeans.apache.org/documentation/index.html) for XMLBeans while there.

The samples included here are only the subset that I wrote. You'll find more at the [Apache XMLBeans site](https://xmlbeans.apache.org/index.html).

Directory | Description
--------- | -----------
[Any](Any) | Illustrates how you can use the XMLBeans API to work with XML based on schema that features `xs:any` particles.
[MixedContent](MixedContent) | Illustrates how to work with mixed content XML by combining strong types generated from schema with an XmlCursor instance.
[SchemaEnum](SchemaEnum) | Illustrates how to access XML values that are defined in schema as enumerations.
[Validation](Validation) | Illustrates how to use the XMLBeans API to validate XML instances against schema.
[XmlTree](XmlTree) | Illustrates how to use the XMLBeans API to create a Java tree view of an XML document.
[XQueryXPath](XQueryXPath) | Illustrates how you can use the XMLBeans API to execute XPath and XQuery expressions.