/*
 * Copyright 2024 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.mod_spec.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArray;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArrayBuilder;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObject;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObjectBuilder;
import org.quiltmc.mod_spec.api.FinalQuiltModJsonV1.FinalEntrypointContainerV1;
import org.quiltmc.mod_spec.api.FinalQuiltModJsonV1.FinalEntrypointV1;
import org.quiltmc.mod_spec.api.FinalQuiltModJsonV1.FinalProvidesV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableEntrypointContainerV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableEntrypointV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1.MutableArrayVersionConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1.MutableLogicVersionConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1.MutableNewVersionConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1.MutableSingleVersionConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModDependencyV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableProvidesV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.ModConstraintV1.VersionConstraintV1.ArrayVersionConstraintV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.ModConstraintV1.VersionConstraintV1.NewVersionConstraintV1;
import org.quiltmc.parsers.json.JsonWriter;

/** The first schema version of a "quilt.mod.json" file - which has a schema_version of 1.
 * <p>
 * Some sub-objects are not directly represented here, like the "quilt_loader" field.
 * <p>
 * You should never extend or implement this - instead use either {@link MutableQuiltModJsonV1} or
 * {@link FinalQuiltModJsonV1}, depending on your needs. */
@Sealed({ MutableQuiltModJsonV1.class, FinalQuiltModJsonV1.class })
@ApiStatus.NonExtendable
public interface QuiltModJsonV1 extends QuiltModJson {

    /** The regular expression for validating {@link #group()} values. Use {@link #GROUP_PATTERN} for a {@link Pattern}
     * variant. */
    public static final String GROUP_REGEX = "^[a-zA-Z0-9-_.]+$";

    /** A {@link Pattern} for validating {@link #group()} values. This is just {@link #GROUP_REGEX}
     * {@link Pattern#compile(String) compiled} */
    public static final Pattern GROUP_PATTERN = Pattern.compile(GROUP_REGEX);

    /** The regular expression for validating {@link #id()} values. */
    public static final String ID_REGEX = "^[a-z][a-z0-9-_]{1,63}$";

    /** A {@link Pattern} for validating {@link #id()} values. This is just {@link #ID_REGEX}
     * {@link Pattern#compile(String) compiled} */
    public static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    /** The regular expression for validating {@link #intermediate_mappings()} values. Use
     * {@link #INTERMEDIATE_MAPPINGS_PATTERN} for a {@link Pattern} variant. */
    public static final String INTERMEDIATE_MAPPINGS_REGEX = "^[a-zA-Z0-9-_.]+:[a-zA-Z0-9-_.]+$";

    /** A {@link Pattern} for validating {@link #intermediate_mappings()} values. This is just
     * {@link #INTERMEDIATE_MAPPINGS_REGEX} {@link Pattern#compile(String) compiled} */
    public static final Pattern INTERMEDIATE_MAPPINGS_PATTERN = Pattern.compile(INTERMEDIATE_MAPPINGS_REGEX);

    @Override
    default int schema_version() {
        return 1;
    }

    /** A unique identifier for the organization behind or developers of the mod. The group string must match the
     * {@value #GROUP_REGEX} regular expression, and must not begin with the reserved namespace `loader.plugin.` It is
     * recommended, but not required, to follow Maven's
     * <a href="https://maven.apache.org/guides/mini/guide-naming-conventions.html">guide to naming conventions</a> */
    String group();

    /** A unique identifier for the mod or library defined by this file, matching the `{@value #ID_REGEX}` regular
     * expression. Best practice is that mod ID's are in snake_case. */
    String id();

    /** A list of {@link ProvidesV1} objects describing other mods / APIs that this mod provides. */
    List<? extends ProvidesV1> provides();

    @ApiStatus.NonExtendable
    @Sealed({ MutableProvidesV1.class, FinalProvidesV1.class })
    public interface ProvidesV1 extends JsonWritable {

        /** May either be in the form group:id or just id (where group follows the restrictions in
         * {@link QuiltModJsonV1#group()}, and id follows the restrictions in {@link QuiltModJsonV1#id()}) */
        String id();

        /** The version. Will be null if omitted, rather than {@link QuiltModJsonV1#version()} */
        String version();

        @Override
        default CustomJsonValue toJson() {
            String version = version();
            if (version == null) {
                return CustomJsonValue.createString(id());
            } else {
                Map<String, CustomJsonValue> map = new LinkedHashMap<>();
                map.put("id", CustomJsonValue.createString(id()));
                map.put("version", CustomJsonValue.createString(version));
                return CustomJsonValue.createObject(map);
            }
        }

        @Override
        default void write(JsonWriter writer) throws IOException {
            String version = version();
            if (version == null) {
                writer.value(id());
            } else {
                writer.beginObject();
                writer.name("id");
                writer.value(id());
                writer.name("version");
                writer.value(version);
                writer.endObject();
            }
        }
    }

    /** The version of this mod, used both to identify different builds of the same mod, and for other mods to depend on
     * or break with.
     * <p>
     * Mods are encouraged to conform to the <a href="https://semver.org/">Semantic Versioning 2.0.0 specifcation</a>,
     * but will be compared using <a href="https://github.com/unascribed/FlexVer">FlexVer<a> if they do not conform.
     * <p>
     * As a special case the value <code>${version}</code> may be used in a development environment. */
    String version();

    EntrypointContainerV1<? extends EntrypointV1> entrypoints();

    @ApiStatus.NonExtendable
    @Sealed({ MutableEntrypointContainerV1.class, FinalEntrypointContainerV1.class })
    public interface EntrypointContainerV1<E extends EntrypointV1> extends Map<String, List<E>>, JsonWritable {
        // This exists mostly due to generics:
        // Map<String, List<? extends EntrypointV1>>
        // cannot be overridden by subclasses to specify the entrypoint type

        @Override
        default CustomJsonObject toJson() {
            Map<String, CustomJsonValue> map = new LinkedHashMap<>();
            for (Map.Entry<String, List<E>> entry : entrySet()) {
                final CustomJsonValue value;
                List<E> list = entry.getValue();

                if (list.size() == 1) {
                    value = list.get(0).toJson();
                } else {
                    List<CustomJsonValue> out = new ArrayList<>();
                    for (E entrypoint : list) {
                        out.add(entrypoint.toJson());
                    }
                    value = CustomJsonValue.createArray(out);
                }
                map.put(entry.getKey(), value);
            }
            return CustomJsonValue.createObject(map);
        }

        @Override
        default void write(JsonWriter writer) throws IOException {
            writer.beginObject();
            for (Map.Entry<String, List<E>> entry : entrySet()) {
                writer.name(entry.getKey());
                List<E> list = entry.getValue();
                if (list.size() == 1) {
                    list.get(0).write(writer);
                } else {
                    writer.beginArray();
                    for (E entrypoint : list) {
                        entrypoint.write(writer);
                    }
                    writer.endArray();
                }
            }
            writer.endObject();
        }
    }

    @ApiStatus.NonExtendable
    @Sealed({ MutableEntrypointV1.class, FinalEntrypointV1.class })
    public interface EntrypointV1 extends JsonWritable {

        /** The default language adapter, provided by quilt loader.
         * <p>
         * This uses the JVM entrypoint notation, described below:
         * <p>
         * When referring to a class, the <em>binary name</em> is used. An example of a binary name is
         * <code>my.mod.MyClass$Inner</code>.
         * <p>
         * One of the following value notations may be used in the JVM notation:
         * <ul>
         * <li>Implementation onto a class
         * <ul>
         * <li>The value must contain a fully qualified binary name to the class.</li>
         * <li>Implementing class must extend or implement the entrypoint interface.</li>
         * <li>Class must have a no-argument public constructor.</li>
         * <li>Example: example.mod.MainModClass.</li>
         * </ul>
         * </li>
         * <li>A field inside of a class.
         * <ul>
         * <li>The value must contain a fully qualified binary name to the class followed by :: and a field name.</li>
         * <li>The field must be static.</li>
         * <li>The type of the field must be assignable from the field's class.</li>
         * <li>Example: example.mod.MainModClass::THE_INSTANCE</li>
         * <li>If there is ambiguity with a method's name, an exception will be thrown.</li>
         * </ul>
         * <li>A method inside of a class.
         * <ul>
         * <li>The value must contain a fully qualified binary name to the class followed by :: and a method name.</li>
         * <li>The method must be capable to implement the entrypoint type as a method reference. Generally this means
         * classes which are functional interfaces.</li>
         * <li>Constructor requirement varies based on the method being static or instance level:
         * <ul>
         * <li>A static method does not require a public no-argument constructor.</li>
         * <li>An instance method requires a public no-argument constructor.</li>
         * </ul>
         * <li>Example: <code>example.mod.MainModClass::init</code></li>
         * <li>If there is ambiguity with a fields's name or other method, an exception will be thrown.</li>
         * </ul>
         */
        public static final String DEFAULT_ADAPTER = "default";

        /** The language adapter to use for this entrypoint. If unspecified this is {@link #DEFAULT_ADAPTER} and loader
         * will use the JVM entrypoint notation when parsing the value. */
        String adapter();

        /** The value passed to the language adapter specified by {@link #adapter()} to load the entrypoint. The
         * definition of the default notation is in {@link #DEFAULT_ADAPTER}. */
        String value();

        @Override
        default CustomJsonValue toJson() {
            String adapter = adapter();
            if (adapter == null) {
                return CustomJsonValue.createString(value());
            } else {
                CustomJsonObjectBuilder obj = new CustomJsonObjectBuilder();
                obj.put("adapter", adapter);
                obj.put("value", value());
                return obj.build();
            }
        }

        @Override
        default void write(JsonWriter writer) throws IOException {
            String adapter = adapter();
            if (adapter == null) {
                writer.value(value());
            } else {
                writer.beginObject();
                writer.name("adapter");
                writer.value(adapter);
                writer.name("value");
                writer.value(value());
                writer.endObject();
            }
        }
    }

    /** A list of nested JAR files to load, relative to {@link QuiltMod#rootPath()}. */
    List<String> jars();

    /** All language adapters that this mod declares. The key will be referenced by other mods
     * {@link EntrypointV1#adapter()} field, and the value should be a class which directly implements
     * <code>org.quiltmc.loader.api.LanguageAdapter</code> */
    Map<String, String> language_adapters();

    /** Defines mods that this mod will not function without. */
    List<? extends ModDependencyV1> depends();

    /** Defines mods that this mod either breaks or is broken by. */
    List<? extends ModDependencyV1> breaks();

    @Sealed({ MutableModDependencyV1.class })
    @ApiStatus.NonExtendable
    public interface ModDependencyV1 extends JsonWritable {
        List<? extends ModConstraintV1> constraints();

    }

    @Sealed({ MutableModConstraintV1.class })
    @ApiStatus.NonExtendable
    public interface ModConstraintV1 extends JsonWritable {
        String id();

        VersionConstraintV1 versions();

        String reason();

        boolean optional();

        ModDependencyV1 unless();

        @Override
        default CustomJsonValue toJson() {
            VersionConstraintV1 versions = versions();
            String reason = reason();
            boolean optional = optional();
            ModDependencyV1 unless = unless();
            if (versions == null && reason == null && optional == false && unless == null) {
                return CustomJsonValue.createString(id());
            }
            CustomJsonObjectBuilder obj = new CustomJsonObjectBuilder();
            obj.put("id", id());
            if (versions != null) {
                obj.put("versions", versions);
            }
            if (reason != null) {
                obj.put("reason", reason);
            }
            if (optional) {
                obj.put("optional", optional);
            }
            if (unless != null) {
                obj.put("unless", unless);
            }
            return obj.build();
        }

        @Sealed({ NewVersionConstraintV1.class, ArrayVersionConstraintV1.class, MutableVersionConstraintV1.class })
        public interface VersionConstraintV1 extends JsonWritable {

            @Sealed({ SingleVersionConstraintV1.class, LogicVersionConstraintV1.class,
                MutableNewVersionConstraintV1.class })
            public interface NewVersionConstraintV1 extends VersionConstraintV1 {
                // marker interface
            }

            @Sealed({ MutableSingleVersionConstraintV1.class })
            public interface SingleVersionConstraintV1 extends NewVersionConstraintV1 {

                /** A version range specifier.
                 * <p>
                 * This can make use of only one of the following patterns:
                 * <ul>
                 * <li>* — Matches any version. Will fetch the latest version available if needed
                 * <li>1.0.0 — Matches the most recent version greater than or equal to version 1.0.0 and less than
                 * 2.0.0
                 * <li>=1.0.0 — Matches exactly version 1.0.0 and no other versions
                 * <li>>=1.0.0 — Matches any version greater than or equal to 1.0.0
                 * <li>>1.0.0 — Matches any version greater than version 1.0.0
                 * <li><=1.0.0 — Matches any version less than or equal to version 1.0.0
                 * <li><1.0.0 — Matches any version less than version 1.0.0
                 * <li>1.0.x — Matches any version with major version 1 and minor version 0.
                 * <li>~1.0.0 — Matches the most recent version greater than or equal to version 1.0.0 and less than
                 * 1.1.0
                 * <li>^1.0.0 — Matches the most recent version greater than or equal to version 1.0.0 and less than
                 * 2.0.0
                 * </ul>
                 */
                String version();

                @Override
                default CustomJsonValue toJson() {
                    return CustomJsonValue.createString(version());
                }
            }

            /** Discouraged array version constraint, as this always is defined as using "ANY" match logic, rather than
             * being configurable. New mods are encouraged to use */
            @Sealed({ MutableArrayVersionConstraintV1.class })
            public interface ArrayVersionConstraintV1 extends VersionConstraintV1 {

                /** A list of version constraints, which should follow the same requirements as
                 * {@link SingleVersionConstraintV1#version()}. */
                List<String> versions();

                @Override
                default CustomJsonArray toJson() {
                    CustomJsonArrayBuilder array = new CustomJsonArrayBuilder();
                    for (String version : versions()) {
                        array.add(version);
                    }
                    return array.build();
                }
            }

            /** Similar to {@link ArrayVersionConstraintV1}, except it allows mods to choose which constraint logic
             * should be applied (any or all) and allows sub-constraints to be either version specifiers or further
             * logic constraints. */
            @Sealed({ MutableLogicVersionConstraintV1.class })
            public interface LogicVersionConstraintV1 extends NewVersionConstraintV1 {

                public enum VersionConstraintLogicTypeV1 {
                    ANY("any"),
                    ALL("all");

                    public final String jsonName;

                    private VersionConstraintLogicTypeV1(String jsonName) {
                        this.jsonName = jsonName;
                    }
                }

                VersionConstraintLogicTypeV1 logic();

                List<? extends NewVersionConstraintV1> constraints();

                @Override
                default CustomJsonValue toJson() {
                    CustomJsonArrayBuilder array = new CustomJsonArrayBuilder();
                    for (NewVersionConstraintV1 constraint : constraints()) {
                        array.add(constraint.toJson());
                    }
                    CustomJsonObjectBuilder obj = new CustomJsonObjectBuilder();
                    obj.put(logic().jsonName, array);
                    return obj.build();
                }
            }
        }
    }

    /** Influences whether or not a mod candiate should be loaded or not. This doesn't affect mods directly placed in
     * the mods folder, which will always be loaded. The default value is {@link LoadTypeV1#if_required} */
    LoadTypeV1 load_type();

    public enum LoadTypeV1 implements JsonWritable {
        /** If any versions of this mod are present, then one of them will be loaded. Due to how mod loading actually
         * works if any of the different versions of this mod are present, and one of them has "load_type" set to
         * "always", then all of them are treated as it being set to "always". */
        always,
        /** If this mod can be loaded, then it will - otherwise it will silently not be loaded. */
        if_possible,
        /** If this mod is in another mods "depends" field then it will be loaded, otherwise it will silently not be
         * loaded. */
        if_required;

        @Override
        public CustomJsonValue toJson() {
            return CustomJsonValue.createString(name());
        }
    }

    /** The intermediate mappings used for this mod. The intermediate mappings string must be a valid maven coordinate
     * and match the ^[a-zA-Z0-9-_.]+:[a-zA-Z0-9-_.]+$ regular expression. This field currently only officially supports
     * org.quiltmc:hashed and net.fabricmc:intermediary. */
    String intermediate_mappings();

    // The "repositories" field is explicitly omitted, since it's not used

    /** A human-readable name for this mod. */
    String name();

    /** A human-readable description for this mod. This description should be plain text, with the exception of line
     * breaks, which can be represented with the newline character \n. */
    String description();

    /** A collection of key: value pairs denoting the persons or organizations that contributed to this project. The key
     * should be the name of the person or organization, while the value can be either a string representing a single
     * role or an array of strings each one representing a single role. A role can be any valid string. The "Owner" role
     * is defined as being the person(s) or organization in charge of the project. */
    Map<String, String> contributors();

    /** Standard {@link #contact()} field for email addresses for the organization/developers. */
    public static final String CONTACT_EMAIL = "email";
    /** Standard {@link #contact()} field for the main website for this mod.
     * <p>
     * This should be a valid http or https URL. */
    public static final String CONTACT_HOMEPAGE = "homepage";
    /** Standard {@link #contact()} field for the mods issue tracker.
     * <p>
     * This should be a valid http or https URL. */
    public static final String CONTACT_ISSUES = "issues";
    /** Standard {@link #contact()} field for the mod source code repository.
     * <p>
     * This should be a valid http or https URL. */
    public static final String CONTACT_SOURCES = "sources";

    /** A collection of key: value pairs denoting various contact information for the people behind this mod, with all
     * values being strings. The following keys are officially defined, though mods can provide as many additional
     * values as they wish:
     * <ul>
     * <li>{@value #CONTACT_EMAIL} — Valid e-mail address for the organization/developers</li>
     * <li>{@value #CONTACT_HOMEPAGE} — Valid HTTP/HTTPS address for the project or the organization/developers behind
     * it</li>
     * <li>{@value #CONTACT_ISSUES} — Valid HTTP/HTTPS address for the project issue tracker</li>
     * <li>{@value #CONTACT_SOURCES} — Valid HTTP/HTTPS address for a source code repository</li>
     * </ul>
     */
    Map<String, String> contact();

    /** The license, or licenses, this mod is available in. */
    List<? extends LicenseV1> license();

    /** A license. Licenses come in two forms: SPDX Identifiers. or custom licenses.
     * <p>
     * SPDX licenses are referenced by just their ID, and have all fields filled out from them.
     * <p>
     * Custom licenses may have any */
    public interface LicenseV1 {

        /** The {@link SpdxLicense} that this refers to, or null if this is not an SPDX license. */
        SpdxLicense spdxLicense();

        /** An identifier for this license. */
        String id();

        /** The name of this license. */
        String name();

        /** A website describing this license. May be empty if the license doesn't have a website. */
        String url();

        /** A description of this license. May be empty. */
        String description();
    }

    /** Either one or more paths to square .PNG files. */
    IconsV1 icon();

    /** A set of icons. This is either a single icon of unknown size ({@link SingleIconV1}), or a map of known icon
     * sizes ({@link MultiIconV1}) */
    @Sealed({ IconsV1.SingleIconV1.class, IconsV1.MultiIconV1.class })
    public interface IconsV1 {

        @Sealed
        public interface SingleIconV1 extends IconsV1 {
            String icon();
        }

        @Sealed
        public interface MultiIconV1 extends IconsV1 {
            /** Map of icon size (in pixels) to the path where the icon may be found. */
            SortedMap<Integer, String> icons();
        }
    }

    /** Config files for mixins. */
    List<? extends MixinEntryV1> mixin();

    public interface MixinEntryV1 {

        String config();

    }

    /**
     * 
     */
    List<String> access_widener();

    public enum MinecraftEnvironmentV1 {
        client,
        dedicated_server,
        /** The actual serialised name is {@link #VALUE_ALL} */
        ALL;

        public static final String VALUE_ALL = "*";
    }

    Map<String, CustomJsonValue> custom();
}
