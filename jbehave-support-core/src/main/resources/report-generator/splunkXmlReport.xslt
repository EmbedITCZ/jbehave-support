<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="splunk">
        <xsl:param name="storyIndex"/>
        <xsl:for-each select="splunk">
            <p>
                <div class="card">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('splunk-queries',$storyIndex)"/>
                    </xsl:attribute>
                    <div class="card-header">
                        <i class="fa fa-file-text-o" aria-hidden="true"></i>
                        Splunk queries (<a href="#splunk-queries-body" id="expand-all-splunk-queries">Toggle all queries</a>)
                        <a href="#splunk-queries-body" data-toggle="collapse" class="float-right">Collapse</a>
                    </div>
                    <div id="splunk-queries-body" class="card-body collapse show">
                        <xsl:choose>
                            <xsl:when test="searchResult">
                                <xsl:call-template name="searchResult"/>
                            </xsl:when>
                            <xsl:otherwise>No splunk queries available</xsl:otherwise>
                        </xsl:choose>
                    </div>
                </div>
            </p>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="searchResult">
        <xsl:for-each select="searchResult">
            <xsl:variable name="searchResultNum">
                <xsl:number level="any"/>
            </xsl:variable>

            <div>
                <xsl:attribute name="class">
                    <xsl:if test="not(record)">emptySplunkSearch</xsl:if>
                </xsl:attribute>

                <a data-toggle="collapse">
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="record">pointerCursor</xsl:when>
                            <xsl:otherwise>inactiveLink</xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>

                    <xsl:if test="record">
                        <xsl:attribute name="href">
                            #search-result-<xsl:value-of select="$searchResultNum"/>
                        </xsl:attribute>
                        <i class="fa fa-plus-circle" aria-hidden="true"/>
                    </xsl:if>
                    <xsl:value-of select="query" disable-output-escaping="yes"/>
                </a>

                <div id="search-result-{$searchResultNum}" class="collapse">
                    <xsl:if test="record">
                        <table class="table table-sm table-hover">
                            <thead>
                                <tr>
                                    <th>Time</th>
                                    <th>Message</th>
                                </tr>
                            </thead>
                            <xsl:for-each select="record">
                                <tr>
                                    <td>
                                        <xsl:value-of select="time"/>
                                    </td>
                                    <td>
                                        <xsl:value-of select="message"/>
                                    </td>
                                </tr>
                            </xsl:for-each>
                        </table>>
                    </xsl:if>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
