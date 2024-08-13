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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArray;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArrayBuilder;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObject;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObjectBuilder;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonString;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonType;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1.MutableArrayVersionConstraintV1;
import org.quiltmc.mod_spec.api.MutableQuiltModJsonV1.MutableModConstraintV1.MutableVersionConstraintV1.MutableNewVersionConstraintV1;

/** A partial {@link QuiltModJsonV1} which may still be modified before being finalised to
 * {@link FinalQuiltModJsonV1}. */
public final class MutableQuiltModJsonV1 extends Located implements QuiltModJsonV1 {

    private String group;
    private String id;
    private final List<MutableProvidesV1> provides = new ArrayList<>();
    private String version;
    private final MutableEntrypointContainerV1 entrypoints = new MutableEntrypointContainerV1();
    private final List<String> jars = new ArrayList<>();
    private final Map<String, String> language_adapters = new LinkedHashMap<>();
    private final List<MutableModDependencyV1> depends = new ArrayList<>();
    private final List<MutableModDependencyV1> breaks = new ArrayList<>();
    private LoadTypeV1 load_type;
    private String intermediate_mappings;
    private String name;
    private String description;
    private final Map<String, String> contributors = new LinkedHashMap<>();
    private final Map<String, String> contact = new LinkedHashMap<>();
    private final List<MutableLicenseV1> license = new ArrayList<>();
    private MutableIconsV1 icon;
    private final List<MutableMixinEntryV1> mixin;
    private final List<String> access_widener;

    public MutableQuiltModJsonV1() {
        super(USE_DEFAULT_SOURCE);
    }

    /** Sets the source to the given json object, and sets {@link #id} and {@link #version} if they are present in the
     * source. */
    public MutableQuiltModJsonV1(CustomJsonObject source) {
        super(source);
        group = MutableJsonUtil.getStringOrNull(source, "group");
        id = MutableJsonUtil.getStringOrNull(source, "id");
        CustomJsonArray providesArray = MutableJsonUtil.getArrayOrEmpty(source, "provides");
        for (CustomJsonValue value : providesArray) {
            if (value instanceof CustomJsonObject) {
                provides.add(new MutableProvidesV1((CustomJsonObject) value));
            }
        }
        version = MutableJsonUtil.getStringOrNull(source, "version");
        CustomJsonObject entrypointsObject = MutableJsonUtil.getObjectOrEmpty(source, "entrypoints");
        for (Map.Entry<String, CustomJsonValue> value : entrypointsObject.entrySet()) {

        }
    }

    /** Creates a copy of the given source */
    public MutableQuiltModJsonV1(QuiltModJsonV1 source) {
        super(((Located) source).source);
        this.group = source.group();
        this.id = source.id();
        for (ProvidesV1 provide : source.provides()) {
            this.provides.add(new MutableProvidesV1(provide));
        }
    }

    @Override
    public List<MutableProvidesV1> provides() {
        return provides;
    }

    public static final class MutableProvidesV1 extends Located implements ProvidesV1 {

        private String id;
        private String version;

        /** Creates a blank provides. */
        public MutableProvidesV1() {
            super(USE_DEFAULT_SOURCE);
        }

        /** Sets the source to the given json object, and sets {@link #id} and {@link #version} if they are present in
         * the source. */
        public MutableProvidesV1(CustomJsonObject source) {
            super(source);

            CustomJsonValue idVal = source.get("id");
            if (idVal instanceof CustomJsonString) {
                this.id = ((CustomJsonString) idVal).value();
            }

            CustomJsonValue versionVal = source.get("version");
            if (versionVal instanceof CustomJsonString) {
                this.version = ((CustomJsonString) versionVal).value();
            }
        }

        /** Creates a copy of the given provides */
        public MutableProvidesV1(ProvidesV1 source) {
            super(((Located) source).source);
            this.id = source.id();
            this.version = source.version();
        }

        @Override
        public String id() {
            return id;
        }

        public MutableProvidesV1 id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public String version() {
            return version;
        }

        public MutableProvidesV1 version(String version) {
            this.version = version;
            return this;
        }
    }

    @Override
    public MutableEntrypointContainerV1 entrypoints() {
        return entrypoints;
    }

    public static final class MutableEntrypointContainerV1 extends LinkedHashMap<String, List<MutableEntrypointV1>>
        implements EntrypointContainerV1<MutableEntrypointV1> {

        // LinkedHashMap is sufficient
    }

    public static final class MutableEntrypointV1 extends Located implements EntrypointV1 {

        private String adapter;
        private String value;

        public MutableEntrypointV1() {
            super(USE_DEFAULT_SOURCE);
        }

        /** Sets the source to the given json object, and sets {@link #value} and {@link #adapter} if they are present
         * in the source. */
        public MutableEntrypointV1(CustomJsonObject source) {
            super(source);

            CustomJsonValue adapterVal = source.get("adapter");
            if (adapterVal instanceof CustomJsonString) {
                this.adapter = ((CustomJsonString) adapterVal).value();
            }

            CustomJsonValue valueVal = source.get("value");
            if (valueVal instanceof CustomJsonString) {
                this.value = ((CustomJsonString) valueVal).value();
            }
        }

        /** Sets the source to the given json object, and sets {@link #id} and {@link #version} if they are present in
         * the source. */
        public MutableEntrypointV1(CustomJsonString source) {
            super(source);
            this.adapter = null;
            this.value = source.value();
        }

        @Override
        public String adapter() {
            return adapter;
        }

        public MutableEntrypointV1 adapter(String adapter) {
            this.adapter = adapter;
            return this;
        }

        @Override
        public String value() {
            return value;
        }

        public MutableEntrypointV1 value(String value) {
            this.value = value;
            return this;
        }
    }

    @Override
    public List<MutableModDependencyV1> depends() {
        return depends;
    }

    @Override
    public List<MutableModDependencyV1> breaks() {
        return breaks;
    }

    public static final class MutableModDependencyV1 extends LocatedSerializable implements ModDependencyV1 {

        private final List<MutableModConstraintV1> constraints = new ArrayList<>();

        public MutableModDependencyV1() {
            super(USE_DEFAULT_SOURCE);
        }

        public MutableModDependencyV1(CustomJsonValue source) {
            super(source);
            if (source instanceof CustomJsonArray) {
                for (CustomJsonValue val : (CustomJsonArray) source) {
                    constraints.add(new MutableModConstraintV1(source));
                }
            } else {
                constraints.add(new MutableModConstraintV1(source));
            }
        }

        @Override
        public List<MutableModConstraintV1> constraints() {
            return constraints;
        }

        /** {@inheritDoc}
         * <p>
         * The only permitted types are {@link CustomJsonType#STRING} and {@link CustomJsonType#ARRAY} */
        @Override
        public void setSerialisationType(CustomJsonType type) {
            if (type != CustomJsonType.STRING && type != CustomJsonType.ARRAY) {
                throw new IllegalArgumentException("");
            }
            super.setSerialisationType(type);
        }

        @Override
        public CustomJsonValue toJson() {
            if (constraints().size() == 1) {
                return constraints().get(0).toJson();
            } else {
                CustomJsonArrayBuilder array = new CustomJsonArrayBuilder();
                for (ModConstraintV1 constraint : constraints()) {
                    array.add(constraint);
                }
                return array.build();
            }
        }
    }

    public static final class MutableModConstraintV1 extends Located implements ModConstraintV1 {

        private String id;
        private MutableVersionConstraintV1 versions;
        private String reason;
        /** Non-null to persist in serialisation. */
        private Boolean optional;
        private MutableModDependencyV1 unless;

        public MutableModConstraintV1() {
            // Use a string as the default location to default serialisation to compact form
            super(CustomJsonValue.createString(CustomJsonValue.DEFAULT_LOCATION, ""));
        }

        public MutableModConstraintV1(CustomJsonValue source) {
            super(source);

            if (source instanceof CustomJsonObject) {
                CustomJsonObject srcObj = (CustomJsonObject) source;
                id = MutableJsonUtil.getStringOrNull(srcObj, "id");
                CustomJsonValue versionsVal = srcObj.get("versions");
                if (versionsVal != null) {
                    versions = MutableVersionConstraintV1.from(versionsVal);
                }
                reason = MutableJsonUtil.getStringOrNull(srcObj, "reason");
                optional = MutableJsonUtil.getBooleanOrNull(srcObj, "optional");
                CustomJsonValue unlessVal = srcObj.get("unless");
                if (unlessVal != null) {
                    unless = new MutableModDependencyV1(unlessVal);
                }
            } else if (source instanceof CustomJsonString) {
                id = ((CustomJsonString) source).value();
            } else {
                // Wrong type - don't read anything
            }
        }

        @Override
        public String id() {
            return id;
        }

        public MutableModConstraintV1 id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public MutableVersionConstraintV1 versions() {
            return versions;
        }

        public MutableModConstraintV1 versions(MutableVersionConstraintV1 versions) {
            this.versions = versions;
            return this;
        }

        @Override
        public String reason() {
            return reason;
        }

        public MutableModConstraintV1 reason(String reason) {
            this.reason = reason;
            return this;
        }

        @Override
        public boolean optional() {
            return optional != null && optional;
        }

        public MutableModConstraintV1 optional(boolean optional) {
            this.optional = optional ? Boolean.TRUE : null;
        }

        @Override
        public MutableModDependencyV1 unless() {
            return unless;
        }

        public MutableModConstraintV1 unless(MutableModDependencyV1 unless) {
            this.unless = unless;
            return this;
        }

        @Override
        public CustomJsonValue toJson() {
            return toJson(this, optional);
        }

        static CustomJsonValue toJson(ModConstraintV1 constraint, Boolean optional) {
            LocatedSerializable located = (LocatedSerializable) constraint;
            VersionConstraintV1 versions = constraint.versions();
            String reason = constraint.reason();
            ModDependencyV1 unless = constraint.unless();
            CustomJsonValue source = located.source;
            if (versions == null && reason == null && optional == null && unless == null) {
                // This ensures we persist objects as objects
                if (located.serialisationType != CustomJsonType.OBJECT) {
                    return CustomJsonValue.createString(constraint.id());
                }
            }
            CustomJsonObjectBuilder obj = new CustomJsonObjectBuilder();
            obj.put("id", constraint.id());
            if (versions != null) {
                obj.put("versions", versions);
            }
            if (reason != null) {
                obj.put("reason", reason);
            }
            if (optional != null) {
                obj.put("optional", optional);
            }
            if (unless != null) {
                obj.put("unless", unless);
            }
            return obj.build();
        }

        @Sealed({ MutableNewVersionConstraintV1.class, MutableArrayVersionConstraintV1.class })
        public static abstract class MutableVersionConstraintV1 extends Located implements VersionConstraintV1 {

            MutableVersionConstraintV1(CustomJsonValue source) {
                super(source);
            }

            MutableVersionConstraintV1(UseDefaultSource marker) {
                super(marker);
            }

            /** Reads the given value, turning it into the appropriate type of version constraint depending on its type.
             * 
             * @return The new constraint object, or null if the value wasn't an object, string, or array. */
            public static MutableVersionConstraintV1 from(CustomJsonValue value) {
                if (value instanceof CustomJsonString) {
                    return new MutableSingleVersionConstraintV1((CustomJsonString) value);
                }
                if (value instanceof CustomJsonObject) {
                    return new MutableLogicVersionConstraintV1((CustomJsonObject) value);
                }
                if (value instanceof CustomJsonArray) {
                    return new MutableArrayVersionConstraintV1((CustomJsonArray) value);
                }
                return null;
            }

            public static MutableVersionConstraintV1 copy(VersionConstraintV1 constraint) {
                if (constraint instanceof SingleVersionConstraintV1) {
                    return new MutableSingleVersionConstraintV1((SingleVersionConstraintV1) constraint);
                }
                if (constraint instanceof ArrayVersionConstraintV1) {
                    return new MutableArrayVersionConstraintV1((ArrayVersionConstraintV1) constraint);
                }
                if (constraint instanceof LogicVersionConstraintV1) {
                    return new MutableLogicVersionConstraintV1((LogicVersionConstraintV1) constraint);
                }
                if (constraint == null) {
                    return null;
                }
                throw new IllegalStateException(
                    "Unknown / illegal implementation of VersionConstraintV1 " + constraint.getClass()
                );
            }

            @Sealed({ MutableSingleVersionConstraintV1.class, MutableLogicVersionConstraintV1.class })
            public static abstract class MutableNewVersionConstraintV1 extends MutableVersionConstraintV1
                implements NewVersionConstraintV1 {

                MutableNewVersionConstraintV1(CustomJsonValue source) {
                    super(source);
                }

                MutableNewVersionConstraintV1(UseDefaultSource marker) {
                    super(marker);
                }

                /** Reads the given value, turning it into the appropriate type of version constraint depending on its
                 * type.
                 * 
                 * @return The new constraint object, or null if the value wasn't an object, string, or array. */
                public static MutableNewVersionConstraintV1 from(CustomJsonValue value) {
                    if (value instanceof CustomJsonString) {
                        return new MutableSingleVersionConstraintV1((CustomJsonString) value);
                    }
                    if (value instanceof CustomJsonObject) {
                        return new MutableLogicVersionConstraintV1((CustomJsonObject) value);
                    }
                    return null;
                }

                public static MutableNewVersionConstraintV1 copy(NewVersionConstraintV1 constraint) {
                    if (constraint instanceof SingleVersionConstraintV1) {
                        return new MutableSingleVersionConstraintV1((SingleVersionConstraintV1) constraint);
                    }
                    if (constraint instanceof LogicVersionConstraintV1) {
                        return new MutableLogicVersionConstraintV1((LogicVersionConstraintV1) constraint);
                    }
                    if (constraint == null) {
                        return null;
                    }
                    throw new IllegalStateException(
                        "Unknown / illegal implementation of NewVersionConstraintV1 " + constraint.getClass()
                    );
                }
            }

            public static final class MutableSingleVersionConstraintV1 extends MutableNewVersionConstraintV1
                implements SingleVersionConstraintV1 {

                private String version;

                public MutableSingleVersionConstraintV1() {
                    super(USE_DEFAULT_SOURCE);
                }

                public MutableSingleVersionConstraintV1(CustomJsonString value) {
                    super(value);
                    this.version = value.value();
                }

                public MutableSingleVersionConstraintV1(SingleVersionConstraintV1 source) {
                    super(((Located) source).source);
                }

                @Override
                public String version() {
                    return version;
                }

                public MutableSingleVersionConstraintV1 version(String version) {
                    this.version = version;
                    return this;
                }
            }

            public static final class MutableArrayVersionConstraintV1 extends MutableVersionConstraintV1
                implements ArrayVersionConstraintV1 {

                private final List<String> versions = new ArrayList<>();

                public MutableArrayVersionConstraintV1() {
                    super(USE_DEFAULT_SOURCE);
                }

                public MutableArrayVersionConstraintV1(CustomJsonArray value) {
                    super(value);
                }

                public MutableArrayVersionConstraintV1(ArrayVersionConstraintV1 from) {
                    super(((Located) from).source);
                    versions.addAll(from.versions());
                }

                /** @return A modifiable list of version constraints. */
                @Override
                public List<String> versions() {
                    return versions;
                }
            }

            public static final class MutableLogicVersionConstraintV1 extends MutableNewVersionConstraintV1
                implements LogicVersionConstraintV1 {

                private VersionConstraintLogicTypeV1 logic;
                private final List<MutableNewVersionConstraintV1> constraints = new ArrayList<>();

                /** @return A new {@link MutableLogicVersionConstraintV1} using {@link VersionConstraintLogicTypeV1#ALL}
                 *         logic. */
                public static MutableLogicVersionConstraintV1 createAll() {
                    return new MutableLogicVersionConstraintV1(VersionConstraintLogicTypeV1.ALL);
                }

                /** @return A new {@link MutableLogicVersionConstraintV1} using {@link VersionConstraintLogicTypeV1#ALL}
                 *         logic. */
                public static MutableLogicVersionConstraintV1 createAny() {
                    return new MutableLogicVersionConstraintV1(VersionConstraintLogicTypeV1.ANY);
                }

                public MutableLogicVersionConstraintV1() {
                    super(USE_DEFAULT_SOURCE);
                }

                public MutableLogicVersionConstraintV1(VersionConstraintLogicTypeV1 logic) {
                    super(USE_DEFAULT_SOURCE);
                    this.logic = logic;
                }

                public MutableLogicVersionConstraintV1(CustomJsonObject value) {
                    super(value);
                    CustomJsonValue subValue = value.get("all");
                    if (subValue != null) {
                        logic = VersionConstraintLogicTypeV1.ALL;
                    } else {
                        subValue = value.get("any");
                        logic = VersionConstraintLogicTypeV1.ANY;
                    }

                    if (subValue instanceof CustomJsonArray) {
                        for (CustomJsonValue versionVal : (CustomJsonArray) subValue) {
                            MutableNewVersionConstraintV1 constraint = MutableNewVersionConstraintV1.from(versionVal);
                            if (constraint != null) {
                                constraints.add(constraint);
                            }
                        }
                    }
                }

                public MutableLogicVersionConstraintV1(LogicVersionConstraintV1 from) {
                    super(((Located) from).source);
                    logic = from.logic();
                    for (NewVersionConstraintV1 constraint : from.constraints()) {
                        constraints.add(MutableNewVersionConstraintV1.copy(constraint));
                    }
                }

                /** Returns the logic type for this constraint. This fields initial value is null, but it may only be
                 * set to a non-null value. */
                @Override
                public VersionConstraintLogicTypeV1 logic() {
                    return logic;
                }

                /** @param logic New logic type. May not be null. */
                public MutableLogicVersionConstraintV1 logic(VersionConstraintLogicTypeV1 logic) {
                    this.logic = Objects.requireNonNull(logic);
                    return this;
                }

                /** @return A modifiable list of version constraints. */
                @Override
                public List<MutableNewVersionConstraintV1> constraints() {
                    return constraints;
                }
            }
        }
    }
}
