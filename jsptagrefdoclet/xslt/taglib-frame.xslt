<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema"
    xmlns:xalan="org.apache.xalan.xslt.extensions.Redirect" extension-element-prefixes="xalan" version="1.0">
    <xsl:param name="taglibLocation"/>
    <xsl:param name="docTitle"/>
    <xsl:param name="windowTitle"/>
    <xsl:template match="/">
        <xsl:call-template name="make-taglib-frame-pages"/>
    </xsl:template>
    <!-- #### Makes the individual taglib-frame.html pages #### -->
    <xsl:template name="make-taglib-frame-pages">
        <!-- #### Go through the Tag Libraries... #### -->
        <xsl:for-each select="ref:taglib-summaries/ref:taglib-summary">
            <xsl:sort select="ref:prefix"/>
            <!-- #### make an HTML file for each Tag Library... #### -->
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
            <xsl:variable name="filename" select="concat($taglibLocation, '/', ref:uri/@uriPath, 'taglib-frame.html')"/>
            <xsl:variable name="doc-root" select="@docRoot"/>
            <xalan:write select="$filename">
                <html>
                    <head>
                        <link rel="stylesheet" type="text/css" href="{$doc-root}/stylesheet.css" title="Style"/>
                    </head>
                    <body bgcolor="white">
                        <!-- #### make a link to a summary page, to be displayed in the right-hand frame #### -->
                        <table border="0" width="100%" summary="">
                            <tr>
                                <td nowrap="nowrap">
                                    <font size="+1" class="FrameTitleFont"><xsl:element name="a">
                                            <xsl:attribute name="href">taglib-summary.html</xsl:attribute>
                                            <xsl:attribute name="target">right-frame</xsl:attribute>
                                                <xsl:value-of select="$taglib-name"/></xsl:element>
                                    </font>
                                </td>
                            </tr>
                        </table>
                            <table border="0" width="100%" summary="">
                                <tr>
                                    <td nowrap="nowrap">
                                        <font  class="FrameHeadingFont">Tags/Functions</font>&#160; <br/>
                                        <!-- #### go through JSP tags in the current library... #### -->
                                    <xsl:variable name="names" select="ref:jsp-tag-summary/ref:name | ref:function-summary/ref:name"/>
                                    <xsl:for-each select="$names">
                                        <xsl:sort select="."/>
                                        <font class="FrameItemFont">
                                        <xsl:element name="a">
                                            <xsl:attribute name="href">
                                                <xsl:value-of select="."/>.html</xsl:attribute>
                                            <xsl:attribute name="target">right-frame</xsl:attribute>
                                            <xsl:choose>
                                                <xsl:when test="local-name(..)='function-summary'">
                                                    <i><xsl:value-of select="../../ref:prefix"/>:<xsl:value-of select="."/></i>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="../../ref:prefix"/>:<xsl:value-of select="."/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <br/>
                                        </xsl:element>
                                            </font>
                                    </xsl:for-each>
                                    </td>
                                </tr>
                                </table>
                    </body>
                </html>
            </xalan:write>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
