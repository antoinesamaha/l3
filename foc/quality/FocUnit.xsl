<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
version="1.0">
<xsl:template match="/Barmaja">
<html>
<head>
<title>XML in Tree View</title>
<meta name="generator" content="ZZEE Art HTML Listing"/>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<style type="text/css">
 <!--
body {
 font-family: "Times New Roman"; 
 font-size: 12pt; 
 background-color: #ffffff; 
 color:#000000; 
 text-align: left;
}

ul.zzul{
list-style-type:none; 
display: block;
}

span.zzspace{
left:11px;
}

a:visited{
color: #0000ff;
}

a:hover{
color: #ff0000;
}
-->
</style>
<!-- [client side code for collapsing and unfolding branches] -->
<script language="JavaScript">
var firstTime = true
function Toggle(node){
//Unfold the branch if it isn't visible

	if(node.nextSibling.style.display == 'none'){
    // Change the image (if there is an image)
	    if (node.children.length > 0){
			if(node.children.item(0).tagName == "IMG"){
	            node.children.item(0).src = "minus.gif";
			}
		}
		node.nextSibling.style.display = '';		
	}
//Collapse the branch if it IS visible
	 else{
    // Change the image (if there is an image)
		if (node.children.length > 0){
			if (node.children.item(0).tagName == "IMG"){
				node.children.item(0).src = "plus.gif";
			}
		}
		node.nextSibling.style.display = 'none';
	}
}

function ToggleAll(){
	if(firstTime){
		firstTime = false
		for( var i = 0; i&lt;document.getElementsByTagName('a').length; i++ ) {
		  Toggle(document.getElementsByTagName('a')[i])
		}
	}
}
</script>
</head>

<body onload="ToggleAll()">
	<ul style="list-style-type:none; margin:0; padding:0;">
	<br/>
	    <xsl:for-each select="/Barmaja/Suite">
		<div align="center"><h2><xsl:value-of select="@name"/></h2></div>
		<br/>
			<table  align="center" border="0" id="allTree" onclick="ToggleAll()" bgcolor="dee7f7" style="font:Georgia, 'Times New Roman', Times, serif; font-weight:bold;">
			  <xsl:for-each select="Test">	
				<tr>
					<td>				
						<xsl:apply-templates select="." mode="render"/>
				   </td>
				   				
						<xsl:choose>
						  <xsl:when test="Status='Success'">
							<td bgcolor="#9BE3A6" valign="top" align="center">
								<xsl:value-of select="Status"/>
							</td>
						  </xsl:when>
						  <xsl:otherwise>
							<td bgcolor="#F82C07" valign="top" align="center">
								<xsl:value-of select="Status"/>
							</td>
								</xsl:otherwise>
						</xsl:choose>
	
				</tr>
			  </xsl:for-each>
			</table>
		</xsl:for-each>		
	</ul>
</body>
</html>

</xsl:template>

<xsl:template match="/" mode="render">
	<xsl:apply-templates mode="render"/>
</xsl:template>
<xsl:template match="*" mode="render">
<table border="0">
	<tr>
		<td/>
		<td/>
		<td/>
		<td>
		<a onClick="Toggle(this)">
			<xsl:if test="local-name()!='Assert'">
				<img src="minus.gif"/>
			</xsl:if>
		
			<xsl:text></xsl:text>
			<xsl:if test="local-name()!='Test'">
				<xsl:value-of select="local-name()"/>			
			</xsl:if>
			
			<xsl:if test="local-name()='Test'">
				<xsl:value-of select="@name"/>
			</xsl:if>

			<xsl:if test="local-name()='Step'">
				- <xsl:value-of select="@sequence"/> : <xsl:value-of select="text()"/>
			</xsl:if>

			<xsl:if test="local-name()='GuiAction'">
				- <xsl:value-of select="@componentType"/>(<xsl:value-of select="@componentName"/>) -> <xsl:value-of select="@action"/>
			</xsl:if>

			<xsl:if test="local-name()='Assert'">
				- <xsl:value-of select="text()"/>
			</xsl:if>			
		</a>
		<div>
			<xsl:if test="local-name()!='Assert'">
				<xsl:apply-templates mode="render"/>
			</xsl:if>
		</div>
		</td>
	</tr>
</table>
</xsl:template>

<xsl:template match="text()" mode="render">
	<xsl:call-template name="escape-ws">
		<xsl:with-param name="text"
		select="translate(.,' ',' ')"/>
	</xsl:call-template>
</xsl:template>

<xsl:template name="escape-ws">
	<xsl:param name="text"/>
	<xsl:choose>
	<xsl:when test="contains($text, ' ')">
	<xsl:call-template name="escape-ws">
	<xsl:with-param name="text" select="substring-before($text, ' ')"/>
	</xsl:call-template>
	<xsl:call-template name="escape-ws">
	<xsl:with-param name="text" select="substring-after($text, ' ')"/>
	</xsl:call-template>
	</xsl:when>
	<xsl:when test="contains($text, ' ')">
	<xsl:value-of select="substring-before($text, ' ')"/>
	<xsl:call-template name="escape-ws">
	<xsl:with-param name="text" select="substring-after($text, ' ')"/>
	</xsl:call-template>
	</xsl:when>
	<xsl:otherwise>
	<xsl:value-of select="$text"/>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>

