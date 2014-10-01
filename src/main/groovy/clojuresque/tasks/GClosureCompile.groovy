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

package clojuresque.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

import kotka.gradle.utils.Delayed

class GClosureCompile extends DefaultTask {
    static final DEFAULT_OPTIMIZATION = "simple"
    static final OPTIMIZATION_LEVELS  = [
        whitespace: "WHITESPACE_ONLY",
        simple:     "SIMPLE",
        advanced:   "ADVANCED"
    ]

    @Delayed
    def optimizationLevel = DEFAULT_OPTIMIZATION

    @Delayed
    def outputDirectory = null

    @TaskAction
    void compile() {
        def deps = project.sourceSets.inject([:], transitiveDepsClosure)
        def args = []

        args << "--compilation_level"
        args << OPTIMIZATION_LEVELS[getOptimizationLevel()]

        args << "--manage_closure_dependencies"

        args << "--module_output_path_prefix"
        args << getOutputDirectory().path

        project.sourceSets.sort(order(deps)).each { addModule args, it }

        project.javaexec {
            main = "com.google.javascript.jscomp.CommandLineRunner"
            args = this.args
            classpath = project.configurations.gclosure
        }
    }

    def addModule(args, sourceSet) {
        def sourceFiles = sourceSet.gclosure.asFileTree.files
        def deps = sourceSet.gclosure.dependsOn.collect { toName it }

        def depsList = ""
        if (deps.size() > 0)
            depsList = ":" + deps.join(",")

        sourceFiles.each {
            args << "--js"
            args << it.path
        }

        args << "--module"
        args << sourceSet.name + ":" + sourceFiles.size() + depsList
    }

    def transitiveDepsClosure = { deps, sourceSet ->
        def name = toName sourceSet
        if (!deps.containsKey(name)) {
            def ds = toSourceSet(sourceSet).gclosure.dependsOn.collect { toName it }
            deps = ds.inject(deps, transitiveDepsClosure)
            deps[name] = (ds + ds.collect { deps[it] }).flatten() as Set
        }

        return deps
    }

    def order(deps) {
        return { a, b ->
            a = toName a
            b = toName b

            if (deps[a].contains(b)) return  1
            if (deps[b].contains(a)) return -1

            return a.compareTo(b)
        }
    }

    def toSourceSet(String name) { project.sourceSets[name] }
    def toSourceSet(SourceSet sourceSet) { sourceSet }

    def toName(String name) { name }
    def toName(SourceSet sourceSet) { sourceSet.name }
}
