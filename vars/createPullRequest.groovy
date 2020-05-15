#!/usr/bin/env groovy

/**
 * Usage:
 *  createPullRequest repository:'service-name' sourceBranch:'feature/TICKET-1234' destinationBranch:'develop'
 */
def call(Map params) {

    def scmProject = params.project
    def scmRepository = params.repository
    def sourceBranch = params.sourceBranch
    def destinationBranch = params.destinationBranch

    def config = getConfig()

    def gitPullApiUrl = "${config.git.api.url}${scmProject}/repos/${scmRepository}/pull-requests"
    def userId = config.jenkins.userid

    def binding = [
        REPOS : scmRepository,
        KEY : scmProject,
        BRANCH : sourceBranch,
        DESTBRANCH : destinationBranch
    ]

    def template = readFile "restmerge.json"
    writeFile file: "restmerge-processed.json" , text: utils.render(template: template, binding: binding)
    sh "cat restmerge-processed.json"
    withCredentials([usernamePassword(credentialsId: "${userId}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        def call = "curl -u ${USERNAME}:${PASSWORD} -X POST ${gitPullApiUrl} -H 'Content-Type:application/json' -d  @restmerge-processed.json"
        def result = sh(returnStdout:true, script: call)

        println result
    
        if(result.contains("errors") && !result.contains("DuplicatePullRequestException") && !result.contains("EmptyPullRequestException")){ //No error if pull request already exists
            error("Merge failed with error " + result)
            currentBuild.result = 'FAILURE'
        }

    }
}