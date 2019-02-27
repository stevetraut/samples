<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema">
    <!--=============================================================== 
     Makes the alltaglibs-frame.html page.
     This page is displayed in the lower-left frame, and lists
     all JSP tags in all tag libraries. 
    ================================================================-->
    <xsl:template match="/">
        <html>
            <head>
                <title>All Tags and Functions</title>
                <link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
            </head>
            <body bgcolor="white">
                <xsl:if test="ref:taglib-summaries/ref:taglib-summary/ref:jsp-tag-summary">
                    <font class="FrameHeadingFont">All Tags/Functions</font>
                    <br/>
                    <table border="0" width="100%" summary="">
                        <tr>
                            <td nowrap="true">
                                <font class="FrameItemFont">
                                    <!-- Loop through all the JSP tags in all libraries: make a link for each tag. -->
                                    <xsl:variable name="names" select="ref:taglib-summaries/ref:taglib-summary/ref:jsp-tag-summary/ref:name | ref:taglib-summaries/ref:taglib-summary/ref:function-summary/ref:name"/>
                                    <xsl:for-each select="$names">
                                        <xsl:sort select="../../ref:prefix"/>
                                        <xsl:element name="a">
                                            <xsl:attribute name="href">
                                                <xsl:value-of select="../../ref:uri/@uriPath"/>
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
                                    </xsl:for-each>
                                </font>
                            </td>
                        </tr>
                    </table>
                </xsl:if>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
