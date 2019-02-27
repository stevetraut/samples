<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema" xmlns:xalan="http://xml.apache.org/xslt">
    <!--    <xsl:import href="header_footer.xslt"/> -->
    <!-- Content source options -->
    <xsl:param name="taglibLocation"/>
    <xsl:param name="jsptagref"/>
    <xsl:param name="tld"/>
    <xsl:param name="standard"/>
    <!-- Javadoc options (mirroring the standard doclet) -->
    <xsl:param name="docTitle"/>
    <xsl:param name="windowTitle"/>
    <xsl:param name="author"/>
    <xsl:param name="footer"/>
    <xsl:param name="header"/>
    <xsl:param name="bottom"/>
    <xsl:param name="helpfile"/>
    <xsl:param name="styleSheetFile"/>
    <xsl:param name="keywords"/>
    <xsl:param name="noIndex"/>
    <xsl:param name="noNavbar"/>
    <xsl:param name="noSince"/>
    <xsl:param name="noTimeStamp"/>
    <xsl:param name="noTagInfo"/>
    <!--=============================================================== 
     Makes the individual tag and function topics.
    ================================================================-->
    <xsl:template match="/">
        <xsl:variable name="doc-root" select="//@docRoot"/>
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="{$doc-root}/stylesheet.css" title="Style"/>
                <xsl:variable name="item-name">
                    <xsl:choose>
                        <xsl:when test="ref:jsp-tag">
                            <xsl:value-of select="ref:jsp-tag/ref:prefix"/>:<xsl:value-of
                            select="ref:jsp-tag/ref:name"/> Tag</xsl:when>
                        <xsl:when test="ref:function">
                            <xsl:value-of select="ref:function/ref:prefix"/>:<xsl:value-of
                            select="ref:function/ref:name"/> Function</xsl:when>
                    </xsl:choose>
                </xsl:variable>
                <title>
                    <xsl:value-of select="$item-name"/>
                    <xsl:if test="string($windowTitle)"> (<xsl:value-of select="$windowTitle"/>)</xsl:if>
                </title>
                <xsl:element name="meta">
                    <xsl:attribute name="name">keywords</xsl:attribute>
                    <xsl:attribute name="content">
                        <xsl:value-of select="$item-name"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:if test="$keywords = 'true'">
                    <xsl:for-each select="//ref:attribute">
                        <xsl:element name="meta">
                            <xsl:attribute name="name">keywords</xsl:attribute>
                            <xsl:attribute name="content">
                                <xsl:value-of select="ref:name"/> Attribute</xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                    <xsl:element name="meta">
                        <xsl:attribute name="name">keywords</xsl:attribute>
                        <xsl:attribute name="content">
                            <xsl:value-of select="node()/ref:name"/> Tag</xsl:attribute>
                    </xsl:element>
                </xsl:if>
                <script type="text/javascript"> function windowTitle() {
                        parent.document.title="<xsl:value-of select="$item-name"/>"; }</script>
            </head>
            <body onload="windowTitle();">
                <!-- NAVBAR Section (the navbar template is in the file header_footer.xslt) -->
                <xsl:if test="$noNavbar = 'false'">
                    <xsl:call-template name="navbar">
                        <xsl:with-param name="relPathToRoot" select="concat(//@docRoot, '/')"/>
                        <xsl:with-param name="cornerTitle" select="$header"/>
                        <xsl:with-param name="displayDetails" select="'true'"/>
                    </xsl:call-template>
                    <hr/>
                </xsl:if>
                <!-- JSP TAG Section (styles XML for a tag topic) -->
                <xsl:for-each select="ref:jsp-tag">
                    <!-- Main title for the topic -->
                    <h2>
                        <font size="-1">
                            <xsl:element name="a">
                                <xsl:attribute name="href">taglib-summary.html</xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="string(ref:display-name)">
                                        <xsl:value-of select="ref:display-name"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="ref:prefix"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:element>
                        </font>
                        <br/>
                        <xsl:value-of select="ref:prefix"/>:<xsl:value-of select="ref:name"/> Tag </h2>
                    <!-- LEAD sentence of the description -->
                    <p>
                        <xsl:call-template name="choose-tag-lead"/>
                    </p>
                    <xsl:call-template name="author-section"/>
                    <!-- SYNTAX section -->
                    <xsl:call-template name="syntax-section"/>
                    <!-- DESCRIPTION Section -->
                    <p>
                        <xsl:call-template name="choose-tag-detail"/>
                    </p>
                    <!-- ATTRIBUTES Section -->
                    <xsl:if test="ref:attributes/ref:attribute">
                        <xsl:call-template name="attributes-list"/>
                    </xsl:if>
                    <!-- EXAMPLE Section -->
                    <xsl:if test="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='example'] or ref:tld-example">
                        <xsl:call-template name="example-code"/>
                    </xsl:if>
                    <!-- TAG INFORMATION Section -->
                    <xsl:if test="$noTagInfo != 'true'">
                        <br/>
                        <xsl:call-template name="tag-information"/>
                    </xsl:if>
                    <!-- 'SEE' Section -->
                    <xsl:if test="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='see']">
                        <xsl:call-template name="javadoc-see-tag"/>
                    </xsl:if>
                </xsl:for-each>
                <!-- FUNCTION Section (styles XML for a function topic) -->
                <xsl:for-each select="ref:function">
                    <!-- Main title for the topic -->
                    <h2>
                        <font size="-1">
                            <xsl:element name="a">
                                <xsl:attribute name="href">taglib-summary.html</xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="ref:display-name">
                                        <xsl:value-of select="ref:display-name"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="ref:prefix"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:element>
                        </font>
                        <br/>
                        <xsl:value-of select="ref:prefix"/>:<xsl:value-of select="ref:name"/> Function</h2>
                    <!-- LEAD sentence of the description -->
                    <p>
                        <xsl:value-of select="ref:tld-description"/>
                    </p>
                    <!-- SYNTAX section -->
                    <xsl:call-template name="function-syntax"/>
                    <!-- DESCRIPTION Section -->
                    <p>
                        <!-- <xsl:apply-templates select="ref:tagdescription/ref:detail"/> -->
                        <!-- <xsl:call-template name="tagdescription-detail"/> -->
                        <xsl:call-template name="choose-tag-detail"/>
                    </p>
                    <!-- EXAMPLE Section -->
                    <!--                    
                    <xsl:if test="ref:example or ref:tld-example">
                        <table border="1" width="100%" cellpadding="3" cellspacing="0">
                            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                                <td colspan="2">
                                    <font SIZE="+2">
                                        <b>
                                            <a name="example">Example</a>
                                        </b>
                                    </font>
                                </td>
                            </tr>
                        </table>
                        <p>
                            <pre>
                                <xsl:choose>
                                    <xsl:when test="ref:tld-example">
                                        <xsl:if test="$tld='true'">
                                            <xsl:value-of select="ref:tld-example" disable-output-escaping="yes"/>
                                        </xsl:if>
                                    </xsl:when>
                                    <xsl:when test="ref:example">
                                        <xsl:if test="$standard='true'">
                                            <xsl:value-of select="ref:example" disable-output-escaping="yes"/>
                                        </xsl:if>
                                    </xsl:when>
                                </xsl:choose>
                            </pre>
                        </p>
                    </xsl:if>
-->
                    <!-- 'SEE' Section -->
                    <xsl:if test="$noTagInfo != 'true'">
                        <br/>
                        <xsl:call-template name="function-information"/>
                    </xsl:if>
                    <xsl:if test="ref:function-class/ref:javadoc-tags/ref:javadoc-tag[@name='see']">
                        <xsl:call-template name="javadoc-see-tag"/>
                    </xsl:if>
                </xsl:for-each>
                <!-- NAVBAR Section (here forms a footer) (the navbar template is in the file header_footer.xslt) -->
                <xsl:if test="$noNavbar = 'false'">
                    <hr/>
                    <xsl:call-template name="navbar">
                        <xsl:with-param name="relPathToRoot" select="concat(//@docRoot, '/')"/>
                        <xsl:with-param name="cornerTitle" select="$footer"/>
                        <xsl:with-param name="displayDetails" select="'true'"/>
                    </xsl:call-template>
                </xsl:if>
                <xsl:if test="string($bottom)">
                    <hr/>
                    <p>
                        <xsl:value-of select="$bottom" disable-output-escaping="yes"/>
                    </p>
                </xsl:if>
            </body>
        </html>
    </xsl:template>
    <!-- LEAD Sentence ///////////////////////////////////////////////////////////////////////////-->
    <!--    <xsl:template match="ref:tagdescription/ref:lead"> -->
    <xsl:template name="javadoc-tag">
        <xsl:choose>
            <xsl:when test="@name='link'">
                <xsl:call-template name="javadoc-link-tag"/>
            </xsl:when>
            <xsl:when test="@name='see'">
                <xsl:call-template name="javadoc-see-tag"/>
            </xsl:when>
            <xsl:when test="@name='docRoot'">
                <xsl:value-of select="."/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="javadoc-link-tag">
        <xsl:for-each select="ref:target">
            <xsl:choose>
                <xsl:when test="@uriPath">
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                            <xsl:value-of select="@uriPath"/>
                            <xsl:value-of select="@tagName"/>.html</xsl:attribute>
                        <xsl:attribute name="target">_self</xsl:attribute>
                                &lt;<xsl:value-of select="@tagPrefix"/>:<xsl:value-of
                            select="@tagName"/>&gt;</xsl:element>
                </xsl:when>
                <xsl:when test="@packagePath">
                    <xsl:choose>
                        <xsl:when test="starts-with(@packagePath,'http')">
                            <xsl:call-template name="externalLinkTemplate"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="internalLinkTemplate"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="@href">
                    <xsl:value-of select="@href" disable-output-escaping="yes"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@inPackage"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of select="@typeName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="tagdescription-lead">
        <xsl:for-each select="ref:handler-class/ref:javadoc-tags/ref:tagdescription[@prefix='jsptagref']/ref:lead">
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="local-name(.)='javadoc-tag'">
                        <xsl:call-template name="javadoc-tag"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="tag-tld-description">
        <xsl:for-each select="ref:tld-description">
            <xsl:value-of select="."/>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="handlerdescription-detail">
        <xsl:for-each select="ref:handler-class/ref:description/ref:detail">
            <table border="1" width="100%" cellpadding="3" cellspacing="0">
                <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                    <td colspan="2">
                        <font SIZE="+2">
                            <b>
                                <a name="description">Description</a>
                            </b>
                        </font>
                    </td>
                </tr>
            </table>
            <p>
                <xsl:for-each select="node()">
                    <xsl:choose>
                        <xsl:when test="local-name(.)='javadoc-tag'">
                            <xsl:call-template name="javadoc-tag"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </p>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="handlerdescription-lead">
        <xsl:for-each select="ref:handler-class/ref:description/ref:lead">
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="local-name(.)='javadoc-tag'">
                        <xsl:call-template name="javadoc-tag"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="choose-tag-detail">
        <xsl:choose>
            <xsl:when test="$jsptagref='true'">
                <xsl:if test="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='tagdescription' and @prefix='jsptagref']//ref:detail">
                    <xsl:call-template name="tagdescription-detail"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$tld='true'">
                <xsl:if test="ref:tld-description">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$standard='true'">
                <xsl:if test="ref:handler-class/ref:description/ref:detail">
                    <xsl:call-template name="handlerdescription-detail"/>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="ref:tld-description">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="choose-tag-lead">
        <xsl:choose>
            <xsl:when test="$jsptagref='true'">
                <xsl:if test="ref:handler-class/ref:javadoc-tags/ref:tagdescription[@prefix='jsptagref']/ref:lead">
                    <xsl:call-template name="tagdescription-lead"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$tld='true'">
                <xsl:if test="ref:tld-description">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$standard='true'">
                <xsl:if test="ref:handler-class/ref:description/ref:lead">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="ref:tld-description">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- SYNTAX Section ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="author-section">
        <xsl:choose>
            <xsl:when test="ref:author">
                <xsl:if test="$author='true' and $jsptagref='true'">
                    <dl>
                        <dt>
                            <b>Author:</b>
                        </dt>
                        <dd>
                            <xsl:value-of select="ref:author"/>
                        </dd>
                    </dl>
                </xsl:if>
            </xsl:when>
            <xsl:when test="ref:tld-author">
                <xsl:if test="$author='true' and $tld='true'">
                    <dl>
                        <dt>
                            <b>Author:</b>
                        </dt>
                        <dd>
                            <xsl:value-of select="ref:tld-author"/>
                        </dd>
                    </dl>
                </xsl:if>
            </xsl:when>
            <xsl:when test="ref:author">
                <xsl:if test="$author='true' and $standard='true'">
                    <dl>
                        <dt>
                            <b>Author:</b>
                        </dt>
                        <dd>
                            <xsl:value-of select="ref:author"/>
                        </dd>
                    </dl>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="syntax-section">
        <table border="1" width="100%" cellpadding="3" cellspacing="0">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                <td colspan="2">
                    <font SIZE="+2">
                        <b>
                            <a name="syntax">Syntax</a>
                        </b>
                    </font>
                </td>
            </tr>
        </table>
        <p>
            <code> &lt;<xsl:value-of select="ref:prefix"/>:<xsl:value-of select="ref:name"/>
                <xsl:if test="ref:attributes">
                    <xsl:text>&#32;</xsl:text>
                    <xsl:for-each select="ref:attributes/ref:attribute">
                        <br/>
                        <xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
                        <xsl:choose>
                            <xsl:when test="ref:required='false'">[<xsl:element name="a">
                                    <xsl:attribute name="href">#<xsl:value-of select="ref:name"/>
                                    </xsl:attribute>
                                    <xsl:value-of select="ref:name"/>
                                </xsl:element>="<xsl:choose>
                                    <xsl:when test="ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag[@name='attributesyntaxvalue']">
                                            <xsl:value-of
                                                select="ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag[@name='attributesyntaxvalue']" disable-output-escaping="yes"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <i>
                                            <xsl:value-of select="ref:name"/>
                                        </i>
                                    </xsl:otherwise>
                                </xsl:choose>"]<xsl:text>&#32;</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:element name="a">
                                    <xsl:attribute name="href">#<xsl:value-of select="ref:name"/>
                                    </xsl:attribute>
                                    <xsl:value-of select="ref:name"/>
                                </xsl:element>="<i>
                                    <xsl:choose>
                                        <xsl:when test="ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag[@name='attributesyntaxvalue']">
                                            <xsl:value-of
                                                select="ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag[@name='attributesyntaxvalue']" disable-output-escaping="yes"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="ref:name"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </i>"<xsl:text>&#32;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="string(ref:body-content)='empty'">/&gt;</xsl:when>
                    <xsl:when test="string(ref:body-content)='tagdependent'">&gt;<br/>
                        <xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
                        <em>... Custom content ...</em>
                        <br/>&lt;/<xsl:value-of select="ref:prefix"/>:<xsl:value-of select="ref:name"/>&gt;</xsl:when>
                    <xsl:otherwise>&gt;<br/>
                        <xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
                        <em>... JSP content ...</em>
                        <br/>&lt;/<xsl:value-of select="ref:prefix"/>:<xsl:value-of select="ref:name"/>&gt;</xsl:otherwise>
                </xsl:choose>
            </code>
        </p>
    </xsl:template>
    <!-- DESCRIPTION Section ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="tagdescription-detail">
        <xsl:for-each select="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='tagdescription' and @prefix='jsptagref']//ref:detail">
            <table border="1" width="100%" cellpadding="3" cellspacing="0">
                <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                    <td colspan="2">
                        <font SIZE="+2">
                            <b>
                                <a name="description">Description</a>
                            </b>
                        </font>
                    </td>
                </tr>
            </table>
            <p>
                <xsl:for-each select="node()">
                    <xsl:choose>
                        <xsl:when test="local-name(.)='javadoc-tag'">
                            <xsl:call-template name="javadoc-tag"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </p>
        </xsl:for-each>
    </xsl:template>
    <!-- ATTRIBUTES Section ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="attributes-list">
        <table border="1" width="100%" cellpadding="4" cellspacing="0">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                <td colspan="2">
                    <font SIZE="+2">
                        <b>
                            <a name="attributes">Attributes</a>
                        </b>
                    </font>
                </td>
            </tr>
            <xsl:for-each select="ref:attributes/ref:attribute">
                <tr>
                    <td width="13%">
                        <strong>
                            <xsl:element name="a">
                                <xsl:attribute name="name">
                                    <xsl:value-of select="ref:name"/>
                                </xsl:attribute>
                            </xsl:element>
                            <xsl:value-of select="ref:name"/>
                        </strong>
                    </td>
                    <td width="87%" align="left">
                        <table border="0">
                            <tr bgcolor="#eeeeff">
                                <td>
                                    <font SIZE="-1"> Required: <strong>
                                            <xsl:choose>
                                                <xsl:when test="ref:required = 'true'">Yes</xsl:when>
                                                <xsl:when test="ref:required = 'false'">No</xsl:when>
                                            </xsl:choose>
                                        </strong>&#160;&#160;|&#160;&#160; Type:
                                            <xsl:for-each select="ref:type">
                                            <code>
                                                <xsl:choose>
                                                  <xsl:when test="@inPackage">
                                                  <xsl:call-template name="linkTemplate"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <xsl:value-of select="ref:full-name"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </code>
                                        </xsl:for-each>&#160;&#160;|&#160;&#160;
                                        Supports runtime evaluation / JSP Expression Language: <strong>
                                            <xsl:choose>
                                                <xsl:when test="ref:rtexprvalue = 'true'">Yes</xsl:when>
                                                <xsl:when test="ref:rtexprvalue = 'false'">No</xsl:when>
                                            </xsl:choose>
                                        </strong>
                                        <xsl:if
                                            test="ref:accessors/ref:method/ref:javadoc-tags/ref:databindable">
                                            &#160;&#160;|&#160;&#160; Data bindable: <strong>
                                                <xsl:choose>
                                                  <xsl:when test="ref:accessors/ref:method/ref:javadoc-tags/ref:databindable = 'true'">Yes</xsl:when>
                                                  <xsl:when test="ref:accessors/ref:method/ref:javadoc-tags/ref:databindable = 'false'">No</xsl:when>
                                                  <xsl:otherwise>
                                                  <xsl:value-of select="ref:accessors/ref:method/ref:javadoc-tags/ref:databindable"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </strong>
                                        </xsl:if>
                                    </font>
                                </td>
                            </tr>
                        </table>
                        <xsl:call-template name="choose-attr-description"/>
                    </td>
                </tr>
            </xsl:for-each>
        </table>
        <br/>
    </xsl:template>
    <xsl:template name="tag-information">
        <table border="1" width="100%" cellpadding="4" cellspacing="0">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                <td colspan="2">
                    <font SIZE="+2">
                        <b>
                            <a name="attributes">Tag Information</a>
                        </b>
                    </font>
                </td>
            </tr>
            <tr>
                <td align="left">Tag Class</td>
                <td align="left">
                    <xsl:for-each select="ref:handler-class">
                        <xsl:choose>
                            <xsl:when test="@inPackage">
                                <xsl:call-template name="linkTemplate"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="ref:full-name"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </td>
            </tr>
            <tr>
                <td align="left">TagExtraInfo Class</td>
                <td align="left">
                    <xsl:choose>
                        <xsl:when test="ref:tei-class">
                            <xsl:for-each select="ref:tei-class">
                                <xsl:choose>
                                    <xsl:when test="@inPackage">
                                        <xsl:call-template name="linkTemplate"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="ref:full-name"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <i>None</i>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td align="left">Body Content</td>
                <td align="left">
                    <xsl:value-of select="ref:body-content"/>
                </td>
            </tr>
            <tr>
                <td align="left">Display Name</td>
                <td align="left">
                    <xsl:choose>
                        <xsl:when test="ref:display-name">
                            <xsl:value-of select="ref:display-name"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <i>None</i>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
        </table>
        <br/>
    </xsl:template>
    <xsl:template name="function-information">
        <table border="1" width="100%" cellpadding="4" cellspacing="0">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                <td colspan="2">
                    <font SIZE="+2">
                        <b>
                            <a name="attributes">Function Information</a>
                        </b>
                    </font>
                </td>
            </tr>
            <tr>
                <td align="left">Function Class</td>
                <td align="left">
                    <xsl:for-each select="ref:function-class">
                        <xsl:choose>
                            <xsl:when test="@inPackage">
                                <xsl:call-template name="linkTemplate"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="ref:full-name"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </td>
            </tr>
            <tr>
                <td align="left">Function Signature</td>
                <td align="left">
                    <xsl:value-of select="ref:function-signature"/>
                </td>
            </tr>
            <tr>
                <td align="left">Display Name</td>
                <td align="left">
                    <xsl:choose>
                        <xsl:when test="ref:display-name">
                            <xsl:value-of select="ref:display-name"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <i>None</i>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
        </table>
        <br/>
    </xsl:template>
    <xsl:template name="choose-attr-description">
        <xsl:choose>
            <xsl:when test="ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag[@name='attributedescription'  and @prefix='jsptagref']">
                <xsl:if test="$jsptagref='true'">
                    <xsl:call-template name="attribute-description"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="ref:tld-description">
                <xsl:if test="$tld='true'">
                    <xsl:call-template name="attr-tld-description"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="ref:accessors/ref:method/ref:description">
                <xsl:if test="$standard='true'">
                    <xsl:call-template name="method-description-detail"/>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="attribute-description">
        <hr/>
        <xsl:for-each select="ref:accessors/ref:method/ref:javadoc-tags/ref:javadoc-tag[@name='attributedescription' and @prefix='jsptagref']//ref:detail">
            <xsl:call-template name="text-with-tags"/>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="attr-tld-description">
        <hr/>
        <xsl:value-of select="ref:tld-description"/>
    </xsl:template>
    <xsl:template name="method-description-detail">
        <hr/>
        <xsl:for-each select="ref:accessors/ref:method/ref:description/ref:detail">
            <xsl:if test="starts-with(../../@name,'set')">
                <xsl:for-each select="node()">
                    <xsl:choose>
                        <xsl:when test="local-name(.)='javadoc-tag'">
                            <xsl:call-template name="javadoc-tag"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <!-- SAMPLE Section ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="example-code">
        <xsl:choose>
            <xsl:when test="$standard='true' or $jsptagref='true'">
                <xsl:if test="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='example']">
                    <table border="1" width="100%" cellpadding="3" cellspacing="0">
                        <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                            <td colspan="2">
                                <font SIZE="+2">
                                    <b>
                                        <a name="example">Example</a>
                                    </b>
                                </font>
                            </td>
                        </tr>
                    </table>
                    <xsl:for-each select="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='example']">
                        <p>
                            <xsl:call-template name="text-with-tags"/>
                        </p>
                    </xsl:for-each>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$tld='true'">
                <xsl:if test="ref:tld-example">
                    <table border="1" width="100%" cellpadding="3" cellspacing="0">
                        <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                            <td colspan="2">
                                <font SIZE="+2">
                                    <b>
                                        <a name="example">Example</a>
                                    </b>
                                </font>
                            </td>
                        </tr>
                    </table>
                    <p>
                        <xsl:value-of select="ref:tld-example"/>
                    </p>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- 'SEE' SECTION ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="javadoc-see-tag">
        <h2>See Also</h2>
        <xsl:for-each select="ref:handler-class/ref:javadoc-tags/ref:javadoc-tag[@name='see']/ref:target">
            <p>
                <xsl:choose>
                    <xsl:when test="@uriPath">
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="@uriPath"/>
                                <xsl:value-of select="@tagName"/>.html</xsl:attribute>
                            <xsl:attribute name="target">_self</xsl:attribute> &lt;<xsl:value-of
                                select="@tagPrefix"/>:<xsl:value-of select="@tagName"/>&gt; Tag </xsl:element>
                    </xsl:when>
                    <xsl:when test="@typeName">
                        <xsl:call-template name="linkTemplate"/>
                    </xsl:when>
                    <xsl:when test="@href">
                        <xsl:value-of select="@href" disable-output-escaping="yes"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </xsl:otherwise>
                </xsl:choose>
            </p>
            <xsl:if test="position()!=last()">
                <!--<br/>-->
            </xsl:if>
            <xsl:if test="position()=last()-1"/>
        </xsl:for-each>
    </xsl:template>
    <!-- Link Templates ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="text-with-tags">
        <xsl:for-each select="node()">
            <xsl:choose>
                <xsl:when test="local-name(.)='javadoc-tag'">
                    <xsl:call-template name="javadoc-tag"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="." disable-output-escaping="yes"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="inlineLinkTemplate">
        <xsl:choose>
            <xsl:when test="@uriPath">
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="@uriPath"/>
                        <xsl:value-of select="@tagName"/>.html</xsl:attribute>
                    <xsl:attribute name="target">_self</xsl:attribute>
                    &lt;<xsl:value-of select="@tagPrefix"/>:<xsl:value-of
                            select="@tagName"/>&gt;
                </xsl:element>
            </xsl:when>
            <xsl:when test="@packagePath">
                <xsl:call-template name="linkTemplate"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="@inPackage"/>
                <xsl:text>.</xsl:text>
                <xsl:value-of select="@typeName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="linkTemplate">
        <xsl:choose>
            <xsl:when test="@uriPath">
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="@uriPath"/>
                        <xsl:value-of select="@tagName"/>.html</xsl:attribute>
                    <xsl:attribute name="target">_self</xsl:attribute>
                            <xsl:value-of select="@tagName"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="string(@packagePath)">
                <xsl:choose>
                    <xsl:when test="starts-with(@packagePath,'http')">
                        <xsl:call-template name="externalLinkTemplate"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="internalLinkTemplate"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="@href">
                <xsl:value-of select="@href" disable-output-escaping="yes"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="@inPackage"/>
                <xsl:text>.</xsl:text>
                <xsl:value-of select="@typeName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- Opens in the same window; base path leads only to root of ref doc tree -->
    <xsl:template name="internalLinkTemplate">
        <xsl:choose>
            <xsl:when test="@signature">
                <xsl:choose>
                    <xsl:when test="string(@typeName)">
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="@packagePath"/><xsl:value-of
                                    select="@typeName"/>.html#<xsl:value-of select="@signature"/>
                            </xsl:attribute>
                            <xsl:attribute name="target">_blank</xsl:attribute> --&gt;
                                <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="string(@typeName)">
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                            <xsl:value-of select="@packagePath"/><xsl:value-of select="@typeName"/>.html</xsl:attribute>
                        <xsl:attribute name="target">_blank</xsl:attribute>
                        <xsl:value-of select="@typeName"/>
                    </xsl:element>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- Opens in a new window; base path leads to an external web site. -->
    <xsl:template name="externalLinkTemplate">
        <xsl:choose>
            <xsl:when test="@signature">
                <xsl:choose>
                    <xsl:when test="string(@typeName)">
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="@packagePath"/>
                                <xsl:value-of select="@typeName"/>.html#<xsl:value-of select="@signature"/>
                            </xsl:attribute>
                            <xsl:attribute name="target">_blank</xsl:attribute>
                            <CODE>
                                <xsl:value-of select="."/>
                            </CODE>
                            <!--                            <xsl:value-of select="."/> -->
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="string(@typeName)">
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="@packagePath"/>
                                <xsl:value-of select="@typeName"/>.html</xsl:attribute>
                            <xsl:attribute name="target">_blank</xsl:attribute>
                            <CODE>
                                <xsl:value-of select="@typeName"/>
                            </CODE>
                            <!--                            <xsl:value-of select="."/> -->
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="function-syntax">
        <table border="1" width="100%" cellpadding="3" cellspacing="0">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                <td colspan="2">
                    <font SIZE="+2">
                        <b>
                            <a name="syntax">Syntax</a>
                        </b>
                    </font>
                </td>
            </tr>
        </table>
        <PRE>
        <xsl:choose>
            <xsl:when test="ref:function-support-method/ref:returns/@typeName">
                <xsl:for-each select="ref:function-support-method">
                        <xsl:for-each select="ref:returns">
                            <xsl:choose>
                                <xsl:when test="@inPackage">
                                    <xsl:call-template name="linkTemplate"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="@typeName"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:value-of select="@dimension"/>
                            <xsl:text>&#160;</xsl:text>
                        </xsl:for-each>
                        <xsl:value-of select="@name"/>(<xsl:for-each select="ref:parameter">
                            <xsl:choose>
                                <xsl:when test="@inPackage">
                                    <xsl:call-template name="linkTemplate"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="@typeName"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:value-of select="@dimension"/>
                            <xsl:text>&#160;</xsl:text>
                            <i>
                                <xsl:value-of select="@name"/>
                            </i>
                            <xsl:if test="position()!=last()">
                                <xsl:text>,</xsl:text>
                                <xsl:text>&#160;</xsl:text>
                                <xsl:call-template name="insert-space">
                                    <xsl:with-param name="counter" select="0"/>
                                    <xsl:with-param name="length" select="string-length(../@name) + string-length(../@mod) + string-length(../ref:returns/@typeName)"/>
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:for-each>)
                    </xsl:for-each>
                </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="ref:function-signature"/>
            </xsl:otherwise>
        </xsl:choose></PRE>
    </xsl:template>
    <xsl:template name="insert-space">
        <xsl:param name="counter"/>
        <xsl:param name="length"/>
        <xsl:if test="$counter &lt; $length">
            <xsl:call-template name="insert-space">
                <xsl:with-param name="counter" select="$counter + 1"/>
                <xsl:with-param name="length" select="$length"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="navbar">
        <xsl:param name="relPathToRoot"/>
        <xsl:param name="displayDetails"/>
        <xsl:param name="cornerTitle"/>
        <table border="0" width="100%" cellpadding="2" cellspacing="0">
            <tr>
                <td colspan="3" bgcolor="#EEEEFF" class="NavBarCell1">
                    <a name="navbar_top_firstrow">
                        <!-- -->
                    </a>
                    <table border="0" cellpadding="0" cellspacing="3">
                        <tr align="center" valign="top">
                            <td bgcolor="#EEEEFF" class="NavBarCell1">
                                <a href="{$relPathToRoot}taglib-overview-summary.html" style="">
                                    <font class="NavBarFont1">
                                        <b>Overview</b>
                                    </font>
                                </a>&#160;</td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">|&#160; <a href="taglib-summary.html">
                                    <font class="NavBarFont1">
                                        <b>Library</b>
                                    </font>
                                </a> &#160;</td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1Rev">&#160; <font class="NavBarFont1Rev">
                                    <b>Tag</b>
                                </font> &#160;</td>
                            <xsl:if test="$noIndex='false'">
                                <td bgcolor="#EEEEFF" class="NavBarCell1">&#160; <a
                                        href="{$relPathToRoot}index-all.html" style="">
                                        <font class="NavBarFont1">
                                            <b>Index</b>
                                        </font>
                                    </a>
                                </td>
                            </xsl:if>
                        </tr>
                    </table>
                </td>
                <xsl:if test="string($cornerTitle)">
                    <td align="right" valign="top" rowspan="3">
                        <em>
                            <xsl:value-of select="$cornerTitle" disable-output-escaping="yes"/>
                        </em>
                    </td>
                </xsl:if>
            </tr>
            <!--
            <xsl:if test="$displayDetails">
                <tr>
                    <td valign="top" bgcolor="white" class="NavBarCell3">
                        <font SIZE="-2"> DETAIL:&#160;<a
                            href="#syntax">Syntax</a>&#160;|&#160; <a
                            href="#description">Description</a>&#160;|&#160; <a
                            href="#attributes">Attributes</a>&#160;|&#160; <a href="#example">Example</a>
                        </font>
                    </td>
                    <td valign="top" class="NavBarCell3">
                        <xsl:for-each select="ref:jsp-tag/ref:handler-class">
                            <font SIZE="-2">Tag class: <xsl:choose>
                                    <xsl:when test="@inPackage">
                                        <xsl:call-template name="linkTemplate"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="ref:full-name"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </font>
                        </xsl:for-each>
                        <xsl:if test="ref:function/ref:function-class">
                            <font SIZE="-2">Function class: <xsl:choose>
                                    <xsl:when test="ref:function/ref:function-support-method/ref:enclosing-type">
                                        <xsl:for-each select="ref:function/ref:function-support-method/ref:enclosing-type">
                                            <xsl:call-template name="linkTemplate"/>
                                        </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="ref:function/ref:function-class"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </font>
                        </xsl:if>
                    </td>
                </tr>
            </xsl:if>
 -->
            <table>
                <tr>
                    <td class="NavBarCell2" bgcolor="white">
                        <font SIZE="-2"/>
                    </td>
                    <td class="NavBarCell2" bgcolor="white">
                        <font size="-2"> &#160;<a target="_top" href="{$relPathToRoot}index.html">
                                <b>FRAMES</b>
                            </a>&#160; &#160; <xsl:element name="a">
                                <xsl:attribute name="target">_top</xsl:attribute>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="node()/ref:name"/>.html</xsl:attribute>
                                <b>NO FRAMES</b>
                            </xsl:element>&#160;
                            <!--                                        
                                        <noscript>
                                            <a target="" href="alltags-noframe.html">
                                                <b>All Tags</b>
                                            </a>
                                        </noscript>
-->
                        </font>
                    </td>
                </tr>
            </table>
        </table>
    </xsl:template>
</xsl:stylesheet>
