<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="report-generator/envelope.xslt"/>
    <xsl:import href="report-generator/story.xslt"/>
    <xsl:import href="report-generator/wsXmlReport.xslt"/>
    <xsl:import href="report-generator/restXmlReport.xslt"/>
    <xsl:import href="report-generator/sqlXmlReport.xslt"/>
    <xsl:import href="report-generator/stacktrace.xslt"/>
    <xsl:import href="report-generator/serverLogXmlRep.xslt"/>
    <xsl:import href="report-generator/failScreenshotReport.xslt"/>
    <xsl:import href="report-generator/screenshotReport.xslt"/>
    <xsl:import href="report-generator/jmsXmlReport.xslt"/>

    <xsl:output method="html" doctype-system="about:legacy-compat"/>

    <xsl:template name="textColor">
        <xsl:param name="outcome"/>
        <xsl:param name="iconize" select="false()"/>
        <xsl:choose>
            <xsl:when test="$outcome = 'successful'">&#x20;text-success</xsl:when>
            <xsl:when test="$outcome = 'failed'">&#x20;text-danger</xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('COLOR ERROR: ', $outcome)"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$iconize">
            <xsl:choose>
                <xsl:when test="$outcome = 'successful'">&#x20;fa fa-check-circle</xsl:when>
                <xsl:when test="$outcome = 'failed'">&#x20;fa fa-exclamation-circle</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('ICON ERROR: ', $outcome)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template match="/">
        <html>
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
                      integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
                      crossorigin="anonymous"/>
                <link rel="stylesheet"
                      href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
                      crossorigin="anonymous"/>
                <script src="https://code.jquery.com/jquery-3.3.1.min.js"
                        integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"/>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
                        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
                        crossorigin="anonymous"></script>
                <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
                        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
                        crossorigin="anonymous"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.20.1/moment.min.js"
                        crossorigin="anonymous"></script>
                <script src="functions.js"/>
                <script>$(document).ready(function(){reportReady()});</script>

                <style type="text/css">
                    body { margin-bottom: 5em; }
                    .comment, .notPerformed { color: grey !important; }
                    a.inactiveLink:hover{ font-weight: normal; }
                    .pointerCursor { cursor:pointer; }
                    .parameterlessStep, .emptySqlStatement { padding-left: 20px; }
                    .sidebar {
                    position: fixed;
                    top: 0px;
                    bottom: 0;
                    left: 0;
                    z-index: 1000;
                    padding: 20px 0;
                    overflow-x: hidden;
                    overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
                    border-right: 1px solid #eee;
                    }
                    #story-overview-table td:first-child { font-weight: bold; }
                    #story-overview-table { border-collapse: separate; border-spacing: 5px 0; /* space between columns
                    */ }

                    .json-string { color: blue; }
                    .json-number { color: dimgray; }
                    .json-boolean { color: darkgoldenrod; }
                    .json-null { color: magenta; }
                    .json-key { color: purple; }
                    .json-message { margin-bottom: 0px; line-height: 1.1}
                    .progress { height: 1.5em; }
                    .report-screenshot { width: 70%; height: 70%; border: 1px solid black }
                </style>
                <title>
                    <xsl:value-of select="/story/@path"/> report
                </title>
            </head>
            <body data-spy="scroll">
                <xsl:apply-imports/>
                <xsl:apply-templates select="//story"/>
                <xsl:call-template name="renderFooter"/>
                <xsl:apply-templates select="jms"/>
            </body>
        </html>
    </xsl:template>

    <!-- meta -->
    <xsl:template match="meta">
        <xsl:for-each select="property">
            <div class="row">
                <div class="col-6 col-sm-3 col-lg-2 font-weight-bold"><xsl:value-of select="@name"/>:
                </div>
                <div class="col">
                    <xsl:value-of select="@value"/>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="step">
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
                        #step-details-<xsl:value-of select="position()"/>
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
                    <xsl:variable name="scenarioIndex" select="count(../preceding-sibling::scenario)+1"/>

                    <xsl:call-template name="renderStacktraceModal">
                        <xsl:with-param name="scenarioIndex" select="$scenarioIndex"/>
                    </xsl:call-template>

                    <xsl:if test="../following-sibling::*[1]/errorScreenshots/screenshot">
                        <xsl:apply-imports/>
                        <xsl:call-template name="renderScreenshotModal">
                            <xsl:with-param name="scenarioIndex" select="$scenarioIndex"/>
                        </xsl:call-template>
                    </xsl:if>
                </xsl:if>
            </a>
            <br/>

            <xsl:choose>
                <xsl:when test="parameter/parameters">
                    <div id="step-details-{position()}" class="collapse">
                        <xsl:apply-templates select="parameter/parameters" mode="scenario"/>
                    </div>
                </xsl:when>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match="results">
        Results:
        <table class="table table-sm table-hover">
            <thead>
                <tr>
                    <xsl:for-each select="columns/column">
                        <th>
                            <xsl:value-of select="."/>
                        </th>
                    </xsl:for-each>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="rows/row">
                    <tr>
                        <xsl:for-each select="value">
                            <td>
                                <xsl:value-of select="."/>
                            </td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>
</xsl:stylesheet>
