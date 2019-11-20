<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- ssh -->
    <xsl:template name="serverLog">
        <xsl:param name="storyIndex"/>
        <xsl:for-each select="serverLog">
            <p>
                <div class="card">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('shell-logs',$storyIndex)"/>
                    </xsl:attribute>
                    <div class="card-header">
                        <i class="fa fa-file-text-o" aria-hidden="true"></i>
                        Shell logs (<a href="#shell-logs-body" id="expand-all-shell-logs">Toggle all logs</a>)
                        <a href="#shell-logs-body" data-toggle="collapse" class="float-right">Collapse</a>
                    </div>
                    <div id="shell-logs-body" class="card-body collapse show">
                        <xsl:choose>
                            <xsl:when test="system">
                                <xsl:call-template name="system"/>
                            </xsl:when>
                            <xsl:otherwise>No logs available</xsl:otherwise>
                        </xsl:choose>
                    </div>
                </div>
            </p>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="system">
        <xsl:for-each select="system">
            <div>
                <a href="#sshLogsDetails-{position()}" data-toggle="collapse">
                    +<xsl:value-of select="@system"/>
                </a>
                <div id="sshLogsDetails-{position()}" class="collapse">
                    <xsl:call-template name="log"/>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="log">
        <xsl:for-each select="log">
            <xsl:variable name="logNum">
                <xsl:number level="any"/>
            </xsl:variable>

            <table width="100%" class="sshLog">
                <tbody>
                    <tr>
                        <td width="15%" class="label">Start log:</td>
                        <td width="85%" class="time-string-millis">
                            <xsl:value-of select="@startDate"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="label">End log:</td>
                        <td class="time-string-millis">
                            <xsl:value-of select="@endDate"/>
                        </td>
                    </tr>
                    <xsl:if test="not(@host = 'N/A')">
                        <tr>
                            <td class="label">Host:</td>
                            <td>
                                <xsl:value-of select="@host"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="not(@logPath = 'N/A')">
                        <tr>
                            <td class="label">Log path:</td>
                            <td>
                                <xsl:value-of select="@logPath"/>
                            </td>
                        </tr>
                    </xsl:if>
                </tbody>
            </table>

            <xsl:call-template name="text">
                <xsl:with-param name="logNum" select="$logNum"/>
            </xsl:call-template>
            <xsl:call-template name="file">
                <xsl:with-param name="logNum" select="$logNum"/>
            </xsl:call-template>
            <xsl:call-template name="fail">
                <xsl:with-param name="logNum" select="$logNum"/>
            </xsl:call-template>

            <div id="sshlog-{$logNum}" class="collapse">
                <p>
                    <pre>
                        <xsl:value-of select="."/>
                    </pre>
                </p>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="text">
        <xsl:param name="logNum"/>
        <xsl:for-each select="text">
                <div class="btn-group form-group align-self-center">
                    <a href="#sshlog-{$logNum}" data-toggle="collapse" class="btn btn-sm btn-outline-primary">Show/hide
                        log
                        contents
                    </a>
                    <button type="button" class="btn btn-sm btn-outline-info btn-copy-clipboard"
                            data-selector="#sshlog-{$logNum}" title="Copy to clipboard">
                        <i class="fa fa-copy" aria-hidden="true"></i>
                    </button>
                </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="file">
        <xsl:param name="logNum"/>
        <xsl:for-each select="file">
            <xsl:if test="not(fail)">
                <div>
                    <label>
                        <i>
                            Log content was to long to be printed, it was saved to the
                        </i>
                    </label>
                    <a target="_blank">
                        <xsl:attribute name="href">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                        file.
                    </a>
                </div>
            </xsl:if>
            <xsl:call-template name="fail">
                <xsl:with-param name="logNum" select="$logNum"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="fail">
        <xsl:param name="logNum"/>
        <xsl:for-each select="fail">
            <p>
                <label>
                    <i>
                        No log available
                    </i>
                </label>
                <br/>
                <a href="#sshlog-{$logNum}" data-toggle="collapse" class="btn btn-sm btn-outline-primary">Show/hide
                    stacktrace
                </a>
            </p>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>