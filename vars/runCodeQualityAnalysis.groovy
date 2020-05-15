#!/usr/bin/env groovy

/**
 * Usage:
 *  runCodeQualityAnalysis command:'mvn sonar:sonar'
 */
def call(Map params) {
    // Run the sonar scan
    withSonarQubeEnv('Environment name for Sonar') {
        sh params.command
    }

   // sleep 15 //seconds

   // timeout(time: 10, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
   //     def qualityGate = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
   //     if (qualityGate.status == 'ERROR') {
   //         error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
   //     }
   // }
}
