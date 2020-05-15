#!/usr/bin/env groovy

def getConfig() {
    def configContent = libraryResource 'config.yaml'
    def config = readYaml text: configContent
    return config
}


//This needs to be removed worked out 


//def getProjectVersion() {
//    pom = readMavenPom file: 'pom.xml'
//    return pom.version
//}

def getProjectVersion(pomFilePath) {
 pom = readMavenPom file: "${pomFilePath}"
 return pom.version
} 


def getReleaseVersion(scmProject, scmRepository) {
    def config = getConfig()
    git credentialsId: "${config.jenkins.userid}", url: "${config.git.project.url}${scmProject}/${scmRepository}.git" , branch: 'master'
    pom = readMavenPom file: 'pom.xml'
    return pom.version
}

def isUpstream(){
    def upsteam = false
    for (cause in currentBuild.rawBuild.getCauses()){
        if(cause.class.toString().contains("UpstreamCause")){
            upsteam = true
        }
    }
    return upsteam
}

/**
 *  Usage:
 *  processTemplate template: 'docker-compose.yml', binding: [key:value], output: docker-compose.env.yml
 */
def render(Map params) {
    def binding = params.binding
    def template = params.template

    //Keep backward compatibility
    if(template.contains("#VERSION#") || template.contains("#BRANCH#")){
        def output = template
        binding.each{k,v->
            output = output.replaceAll("#${k}#","${v}")
        }
        return output
    }else { //Use template engine to replace ${} placeholders. Test only
        try {
            def engine = new groovy.text.SimpleTemplateEngine()
            String output = engine.createTemplate(template).make(binding).toString()
            return output
        }catch (err) {
            return "ERROR: ${err}"
        }
    }
}

def isFeatureBranch(String branchName) {
    return branchName.startsWith("feature")
}

def isDevelopBranch(String branchName) {
    return branchName == "develop"
}

def isMasterBranch(String branchName) {
    return branchName == "master"

}

