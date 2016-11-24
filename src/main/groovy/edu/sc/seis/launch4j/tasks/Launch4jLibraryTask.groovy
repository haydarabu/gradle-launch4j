package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.ExtractLibraries
import edu.sc.seis.launch4j.Launch4jPlugin
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class Launch4jLibraryTask extends DefaultLaunch4jTask {

    private static final String TEMPORARY_DIRECTORY = "tmp/launch4j"

    @TaskAction
    def run() {
        copyLibraries()
        def tmpDir = new File(project.buildDir, TEMPORARY_DIRECTORY)
        new ExtractLibraries(project).execute(tmpDir)
        createXML()
        getDest().delete()
        def stdOut = new ByteArrayOutputStream()
        def execResult = project.exec {
            commandLine "java", "-jar", "${tmpDir}/${ExtractLibraries.LAUNCH4J_BINARY_DIRECTORY}/launch4j-${Launch4jPlugin.ARTIFACT_VERSION}.jar", "${getXmlFile()}"
            workingDir getOutputDirectory()
            standardOutput = stdOut
            errorOutput = stdOut
            ignoreExitValue = true
        }

        if (execResult.exitValue == 0) {
            project.delete(getXmlFile())
        } else {
            throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${stdOut.toString()}");
        }
    }

}
