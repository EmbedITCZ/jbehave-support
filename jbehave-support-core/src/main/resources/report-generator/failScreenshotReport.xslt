<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="renderScreenshotModal">
        <xsl:param name="scenarioIndex"/>
        <a href="#" data-toggle="modal" class="badge badge-pill badge-info">
            <xsl:attribute name="data-target">
                <xsl:value-of select="concat('#screenshotModal', $scenarioIndex)"/>
            </xsl:attribute>
            Screenshot
        </a>
        <div class="modal fade" tabindex="-1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat('screenshotModal',$scenarioIndex)"/>
            </xsl:attribute>

            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-body">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:value-of select="../../following-sibling::*[1]/errorScreenshots/screenshot"/>
                            </xsl:attribute>
                            <img class="img-fluid" width="770">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="../../following-sibling::*[1]/errorScreenshots/screenshot"/>
                                </xsl:attribute>
                            </img>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
