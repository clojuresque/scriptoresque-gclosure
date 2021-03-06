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

import kotka.gradle.utils.tasks.GenericSourceSet

@GenericSourceSet(sourceName="gclosure", sourcePatterns=["**/*.js"])
class GClosureSourceSet {
    def protected initSourceSet(displayString, fileResolver) {
        new GClosureSourceDirectorySet(displayString, fileResolver)
    }
}
