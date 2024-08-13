package org.quiltmc.mod_spec.api;

/** Summarises common metadata into a single interface. This should be used in preference to {@link QuiltModJson} if you
 * don't care about how this is represented in json.
 * <p>
 * This interface is heavily based on the ModMetadata interface from quilt loader. */
public interface ExposedModMetadata {

    String id();

    String group();

    String version();

    String name();

    String description();

    public interface ExposedModLicense {

    }

}
