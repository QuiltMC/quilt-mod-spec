package org.quiltmc.mod_spec.api;

import java.util.Collections;

/** Base class for all mod json objects which can be sourced from a {@link CustomJsonValue} */
abstract class Located {
    static final UseDefaultSource USE_DEFAULT_SOURCE = UseDefaultSource.CREATE;

    /** Marker enum, used to differentiate constructors. */
    enum UseDefaultSource {
        CREATE;
    }

    final CustomJsonValue source;

    /** Creates a {@link Located} without a source (it will have a default source). */
    Located(UseDefaultSource marker) {
        this.source = CustomJsonValue.createObject(CustomJsonValue.DEFAULT_LOCATION, Collections.emptyMap());
    }

    Located(CustomJsonValue source) {
        this.source = source;
    }
}
