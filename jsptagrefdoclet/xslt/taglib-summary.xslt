<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema"
    xmlns:xalan="org.apache.xalan.xslt.extensions.Redirect" extension-element-prefixes="xalan" version="1.0">
    <!--    <xsl:import href="header_footer.xslt"/> -->
    <xsl:param name="taglibLocation"/>
    <xsl:param name="jsptagref"/>
    <xsl:param name="tld"/>
    <xsl:param name="standard"/>
    <xsl:param name="docTitle"/>
    <xsl:param name="windowTitle"/>
    <xsl:param name="noNavbar"/>
    <xsl:param name="header"/>
    <xsl:param name="footer"/>
    <xsl:param name="noIndex"/>
    <xsl:param name="bottom"/>
    <xsl:param name="noTagInfo"/>
    <xsl:param name="keywords"/>
    <xsl:template match="/">
        <xsl:call-template name="make-taglib-summary-pages"/>
    </xsl:template>
    <!--=============================================================== 
     Makes the individual taglib-summary.html pages.
     For each <taglib-summary> element in overview.xml,
     a taglib-summary.html page is made. 
    ================================================================-->
    <xsl:template name="make-taglib-summary-pages">
        <xsl:for-each select="ref:taglib-summaries/ref:taglib-summary">
            <xsl:sort select="ref:prefix"/>
            <xsl:variable name="taglib-name">
                <xsl:choose>
                    <xsl:when test="string(ref:display-name)">
                        <xsl:value-of select="ref:display-name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ref:prefix"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="doc-root" select="@docRoot"/>
            <xsl:variable name="filename" select="concat($taglibLocation, '/', ref:uri/@uriPath, 'taglib-summary.html')"/>
            <xalan:write select="$filename">
                <html>
                    <head>
                        <title>
                            <xsl:value-of select="$taglib-name"/> Library <xsl:if
                                test="string($windowTitle)"> (<xsl:value-of select="$windowTitle"/>)</xsl:if>
                        </title>
                    <xsl:if test="$keywords = 'true'">
                        <xsl:for-each select="//ref:attribute">
                            <xsl:element name="meta">
                                <xsl:attribute name="name">keywords</xsl:attribute>
                                <xsl:attribute name="content"><xsl:value-of select="ref:name"/> Attribute</xsl:attribute>
                            </xsl:element>
                        </xsl:for-each>
                    <xsl:element name="meta">
                        <xsl:attribute name="name">keywords</xsl:attribute>
                        <xsl:attribute name="content"><xsl:value-of select="node()/ref:name"/> Tag</xsl:attribute>
                    </xsl:element>
                    </xsl:if>
                        <link rel="stylesheet" type="text/css" href="{$doc-root}/stylesheet.css" title="Style"/>
                        <script type="text/javascript"> function windowTitle() {
                                parent.document.title="<xsl:value-of select="$taglib-name"/>
                            Library"; } </script>
                    </head>
                    <body bgcolor="white" onload="windowTitle();">
                        <!-- ========= START OF TOP NAVBAR ========= -->
                        <xsl:if test="$noNavbar = 'false'">
                            <xsl:call-template name="navbar">
                                <xsl:with-param name="relPathToRoot" select="concat(@docRoot, '/')"/>
                                <xsl:with-param name="cornerTitle" select="$header"/>
                            </xsl:call-template>
                            <hr/>
                        </xsl:if>
                        <!-- ========= END OF TOP NAVBAR ========= -->
                        <h2>
                            <xsl:value-of select="$taglib-name"/> Library </h2>
                        <xsl:if test="string(ref:tld-description)">
                            <p>
                                <xsl:value-of select="ref:tld-description"/>
                            </p>
                        </xsl:if>
                        <hr/>
                        <dl>
                            <dt>
                                <b>Standard Syntax:</b>
                            </dt>
                            <dd>
                                <code>&lt;%@ taglib prefix="<xsl:value-of select="ref:prefix"/>"
                                        uri="<xsl:value-of select="ref:uri"/>" %&gt;</code>
                            </dd>
                        </dl>
                        <dl>
                            <dt>
                                <b>XML Syntax:</b>
                            </dt>
                            <dd>
                                <code>&lt;anyxmlelement xmlns:<xsl:value-of
                                        select="ref:prefix"/>="<xsl:value-of select="ref:uri"/>" /&gt;</code>
                            </dd>
                        </dl>
                        <hr/>
                        <xsl:if test="ref:jsp-tag-summary">
                            <p>
                                <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
                                    <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                                        <th align="left" colspan="2">
                                            <font size="+2">
                                                <b>Tag Summary</b>
                                            </font>
                                        </th>
                                    </tr>
                                    <!-- ========= Write out links to the individual JSP tag topics ========= -->
                                    <xsl:for-each select="ref:jsp-tag-summary">
                                        <tr bgcolor="white" class="TableRowColor">
                                            <td WIDTH="15%" nowrap="nowrap">
                                                <b>
                                                  <xsl:element name="a">
                                                  <xsl:attribute name="href">
                                                  <xsl:value-of select="ref:name"/>.html</xsl:attribute>
                                                  <xsl:attribute name="target">right-frame</xsl:attribute>
                                                  <xsl:value-of select="ref:name"/>
                                                  </xsl:element>
                                                </b>
                                            </td>
                                            <td>
                                                <xsl:call-template name="choose-tag-lead"/>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>&#160;</p>
                        </xsl:if>
                        <xsl:if test="ref:function-summary">
                            <p>
                                <hr/>
                                <!-- ========= Write out links to the individual function topics ========= -->
                                <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
                                    <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                                        <th align="left" colspan="2">
                                            <font size="+2">
                                                <b>Function Summary</b>
                                            </font>
                                        </th>
                                    </tr>
                                    <xsl:for-each select="ref:function-summary">
                                        <tr bgcolor="white" class="TableRowColor">
                                            <td nowrap="nowrap">
                                                <code>
                                                  <xsl:value-of select="ref:return"/>
                                                </code>
                                            </td>
                                            <td>
                                                <dl>
                                                  <dt>
                                                  <code>
                                                  <b>
                                                  <xsl:element name="a">
                                                      <xsl:attribute name="href">
                                                      <xsl:value-of select="ref:name"/>.html</xsl:attribute>
                                                      <xsl:attribute name="target">right-frame</xsl:attribute>
                                                      <xsl:value-of select="ref:signature"/>
                                                  </xsl:element>
                                                  </b>
                                                  </code>
                                                  </dt>
                                                  <dd>
                                                  <xsl:call-template name="choose-tag-lead"/>
                                                  </dd>
                                                </dl>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>&#160; </p>
                        </xsl:if>
                        <xsl:if test="$noTagInfo != 'true'">
                            <xsl:call-template name="taglib-information"/>
                        </xsl:if>
                        <!-- ======= START OF BOTTOM NAVBAR ====== -->
                        <xsl:if test="$noNavbar = 'false'">
                            <hr/>
                            <xsl:call-template name="navbar">
                                <xsl:with-param name="relPathToRoot" select="concat(@docRoot, '/')"/>
                                <xsl:with-param name="cornerTitle" select="$footer"/>
                            </xsl:call-template>
                        </xsl:if>
                        <!-- ======== END OF BOTTOM NAVBAR ======= -->
                        <xsl:if test="string($bottom)">
                            <hr/>
                            <p>
                                <xsl:value-of select="$bottom" disable-output-escaping="yes"/>
                            </p>
                        </xsl:if>
                    </body>
                </html>
            </xalan:write>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="taglib-information">
        <table border="1" width="100%" cellpadding="4" cellspacing="0">
            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                <td colspan="2">
                    <font SIZE="+2">
                        <b>
                            <a name="attributes">Library Information</a>
                        </b>
                    </font>
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
            <tr>
                <td align="left">Version</td>
                <td align="left">
                    <xsl:value-of select="ref:taglib-version"/>
                </td>
            </tr>
            <tr>
                <td align="left">Short Name</td>
                <td align="left">
                    <xsl:choose>
                        <xsl:when test="ref:prefix">
                            <xsl:value-of select="ref:prefix"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <i>None</i>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td align="left">URI</td>
                <td align="left">
                    <xsl:value-of select="ref:uri"/>
                </td>
            </tr>
        </table>
        <br/>
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
                            <td bgcolor="#EEEEFF" class="NavBarCell1Rev">&#160;<font class="NavBarFont1Rev">
                                    <b>Library</b>
                                </font>&#160;</td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">&#160;<font class="NavBarFont1">
                                    <b>Tag</b>
                                </font>&#160;</td>
                            <xsl:if test="$noIndex='false'">
                                <td bgcolor="#EEEEFF" class="NavBarCell1">|&#160;<a
                                        href="{$relPathToRoot}index-all.html" style="">
                                        <font class="NavBarFont1">
                                            <b>Index</b>
                                        </font>
                                    </a>&#160;</td>
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
            <table>
                <tr>
                    <td class="NavBarCell2" bgcolor="white">
                        <font SIZE="-2"/>
                    </td>
                    <td class="NavBarCell2" bgcolor="white">
                        <font size="-2"> &#160;<a target="_top" href="{$relPathToRoot}index.html">
                                <b>FRAMES</b>
                            </a>&#160; &#160;<a target="_top" href="taglib-summary.html">
                                <b>NO FRAMES</b>
                            </a>&#160; 
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
    <xsl:template name="choose-tag-lead">
        <xsl:choose>
            <xsl:when test="ref:tagdescription/ref:lead">
                <xsl:if test="$jsptagref='true'">
                    <xsl:call-template name="tagdescription-lead"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="ref:tld-description">
                <xsl:if test="$tld='true'">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="ref:handler-class/ref:description/ref:lead">
                <xsl:if test="$standard='true'">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- LEAD Sentence ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="tag-tld-description">
        <xsl:value-of select="ref:tld-description"/>
    </xsl:template>
    <xsl:template name="tagdescription-lead">
        <xsl:for-each select="ref:tagdescription/ref:lead">
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="local-name(.)='link'">
                        <xsl:value-of select="."/>
                        <!--                    <xsl:call-template name="inlineLinkTemplate"/> -->
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="handlerdescription-lead">
        <xsl:for-each select="ref:tag-class-description/ref:lead">
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="local-name(.)='link'">
                        <xsl:value-of select="."/>
                        <!--                    <xsl:call-template name="inlineLinkTemplate"/> -->
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="choose-attr-lead">
        <xsl:choose>
            <!--
            <xsl:when test="../ref:tagdescription/ref:lead">
                <xsl:if test="$jsptagref='true'">
                    <xsl:call-template name="tagdescription-lead"/>
                </xsl:if>
            </xsl:when>
-->
            <xsl:when test="../ref:tld-description">
                <xsl:if test="$tld='true'">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:when>
            <!--            
            <xsl:when test="../ref:handler-class/ref:description/ref:lead">
                <xsl:if test="$standard='true'">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
-->
        </xsl:choose>
    </xsl:template>
    <xsl:template name="choose-function-lead">
        <xsl:choose>
            <!--        
            <xsl:when test="../ref:tagdescription/ref:lead">
                <xsl:if test="$jsptagref='true'">
                    <xsl:call-template name="tagdescription-lead"/>
                </xsl:if>
            </xsl:when>
-->
            <xsl:when test="../ref:tld-description">
                <xsl:if test="$tld='true'">
                    <xsl:call-template name="tag-tld-description"/>
                </xsl:if>
            </xsl:when>
            <!--            
            <xsl:when test="../ref:handler-class/ref:description/ref:lead">
                <xsl:if test="$standard='true'">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
-->
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
