def call(Closure body) {

    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    def releaseRole = pipelineParams.releaseRole
    if(!releaseRole?.trim()) {
        error "Release role is not defined in the release pipeline. Please check readme.md for more details."
    }
    
	def scmRepository = pipelineParams.scmRepository
    def scmProject = pipelineParams.scmProject
    def service = pipelineParams.serviceName
    def mavenReleaseCommand = pipelineParams.mavenReleaseCommand
	def	artifactId= pipelineParams.tomcatDeploy1.artifactId
	def	pkgExtension= pipelineParams.tomcatDeploy1.pkgExtension
	def	versionId= pipelineParams.tomcatDeploy1.versionId
	def	GroupId= pipelineParams.tomcatDeploy1.GroupId
	def	deployDirectory= pipelineParams.tomcatDeploy1.deployDirectory
	def keytabfile = pipelineParams.tomcatDeploy1.keytabfile
	def	deployTarget = pipelineParams.tomcatDeploy1.deployTarget
	def applicationUser = pipelineParams.tomcatDeploy1.applicationUser
	def destinationFileName = pipelineParams.tomcatDeploy1.destinationFileName
    def repoName = pipelineParams.scmRepository

    try {
      
	  dir(pipelineParams.scmRepository) {

        def choices = [Component:false, Security:false]
        def pomFilePath	= pipelineParams.pomFilePath  
            
       
       
      if (pipelineParams.scmRepository && pipelineParams.scmProject ){
	   stage('Checkout Source code') {
	    checkout project: scmProject, repository: scmRepository
	   }
	   
	   }
      
       stage('Request Release UAT/PRD') {
           timeout(time: 15, unit: 'MINUTES') {
          //      if (!utils.isUpstream()) {
          //          input message: 'Want to deploy to PRD?', ok: 'Deploy To Production', submitter: releaseRole
        //     }

                def version = null
                stage('Get Release version') {
                    version = utils.getReleaseVersion(scmProject, scmRepository)
                    echo "Releasing version: ${version}"
                    currentBuild.displayName = " Version : ${version}"
                }
       
     
             
   //           if(!env.BRANCH_NAME == 'master' ) {
     //   error('Release version cannot be SNAPSHOT')
   // }
             
             
             
        if (pipelineParams.mavenReleaseCommand){
	   stage('Prepare and Perform Release') {
             mavenRelease command: mavenReleaseCommand, pomFilePath: pomFilePath
        }
        
      }	      
             
                				
		stage('Tomcat Deployments') {
			tomcatDeploy keytabfile: keytabfile, deployTarget: deployTarget, artifactId: artifactId, pkgExtension: pkgExtension, versionId: versionId, GroupId: GroupId, deployDirectory: deployDirectory, applicationUser: applicationUser, destinationFileName: destinationFileName, repoName: repoName
          }
       
		
            }
        }
    } 
	
	}catch (error) {
        throw error
    }
      
  }
  
  
