<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- ws -->
    <xsl:template name="ws">
        <xsl:param name="storyIndex"/>
        <xsl:for-each select="ws">
            <p>
                <div class="card">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('soap-calls',$storyIndex)"/>
                    </xsl:attribute>
                    <div class="card-header">
                        <i class="fa fa-exchange" aria-hidden="true"></i>
                        SOAP calls (<a href="#soap-calls-body" id="expand-all-soap-calls">Toggle all
                        requests/responses</a>)
                        <a href="#soap-calls-body" data-toggle="collapse" class="float-right">Collapse</a>
                    </div>
                    <div id="soap-calls-body" class="card-body collapse show">
                        <xsl:choose>
                            <xsl:when test="requestResponse">
                                <xsl:call-template name="showRequestResponse"/>
                            </xsl:when>
                            <xsl:otherwise>No SOAP calls available</xsl:otherwise>
                        </xsl:choose>
                    </div>
                </div>
            </p>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="showRequestResponse">
        <xsl:for-each select="requestResponse">
            <xsl:variable name="callNum">
                <xsl:number level="any"/>
            </xsl:variable>

            <div>
                <a href="#soap-call-{$callNum}" data-toggle="collapse" class="pointerCursor">
                    <xsl:value-of select="current()/request/@type"/> ↔
                    <xsl:value-of select="current()/response/@type"/>
                </a>
                <div id="soap-call-{$callNum}" class="collapse">
                    <ul class="list-group list-group-flush">
                        <xsl:call-template name="showRequest"/>
                        <xsl:call-template name="showResponse"/>
                    </ul>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="showRequest">
        <xsl:for-each select="request">
            <li class="list-group-item list-group-item-light">
                <strong>Request:
                    <span class="time-string-millis">
                        <xsl:value-of select="@time"/>
                    </span>
                </strong>
                <xsl:value-of select="concat(' ', @type)"/>
                <xsl:if test=".">
                    <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre"
                            title="Copy to clipboard">
                        <i class="fa fa-copy" aria-hidden="true"></i>
                    </button>
                    <pre class="mb-0">
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </pre>
                </xsl:if>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="showResponse">
        <xsl:for-each select="response">
            <li class="list-group-item list-group-item-light">
                <strong>Response:
                    <span class="time-string-millis">
                        <xsl:value-of select="@time"/>
                    </span>
                </strong>
                <xsl:value-of select="concat(' ', @type)"/>
                <xsl:if test=".">
                    <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre"
                            title="Copy to clipboard">
                        <i class="fa fa-copy" aria-hidden="true"></i>
                    </button>
                    <pre class="mb-0">
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </pre>
                </xsl:if>
            </li>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
