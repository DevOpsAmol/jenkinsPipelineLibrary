#!/usr/bin/env groovy

/**
 * Usage:
 *  build project: 'fez' 'repository:'customer-details' command:'mvn clean install'
 */
def call(Map params) {

    def pomFilePath = "${params.pomFilePath}"
    
    def version = utils.getProjectVersion(pomFilePath)
  // def version = utils.getProjectVersion()

    // Check if right version of pom is on master branch
  //  if(env.BRANCH_NAME == 'master' && version.contains('SNAPSHOT')) {
    //    error('Release version cannot be SNAPSHOT')
    //}

    // Run the maven build
    sh "${params.command}"
}
