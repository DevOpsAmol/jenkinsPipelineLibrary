/**
 * Usage:
 *  runSoapUITests soapuifolder: 'customer-details-loadtests', hostname:'', testname: '', hostnamelb: ''
 */
def call(Map params) {
    
    def soapuifolder = params.soapuifolder
	def hostname = params.hostname
	def testname = params.testname
	def hostnamelb = params.hostnamelb
	
	
    def antVersion = 'Ant'
    withEnv( ["ANT_HOME=${tool antVersion}"] ) {
        def ant = "$ANT_HOME/bin/ant"
	    sh "${ant} -f ${env.WORKSPACE}${soapuifolder}/build-test-unix.xml -Dhostname=${hostname} -Dfolder=${env.WORKSPACE}${soapuifolder}/smoke -Dtestfile=${testname}.xml -Dhostnamelb=${hostnamelb} "
    }

}
