package org.jbehavesupport.core.ssh;

/**
 * Modes for ServerLogXmlReporterExtension
 * FULL for full log content from all sshTemplates
 * TEMPLATE for full log content from reportable sshTemplates
 * CACHE from all log parts saved in case during scenario run
 */
public enum SshReportType {
    FULL,
    TEMPLATE,
    CACHE
}
