/**
 * Usage:
 *  runPerformanceTests dir: 'customer-details-loadtests', command:'mvn gatling:test'
 */
def call(Map params) {

    def testDir = params.dir
    def command = params.command

    dir(testDir){
        try{
            sh command
        } catch (err){
            error("An error occurred while running loadtests " + err)
            throw err
        } finally {
            /* Report contains unique id, so it makes it difficult to find it for use in reportDir.
               Renaming it to 'site' */
            sh "mv target/gatling/report* target/gatling/site"
            publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/gatling/site',
                    reportFiles: "index.html",
                    reportName: "Performance test report"
            ])
        }
    }
}