#!/usr/bin/env groovy

/**
 * Usage:
 *  runSecurityTests endpoints:['http://localhost:8080/login.htm', 'http://localhost:8080/login.htm']
 */
def call(Map params) {

    def config = utils.getConfig()
    def endpoints = params.endpoints

    def endpointsString = '';
    endpoints.each{endpoint->
        endpointsString += " -t ${endpoint}"
    }

    // Run zap scanner
    try{
        sh "mkdir ${env.WORKSPACE}/owasp"
    }catch (error){
        println(error) //directory might already exist after previous run
    }
    def containerid = sh returnStdout: true, script: "docker run --rm -v ${env.WORKSPACE}/owasp:/zap/wrk/:rw -t -d -u root dtrdl.nl.corp.tele2.com:9443/operations/zap2docker-stable zap-baseline.py ${endpointsString} -i -m5 -j -a -r zapreport.html"
    retry = 0
    while(!fileExists("${env.WORKSPACE}/owasp/zapreport.html")){
        if(retry > 20){
            error('took to long for the zap to make a perort to be deployed')
        }
        sleep 30
        retry++
    }

    // obtain the report
    publishHTML (target: [
            allowMissing: false,
            alwaysLinkToLastBuild: false,
            keepAll: true,
            reportDir: "../owasp",
            reportFiles: "zapreport.html",
            reportName: "Security Report(OWASP)"
    ])
}