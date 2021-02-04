<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- REST -->
    <xsl:template name="rest">
        <xsl:param name="storyIndex"/>
        <xsl:for-each select="rest">
            <p>
                <div class="card">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('rest-calls',$storyIndex)"/>
                    </xsl:attribute>
                    <div class="card-header">
                        <i class="fa fa-exchange" aria-hidden="true"></i>
                        REST calls (<a href="#rest-calls-body" id="expand-all-rest-calls">Toggle all
                        requests/responses</a>)
                        <a href="#rest-calls-body" data-toggle="collapse" class="float-right">Collapse</a>
                    </div>
                    <div id="rest-calls-body" class="card-body collapse show">
                        <xsl:choose>
                            <xsl:when test="requestResponse">
                                <xsl:call-template name="requestResponse"/>
                            </xsl:when>
                            <xsl:otherwise>No REST calls available</xsl:otherwise>
                        </xsl:choose>
                    </div>
                </div>
            </p>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="requestResponse">
        <xsl:for-each select="requestResponse">
            <xsl:variable name="callNum">
                <xsl:number level="any"/>
            </xsl:variable>
            <div>
                <a href="#rest-call-{$callNum}" data-toggle="collapse" class="pointerCursor">
                    <strong>
                        [<xsl:value-of select="request/@method"/>]
                    </strong>
                    <xsl:value-of select="request/@url"/>
                </a>
                <div id="rest-call-{$callNum}" class="collapse">
                    <ul class="list-group list-group-flush">
                        <xsl:call-template name="request"/>
                        <xsl:call-template name="response"/>
                    </ul>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="request">
        <xsl:for-each select="request">
            <li class="list-group-item list-group-item-light">
                <strong>Request:
                    <span class="time-string-millis">
                        <xsl:value-of select="@time"/>
                    </span>
                </strong>
                <xsl:if test="body">
                    <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre"
                            title="Copy to clipboard">
                        <i class="fa fa-copy" aria-hidden="true"></i>
                    </button>
                </xsl:if>
                <br/>
                <xsl:if test="headers">
                    <xsl:call-template name="headers">
                        <xsl:with-param name="requestResponse" select="'request'"/>
                    </xsl:call-template>
                </xsl:if>
                <xsl:if test="body">
                    <pre class="json-message">
                        <xsl:value-of select="body" disable-output-escaping="yes"/>
                    </pre>
                </xsl:if>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="response">
        <xsl:for-each select="response">
            <li class="list-group-item list-group-item-light">
                <strong>Response:
                    <span class="time-string-millis">
                        <xsl:value-of select="@time"/>
                    </span>
                </strong>
                status:
                <xsl:value-of select="@status"/>

                <xsl:if test="headers">
                    <br/>
                    <xsl:call-template name="headers">
                        <xsl:with-param name="requestResponse" select="'response'"/>
                    </xsl:call-template>
                    <br/>
                </xsl:if>
                <xsl:if test="body">
                    <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre"
                            title="Copy to clipboard">
                        <i class="fa fa-copy" aria-hidden="true"></i>
                    </button>
                    <pre class="json-message">
                        <xsl:value-of select="body" disable-output-escaping="yes"/>
                    </pre>
                </xsl:if>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name = "headers">
        <xsl:param name="requestResponse"/>
        <xsl:variable name="headersNum">
            <xsl:number level="any"/>
        </xsl:variable>
        <a href="#header-{$requestResponse}-{$headersNum}" data-toggle="collapse" class="pointerCursor">
            Headers
        </a>
        <div id="header-{$requestResponse}-{$headersNum}" class="collapse">
        <table class="table table-sm table-hover">
            <thead>
                <tr>
                    <th>key</th>
                    <th>value</th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="headers/header">
                    <tr>
                        <td>
                            <xsl:value-of select="@key"/>
                        </td>
                        <td>
                            <xsl:value-of select="@value"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
        </div>
    </xsl:template>

</xsl:stylesheet>