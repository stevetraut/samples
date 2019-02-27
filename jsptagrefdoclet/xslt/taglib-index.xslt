<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema"
    xmlns:xalan="org.apache.xalan.xslt.extensions.Redirect" extension-element-prefixes="xalan" version="1.0">
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
                <title>Index<xsl:if test="string($windowTitle)"> (<xsl:value-of select="$windowTitle"/>)</xsl:if>
                </title>
                <link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
                <script type="text/javascript"> function windowTitle() {
                    parent.document.title="Index"; } </script>
            </head>
            <body bgcolor="white" onload="windowTitle();">
                <!-- NAVBAR Section (the navbar template is in the file header_footer.xslt) -->
                <!-- ========= START OF TOP NAVBAR ======= -->
                <xsl:if test="$noNavbar = 'false'">
                    <xsl:call-template name="navbar">
                        <xsl:with-param name="relPathToRoot" select="'./'"/>
                        <xsl:with-param name="cornerTitle" select="$header"/>
                    </xsl:call-template>
                    <hr/>
                </xsl:if>
                <!-- ========= END OF TOP NAVBAR ========= -->
                <xsl:call-template name="sortname"/>
                <!-- NAVBAR Section (here forms a footer) (the navbar template is in the file header_footer.xslt) -->
                <!-- ========= START OF BOTTOM NAVBAR ======= -->
                <xsl:if test="$noNavbar = 'false'">
                    <hr/>
                    <xsl:call-template name="navbar">
                        <xsl:with-param name="relPathToRoot" select="'./'"/>
                        <xsl:with-param name="cornerTitle" select="$footer"/>
                    </xsl:call-template>
                </xsl:if>
                <!-- ========= END OF BOTTOM NAVBAR ========= -->
                <xsl:if test="string($bottom)">
                    <hr/>
                    <p>
                        <xsl:value-of select="$bottom" disable-output-escaping="yes"/>
                    </p>
                </xsl:if>
            </body>
        </html>
    </xsl:template>
    <xsl:key name="letters" match="//ref:name" use="translate (substring(.,1,1), 'abcdefghijklmnopqrstuvwxyz1234567890@', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ###########')"/>
    <!--    
    <xsl:template name="letterlinks">
    <p>
            <xsl:choose>
                <xsl:when test="">
                    <a>
                    <xsl:attribute name="href">
                        
                    </xsl:attribute>
                    
                    </a>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
        </p>
    </xsl:template>
-->
    <xsl:template name="sortname">
        <xsl:for-each select="//ref:name[count(. | key('letters', translate(substring(.,1,1), 'abcdefghijklmnopqrstuvwxyz1234567890@', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ###########'))[1]) = 1]">
            <xsl:sort select="."/>
            <xsl:variable name="initial" select="translate(substring(.,1,1), 'abcdefghijklmnopqrstuvwxyz1234567890@', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ###########')"/>
            <a name="{$initial}"/>
            <xsl:choose>
                <xsl:when test="$initial = '#'">
                    <h2>Numbers &amp; symbols</h2>
                </xsl:when>
                <xsl:otherwise>
                    <h2>
                        <xsl:value-of select="$initial"/>
                    </h2>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:for-each select="key('letters', $initial)">
                <xsl:sort select="."/>
                <xsl:call-template name="entries"/>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="entries">
        <dl>
            <xsl:choose>
                <xsl:when test="../../ref:jsp-tag-summary">
                    <dt>
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../ref:uri/@uriPath"/>
                                <xsl:value-of select="."/>.html</xsl:attribute>
                            <b>&lt;<xsl:value-of select="."/>&gt;</b>
                        </xsl:element> - JSP tag in the <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="../../ref:display-name">
                                    <xsl:value-of select="../../ref:display-name"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="../../ref:prefix"/>
                                </xsl:otherwise>
                            </xsl:choose> (<xsl:value-of
                        select="../../ref:taglib-version"/>)</xsl:element> library. </dt>
                    <dd>
                        <xsl:call-template name="choose-tag-lead"/>
                    </dd>
                </xsl:when>
                <xsl:when test="../../ref:attribute-summary">
                    <dt>
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../../../ref:uri/@uriPath"/>
                                <xsl:value-of select="../../../ref:name"/>.html#<xsl:value-of select="."/>
                            </xsl:attribute>
                            <b>
                                <xsl:value-of select="."/>
                            </b>
                        </xsl:element> - Attribute of <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../../../ref:uri/@uriPath"/>
                                <xsl:value-of select="../../../ref:name"/>.html</xsl:attribute>
                                &lt;<xsl:value-of
                        select="../../../ref:name"/>&gt;</xsl:element> tag in the <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../../../ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="../../../../ref:display-name">
                                    <xsl:value-of select="../../../../ref:display-name"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="../../../../ref:prefix"/>
                                </xsl:otherwise>
                            </xsl:choose> (<xsl:value-of
                        select="../../../../ref:taglib-version"/>)</xsl:element> library. </dt>
                    <dd>
                        <xsl:call-template name="choose-attr-lead"/>
                    </dd>
                </xsl:when>
                <xsl:when test="../../ref:function-summary">
                    <dt>
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../ref:uri/@uriPath"/>
                                <xsl:value-of select="."/>.html</xsl:attribute>
                            <b>
                                <xsl:value-of select="../ref:signature"/>
                            </b>
                        </xsl:element> - Function in the <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="../../ref:display-name">
                                    <xsl:value-of select="../../ref:display-name"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="../../ref:prefix"/>
                                </xsl:otherwise>
                            </xsl:choose> (<xsl:value-of
                        select="../../ref:taglib-version"/>)</xsl:element> library. </dt>
                    <dd>
                        <xsl:call-template name="choose-function-lead"/>
                    </dd>
                </xsl:when>
            </xsl:choose>
        </dl>
    </xsl:template>
    <xsl:template name="getname">
        <xsl:for-each select="//ref:name">
            <xsl:sort select="."/>
            <dl>
                <xsl:choose>
                    <xsl:when test="../../ref:jsp-tag-summary">
                        <dt>
                            <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../ref:uri/@uriPath"/>
                                    <xsl:value-of select="."/>.html</xsl:attribute>
                                <b>&lt;<xsl:value-of select="."/>&gt;</b>
                            </xsl:element> - JSP tag in the <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="../../ref:display-name">
                                        <xsl:value-of select="../../ref:display-name"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="../../ref:prefix"/>
                                    </xsl:otherwise>
                                </xsl:choose> (<xsl:value-of
                            select="../../ref:taglib-version"/>)</xsl:element> library. </dt>
                        <dd>
                            <xsl:call-template name="choose-tag-lead"/>
                        </dd>
                    </xsl:when>
                    <xsl:when test="../../ref:attribute-summary">
                        <dt>
                            <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../../../ref:uri/@uriPath"/>
                                    <xsl:value-of select="../../../ref:name"/>.html#<xsl:value-of select="."/>
                                </xsl:attribute>
                                <b>
                                    <xsl:value-of select="."/>
                                </b>
                            </xsl:element> - Attribute of <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../../../ref:uri/@uriPath"/>
                                    <xsl:value-of select="../../../ref:name"/>.html</xsl:attribute>
                                    &lt;<xsl:value-of select="../../../ref:name"/>&gt;
                            </xsl:element> tag in the <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../../../ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="../../../../ref:display-name">
                                        <xsl:value-of select="../../../../ref:display-name"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="../../../../ref:prefix"/>
                                    </xsl:otherwise>
                                </xsl:choose> (<xsl:value-of
                            select="../../../../ref:taglib-version"/>)</xsl:element> library. </dt>
                        <dd>
                            <xsl:call-template name="choose-attr-lead"/>
                        </dd>
                    </xsl:when>
                    <xsl:when test="../ref:function-summary">
                        <dt>
                            <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../ref:uri/@uriPath"/>
                                    <xsl:value-of select="."/>.html</xsl:attribute>
                                <b>
                                    <xsl:value-of select="../ref:signature"/>
                                </b>
                            </xsl:element> - Function in the <xsl:element name="a">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="../../ref:uri/@uriPath"/>taglib-summary.html</xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="../../ref:display-name">
                                        <xsl:value-of select="../../ref:display-name"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="../../ref:prefix"/>
                                    </xsl:otherwise>
                                </xsl:choose> (<xsl:value-of
                            select="../../ref:taglib-version"/>)</xsl:element> library. </dt>
                        <dd>
                            <xsl:call-template name="choose-function-lead"/>
                        </dd>
                    </xsl:when>
                </xsl:choose>
            </dl>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="choose-tag-lead">
        <xsl:choose>
            <xsl:when test="$jsptagref='true' and ../ref:tagdescription[@prefix='jsptagref']/ref:lead">
                <xsl:if test="../ref:tagdescription/ref:lead">
                    <xsl:call-template name="tagdescription-lead"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$tld='true' and ../ref:tld-description">
                <xsl:if test="../ref:tld-description">
                    <xsl:call-template name="tld-description"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$standard='true' and ../ref:handler-class/ref:description/ref:lead">
                <xsl:if test="../ref:handler-class/ref:description/ref:lead">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="not($jsptagref='true') and not($tld='true') and not($standard='true')">
                <xsl:if test="../ref:tld-description">
                    <xsl:call-template name="tld-description"/>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- LEAD Sentence ///////////////////////////////////////////////////////////////////////////-->
    <xsl:template name="tld-description">
        <xsl:value-of select="../ref:tld-description"/>
    </xsl:template>
    <xsl:template name="tagdescription-lead">
        <xsl:for-each select="../ref:tagdescription/ref:lead">
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
    <xsl:template name="attributedescription">
        <xsl:for-each select="../ref:attributedescription/ref:detail">
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
        <xsl:for-each select="../ref:tag-class-description/ref:lead">
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
            <xsl:when test="$jsptagref='true' and ../ref:attributedescription/ref:detail">
                <xsl:call-template name="attributedescription"/>
            </xsl:when>
            <xsl:when test="$tld='true' and ../ref:tld-description">
                <xsl:if test="../ref:tld-description">
                    <xsl:call-template name="tld-description"/>
                </xsl:if>
            </xsl:when>
            <!--            
                <xsl:when test="$standard='true'">
            <xsl:if test="../ref:handler-class/ref:description/ref:lead">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
-->
            <xsl:when test="not($jsptagref='true') and not($tld='true') and not($standard='true')">
                <xsl:if test="../ref:tld-description">
                    <xsl:call-template name="tld-description"/>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="choose-function-lead">
        <xsl:choose>
            <xsl:when test="$jsptagref='true' and ../ref:tagdescription/ref:lead">
                <xsl:if test="../ref:tagdescription/ref:lead">
                    <xsl:call-template name="tagdescription-lead"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$tld='true' and ../ref:tld-description">
                <xsl:if test="../ref:tld-description">
                    <xsl:call-template name="tld-description"/>
                </xsl:if>
            </xsl:when>
            <!--            
                <xsl:when test="$standard='true'">
            <xsl:if test="../ref:handler-class/ref:description/ref:lead">
                    <xsl:call-template name="handlerdescription-lead"/>
                </xsl:if>
            </xsl:when>
-->
            <xsl:when test="not($jsptagref='true') and not($tld='true') and not($standard='true')">
                <xsl:if test="../ref:tld-description">
                    <xsl:call-template name="tld-description"/>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
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
                                </a> &#160;</td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">|&#160; <font class="NavBarFont1">
                                    <b>Library</b>
                                </font> &#160;</td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">|&#160; <font class="NavBarFont1">
                                    <b>Tag</b>
                                </font>&#160;</td>
                            <xsl:if test="$noIndex='false'">
                                <td bgcolor="#EEEEFF" class="NavBarCell1Rev">&#160; <font class="NavBarFont1Rev">
                                        <b>Index</b>
                                    </font>&#160;</td>
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
                        <font size="-2"> &#160;<a target="_top" href="index.html">
                                <b>FRAMES</b>
                            </a>&#160; &#160;<a target="_top" href="index-all.html">
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
