<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'"/>
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>

    <xsl:variable name="index-source" select="document('index.xml')"/>
    <xsl:variable name="totalStories" select="count(//story)"/>

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
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
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
                </style>
                <title>
                    <xsl:value-of select="/story/@path"/> report
                </title>
            </head>
            <body data-spy="scroll">
                <xsl:apply-templates select="//story"/>
                <xsl:call-template name="renderFooter"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="story">
        <xsl:variable name="storyIndex" select="position()"/>
        <div class="container-fluid" type="story">
            <xsl:attribute name="id">
                <xsl:value-of select="concat('story',$storyIndex)"/>
            </xsl:attribute>

            <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('navbar',position())"/>
                </xsl:attribute>

                <div class="border-bottom">
                    <div class="btn-group form-group col align-self-center">
                        <button type="button" class="btn btn-sm btn-info btn-block"
                                onclick="location.href = 'index.xml'">&lt; Index
                        </button>
                        <button type="button" class="btn btn-sm btn-info dropdown-toggle dropdown-toggle-split"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"/>
                        <div class="dropdown-menu" style="max-width: 90%">
                            <xsl:call-template name="indexDropDown">
                                <xsl:with-param name="currentStory" select="@path"/>
                            </xsl:call-template>
                        </div>
                    </div>
                </div>
                <ul class="nav nav-pills flex-column">
                    <li class="nav-item">
                        <a class="nav-link active">
                            <xsl:attribute name="href">
                                <xsl:value-of select="concat('#story-overview',$storyIndex)"/>
                            </xsl:attribute>
                            <i class="fa fa-eye" aria-hidden="true"></i>
                            Story overview
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link">
                            <xsl:attribute name="href">
                                <xsl:value-of select="concat('#scenario-overview',$storyIndex)"/>
                            </xsl:attribute>
                            <i class="fa fa-eye" aria-hidden="true"></i>
                            Scenario overview
                        </a>
                    </li>
                    <xsl:if test="ws">
                        <li class="nav-item">
                            <a class="nav-link">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('#soap-calls',$storyIndex)"/>
                                </xsl:attribute>
                                <i class="fa fa-exchange" aria-hidden="true"></i>
                                SOAP calls
                            </a>
                        </li>
                    </xsl:if>
                    <xsl:if test="rest">
                        <li class="nav-item">
                            <a class="nav-link">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('#rest-calls',$storyIndex)"/>
                                </xsl:attribute>
                                <i class="fa fa-exchange" aria-hidden="true"></i>
                                REST calls
                            </a>
                        </li>
                    </xsl:if>
                    <xsl:if test="sql">
                        <li class="nav-item">
                            <a class="nav-link">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('#sql-queries',$storyIndex)"/>
                                </xsl:attribute>
                                <i class="fa fa-table" aria-hidden="true"></i>
                                SQL queries
                            </a>
                        </li>
                    </xsl:if>
                    <xsl:if test="stepScreenshots">
                        <li class="nav-item">
                            <a class="nav-link">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('#step-screenshots',$storyIndex)"/>
                                </xsl:attribute>
                                <i class="fa fa-camera-retro" aria-hidden="true"></i>
                                Screenshots
                            </a>
                        </li>
                    </xsl:if>
                    <xsl:if test="serverLog">
                        <li class="nav-item">
                            <a class="nav-link">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('#shell-logs',$storyIndex)"/>
                                </xsl:attribute>
                                <i class="fa fa-file-text-o" aria-hidden="true"></i>
                                Shell logs
                            </a>
                        </li>
                    </xsl:if>
                </ul>
            </nav>

            <main role="main" class="col-sm-9 ml-sm-auto col-md-10 pt-3">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">

                        <xsl:for-each select="ancestor::story">
                            <xsl:variable name="storyID"
                                          select="$totalStories - count(following::story) - count(descendant::story)"/>
                            <li class="breadcrumb-item">
                                <a href="#" onclick="display('story1')">
                                    <xsl:attribute name="onclick">
                                        <xsl:value-of select="concat('display(&quot;story',$storyID,'&quot;)')"/>
                                    </xsl:attribute>
                                    <xsl:call-template name="storyName">
                                        <xsl:with-param name="path" select="@path"/>
                                    </xsl:call-template>
                                </a>
                            </li>
                        </xsl:for-each>

                        <li class="breadcrumb-item active">
                            <xsl:call-template name="storyName">
                                <xsl:with-param name="path" select="@path"/>
                            </xsl:call-template>
                        </li>
                    </ol>
                </nav>

                <xsl:apply-templates select="." mode="storyInfo">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="scenario">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="ws">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="rest">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="sql">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="stepScreenshots">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="serverLog">
                    <xsl:with-param name="storyIndex" select="$storyIndex"/>
                </xsl:apply-templates>
            </main>
        </div>
    </xsl:template>

    <xsl:template name="storyName">
        <xsl:param name="path"/>
        <xsl:choose>
            <xsl:when test="contains($path,'/')">
                <xsl:call-template name="storyName">
                    <xsl:with-param name="path" select="substring-after($path, '/')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$path"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- header -->
    <xsl:template mode="storyInfo" match="story">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('story-overview',$storyIndex)"/>
                </xsl:attribute>
                <div>
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="count(descendant::step[@outcome = 'failed']) = 0">card-header bg-success
                                text-white
                            </xsl:when>
                            <xsl:otherwise>card-header bg-danger text-white</xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    Story overview
                    <a data-toggle="collapse" class="float-right text-white">
                        <xsl:attribute name="href">
                            <xsl:value-of select="concat('#story-overview-body',$storyIndex)"/>
                        </xsl:attribute>
                        Collapse
                    </a>
                </div>
                <div class="card-body collapse show">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat('story-overview-body',$storyIndex)"/>
                    </xsl:attribute>
                    <table id="story-overview-table">
                        <tbody>
                            <tr>
                                <td>Story path:</td>
                                <td>
                                    <xsl:value-of select="@path"/>
                                </td>
                            </tr>
                            <xsl:if test="narrative">
                                <tr>
                                    <td colspan="2">Narrative:</td>
                                </tr>
                                <tr>
                                    <td>- In order to:</td>
                                    <td>
                                        <xsl:value-of select="narrative/inOrderTo"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>- As a:</td>
                                    <td>
                                        <xsl:value-of select="narrative/asA"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>- I want to:</td>
                                    <td>
                                        <xsl:value-of select="narrative/iWantTo"/>
                                    </td>
                                </tr>
                            </xsl:if>
                            <tr>
                                <td>Scenario name:</td>
                                <td>
                                    <xsl:value-of select="scenario/@title"/>
                                </td>
                            </tr>
                            <xsl:if test="environmentInfo">
                                <tr>
                                    <td colspan="2">Environment</td>
                                </tr>
                                <xsl:for-each select="environmentInfo/values">
                                    <tr>
                                        <td>- <xsl:value-of select="key"/>:
                                        </td>
                                        <td>
                                            <xsl:value-of select="value"/>
                                        </td>
                                    </tr>
                                </xsl:for-each>
                            </xsl:if>
                            <tr>
                                <td>Start:</td>
                                <td class="time-string">
                                    <xsl:value-of select="startExecution"/>
                                </td>
                            </tr>
                            <tr>
                                <td>End:</td>
                                <td class="time-string">
                                    <xsl:value-of select="endExecution"/>
                                </td>
                            </tr>
                            <tr>
                                <td>Duration:</td>
                                <td>
                                    <xsl:value-of select="duration"/> ms
                                </td>
                            </tr>
                            <tr>
                                <td>Status:</td>
                                <td>
                                    <span>
                                        <xsl:attribute name="class">
                                            <xsl:value-of select="translate(status, $uppercase, $smallcase) "/>
                                            font-weight-bold
                                        </xsl:attribute>
                                        <xsl:value-of select="status"/>
                                    </span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </p>
    </xsl:template>

    <!-- other stories in dropdown -->
    <xsl:template name="indexDropDown">
        <xsl:param name="currentStory"/>
        <xsl:for-each select="$index-source/index/item">
            <a class="dropdown-item pt-0 pb-0 pl-2">
                <xsl:attribute name="title">
                    <xsl:value-of select="substring-before(fileName, '.xml')"/>
                </xsl:attribute>
                <xsl:attribute name="href">
                    <xsl:value-of select="fileName"/>
                </xsl:attribute>
                <xsl:if test="current()/path = $currentStory">
                    <xsl:attribute name="class">
                        dropdown-item pt-0 pb-0 pl-2 disabled
                    </xsl:attribute>
                    <xsl:attribute name="disabled">
                        true
                    </xsl:attribute>
                </xsl:if>
                <div class="text-truncate">
                    <i>
                        <xsl:attribute name="class">
                            pr-1
                            <xsl:call-template name="textColor">
                                <xsl:with-param name="outcome" select="translate(status, $uppercase, $smallcase)"/>
                                <xsl:with-param name="iconize" select="true()"/>
                            </xsl:call-template>
                        </xsl:attribute>
                    </i>
                    <xsl:value-of select="substring-before(fileName,'.xml')"/>
                </div>
            </a>
        </xsl:for-each>
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
                        <xsl:apply-templates select="parameter/parameters" mode="scenario" />
                    </div>
                </xsl:when>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template name="renderStacktraceModal">
        <xsl:param name="scenarioIndex"/>
        <a href="#" data-toggle="modal" class="badge badge-pill badge-danger">
            <xsl:attribute name="data-target">
                <xsl:value-of select="concat('#stacktraceModal', $scenarioIndex)"/>
            </xsl:attribute>
            Stacktrace
        </a>
        <div class="modal fade" tabindex="-1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat('stacktraceModal',$scenarioIndex)"/>
            </xsl:attribute>
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-body">
                        <div class="form-group">
                            <textarea class="form-control" wrap="off" readonly="true" rows="26">
                                <xsl:value-of select="../failure"/>
                            </textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="renderScreenshotModal">
        <xsl:param name="scenarioIndex"/>
        <a href="#" data-toggle="modal" class="badge badge-pill badge-info">
            <xsl:attribute name="data-target">
                <xsl:value-of select="concat('#screenshotModal', $scenarioIndex)"/>
            </xsl:attribute>
            Screenshot
        </a>
        <div class="modal fade" tabindex="-1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat('screenshotModal',$scenarioIndex)"/>
            </xsl:attribute>

            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-body">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:value-of select="../following-sibling::*[1]/errorScreenshots/screenshot"/>
                            </xsl:attribute>
                            <img class="img-fluid" width="770">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="../following-sibling::*[1]/errorScreenshots/screenshot"/>
                                </xsl:attribute>
                            </img>
                        </a>
                    </div>
                </div>
            </div>
        </div>
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

    <!-- ssh -->
    <xsl:template match="serverLog">
        <xsl:param name="storyIndex"/>
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
                    <xsl:apply-templates select="system" mode="serverLog"/>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="system" mode="serverLog">
        <div>
            <a href="#sshLogsDetails-{position()}" data-toggle="collapse">
                +<xsl:value-of select="@id"/>
            </a>
            <div id="sshLogsDetails-{position()}" class="collapse">
                <xsl:apply-templates select="log" mode="serverLog"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="log" mode="serverLog">
        <xsl:variable name="logNum">
            <xsl:number level="any"/>
        </xsl:variable>

        <div>
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
                    <tr>
                        <td class="label">Host:</td>
                        <td>
                            <xsl:value-of select="@host"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="label">Log path:</td>
                        <td>
                            <xsl:value-of select="@logPath"/>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div class="btn-group form-group align-self-center">
                <a href="#sshlog-{$logNum}" data-toggle="collapse" class="btn btn-sm btn-outline-primary">Show/hide log contents</a>
                <button type="button" class="btn btn-sm btn-outline-info btn-copy-clipboard" data-selector="#sshlog-{$logNum}" title="Copy to clipboard">
                    <i class="fa fa-copy" aria-hidden="true"></i>
                </button>
            </div>

            <div id="sshlog-{$logNum}" class="collapse">
                <p>
                    <xsl:if test="fail">
                        No log available
                        <br/>
                    </xsl:if>

                    <pre>
                        <xsl:if test="fail">
                            <xsl:attribute name="class">failed</xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="."/>
                    </pre>
                </p>
            </div>
        </div>
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

    <!-- REST -->
    <xsl:template match="rest">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('rest-calls',$storyIndex)"/>
                </xsl:attribute>
                <div class="card-header">
                    <i class="fa fa-exchange" aria-hidden="true"></i>
                    REST calls (<a href="#rest-calls-body" id="expand-all-rest-calls">Toggle all requests/responses</a>)
                    <a href="#rest-calls-body" data-toggle="collapse" class="float-right">Collapse</a>
                </div>
                <div id="rest-calls-body" class="card-body collapse show">
                    <xsl:choose>
                        <xsl:when test="requestResponse">
                            <xsl:apply-templates select="requestResponse" mode="rest"/>
                        </xsl:when>
                        <xsl:otherwise>No REST calls available</xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="requestResponse" mode="rest">
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
                    <xsl:apply-templates select="request | response" mode="rest"/>
                </ul>

            </div>
        </div>
    </xsl:template>

    <xsl:template match="request" mode="rest">
        <li class="list-group-item list-group-item-light">
            <strong>Request:
                <span class="time-string-millis">
                    <xsl:value-of select="@time"/>
                </span>
            </strong>
            <xsl:if test="body">
                <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre" title="Copy to clipboard">
                    <i class="fa fa-copy" aria-hidden="true"></i>
                </button>
            </xsl:if>
            <br/>
            <xsl:if test="body">
                <pre class="json-message">
                    <xsl:value-of select="body" disable-output-escaping="yes"/>
                </pre>
            </xsl:if>
        </li>
    </xsl:template>

    <xsl:template match="response" mode="rest">
        <li class="list-group-item list-group-item-light">
            <strong>Response:
                <span class="time-string-millis">
                    <xsl:value-of select="@time"/>
                </span>
            </strong>
            status:
            <xsl:value-of select="@status"/>

            <xsl:if test="body">
                <button type="button" class="btn btn-sm btn-link btn-copy-clipboard" data-selector="~ pre" title="Copy to clipboard">
                    <i class="fa fa-copy" aria-hidden="true"></i>
                </button>
                <pre class="json-message">
                    <xsl:value-of select="body" disable-output-escaping="yes"/>
                </pre>
            </xsl:if>
        </li>
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

    <xsl:template match="results">
        Results:
        <table class="table table-sm table-hover">
            <thead>
                <tr>
                    <xsl:for-each select="columns/column">
                        <th><xsl:value-of select="."/></th>
                    </xsl:for-each>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="rows/row">
                    <tr>
                        <xsl:for-each select="value">
                            <td><xsl:value-of select="."/></td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="stepScreenshots">
        <xsl:param name="storyIndex"/>
        <p>
            <div class="card">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('step-screenshots',$storyIndex)"/>
                </xsl:attribute>
                <div class="card-header">
                    <i class="fa fa-exchange" aria-hidden="true"></i>
                    Step screen shots (<a href="#step-screen-shots" id="expand-all-step-screenshots">Toggle all screenshots</a>)
                    <a href="#step-screen-shots" data-toggle="collapse" class="float-right">Collapse</a>
                </div>
                <div id="step-screen-shots" class="card-body collapse show">
                    <xsl:choose>
                        <xsl:when test="stepScreenshot">
                            <xsl:apply-templates select="stepScreenshot"/>
                        </xsl:when>
                        <xsl:otherwise>No screenshots available</xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </p>
    </xsl:template>

    <xsl:template match="stepScreenshot">
        <xsl:variable name="screenshotNum">
            <xsl:number level="any"/>
        </xsl:variable>
        <div>
            <a class="pointerCursor" data-toggle="collapse">
                <xsl:attribute name="href">
                    <xsl:value-of select="concat('#step-screen-shot-',$screenshotNum)"/>
                </xsl:attribute>
                <i class="fa fa-camera-retro" aria-hidden="true"/>
                <xsl:value-of select="concat('Screenshot_',$screenshotNum)"/>
            </a>
            <div class="collapse">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('step-screen-shot-',$screenshotNum)"/>
                </xsl:attribute>
                <img style="width: 70%; height: 70%; border: 1px solid black">
                    <xsl:attribute name="src">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </img>
            </div>
        </div>
    </xsl:template>

    <!-- footer -->
    <xsl:template name="renderFooter">
        <div class="fixed-bottom bg-white border-top">
            <div class="container-fluid">
                <div class="row pt-2 pb-2">
                    <div class="col-4 text-muted text-center">
                        <a href="https://github.com/EmbedITCZ/jbehave-support/README.md">
                            <i class="fa fa-question-circle-o"/>
                            Help
                        </a>
                    </div>
                    <div class="col-4 text-muted text-center" id="userVersion" version="${project.version}">
                        Created by jbehave-support reporter (build ${project.version})
                    </div>
                    <div class="col-4 text-muted text-center invisible" id="currentVersion">
                        Current version: connection error
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
