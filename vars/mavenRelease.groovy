#!/usr/bin/env groovy

/**
 * Usage:
 *  build project: 'Project Name' 'repository:'Repository Name' command:'mvn clean install'
 */
def call(Map params) {

    def pomFilePath = "${params.pomFilePath}"
    
    def version = utils.getProjectVersion(pomFilePath)

    // Check if right version of pom is on master branch
    if(env.BRANCH_NAME == 'master' && version.contains('SNAPSHOT')) {
        error('Release version cannot be SNAPSHOT')
    }

    // Run the maven release commands
    sh "${params.command}"
}
