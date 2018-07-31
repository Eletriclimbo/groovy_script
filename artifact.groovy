@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import groovyx.net.http.*

def authString = 'myuser:pass'.getBytes().encodeBase64().toString()
def repo = "For_TAR"
def newart = args[1]
def group = args[1].split("-",3)[0]
def artifact = args[1].split("-",3)[1]
def version = args[1].replaceAll("\\D+","")
def http1 = new URL("http://192.168.100.100/repository/${repo}/${group}/${group}-${artifact}/${version}/${newart}").openConnection()
http1.setRequestProperty("Authorization", "Basic ${authString}")
if (args[0] == 'pull') {
    new File("${newart}").withOutputStream { out ->
        def getRC = http1.getResponseCode()
        if(getRC.equals(200)) {
            out << http1.inputStream
            File file = new File("out.txt")
            file.append(' Group: ' + "${group}"+ '\n' + ' Artifact: ' + "${artifact}"+ '\n' + ' Version: ' + "${version}" + '\n'+ '\n')
            println file.text
        }
        else {println('ERROR! NO SUCH REPO_NAME')}
    }
}
else {
    def http = new URL("http://192.168.100.100/repository/${repo}/${group}/${group}-${artifact}/${version}/${newart}").openConnection()
    http.setRequestProperty("Authorization", "Basic ${authString}")
    http.doOutput = true
    http.setRequestMethod("PUT")
    http.setRequestProperty("Content-Type", "application/x-gzip")
    def out = new DataOutputStream(http.outputStream)
    out.write(new File ("${newart}").getBytes())
    out.close()
    println http.responseCode
}
