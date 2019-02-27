<?xml version="1.0"?>
<!--
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ref="http://apache.org/beehive/netui/tools/doclet/schema" xmlns:xalan="http://xml.apache.org/xslt">
    <!-- Makes the header and footer NAVBAR Sections ////////////////////////////////////////////////-->
    <xsl:template name="navbar">
        <xsl:param name="relPathToRoot"/>
        <xsl:param name="displayDetails"/>
        <!--    
    <xsl:param name="uri-path" select="ref:jsp-tag/ref:handler-class/@packagePath" />
-->
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
                                </a>
                                <!--&#160;|-->
                            </td>
                            <!--<td bgcolor="#EEEEFF" class="NavBarCell1">    
                                <a href="{$relPathToRoot}/deprecated-list.html" style="text-decoration:none"><font class="NavBarFont1"><b>Deprecated</b></font></a>&#160;|
                            </td>
                            <td bgcolor="#EEEEFF" class="NavBarCell1">    
                                <a href="{$relPathToRoot}/index-all.html" style="text-decoration:none"><font class="NavBarFont1"><b>Index</b></font></a>&#160;|
                            </td>-->
                        </tr>
                    </table>
                </td>
            </tr>
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
                        <font SIZE="-2"> Implementing Class: <xsl:choose>
                                <xsl:when test="ref:jsp-tag/ref:handler-class/@packagePath">
                                    <xsl:element name="a">
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="ref:jsp-tag/ref:handler-class/@packagePath"/>
                                            <xsl:value-of select="ref:jsp-tag/ref:handler-class/@typeName"/>.html</xsl:attribute>
                                        <xsl:attribute name="target">_blank</xsl:attribute>
                                        <b>
                                            <xsl:value-of
                                                select="ref:jsp-tag/ref:handler-class/@inPackage"/>.<xsl:value-of select="ref:jsp-tag/ref:handler-class/@typeName"/>
                                        </b>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="ref:jsp-tag/ref:handler-class/@inPackage"/>
                                    <xsl:text>.</xsl:text>
                                    <xsl:value-of select="ref:jsp-tag/ref:handler-class/@typeName"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </font>
                    </td>
                </tr>
            </xsl:if>
        </table>
        <hr/>
    </xsl:template>
</xsl:stylesheet>
