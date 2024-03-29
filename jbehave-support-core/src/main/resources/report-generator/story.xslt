<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:variable name="totalStories" select="count(//story)"/>

    <!--scenario -->
    <xsl:template name="scenario">
        <xsl:param name="storyIndex"/>
        <xsl:for-each select="scenario">
            <xsl:variable name="scenarioIndex" select="count(preceding-sibling::scenario)+1"/>
            <p>
                <div class="card">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('scenario-overview',$storyIndex)"/>
                    </xsl:attribute>
                    <div>
                        <xsl:attribute name="class">
                            <xsl:choose>
                                <xsl:when test="not(descendant::steps/step[@outcome='failed'])">card-header bg-success
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
                                    <xsl:call-template name="meta"/>
                                </div>
                                <div class="col-6">
                                    <xsl:choose>
                                        <xsl:when test="not(descendant::examples)">
                                            <xsl:call-template name="renderStepOccurrence">
                                                <xsl:with-param name="stepsPath" select="steps/step"/>
                                            </xsl:call-template>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:call-template name="renderStepOccurrence">
                                                <xsl:with-param name="stepsPath" select="examples/steps/step"/>
                                            </xsl:call-template>
                                        </xsl:otherwise>
                                    </xsl:choose>
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

                                <xsl:call-template name="story"/>

                                <xsl:choose>
                                    <xsl:when test="not(descendant::examples)">
                                        <xsl:call-template name="step">
                                            <xsl:with-param name="stepsPath" select="steps/step"/>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <!-- print steps-->
                                        <!-- TODO format examples table, it has to be written differently in xml-->
                                        <div class="card-header">
                                            Scenario test steps (
                                            <a data-toggle="collapse">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                        select="concat('#example-scenario-test-steps',$storyIndex,$scenarioIndex)"/>
                                                </xsl:attribute>
                                                Collapse
                                            </a>
                                            )
                                        </div>
                                        <div class="card-body collapse show">
                                            <xsl:attribute name="id">
                                                <xsl:value-of
                                                    select="concat('example-scenario-test-steps',$storyIndex,$scenarioIndex)"/>
                                            </xsl:attribute>
                                            <xsl:call-template name="step">
                                                <xsl:with-param name="stepsPath" select="examples/step"/>
                                            </xsl:call-template>
                                        </div>
                                        <!-- print examples table -->
                                        <div class="card-header">
                                            Scenario Examples table (
                                            <a data-toggle="collapse">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                        select="concat('#examples-table-',$storyIndex,$scenarioIndex)"/>
                                                </xsl:attribute>
                                                Collapse
                                            </a>
                                            )
                                        </div>
                                        <div class="card-body collapse show" style="overflow:auto;">
                                            <xsl:attribute name="id">
                                                <xsl:value-of
                                                    select="concat('examples-table-',$storyIndex,$scenarioIndex)"/>
                                            </xsl:attribute>
                                            <table class="table table-bordered">
                                                <tr>
                                                    <xsl:for-each select="examples/parameters/names/name">
                                                        <th>
                                                            <xsl:value-of select="."/>
                                                        </th>
                                                    </xsl:for-each>
                                                </tr>
                                                <xsl:for-each select="examples/parameters/values">
                                                    <tr>
                                                        <xsl:for-each select="value">
                                                            <td>
                                                                <xsl:value-of select="."/>
                                                            </td>
                                                        </xsl:for-each>
                                                    </tr>
                                                </xsl:for-each>
                                            </table>
                                        </div>
                                        <div class="card-header">
                                            Parametrized scenarios (<a href="#expand-all-example-scenarios"
                                                                       id="expand-all-example-scenarios">Toggle all
                                            scenarios</a>)
                                        </div>
                                        <!-- process scenarios -->
                                        <xsl:for-each select="examples/child::*">

                                            <!-- print scenario steps -->
                                            <xsl:if test="self::steps">
                                                <xsl:variable name="exampleIndex"
                                                              select="count(preceding-sibling::example)"/>
                                                <!-- TODO format examples table, it has to be written differently in xml-->
                                                <xsl:variable name="exampleStepsIndex"
                                                              select="count(preceding-sibling::example)"/>
                                                <div id="example-scenarios">
                                                    <div class="card-body collapse">
                                                        <xsl:attribute name="id">
                                                            <xsl:value-of
                                                                select="concat('example-scenario',$storyIndex,$scenarioIndex,$exampleIndex)"/>
                                                        </xsl:attribute>
                                                        <div>
                                                            <xsl:attribute name="id">
                                                                <xsl:value-of
                                                                    select="concat('parameters-example',$storyIndex,$scenarioIndex,$exampleIndex)"/>
                                                            </xsl:attribute>
                                                            <xsl:value-of select="preceding-sibling::example[1]"/>
                                                        </div>
                                                        <div>
                                                            <xsl:attribute name="id">
                                                                <xsl:value-of
                                                                    select="concat('test-steps-example',$storyIndex,$scenarioIndex,$exampleStepsIndex)"/>
                                                            </xsl:attribute>
                                                            <xsl:call-template name="step">
                                                                <xsl:with-param name="stepsPath" select="step"/>
                                                            </xsl:call-template>
                                                        </div>
                                                    </div>
                                                </div>
                                            </xsl:if>
                                            <!-- print scenario header -->
                                            <xsl:if test="self::example">
                                                <xsl:variable name="exampleIndex"
                                                              select="count(preceding-sibling::example)+1"/>
                                                <xsl:variable name="divClass">
                                                    <xsl:choose>
                                                        <xsl:when
                                                            test="count(following-sibling::steps[1]/step[@outcome='failed']) > 0">
                                                            card-header bg-danger text-white
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            card-header bg-success text-white
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </xsl:variable>
                                                <div>
                                                    <xsl:attribute name="class">
                                                        <xsl:value-of select="$divClass"/>
                                                    </xsl:attribute>
                                                    <i class="fa fa-bars" aria-hidden="true"/>
                                                    <xsl:value-of
                                                        select="concat(' ',$exampleIndex,'. parameterized scenario')"/>
                                                    <a data-toggle="collapse" class="float-right">
                                                        <xsl:attribute name="href">
                                                            <xsl:value-of
                                                                select="concat('#example-scenario',$storyIndex,$scenarioIndex,$exampleIndex)"/>
                                                        </xsl:attribute>
                                                        Collapse/Show
                                                    </a>
                                                </div>
                                            </xsl:if>
                                        </xsl:for-each>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </div>
                        </div>

                        <xsl:if test="following-sibling::*[1]/testContext/values">
                            <div class="card">
                                <div class="card-header">
                                    <i class="fa fa-table" aria-hidden="true"/>
                                    Test Context
                                    <a data-toggle="collapse" class="float-right">
                                        <xsl:attribute name="href">
                                            <xsl:value-of
                                                select="concat('#test-context-',$storyIndex, $scenarioIndex)"/>
                                        </xsl:attribute>
                                        Collapse
                                    </a>
                                </div>
                                <div class="card-body collapse show">
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="concat('test-context-',$storyIndex, $scenarioIndex)"/>
                                    </xsl:attribute>
                                    <xsl:call-template name="testContext"/>
                                </div>
                            </div>
                        </xsl:if>

                    </div>
                </div>
            </p>
        </xsl:for-each>
    </xsl:template>

    <!-- meta -->
    <xsl:template name="meta">
        <xsl:for-each select="meta">
            <xsl:for-each select="property">
                <div class="row">
                    <div class="col-6 col-sm-3 col-lg-2 font-weight-bold"><xsl:value-of select="@name"/>:
                    </div>
                    <div class="col">
                        <xsl:value-of select="@value"/>
                    </div>
                </div>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="renderStepOccurrence">
        <xsl:param name="stepsPath"/>
        <div class="progress" style="height: 18px;">
            <div class="progress-bar bg-success" role="progressbar" title="successful">
                <xsl:variable name="percent"
                              select="format-number((count($stepsPath[@outcome='successful']) div count($stepsPath)), '0%')"/>
                <xsl:attribute name="style">
                    width:
                    <xsl:value-of select="$percent"/>
                </xsl:attribute>
                <xsl:value-of select="$percent"/>
            </div>
            <xsl:if test="count($stepsPath[@outcome='failed']) > 0">
                <div class="progress-bar bg-danger" role="progressbar" title="failed">
                    <xsl:variable name="percent"
                                  select="format-number((count($stepsPath[@outcome='failed']) div count($stepsPath)), '0%')"/>
                    <xsl:attribute name="style">
                        width:
                        <xsl:value-of select="$percent"/>
                    </xsl:attribute>
                    <xsl:value-of select="$percent"/>
                </div>
            </xsl:if>
            <xsl:if test="count($stepsPath[@outcome='ignorable']) > 0">
                <div class="progress-bar bg-warning" role="progressbar" title="ignored">
                    <xsl:variable name="percent"
                                  select="format-number((count($stepsPath[@outcome='ignorable']) div count($stepsPath)), '0%')"/>
                    <xsl:attribute name="style">
                        width:
                        <xsl:value-of select="$percent"/>
                    </xsl:attribute>
                    <xsl:value-of select="$percent"/>
                </div>
            </xsl:if>
            <xsl:if test="count($stepsPath[@outcome='notPerformed']) > 0">
                <div class="progress-bar bg-secondary" role="progressbar" title="not performed">
                    <xsl:variable name="percent"
                                  select="format-number((count($stepsPath[@outcome='notPerformed']) div count($stepsPath)), '0%')"/>
                    <xsl:attribute name="style">
                        width:
                        <xsl:value-of select="$percent"/>
                    </xsl:attribute>
                    <xsl:value-of select="$percent"/>
                </div>
            </xsl:if>
            <div class="progress-bar bg-info" role="progressbar" title="comment">
                <xsl:variable name="percent"
                              select="format-number((count($stepsPath[@outcome='comment']) div count($stepsPath)), '0%')"/>
                <xsl:attribute name="style">
                    width:
                    <xsl:value-of select="$percent"/>
                </xsl:attribute>
                <xsl:value-of select="$percent"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="story">
        <xsl:for-each select="story">
            <xsl:variable name="isFailed" select="count(descendant::steps/step[@outcome='failed']) > 0"/>
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
        </xsl:for-each>
    </xsl:template>

    <!-- testcontext -->
    <xsl:template name="testContext">
        <xsl:for-each select="following-sibling::*[1]/testContext">
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
                                <xsl:call-template name="break">
                                    <xsl:with-param name="text" select="value"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </xsl:for-each>
                </tbody>
            </table>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="parameter">
        <xsl:for-each select="parameter/parameters">
            <table class="table table-sm table-hover">
                <thead>
                    <xsl:call-template name="names"/>
                </thead>
                <tbody>
                    <xsl:call-template name="values"/>
                </tbody>
            </table>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="names">
        <xsl:for-each select="names">
            <tr>
                <xsl:call-template name="name"/>
            </tr>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="name">
        <xsl:for-each select="name">
            <th>
                <xsl:value-of select="."/>
            </th>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="values">
        <xsl:for-each select="values">
            <tr>
                <xsl:call-template name="value"/>
            </tr>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="value">
        <xsl:for-each select="value">
            <td>
                <xsl:value-of select="." disable-output-escaping="yes"/>
            </td>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="step">
        <xsl:param name="stepsPath"/>
        <xsl:for-each select="$stepsPath">
            <xsl:variable name="scenarioIndex" select="count(../../preceding-sibling::scenario)+1"/>
            <div>
                <xsl:attribute name="class">
                    <xsl:if test="not(parameter/parameters)">parameterlessStep</xsl:if>
                </xsl:attribute>

                <a data-toggle="collapse">
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="parameter/parameters">pointerCursor</xsl:when>
                            <xsl:otherwise>inactiveLink</xsl:otherwise>
                        </xsl:choose>
                        <xsl:call-template name="textColor">
                            <xsl:with-param name="outcome" select="@outcome"/>
                        </xsl:call-template>
                    </xsl:attribute>

                    <xsl:if test="parameter/parameters">
                        <xsl:attribute name="href">
                            #step-details-<xsl:value-of select="$scenarioIndex"/>-<xsl:value-of select="position()"/>
                        </xsl:attribute>
                        <i class="fa fa-plus-circle" aria-hidden="true"/>
                    </xsl:if>

                    <span>
                        Step:
                        <xsl:for-each select="./text() | *[not(self::parameter/parameters)]">
                            <xsl:value-of select="."/>
                        </xsl:for-each>
                    </span>
                    <xsl:if test="@outcome='failed'">
                        <xsl:call-template name="renderStacktraceModal">
                            <xsl:with-param name="scenarioIndex" select="$scenarioIndex"/>
                        </xsl:call-template>

                        <xsl:if test="../../following-sibling::*[1]/errorScreenshots/screenshot">
                            <xsl:call-template name="renderScreenshotModal">
                                <xsl:with-param name="scenarioIndex" select="$scenarioIndex"/>
                            </xsl:call-template>
                        </xsl:if>
                    </xsl:if>
                </a>
                <br/>

                <xsl:choose>
                    <xsl:when test="parameter/parameters">
                        <div id="step-details-{$scenarioIndex}-{position()}" class="collapse">
                            <xsl:call-template name="parameter"/>
                        </div>
                    </xsl:when>
                </xsl:choose>
            </div>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
