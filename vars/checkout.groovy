#!/usr/bin/env groovy

/**
 * Usage:
 *  checkout source code: 'oscar' 'repository:'oscar'
 */
def call(Map params) {

    //def config = getConfig()
    def giturl = "https://github.com/${params.project}/${params.repository}.git"
	def credentialsId = "github-connection"

  
    // Get project code from a Git repository
    git credentialsId: credentialsId, url: giturl, branch: env.BRANCH_NAME    
  
  sh "ls -l"
}
