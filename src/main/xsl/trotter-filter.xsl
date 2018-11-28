<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
		xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
		xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
		exclude-result-prefixes="office table text">
  
 <xsl:template match="/">
  <root>
   <xsl:apply-templates select="/*/office:body" />
  </root>
 </xsl:template>

 <xsl:template match="office:body">
  <xsl:apply-templates />
 </xsl:template>

 <xsl:template match="office:spreadsheet">
  <xsl:apply-templates />
 </xsl:template>

 <xsl:template match="office:spreadsheet/table:table">

   <xsl:for-each select="table:table-row[position() &gt; 1]">

     <archive-entry>
       <object-number>
	 <xsl:value-of select="table:table-cell[1]/text:p" />
       </object-number>
       <collection>
 	 <xsl:value-of select="table:table-cell[2]/text:p" />
       </collection>
       <location1>
	 <xsl:value-of select="table:table-cell[3]/text:p" />
       </location1>
       <!--
       <location2>
	 <xsl:value-of select="table:table-cell[4]/text:p" />
       </location2>
       <location3>
	 <xsl:value-of select="table:table-cell[5]/text:p" />
       </location3>
       <location4>
	 <xsl:value-of select="table:table-cell[6]/text:p" />
       </location4>
       <location5>
	 <xsl:value-of select="table:table-cell[7]/text:p" />
       </location5>
       <location6>
	 <xsl:value-of select="table:table-cell[8]/text:p" />
       </location6>
       <location7>
	 <xsl:value-of select="table:table-cell[9]/text:p" />
       </location7>
       <location8>
	 <xsl:value-of select="table:table-cell[10]/text:p" />
       </location8>
       <location9>
	 <xsl:value-of select="table:table-cell[11]/text:p" />
       </location9>
       <location10>
	 <xsl:value-of select="table:table-cell[12]/text:p" />
	 </location1>
       <level>
	 <xsl:value-of select="table:table-cell[13]/text:p" />
	 </level>
	 	 -->
       <name>
	 <xsl:value-of select="table:table-cell[4]/text:p" />
       </name>
       <description>
	 <xsl:value-of select="table:table-cell[5]/text:p" />
       </description>
       <date-created>
	 <xsl:value-of select="table:table-cell[6]/text:p" />
       </date-created>
       <extent>
	 <xsl:value-of select="table:table-cell[7]/text:p" />
       </extent>
       <phys-tech-desc>
	 <xsl:value-of select="table:table-cell[8]/text:p" />
	 </phys-tech-desc>
       <multimedia-name>
         <xsl:value-of select="table:table-cell[9]/text:p" />
       </multimedia-name>
       <copyright>
	 <xsl:value-of select="table:table-cell[10]/text:p" />
       </copyright>
       <multimedia-irn>
	 <xsl:value-of select="table:table-cell[11]/text:p" />
       </multimedia-irn>

     </archive-entry>

   </xsl:for-each>

 </xsl:template>
</xsl:stylesheet>
