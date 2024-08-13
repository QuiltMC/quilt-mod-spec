package org.quiltmc.mod_spec.api;

import java.io.IOException;

import org.quiltmc.parsers.json.JsonWriter;

/** Used for objects which can be represented as {@link CustomJsonValue}, and so can be written to a {@link JsonWriter}.
 * <p>
 * Most implementations also support construction from a {@link CustomJsonValue}. */
public interface JsonWritable {
    CustomJsonValue toJson();

    /** Writes this to the given writer. The default implementation writes the return value of {@link #toJson()} to the
     * writer, but subclasses may be able to do this more efficiently. */
    default void write(JsonWriter writer) throws IOException {
        toJson().write(writer);
    }
}
