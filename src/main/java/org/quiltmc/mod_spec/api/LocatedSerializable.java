package org.quiltmc.mod_spec.api;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonType;

/** An object which can be serialised in different ways, but the client can choose which way is preferred. */
abstract class LocatedSerializable extends Located {

    CustomJsonType serialisationType;

    LocatedSerializable(CustomJsonValue source) {
        super(source);
    }

    LocatedSerializable(UseDefaultSource marker) {
        super(marker);
    }

    /** Changes the serialisation type to the given type
     * 
     * @throws IllegalArgumentException if the given type is not valid for this object. */
    public void setSerialisationType(CustomJsonType type) {
        this.serialisationType = type;
    }
}
