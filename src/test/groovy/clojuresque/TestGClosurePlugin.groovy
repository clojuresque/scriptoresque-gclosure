package clojuresque

import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Shared
import spock.lang.Specification

class TestGClosurePlugin extends Specification {
    @Shared
    def project

    def setupSpec() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: "clojuresque.clojurescript-gclosure"
        project.sourceSets { a }
    }

    def "the plugin sets up the gclosure configuration"() {
        expect:
        project.configurations.hasProperty("gclosure")
    }

    def "the plugin sets up the source set conventions"() {
        expect:
        project.sourceSets.a.hasProperty("gclosure")
    }

    def "the plugin sets up the compilation task"() {
        expect:
        project.hasProperty("gclosureCompile")
    }
}
