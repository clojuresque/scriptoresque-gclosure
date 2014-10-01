/*-
 * Copyright 2014 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Licensed under the EUPL V.1.1 (cf. file EUPL-1.1 distributed with the
 * source code.) Translations in other european languages available at
 * https://joinup.ec.europa.eu/software/page/eupl.
 *
 * Alternatively, you may choose to use the software under the MIT license
 * (cf. file MIT distributed with the source code).
 */

package clojuresque

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal

import clojuresque.tasks.GClosureCompile
import clojuresque.tasks.GClosureSourceSet

class GClosurePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: "java-base"

        configureConfigurations(project)
        configureSourceSets(project)
        configureCompilation(project)
    }

    def private configureConfigurations(project) {
        project.configurations {
            gclosure {
                transitive = false
                visible = false
                description = "Configuration for the closure compiler"
            }
        }

        project.dependencies {
            gclosure "com.google.javascript:closure-compiler:v20140625"
        }
    }

    def private configureSourceSets(project) {
        project.sourceSets.all { sourceSet ->
            def gclosureSourceSet = new GClosureSourceSet(sourceSet.name,
                ((ProjectInternal)project).fileResolver)

            sourceSet.convention.plugins.gclosure = gclosureSourceSet
            sourceSet.gclosure.srcDir "src/${sourceSet.name}/gclosure"
            sourceSet.allSource.source gclosureSourceSet.gclosure
        }
    }

    def private configureCompilation(project) {
        project.task("gclosureCompile", type: GClosureCompile) {
            delayedOutputDirectory = { new File(project.buildDir, "gclosure") }
            description = "Compile all google closure source modules"
        }
    }
}
