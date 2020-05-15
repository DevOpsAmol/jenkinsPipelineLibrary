/**
 * Usage:
 *  runPerformanceTests dir: 'loadtests', command:'mvn gatling:test'
 */
def call(Map params) {

    def testDir = params.dir
    def execommand = params.executecommand
	def analysecommand = params.analysecommand

    dir(testDir){
        try{
            sh execommand
			sh analysecommand
        } catch (err){
            error("An error occurred while running loadtests " + err)
            throw err
        } finally {
           publishHTML (target: [
		      allowMissing: false,
		      alwaysLinkToLastBuild: false,
		      keepAll: true,
		      includes: '*.html,*.png,*.json,*.txt,*.csv,*.css',
		      reportDir: 'target/jmeter',
		      reportFiles: "index.html",
		      reportName: "Performance Report(Jmeter)"
		    ])
        }
    }
}