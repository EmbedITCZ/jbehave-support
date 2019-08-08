<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
    </xsl:template>
    <xsl:template match="stepScreenshots">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('step-screenshots',$storyIndex)"/>
                </xsl:attribute>
                <div class="card-header">
                    <i class="fa fa-exchange" aria-hidden="true"></i>
                    Step screen shots (<a href="#step-screen-shots" id="expand-all-step-screenshots">Toggle all screenshots</a>)
                    <a href="#step-screen-shots" data-toggle="collapse" class="float-right">Collapse</a>
                </div>
                <div id="step-screen-shots" class="card-body collapse show">
                    <xsl:choose>
                        <xsl:when test="stepScreenshot">
                            <xsl:apply-templates select="stepScreenshot"/>
                        </xsl:when>
                        <xsl:otherwise>No screenshots available</xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="stepScreenshot">
        <xsl:variable name="screenshotNum">
            <xsl:number level="any"/>
        </xsl:variable>
        <div>
            <a class="pointerCursor" data-toggle="collapse">
                <xsl:attribute name="href">
                    <xsl:value-of select="concat('#step-screen-shot-',$screenshotNum)"/>
                </xsl:attribute>
                <i class="fa fa-camera-retro" aria-hidden="true"/>
                <xsl:value-of select="concat('Screenshot_',$screenshotNum)"/>
            </a>
            <div class="collapse">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('step-screen-shot-',$screenshotNum)"/>
                </xsl:attribute>
                <img class="report-screenshot">
                    <xsl:attribute name="src">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </img>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
