<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
    </xsl:template>

    <!-- SQL -->
    <xsl:template match="sql">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('sql-queries',$storyIndex)"/>
                </xsl:attribute>
                <div class="card-header">
                    <i class="fa fa-table" aria-hidden="true"></i>
                    SQL queries (<a href="#sql-queries-body" id="expand-all-sql-queries">Toggle all queries</a>)
                    <a href="#sql-queries-body" data-toggle="collapse" class="float-right">Collapse</a>
                </div>
                <div id="sql-queries-body" class="card-body collapse show">
                    <xsl:choose>
                        <xsl:when test="statementResult">
                            <xsl:apply-templates select="statementResult"/>
                        </xsl:when>
                        <xsl:otherwise>No SQL queries available</xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="statementResult">
        <xsl:variable name="statementNum">
            <xsl:number level="any"/>
        </xsl:variable>

        <div>
            <xsl:attribute name="class">
                <xsl:if test="not(parameters) and not(results)">emptySqlStatement</xsl:if>
            </xsl:attribute>

            <a data-toggle="collapse">
                <xsl:attribute name="class">
                    <xsl:choose>
                        <xsl:when test="parameters or results">pointerCursor</xsl:when>
                        <xsl:otherwise>inactiveLink</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>

                <xsl:if test="parameters or results">
                    <xsl:attribute name="href">
                        #sql-query-<xsl:value-of select="$statementNum"/>
                    </xsl:attribute>
                    <i class="fa fa-plus-circle" aria-hidden="true"/>
                </xsl:if>
                <xsl:value-of select="statement" disable-output-escaping="yes"/>
            </a>

            <div id="sql-query-{$statementNum}" class="collapse">
                <xsl:if test="parameters">
                    <ul class="list-group list-group-flush">
                        <xsl:apply-templates select="parameters" mode="sql"/>
                    </ul>
                </xsl:if>
                <xsl:if test="results">
                    <ul class="list-group list-group-flush">
                        <xsl:apply-templates select="results"/>
                    </ul>
                </xsl:if>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="parameters" mode="sql">
        Parameters:
        <table class="table table-sm table-hover">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="parameter">
                    <tr>
                        <td><xsl:value-of select="name"/></td>
                        <td><xsl:value-of select="value"/></td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>

</xsl:stylesheet>
