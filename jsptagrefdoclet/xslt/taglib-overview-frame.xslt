<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema">
    <xsl:param name="taglibLocation"/>
    <xsl:param name="doc-root" select="$taglibLocation"/>
    <xsl:param name="docTitle"/>
    <xsl:param name="windowTitle"/>
    <!--=============================================================== 
     Makes the individual taglib-overview-frame.html pages.
    ================================================================-->
    <xsl:template match="/">
        <html>
            <head>
                <title>All Tags/Functions <xsl:if test="string($windowTitle)"> (<xsl:value-of select="$windowTitle"/>)</xsl:if>
                </title>
                <link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
            </head>
            <body bgcolor="white">
                <table border="0" width="100%" summary="">
                    <tr>
                        <th align="left" nowrap="true">
                            <font size="+1" CLASS="FrameTitleFont">
                                <b/>
                            </font>
                        </th>
                    </tr>
                </table>
                <table border="0" width="100%" summary="">
                    <tr>
                        <td nowrap="true">
                            <font class="FrameItemFont">
                                <a href="alltaglibs-frame.html" target="lower-left">All Libraries</a>
                            </font>
                            <p>
                                <font size="+1" class="FrameHeadingFont">Libraries</font>
                                <br/>
                                <font class="FrameItemFont">
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
                                    <xsl:for-each select="ref:taglib-summaries/ref:taglib-summary">
                                        <xsl:sort select="ref:prefix"/>
                                        <xsl:if test="ref:jsp-tag-summary or ref:function-summary">
                                            <xsl:element name="a">
                                                <xsl:attribute name="href">
                                                  <xsl:value-of select="ref:uri/@uriPath"/>taglib-frame.html</xsl:attribute>
                                                <xsl:attribute name="target">lower-left</xsl:attribute>
                                                <xsl:choose>
                                                  <xsl:when test="ref:display-name">
                                                      <xsl:value-of select="ref:display-name"/> (<xsl:value-of select="ref:taglib-version"/>)
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                      <xsl:value-of select="ref:prefix"/>  (<xsl:value-of select="ref:taglib-version"/>)
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <br/>
                                            </xsl:element>
                                        </xsl:if>
                                    </xsl:for-each>
                                </font>
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
