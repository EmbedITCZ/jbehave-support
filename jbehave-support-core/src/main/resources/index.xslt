<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html>
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                <title>Test reports index</title>

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
                <script src="functions.js"/>
                <script>$(document).ready(function(){indexReady()});</script>

                <style type="text/css">
                    body { margin-bottom: 5em; }
                    .progress { height: 1.5em; }
                </style>
            </head>
            <body>
                <div class="container-fluid w-75">
                    <main role="main">
                        <p>
                            <div class="card">
                                <div>
                                    <xsl:attribute name="class">
                                        <xsl:choose>
                                            <xsl:when
                                                test="count(/index/item[status='SUCCESSFUL']) = count(/index/item)">
                                                card-header bg-success text-white font-weight-bold
                                            </xsl:when>
                                            <xsl:otherwise>card-header bg-danger text-white font-weight-bold
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:attribute>
                                    Test results
                                </div>
                                <div id="header-body" class="card-body collapse show">
                                    <div class="row">
                                        <div class="col-12 col-sm-6 align-self-start">
                                            <div class="progress font-weight-bold">
                                                <div class="progress-bar bg-success" role="progressbar"
                                                     title="SUCCESSFUL">
                                                    <xsl:variable name="percent"
                                                                  select="round(count(//item[status='SUCCESSFUL']) div count(//item) * 100)"/>
                                                    <xsl:attribute name="style">
                                                        width: <xsl:value-of select="$percent"/>%
                                                    </xsl:attribute>
                                                    <xsl:value-of select="$percent"/>%
                                                </div>
                                                <div class="progress-bar bg-danger" role="progressbar" title="FAILED">
                                                    <xsl:variable name="percent"
                                                                  select="round(count(//item[status='FAILED']) div count(//item) * 100)"/>
                                                    <xsl:attribute name="style">
                                                        width: <xsl:value-of select="$percent"/>%
                                                    </xsl:attribute>
                                                    <xsl:value-of select="$percent"/>%
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-12 col-sm-6 align-self-start">
                                            <xsl:apply-templates select="/index/environmentInfo"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </p>

                        <ul class="list-group list-group-flush list-group-item-action" id="report-table">
                            <li class="list-group-item pt-1 pb-1 header">
                                <div class="row justify-content-between">
                                    <div class="col-6">
                                        <div class="btn-group btn-group-sm" role="group">
                                            <button type="button" class="btn btn-outline-primary pt-0 pb-0"
                                                    onclick="filter()">All
                                            </button>
                                            <button type="button" class="btn btn-outline-success pt-0 pb-0"
                                                    onclick="filter('SUCCESSFUL')">
                                                <i class="fa fa-check-circle"/>
                                            </button>
                                            <button type="button" class="btn btn-outline-danger pt-0 pb-0"
                                                    onclick="filter('FAILED')">
                                                <i class="fa fa-exclamation-circle"/>
                                            </button>
                                        </div>
                                    </div>
                                    <div class="col-5 align-self-end text-right">
                                        <small class="text-secondary">
                                            Total test count:
                                            <xsl:value-of select="count(index/item)"/> (<xsl:value-of
                                            select="count(index/item[status='FAILED'])"/> failed);
                                            total duration:
                                            <xsl:call-template name="formatTime">
                                                <xsl:with-param name="millis" select="sum(//item/duration)"/>
                                            </xsl:call-template>
                                        </small>
                                    </div>
                                </div>

                            </li>
                            <xsl:apply-templates select="index/item"/>
                        </ul>
                    </main>
                </div>
                <xsl:call-template name="renderFooter"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="item">
        <li class="list-group-item list-group-item-action pt-0 pb-0">
            <xsl:attribute name="status">
                <xsl:value-of select="status"/>
            </xsl:attribute>

            <div class="row">
                <div class="col-12 col-md-7 col-lg-9 text-truncate">
                    <xsl:attribute name="title">
                        <xsl:value-of select="path"/>
                    </xsl:attribute>
                    <xsl:value-of select="path"/>
                </div>

                <div class="col col-md-5 col-lg-3">
                    <div class="row">
                        <div class="col-6 col-md-7">
                            <span>
                                <xsl:attribute name="class">
                                    <xsl:choose>
                                        <xsl:when test="status='SUCCESSFUL'">fa-check-circle text-success</xsl:when>
                                        <xsl:otherwise>fa-exclamation-circle text-danger</xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:value-of select="concat(' ','fa pr-1')"/>
                                </xsl:attribute>
                            </span>
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="fileName"/>
                                </xsl:attribute>
                                <xsl:call-template name="translateStatus">
                                    <xsl:with-param name="inputStatus" select="status"/>
                                </xsl:call-template>
                            </a>
                        </div>
                        <div class="col-6 col-md-5 text-right">
                            <span class="text-secondary ml-1">
                                <xsl:call-template name="formatTime">
                                    <xsl:with-param name="millis" select="duration"/>
                                </xsl:call-template>
                            </span>

                        </div>
                    </div>
                </div>
            </div>
        </li>
    </xsl:template>

    <xsl:template match="environmentInfo">
        <xsl:if test="count(entry) > 0">
            <h6 class="card-subtitle font-weight-bold">Environment info</h6>
            <xsl:for-each select="entry">
                <div class="row">
                    <div class="col-4 col-sm-6">
                        <xsl:value-of select="key"/>
                    </div>
                    <div class="col-auto">
                        <xsl:value-of select="value"/>
                    </div>
                </div>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

    <xsl:template name="formatTime">
        <xsl:param name="millis"/>

        <xsl:variable name="sec" select="format-number(($millis div 1000) mod 60,'0')"/>
        <xsl:variable name="min">
            <xsl:choose>
                <xsl:when test="$millis > 60000">
                    <xsl:value-of select="floor(($millis div 1000) div 60)"/>
                </xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="min-print">
            <xsl:if test="$min > 0">
                <xsl:value-of select="concat($min,'m:')"/>
            </xsl:if>
        </xsl:variable>

        <xsl:value-of select="concat($min-print,$sec)"/>s
    </xsl:template>

    <xsl:template name="translateStatus">
        <xsl:param name="inputStatus"/>
        <xsl:choose>
            <xsl:when test="$inputStatus = 'SUCCESSFUL'">Success</xsl:when>
            <xsl:when test="$inputStatus = 'FAILED'">Failure</xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$inputStatus"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

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
</xsl:stylesheet>
