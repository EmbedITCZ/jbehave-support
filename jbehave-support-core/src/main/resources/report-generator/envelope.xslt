<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:variable name="totalStories" select="count(//story)"/>
    <xsl:variable name="index-source" select="document('../index.xml')"/>
    <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'"/>
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>

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
                    <div class="col-4 text-muted text-center" id="userVersion" version="1.0.8-SNAPSHOT">
                        Created by jbehave-support reporter (build 1.0.8-SNAPSHOT)
                    </div>
                    <div class="col-4 text-muted text-center invisible" id="currentVersion">
                        Current version: connection error
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>

    <!-- Menu -->
    <xsl:template name="showStory">
        <xsl:for-each select="//story">
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
                        <xsl:if test="jms">
                            <li class="nav-item">
                                <a class="nav-link">
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat('#jms-messages',$storyIndex)"/>
                                    </xsl:attribute>
                                    <i class="fa fa-exchange" aria-hidden="true"></i>
                                    JMS messages
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

                    <xsl:call-template name="storyInfo">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="scenario">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="ws">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="rest">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="jms">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="sql">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="stepScreenshots">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                    <xsl:call-template name="serverLog">
                        <xsl:with-param name="storyIndex" select="$storyIndex"/>
                    </xsl:call-template>
                </main>
            </div>
        </xsl:for-each>
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

    <xsl:template name="storyInfo">
        <xsl:param name="storyIndex"/>
        <xsl:for-each select="/story">
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
                                        <td class="align-baseline">- In order to:</td>
                                        <td class="align-baseline">
                                            <xsl:call-template name="break">
                                                <xsl:with-param name="text" select="narrative/inOrderTo" />
                                            </xsl:call-template>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="align-baseline">- As a:</td>
                                        <td class="align-baseline">
                                            <xsl:call-template name="break">
                                                <xsl:with-param name="text" select="narrative/asA" />
                                            </xsl:call-template>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="align-baseline">- I want to:</td>
                                        <td class="align-baseline">
                                            <xsl:call-template name="break">
                                                <xsl:with-param name="text" select="narrative/iWantTo" />
                                            </xsl:call-template>
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
        </xsl:for-each>
    </xsl:template>

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

    <xsl:template name="break">
        <xsl:param name="text" select="string(.)"/>
        <xsl:choose>
            <xsl:when test="contains($text, '&#xa;')">
                <xsl:value-of select="substring-before($text, '&#xa;')"/>
                <br/>
                <xsl:call-template name="break">
                    <xsl:with-param
                        name="text"
                        select="substring-after($text, '&#xa;')"
                    />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
