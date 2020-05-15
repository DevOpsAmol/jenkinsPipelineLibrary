def call(String type, Closure body) {


    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '3', numToKeepStr: '5')), disableConcurrentBuilds(), pipelineTriggers([])])

    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
     
	def isFeatureBranch = utils.isFeatureBranch(env.BRANCH_NAME)
    def isDevelopBranch = utils.isDevelopBranch(env.BRANCH_NAME)
    def isMasterBranch = utils.isMasterBranch(env.BRANCH_NAME)
    
	def scmProject = pipelineParams.scmProject
    def scmRepository = pipelineParams.scmRepository
    def buildCommand = pipelineParams.buildCommand
	//def mavenReleaseCommand = pipelineParams.mavenReleaseCommand
    def codeQualityToolCommand = pipelineParams.sonarCommand
    def hostName = pipelineParams.hostName
    def pomFilePath	= pipelineParams.pomFilePath 

  	def serviceEndpoints
    if(pipelineParams.securityTests) {
        serviceEndpoints = pipelineParams.securityTests.endpoints
    }
  
   	def performanceTestsDir
    def performanceTestsCommand
    if(pipelineParams.performanceTests) {
        performanceTestsDir = pipelineParams.performanceTests.dir
        performanceTestsExecuteCommand = pipelineParams.performanceTests.executecommand
        performanceTestsAnalyseCommand = pipelineParams.performanceTests.analysecommand
    }
	
	def reportDir 
	def reportFiles 
	def reportName 
    if(pipelineParams.publishTestReports) {
	    reportDir = pipelineParams.publishTestReports.reportDir
		reportFiles = pipelineParams.publishTestReports.reportFiles
		reportName = pipelineParams.publishTestReports.reportName
		
    }

    def soaptestsoapuifolder
	def soaptesthostname
	def soaptesttestname
	def soaptesthostnamelb
    if(pipelineParams.soapUITests) {
	    soaptestsoapuifolder = pipelineParams.soapUITests.soapuifolder
		soaptesthostname = pipelineParams.soapUITests.hostname
		soaptesttestname = pipelineParams.soapUITests.testname
		soaptesthostnamelb = pipelineParams.soapUITests.hostnamelb
    }
	
	
	
	def artifactId1
    def pkgExtension1
    def versionId1
    def GroupId1
    def deployDirectory1
    def credentialsId1
	def keytabfile1
	def deployTarget1
    def applicationUser1
	def destinationFileName1
    def repoName1
	
	if (pipelineParams.tomcatDeploy1) {
		artifactId1= pipelineParams.tomcatDeploy1.artifactId
		pkgExtension1= pipelineParams.tomcatDeploy1.pkgExtension
        versionId1= pipelineParams.tomcatDeploy1.versionId
		//versionId1= utils.getProjectVersion(pomFilePath)
		GroupId1= pipelineParams.tomcatDeploy1.GroupId
		deployDirectory1= pipelineParams.tomcatDeploy1.deployDirectory
		credentialsId1 = pipelineParams.tomcatDeploy1.credentialsId
		keytabfile1 = pipelineParams.tomcatDeploy1.keytabfile
		deployTarget1 = pipelineParams.tomcatDeploy1.deployTarget
		applicationUser1= pipelineParams.tomcatDeploy1.applicationUser
	    destinationFileName1 = pipelineParams.tomcatDeploy1.destinationFileName
        repoName1 = pipelineParams.scmRepository
    	
	}
  
   
	 
  
    //dir(pipelineParams.scmRepository) {

        def choices = [Component:false, Security:false]
       // def pomFilePath	= pipelineParams.pomFilePath       

      
      if (pipelineParams.scmRepository && pipelineParams.scmProject ){
	   stage('Checkout Source code') {
	    checkout project: scmProject, repository: scmRepository
	   }
	   
	   }
	  
	//  if (pipelineParams.mavenReleaseCommand){
	//   stage('Prepare and Perform Release') {
    //         mavenRelease command: mavenReleaseCommand, pomFilePath: pomFilePath
    //    }
        
    //  }	  
	  
	  if (pipelineParams.buildCommand){
	   stage('Build Project') {
             build command: buildCommand, pomFilePath: pomFilePath
        }
        
      }
		
     // This is the version name which is getting pulled from POM file 	
		 def version = utils.getProjectVersion(pomFilePath)
     
      
      
      if (pipelineParams.sonarCommand) {
		stage('Code Quality') {
              runCodeQualityAnalysis command: codeQualityToolCommand
          }
      }
		  
		if (pipelineParams.publishTestReports) {
          stage('Publish Unit Testing reports') {
              publishTestReports reportDir: reportDir, reportFiles: reportFiles, reportName: reportName
          }
        }

             
     
      
      //This adjusted to cater oscars pipeline branch
      
     // if (pipelineParams.securityTests && env.BRANCH_NAME == 'pocpipelinelibary') {
      
      if (pipelineParams.securityTests) {
      
           stage('Security Tests') {
              runSecurityTests endpoints: serviceEndpoints
           }
         }
      
	  
	         
        if (pipelineParams.tomcatDeploy1) {
          stage('Tomcat Deployments') {
              tomcatDeploy keytabfile: keytabfile1, deployTarget: deployTarget1, artifactId: artifactId1, pkgExtension: pkgExtension1, versionId: versionId1, GroupId: GroupId1, deployDirectory: deployDirectory1, applicationUser: applicationUser1, destinationFileName: destinationFileName1, repoName: repoName1
          }
        }
      
              
           
		if (pipelineParams.soapUITests && env.BRANCH_NAME == 'develop') {
          stage('Soap UI Tests') {
              runSoapUITests soapuifolder:soaptestsoapuifolder, hostname:soaptesthostname, testname:soaptesttestname, hostnamelb:soaptesthostnamelb
          }
        }

          
      
      
        if (pipelineParams.performanceTests && env.BRANCH_NAME == 'develop') {
          stage('Performance Tests') {
               runJMeterPerformanceTests dir:performanceTestsDir, executecommand: performanceTestsExecuteCommand, analysecommand: performanceTestsAnalyseCommand
          }
        }
		
		if(env.BRANCH_NAME == 'develop') {
            stage('Promote all') {
                parallel(
                    'Promote DEV': {
                        //implement deployment through jenkins pipeline
                    },
                    'Promote INT': {
                        //implement deployment through jenkins pipeline
                    }
                )
                if(!pipelineParams.loadTests) {
                    //Since we didn't run performance tests we promote prf additionally
                    stage('Promote PRF') {
                        //implement deployment through jenkins pipeline
                    }
                }
            }
        } 
      
      if(env.BRANCH_NAME == 'master') {
            stage('Promote UAT') {
                //Add code to promote to UAT
            }
        }

       
// written tomcatDeploy library for deployments to tomcat, this needs to be revisited.

//	   if(deployArtifacts) {
//            stage('Deploy artifacts') {
//                deployArtifact()
//            }
//        }



 //     if (env.BRANCH_NAME != 'master') {
 //          stage('Create pull request') {
 //               if (env.BRANCH_NAME == 'develop') {
 //                   createPullRequest project: scmProject, repository: scmRepository, sourceBranch: env.BRANCH_NAME, destinationBranch: 'master'
  //              } else {
 //                   createPullRequest project: scmProject, repository: scmRepository, sourceBranch: env.BRANCH_NAME, destinationBranch: 'develop'
 //               }
 //           }
 //       }

        deleteDir()
    }
//}
