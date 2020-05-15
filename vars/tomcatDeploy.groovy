#!/usr/bin/env groovy

/**
 * Usage:
 *  Deploy project on tomcat, it takes the parameters like artifactId, pkgExtension, versionId, GroupId to fetch artifact from nexus. It also takes deployDirectory, deployUser, deployTarget, credentialsId as inputs to deploy to the destination server and respective deployment directory. 
 */
 
 
 /**
 * Usage:
 *  runSecurityTests endpoints:['http://localhost:8080/login.htm', 'http://localhost:8080/login.htm']
 */
 
 
def call(Map params) {
    
	def artifactId= params.artifactId
    def pkgExtension= params.pkgExtension
    def versionId= params.versionId
    def GroupId= params.GroupId
    def deployDirectory= params.deployDirectory
   	def keytabfile = params.keytabfile
	def deployTarget = params.deployTarget
	def applicationUser= params.applicationUser
	def destinationFileName = params.destinationFileName
    def repoName = params.repoName

      // Check if right version of pom is on master branch
    if(env.BRANCH_NAME == 'master' && version.contains('SNAPSHOT')) {
       error('Release version cannot be SNAPSHOT')
    }
 
	def containerid = sh returnStdout: true, script: "docker run -v /home/kerberos-keytabs:/app -v $workspace/${repoName}:/workspace --rm dtrdl.nl.corp.tele2.com:9443/operations/kerberos_ubuntu sh /home/deploy.sh ${keytabfile} ${deployTarget} ${artifactId} ${pkgExtension} ${versionId} ${GroupId} ${deployDirectory} ${applicationUser} ${destinationFileName} > deploy.log"
  sh "cat $WORKSPACE/$repoName/deploy.log"
     
}

