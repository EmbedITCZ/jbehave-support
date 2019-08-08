<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:variable name="totalStories" select="count(//story)"/>

    <xsl:template match="/">
    </xsl:template>

    <!--scenario -->
    <xsl:template match="scenario">
        <xsl:param name="storyIndex"/>
        <xsl:variable name="scenarioIndex" select="count(preceding-sibling::scenario)+1"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('scenario-overview',$storyIndex)"/>
                </xsl:attribute>
                <div>
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="not(descendant::step[@outcome='failed'])">card-header bg-success
                                text-white
                            </xsl:when>
                            <xsl:otherwise>card-header bg-danger text-white</xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    Scenario:
                    <xsl:value-of select="@title"/>
                    <a data-toggle="collapse" class="float-right align-middle text-white">
                        <xsl:attribute name="href">
                            <xsl:value-of select="concat('#scenario-overview-body',$storyIndex,$scenarioIndex)"/>
                        </xsl:attribute>
                        Collapse
                    </a>
                </div>
                <div class="collapse show">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('scenario-overview-body',$storyIndex,$scenarioIndex)"/>
                    </xsl:attribute>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-6">
                                <xsl:apply-templates select="meta"/>
                            </div>
                            <div class="col-6">
                                <xsl:call-template name="renderStepOccurrence"/>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header">
                            <i class="fa fa-list-ul" aria-hidden="true"/>
                            Test Steps
                            <a data-toggle="collapse" class="float-right">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('#test-steps-',$storyIndex,$scenarioIndex)"/>
                                </xsl:attribute>
                                Collapse
                            </a>
                        </div>
                        <div class="card-body collapse show">
                            <xsl:attribute name="id">
                                <xsl:value-of select="concat('test-steps-',$storyIndex,$scenarioIndex)"/>
                            </xsl:attribute>

                            <xsl:apply-templates select="story" mode="given"/>
                            <xsl:apply-templates select="step"/>
                        </div>
                    </div>

                    <xsl:if test="following-sibling::*[1]/testContext/values">
                        <div class="card">
                            <div class="card-header">
                                <i class="fa fa-table" aria-hidden="true"/>
                                Test Context
                                <a data-toggle="collapse" class="float-right">
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat('#test-context-',$storyIndex, $scenarioIndex)"/>
                                    </xsl:attribute>
                                    Collapse
                                </a>
                            </div>
                            <div class="card-body collapse show">
                                <xsl:attribute name="id">
                                    <xsl:value-of select="concat('test-context-',$storyIndex, $scenarioIndex)"/>
                                </xsl:attribute>
                                <xsl:apply-templates select="following-sibling::*[1]/testContext"/>
                            </div>
                        </div>
                    </xsl:if>

                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template name="renderStepOccurrence">
        <div class="progress" style="height: 18px;">
            <div class="progress-bar bg-success" role="progressbar" title="successful">
                <xsl:variable name="percent"
                              select="format-number((count(step[@outcome='successful']) div count(step)), '0%')"/>
                <xsl:attribute name="style">
                    width:
                    <xsl:value-of select="$percent"/>
                </xsl:attribute>
                <xsl:value-of select="$percent"/>
            </div>
            <xsl:if test="count(step[@outcome='failed']) > 0">
                <div class="progress-bar bg-danger" role="progressbar" title="failed">
                    <xsl:variable name="percent"
                                  select="format-number((count(step[@outcome='failed']) div count(step)), '0%')"/>
                    <xsl:attribute name="style">
                        width:
                        <xsl:value-of select="$percent"/>
                    </xsl:attribute>
                    <xsl:value-of select="$percent"/>
                </div>
            </xsl:if>
            <xsl:if test="count(step[@outcome='ignorable']) > 0">
                <div class="progress-bar bg-warning" role="progressbar" title="ignored">
                    <xsl:variable name="percent"
                                  select="format-number((count(step[@outcome='ignorable']) div count(step)), '0%')"/>
                    <xsl:attribute name="style">
                        width:
                        <xsl:value-of select="$percent"/>
                    </xsl:attribute>
                    <xsl:value-of select="$percent"/>
                </div>
            </xsl:if>
            <xsl:if test="count(step[@outcome='notPerformed']) > 0">
                <div class="progress-bar bg-secondary" role="progressbar" title="not performed">
                    <xsl:variable name="percent"
                                  select="format-number((count(step[@outcome='notPerformed']) div count(step)), '0%')"/>
                    <xsl:attribute name="style">
                        width:
                        <xsl:value-of select="$percent"/>
                    </xsl:attribute>
                    <xsl:value-of select="$percent"/>
                </div>
            </xsl:if>
            <div class="progress-bar bg-info" role="progressbar" title="comment">
                <xsl:variable name="percent"
                              select="format-number((count(step[@outcome='comment']) div count(step)), '0%')"/>
                <xsl:attribute name="style">
                    width:
                    <xsl:value-of select="$percent"/>
                </xsl:attribute>
                <xsl:value-of select="$percent"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="story" mode="given">
        <xsl:variable name="isFailed" select="count(descendant::step[@outcome='failed']) > 0"/>
        <div>
            <xsl:choose>
                <xsl:when test="$isFailed">
                    <i class="fa fa-exclamation-circle text-danger" aria-hidden="true"/>
                </xsl:when>
                <xsl:otherwise>
                    <i class="fa fa-check-circle text-success" aria-hidden="true"/>
                </xsl:otherwise>
            </xsl:choose>

            <span>
                <a href="#">
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="$isFailed">text-danger</xsl:when>
                            <xsl:otherwise>text-success</xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:variable name="storyID"
                                  select="$totalStories - count(following::story) - count(descendant::story)"/>
                    <xsl:attribute name="onclick">
                        <xsl:value-of select="concat('display(&quot;story',$storyID,'&quot;)')"/>
                    </xsl:attribute>
                    Given story:
                    <xsl:value-of select="@path"/>

                </a>
            </span>
        </div>
    </xsl:template>

    <!-- testcontext -->
    <xsl:template match="testContext">
        <table class="table table-sm table-hover">
            <thead>
                <tr>
                    <th>name</th>
                    <th>data</th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="values">
                    <tr>
                        <td>
                            <xsl:value-of select="key"/>
                        </td>
                        <td>
                            <xsl:value-of select="value"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="parameters" mode="scenario">
        <table class="table table-sm table-hover">
            <thead>
                <xsl:apply-templates select="names"/>
            </thead>
            <tbody>
                <xsl:apply-templates select="values"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="names">
        <tr>
            <xsl:apply-templates select="name"/>
        </tr>
    </xsl:template>

    <xsl:template match="name">
        <th>
            <xsl:value-of select="."/>
        </th>
    </xsl:template>

    <xsl:template match="values">
        <tr>
            <xsl:apply-templates select="value"/>
        </tr>
    </xsl:template>

    <xsl:template match="value">
        <td>
            <xsl:value-of select="."/>
        </td>
    </xsl:template>

</xsl:stylesheet>
