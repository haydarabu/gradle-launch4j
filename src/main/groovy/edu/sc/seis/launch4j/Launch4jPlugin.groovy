package edu.sc.seis.launch4j;


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Sync

class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String TASK_XML_GENERATE_NAME = "generateXmlConfig"
    static final String TASK_LIB_COPY_NAME = "l4jLib"
    static final String TASK_RUN_NAME = "createExe"
    Launch4jPluginExtension pluginConvention;

    def void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        project.configurations.add(LAUNCH4J_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
                .setDescription('The launch4j configuration for this project.')
        Launch4jPluginExtension pluginExtension = new Launch4jPluginExtension()
        pluginExtension.initExtensionDefaults(project)
        project.extensions.launch4j = pluginExtension
        Task xmlTask = addCreateLaunch4jXMLTask(project)
        Task copyTask = addCopyToLibTask(project)
        Task runTask = addRunLauch4jTask(project)
        runTask.dependsOn(copyTask)
        runTask.dependsOn(xmlTask)
    }

    private Task addCreateLaunch4jXMLTask(Project project) {
        Task task = project.tasks.add(TASK_XML_GENERATE_NAME, CreateLaunch4jXMLTask)
        task.description = "Creates XML configuration file used by launch4j to create an windows exe."
        task.group = LAUNCH4J_GROUP
        return task
    }

    private Task addCopyToLibTask(Project project) {
        Sync task = project.tasks.add(TASK_LIB_COPY_NAME, Sync)
        task.description = "Copies the project dependency jars in the lib directory."
        task.group = LAUNCH4J_GROUP
        task.with configureDistSpec(project)
        task.into { project.file("${project.buildDir}/${project.launch4j.outputDir}/lib") }
        return task
    }

    private Task addRunLauch4jTask(Project project) {
        def run = project.tasks.add(TASK_RUN_NAME, ExecLaunch4JTask)
        run.description = "Runs launch4j to generate an .exe file"
        run.group = LAUNCH4J_GROUP
        return run
    }

    private CopySpec configureDistSpec(Project project) {
        CopySpec distSpec = project.copySpec {}
        def jar = project.tasks[JavaPlugin.JAR_TASK_NAME]

        distSpec.with {
            from(jar)
            from(project.configurations.runtime)
        }

        return distSpec
    }
}





