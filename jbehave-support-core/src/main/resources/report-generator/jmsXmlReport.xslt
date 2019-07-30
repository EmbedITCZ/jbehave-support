<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="jms">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('jms-messages',$storyIndex)"/>
                </xsl:attribute>
                <div class="card-header">
                    <i class="fa fa-exchange" aria-hidden="true"></i>
                    JMS messages (<a href="#jms-messages-body" id="expand-all-jms-messages">Toggle all messages</a>)
                    <a href="#jms-messages-body" data-toggle="collapse" class="float-right">Collapse</a>
                </div>
                <div id="jms-messages-body" class="card-body collapse show">
                    <xsl:choose>
                        <xsl:when test="message">
                            <xsl:apply-templates select="message"/>
                        </xsl:when>
                        <xsl:otherwise>No JMS messages available</xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="message">
        <xsl:variable name="statementNum">
            <xsl:number level="any"/>
        </xsl:variable>
        <div>
            <a data-toggle="collapse">
                <xsl:attribute name="class">
                    pointerCursor
                </xsl:attribute>

                <xsl:attribute name="href">
                    #jms-message-<xsl:value-of select="$statementNum"/>
                </xsl:attribute>
                <i class="fa fa-plus-circle" aria-hidden="true"/>

                <xsl:value-of select="concat(' ',cid)" disable-output-escaping="yes"/>
            </a>
        </div>

        <div id="jms-message-{$statementNum}" class="collapse">
            <ul class="list-group list-group-flush">
                <xsl:apply-templates select="answers"/>
            </ul>
        </div>
    </xsl:template>

    <xsl:template match="answers">
        <table class="table table-sm table-hover">
            <thead>
                <tr>
                    <th>name</th>
                    <th>data</th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="row">
                    <tr>
                        <xsl:for-each select="column">
                            <td>
                                <xsl:value-of select="."/>
                            </td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
        <xsl:apply-templates select="toString"/>
    </xsl:template>

    <xsl:template match="toString">
        <li class="list-group-item list-group-item-light">
            <strong>Message:</strong>
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
    </xsl:template>
</xsl:stylesheet>
