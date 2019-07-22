<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
    </xsl:template>

    <!-- ws -->
    <xsl:template match="ws">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('soap-calls',$storyIndex)"/>
                </xsl:attribute>
                <div class="card-header">
                    <i class="fa fa-exchange" aria-hidden="true"></i>
                    SOAP calls (<a href="#soap-calls-body" id="expand-all-soap-calls">Toggle all requests/responses</a>)
                    <a href="#soap-calls-body" data-toggle="collapse" class="float-right">Collapse</a>
                </div>
                <div id="soap-calls-body" class="card-body collapse show">
                    <xsl:choose>
                        <xsl:when test="requestResponse">
                            <xsl:apply-templates select="requestResponse" mode="ws"/>
                        </xsl:when>
                        <xsl:otherwise>No SOAP calls available</xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="requestResponse" mode="ws">
        <xsl:variable name="callNum">
            <xsl:number level="any"/>
        </xsl:variable>

        <div>
            <a href="#soap-call-{$callNum}" data-toggle="collapse" class="pointerCursor">
                <xsl:value-of select="current()/request/@type"/> ↔ <xsl:value-of select="current()/response/@type"/>
            </a>
            <div id="soap-call-{$callNum}" class="collapse">
                <ul class="list-group list-group-flush">
                    <xsl:apply-templates select="request | response" mode="ws"/>
                </ul>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="request" mode="ws">
        <li class="list-group-item list-group-item-light">
            <strong>Request:
                <span class="time-string-millis">
                    <xsl:value-of select="@time"/>
                </span>
            </strong>
            <xsl:value-of select="concat(' ', @type)"/>
            <xsl:if test=".">
                <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre" title="Copy to clipboard">
                    <i class="fa fa-copy" aria-hidden="true"></i>
                </button>
                <pre class="mb-0">
                    <xsl:value-of select="." disable-output-escaping="yes"/>
                </pre>
            </xsl:if>
        </li>
    </xsl:template>

    <xsl:template match="response" mode="ws">
        <li class="list-group-item list-group-item-light">
            <strong>Response:
                <span class="time-string-millis">
                    <xsl:value-of select="@time"/>
                </span>
            </strong>
            <xsl:value-of select="concat(' ', @type)"/>
            <xsl:if test=".">
                <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre" title="Copy to clipboard">
                    <i class="fa fa-copy" aria-hidden="true"></i>
                </button>
                <pre class="mb-0">
                    <xsl:value-of select="." disable-output-escaping="yes"/>
                </pre>
            </xsl:if>
        </li>
    </xsl:template>

</xsl:stylesheet>
