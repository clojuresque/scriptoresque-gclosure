package clojuresque

import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

class TestGClosureCompile extends Specification {
    def "the plugin sets up the source set conventions"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.apply plugin: "clojuresque.clojurescript-gclosure"
        project.sourceSets {
            a
            b { gclosure.dependsOn = ["a"] }
            c { gclosure.dependsOn = ["a", "b"] }
            d { gclosure.dependsOn = ["b"] }
            e { gclosure.dependsOn = ["c", "d"] }
        }

        expect:
        project.sourceSets.inject([:], project.gclosureCompile.transitiveDepsClosure) == [
            a:    []         as Set,
            b:    ["a"]      as Set,
            c:    ["a", "b"] as Set,
            d:    ["a", "b"] as Set,
            e:    ["a", "b", "c", "d"] as Set
        ]
    }
}
