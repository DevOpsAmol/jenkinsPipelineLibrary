#!/usr/bin/env groovy

/**
 * Usage:
 *  publishTestReports
 */
def call(Map params) {

    def reportDir = params.reportDir
	def reportFiles = params.reportFiles
	def reportName = params.reportName
	
        
    publishHTML target: [
            allowMissing: false,
            alwaysLinkToLastBuild: false,
            keepAll: true,
            reportDir: "${reportDir}",
            reportFiles: "${reportFiles}",
            reportName: "${reportName}"
    ]
}