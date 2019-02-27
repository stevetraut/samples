# JspTagDoc doclet

JspTagDoc didn't start out as a sample. It's a tool I wrote where there was a need to create references for a large number of custom [JavaServer Pages (JSP)](https://en.wikipedia.org/wiki/JavaServer_Pages) tags. JSP tags are a way to encapsulate functionality for use in JSP files.

JspTagDoc is a Javadoc doclet that generates documentation for JSP tags. Unlike Javadoc's standard doclet, which generates docs for classes and interfaces, JspTagDoc generates docs that are designed specifically for use with tags. 

So just as the tags themselves are designed to mask the underlying Java implementation in favor of a declarative model that is better suited for JSP authoring, so JspTagDoc generates docs that mask the underlying API details in favor of tags, attributes and a declarative syntax that is better suited for JSP authors. An underlying assumption in the design and use of JSP tags seems to be that users may or may not be very Java API-savvy; JspTagDoc output is based on the same assumption.

## So What's the Difference?

For a more concrete sense of the difference, take a brief look at the specifics of doclet output while imagining you're a JSP tag user immersed in that (largely) declarative programming model. Looking for information on the `<foo>` tag, which you think will achieve your programming ends, you can find only Javadoc for the Foo class that provides the `<foo>` tag's underlying logic.

What you see there is the `Foo` class hierarchy, maybe something like this:

```
Object
  - TagLogic
    - BarTagLogic
      - AbstractBarTag
        - Foo
```

But the hierarchy is probably irrelevant to use of the <foo> tag. You might also find the list of interfaces implemented by the Foo class, as well as any subclasses of the Foo class -- both of which are also irrelevant.

The syntax line you see is Java syntax, which is also unuseful:

```
public class Foo 
	extends AbstractBarTag
```

The description you get is for the Foo class, which needn't say much about the <foo> tag -- in fact, it may well give a fair amount of information about the Foo class's role in the API, or implementation information, rather than  meaty specifics about the <foo> tag's use. 

Your "See Also" links probably take you to other classes in the API, some of which may not correspond to JSP tags at all, or which are in parts of the API that aren't even available to you as a JSP tag user.

For the Foo class, you'll get lists of fields whose connection to the <foo> tag may take some work on your part to make. You'll get a mention of a constructor, which will probably be irrelevant. You'll get reference for methods that seem to correspond to the <foo> tags attributes -- if you remove the accessor prefix, such as "get", "set", "is", or "has" -- but only if those methods are implemented in the current class; if the method is inherited, you'll have to go elsewhere.

Perhaps most annoying of all, you might get reference for methods that seem to have no connection to your <foo> tag at all (unless you've written JSP tags yourself, which is unlikely): doAfterBody, doEndTag, doStartTag, and so on.

If the Foo class's Javadoc has been written just so, you might be able to come away with something useful. If you're diligent, you might be able to figure out that the setBiff method has information about the <foo> tag's biff attribute that you can use. But you'll have had to sift through all of the other irrelevant information to get there -- and bend your brain around the job of smaking the translation from JSP to Java. You'll have effectively unimplemented the <foo> tag, defeating a goal of implementing <foo> as a JSP tag.

On the other hand, documentation tailored to help you be successful with the <foo> tag might include at least the following:

- The name of the <foo> tag as a title.
- Tag syntax in the declarative style, perhaps like this:

	```xml
	<baz:foo
		biff="biffDescriptor"
		bam="bamDescriptor"
		[optionalBlammo="blammoDescriptor"] 
		isBoffo="true | false"/>
	```

- A description specifically about the <foo> tag. This might include information about nested tags, or required parent tags.
- Descriptions for each of the tag's attributes as attributes, rather than class methods.
- "See Also" links to take you to related tags in the library, or to other docs relevant to your task at hand: programming with the <foo> tag in the declarative JSP style.
- Content presented in a style that complements Javadoc for classes (similar navigation and presentation conventions, for example); if you do move between the two types of references, the transition won't be jarring.

These last are the reference characteristics that JspTagDoc attempts to incorporate.

## Doclet Features

In addition to its similarity to standard API-oriented Javadoc output, this doclet does the following:

- Supports content from TLD files, Javadoc comments, and both.
- Supports Javadoc content from standard Javadoc tags, from custom "jsptagref" Javadoc tags, and both.
- Generates XML or HTML output, or both.
- Creates a uri-list file for linking to external references of the sort generated by this doclet (similar to a package-list file).

## How the Doclet Works

JspTagDoc combines content from tag library descriptor (TLD) files and content collected from tag classes by Javadoc into a single XML shape, then transforms that XML into HTML output. As with other Javadoc doclets, you use this one by specifying it with the -doclet and -docletpath Javadoc options. In addition, you provide a -tldpath option that tells the doclet where to find TLD files for tag libraries to be documented.

1. The doclet's central class is JspTagDoc. After evaluating command-line options and initializing taglets (ConfigurationJspTagDoc), the doclet begins by binding the TLD files to XMLBeans types generated from schema. 
2. It then copies information from these types into a new XML shape that is bound to types generated from another schema (TaglibHandler* classes). This generates one XML document for each JSP tag and function. 
3. When information from all of the TLD files has been collected, the doclet uses information in the new XML to locate tag classes and attribute accessor methods from among the classes known to Javadoc.
4. Where supporting classes are found, it copies information collected by Javadoc into the XML that already includes TLD information (using JavadocHandler). The doclet adds the completed XML for each JSP tag and function to a summary XML document.
5. Along the way, several utility classes are used to keep track of all the JSP tags known to the current run (these are JspTagContext, JspTagDetail, and Linker). These are used to create links and to provide information needed by taglets (in the org.apache.beehive.netui.tools.doclet.taglets package).
6. When the XML has been generated, the doclet optionally transforms it to HTML (using JspTagDocTransformer). Again, it generates one file for each tag and function, along with several summary-style files.

(Note on generating docs from TLD content only. This is a little tricky because Javadoc expects a doclet to always be using some path to Java source for content. When your JspTagDoc run will not include Java sources, specify a Java source path that has no Java source files. The doclet always requires a -tldpath option pointing to TLD files.)

## Note About the Schemas

This doclet uses XMLBeans types compiled from schemas in order to handle XML with XMLBeans. With the exception of the schemas that support version 2.0 tag libraries, these schemas have been created solely for use with the doclet. For example, the schemas that describe version 1.0 and 1.1 libraries were created for use with the doclet (they're not official descriptors of tag library TLD XML). What's more, the schemas are, to date, useful only for generating the XMLBeans types needed by the doclet -- XML generated by the doclet does not necessarily validate against the schemas.

## New Options Supported by this Doclet

`-tldpath path/to/tld/file.tld;path/to/tld/directory`

Specifies a path to TLD files listing tags to document.
This path can be a combination of TLD files and directories (in which
case all TLD files in the directory will be used).

`-xmldir path/to/xml/output/directory`

Specifies the directory to which generated XML should be
written. Omit this option if you don't want XML.

`-contentsource [jsptagref;][tld;][standard]`

Description: Specifies the documentation content sources to use in 
generated docs. The doclet assumes a priority order of 1) "jsptagref" for
the content of @jsptagref.* Javadoc tags; 2) "tld" for content in TLD
files; and 3) "standard" for standard Javadoc documentation content. In 
other words, all three arguments are used, the doclet will look for 1 first;
if it's not found, it will look for 2; if neither 1 or 2 is found, the 
doclet will look for 3. (Note that the order of the arguments is irrelevant.)

`-nolinkconversion`

Description: By default, when generating HTML output the doclet will 
convert links to tag handler classes into links to the generated tag topic.
This option cancels this behavior.
  
`-notaginfo`

Description: Specifies that HTML output should not include "Tag Information"
and "Library Information" sections.

## Standard Javadoc Options *Not* Supported by this Doclet

In some cases, support is omitted because it is irrelevant for JSP tags; in some cases, it simply hasn't been implemented yet.

```
-charset
-docencoding
-docfilessubdirs
-excludedocfilessubdir
-group
-helpfile
-linksource
-nocomment
-nodeprecated
-nodeprecatedlist
-nohelp
-noqualifier
-notree
-serialwarn
-splitindex
-stylesheetfile
-taglet
-tagletpath
-title
-use
-version
```

## Javadoc Tags Introduced by this Doclet

The doclet provides support for several custom Javadoc tags that are designed
to help in authoring JSP-context-specific JSP tag documentation. 

`@jsptagref.tagdescription Text describing the JSP tag.`

- Description: Similar to the description text that precedes a class declaration. The content should be a tag-user-friendly description of  the tag for which the class is a tag handler.
- Location: After the class description Javadoc (if any) preceding the class declaration.

`@jsptagref.attributedescription Text describing the tag attribute.`

- Description: Similar to the description text that precedes an attribute setter method. The content should be a tag-user-friendly description of of the attribute for which the setter method is an accessor.
- Location: After the setter method description (if any) preceding the method code.

`@jsptagref.attributesyntaxvalue concatenatedTextToUseInSyntax.`

- Description: The placeholder value that should appear between the equals signs for the attribute in a syntax block.
- Location: After the setter method description (if any) preceding the method code.

`{@jsptagref.taglink jspTagName@jspTagLibraryUri}`

- Description: An inline tag similar to the @link tag, but for linking to topics such as those generated by this doclet. 
- Location: In the body of a @jsptagref.tagdescription tag. Note that this tag should not be used in a standard Javadoc comment unless the user explicitly adds support for it.
 
`@jsptagref.databindable true`

- Description: Used to indicate whether an attribute is databindable in the Beehive netui sense.
- Location: After the setter method description (if any) preceding the method code.

`@jsptagref.see ClassName | URL`

- Description: Similar to the @see tag. Used when the link is not relevant or available in the context of standard doclet output.
- Location: The same as the @see tag. 

## Conventions Used in the Source

The Java source code uses a variable prefix convention to disambiguate between variables that represent Javadoc types (jvd), generated XML (xml), and TLD XML (tld). Examples include:

- jvdAttributeType, for the Javadoc type representing a JSP attribute type (it might be String, int, or some such underneath)
- xmlAttribute, for the schema type to which generated XML for an attribute is bound while the doclet is generating XML.
- tldAttribute, for the schema type to which TLD XML for an attribute is bound while the doclet is copying TLD info info into new XML.