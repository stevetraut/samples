<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema">
    <!--    <xsl:import href="header_footer.xslt"/> -->
    <!--=============================================================== 
     Makes the taglib-overview-summary.html page.
    ================================================================-->
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
    <xsl:template match="/">
        <html>
            <head>
                <title>Overview <xsl:if test="string($windowTitle)"> (<xsl:value-of select="$windowTitle"/>)</xsl:if>
                </title>
                <link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
                <script type="text/javascript"> function windowTitle() {
                    parent.document.title="Overview"; } </script>
            </head>
            <body bgcolor="white" onload="windowTitle();">
                <!-- ========= START OF TOP NAVBAR ======= -->
                <xsl:if test="$noNavbar = 'false'">
                    <xsl:call-template name="navbar">
                        <xsl:with-param name="relPathToRoot" select="'./'"/>
                        <xsl:with-param name="cornerTitle" select="$header"/>
                    </xsl:call-template>
                    <hr/>
                </xsl:if>
                <!-- ========= END OF TOP NAVBAR ========= -->
                <center>
                    <h1>
                        <xsl:value-of select="$docTitle" disable-output-escaping="yes"/>
                    </h1>
                </center>
                <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
                    <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                        <th align="left" colspan="2">
                            <font size="+1">
                                <!-- Makes a bare anchor, not a link -->
                                <xsl:element name="a">
                                    <xsl:attribute name="name">
                                        <xsl:value-of select="ref:prefix"/>
                                    </xsl:attribute>
                                </xsl:element>
                                <b>Tag Libraries</b>
                            </font>
                        </th>
                    </tr>
                    <xsl:for-each select="ref:taglib-summaries/ref:taglib-summary">
                        <xsl:variable name="filename" select="concat($taglibLocation, '/', ref:uri/@uriPath, 'taglib-summary.html')"/>
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
                        <tr>
                            <td>
                                <xsl:element name="a">
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="ref:display-name">
                                            <xsl:value-of select="ref:display-name"/> (<xsl:value-of
                                            select="ref:taglib-version"/>) </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="ref:prefix"/> (<xsl:value-of
                                            select="ref:taglib-version"/>) </xsl:otherwise>
                                    </xsl:choose>
                                    <br/>
                                </xsl:element>
                                <!--
                                <a href="{$filename}">
                                    <xsl:value-of select="$taglib-name"/> (<xsl:value-of
                                    select="ref:taglib-version"/>) </a>
-->
                            </td>
                            <td>
                                <xsl:value-of select="ref:tld-description"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
                <!--
                    
                    <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
                        <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                            <th align="left" colspan="2">
                                <font size="+1">
                                    <xsl:element name="a">
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="ref:prefix"/>
                                        </xsl:attribute>
                                    </xsl:element>
                                    <b>
                                        <xsl:value-of select="$taglib-name"/> Library</b>
                                </font>
                            </th>
                        </tr>
                        <xsl:variable name="summaries" select="ref:jsp-tag-summary | ref:function-summary"/>
                        <xsl:for-each select="$summaries">
                            <xsl:sort select="ref:name"/>
                            <tr bgcolor="white" class="TableRowColor">
                                <td width="20%" nowrap="nowrap">
                                    <b>
                                        <xsl:element name="a">
                                            <xsl:attribute name="href">
                                                <xsl:value-of select="../ref:uri/@uriPath"/>
                                                <xsl:value-of select="ref:name"/>.html</xsl:attribute>
                                            <xsl:choose>
                                                <xsl:when test="local-name()='function-summary'">
                                                  <i>
                                                  <xsl:value-of
                                                  select="../ref:prefix"/>:<xsl:value-of select="ref:name"/>
                                                  </i>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <xsl:value-of
                                                  select="../ref:prefix"/>:<xsl:value-of select="ref:name"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:element>
                                        <br/>
                                    </b>
                                </td>
                                <td>
                                    <xsl:call-template name="choose-tag-lead"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                        <xsl:for-each select="ref:function-summary">
                            <xsl:sort select="ref:name"/>
                            <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                                <th align="left" colspan="2">
                                    <font size="+1">
                                        <xsl:element name="a">
                                            <xsl:attribute name="name">
                                                <xsl:value-of select="ref:prefix"/>
                                            </xsl:attribute>
                                        </xsl:element>
                                        <b>
                                            <xsl:value-of select="ref:prefix"/> Function Summary</b>
                                    </font>
                                </th>
                            </tr>
                            <tr bgcolor="white" class="TableRowColor">
                                <td width="20%" nowrap="nowrap">
                                    <b>
                                        <xsl:element name="a">
                                            <xsl:attribute name="href">
                                                <xsl:value-of select="../ref:uri/@uriPath"/>
                                                <xsl:value-of select="ref:name"/>.html</xsl:attribute>
                                            <xsl:value-of select="../ref:prefix"/>:<xsl:value-of select="ref:name"/>
                                        </xsl:element>
                                        <br/>
                                    </b>
                                </td>
                                <td>
                                    <xsl:value-of select="ref:tld-function-description" disable-output-escaping="yes"/>&#160;</td>
                            </tr>
                        </xsl:for-each>
                    </table>
                    <br/>
                </xsl:for-each>
-->
                <!-- ======= START OF BOTTOM NAVBAR ====== -->
                <xsl:if test="$noNavbar = 'false'">
                    <hr/>
                    <xsl:call-template name="navbar">
                        <xsl:with-param name="relPathToRoot" select="'./'"/>
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
                            <td bgcolor="#EEEEFF" class="NavBarCell1Rev">&#160; <font class="NavBarFont1Rev">
                                    <b>Overview</b>
                                </font>&#160;</td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">&#160; <font class="NavBarFont1">
                                    <b>Library</b>
                                </font>&#160; </td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">|&#160; <font class="NavBarFont1">
                                    <b>Tag</b>
                                </font>&#160;</td>
                            <xsl:if test="$noIndex='false'">
                                <td bgcolor="#EEEEFF" class="NavBarCell1">|&#160; <a
                                        href="{$relPathToRoot}index-all.html" style="">
                                        <font class="NavBarFont1">
                                            <b>Index</b>
                                        </font>
                                    </a>
                                </td>
                            </xsl:if>
                        </tr>
                    </table>
                    <xsl:if test="string($cornerTitle)">
                        <td align="right" valign="top" rowspan="3">
                            <em>
                                <xsl:value-of select="$cornerTitle" disable-output-escaping="yes"/>
                            </em>
                        </td>
                    </xsl:if>
                </td>
            </tr>
            <table>
                <tr>
                    <td class="NavBarCell2" bgcolor="white">
                        <font SIZE="-2"/>
                    </td>
                    <td class="NavBarCell2" bgcolor="white">
                        <font size="-2"> &#160;<a target="_top" href="index.html">
                                <b>FRAMES</b>
                            </a>&#160; &#160;<a target="_top" href="taglib-overview-summary.html">
                                <b>NO FRAMES</b>
                            </a>&#160; <script/>
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
