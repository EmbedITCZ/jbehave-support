<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="renderStacktraceModal">
        <xsl:param name="scenarioIndex"/>
        <a href="#" data-toggle="modal" class="badge badge-pill badge-danger">
            <xsl:attribute name="data-target">
                <xsl:value-of select="concat('#stacktraceModal', $scenarioIndex)"/>
            </xsl:attribute>
            Stacktrace
        </a>
        <div class="modal fade" tabindex="-1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat('stacktraceModal',$scenarioIndex)"/>
            </xsl:attribute>
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-body">
                        <div class="form-group">
                            <textarea class="form-control" wrap="off" readonly="true" rows="26">
                                <xsl:value-of select="../../failure"/>
                            </textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
