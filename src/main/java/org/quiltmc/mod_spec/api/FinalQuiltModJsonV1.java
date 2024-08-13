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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArray;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObject;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonString;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonType;

public final class FinalQuiltModJsonV1 implements QuiltModJsonV1 {

    private final List<FinalProvidesV1> provides;
    private final FinalEntrypointContainerV1 entrypoints;

    @Override
    public List<FinalProvidesV1> provides() {
        return provides;
    }

    public static final class FinalProvidesV1 extends Located implements ProvidesV1 {

        private static final String REASON_ID = "[TODO:ID]";
        private static final String REASON_VERSION = "[TODO:VERSION]";

        private final String id;
        private final String version;

        FinalProvidesV1(ProvidesV1 source, ErrorBuilder errors) {
            super(((Located) source).source);
            String location = this.source.location();

            this.id = source.id();
            this.version = source.version();

            if (id == null) {
                errors.appendMissingValue(location, "id", REASON_ID, CustomJsonType.STRING);
            } else {
                validateID(errors);
            }
        }

        private void validateID(ErrorBuilder errors) {
            if (!ID_PATTERN.matcher(id).matches()) {
                final String actualLocation;
                CustomJsonValue actualValue;
                if (source instanceof CustomJsonObject) {
                    actualValue = ((CustomJsonObject) source).get("id");
                } else {
                    actualValue = source;
                }
                if (actualValue != null) {
                    actualLocation = actualValue.location();
                } else {
                    actualLocation = source.location() + ".id";
                    actualValue = CustomJsonValue.createString(actualLocation, id);
                }
                errors.appendInvalidValue("id", actualValue, REASON_ID);
            }
        }

        FinalProvidesV1(CustomJsonObject source, ErrorBuilder errors) {
            super(source);

            CustomJsonString id = errors.expectString(source, "id", REASON_ID);
            if (id != null) {
                this.id = id.value();
            } else {
                this.id = "";
            }

            CustomJsonValue version = source.get("version");
            if (version != null) {
                CustomJsonString versionVal = errors.expectString(version, "version", REASON_VERSION);
                if (versionVal != null) {
                    this.version = versionVal.value();
                } else {
                    this.version = null;
                }
            } else {
                this.version = null;
            }
        }

        public static FinalProvidesV1 from(ProvidesV1 source) throws InvalidModJsonException {
            ErrorBuilder errors = new ErrorBuilder();
            FinalProvidesV1 result = new FinalProvidesV1(source, errors);
            errors.throwIfErrored();
            return result;
        }

        public static FinalProvidesV1 from(CustomJsonObject source) throws InvalidModJsonException {
            ErrorBuilder errors = new ErrorBuilder();
            FinalProvidesV1 result = new FinalProvidesV1(source, errors);

            InvalidModJsonException exception = errors.build();
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        FinalProvidesV1(String id, String version) {
            super(USE_DEFAULT_SOURCE);
            this.id = id;
            this.version = version;
        }

        public static FinalProvidesV1 of(String id, String version) throws InvalidModJsonException {
            FinalProvidesV1 result = new FinalProvidesV1(id, version);
            ErrorBuilder errors = new ErrorBuilder();
            result.validateID(errors);
            errors.throwIfErrored();
            return result;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String version() {
            return version;
        }
    }

    @Override
    public FinalEntrypointContainerV1 entrypoints() {
        return entrypoints;
    }

    public static final class FinalEntrypointContainerV1 extends AbstractMap<String, List<FinalEntrypointV1>>
        implements EntrypointContainerV1<FinalEntrypointV1> {

        private static final String REASON_ENTRYPOINTS = "[TODO:ENTRYPOINTS]";
        private static final String REASON_ENTRYPOINT_ENTRY = "[TODO:ENTRYPOINT_ENTRY]";

        private final Map<String, List<FinalEntrypointV1>> map;

        <E extends EntrypointV1> FinalEntrypointContainerV1(EntrypointContainerV1<E> source, ErrorBuilder errors) {
            Map<String, List<FinalEntrypointV1>> out = new LinkedHashMap<>();

            for (Map.Entry<String, List<E>> entry : source.entrySet()) {
                List<E> sourceList = entry.getValue();
                List<FinalEntrypointV1> destList = new ArrayList<>();
                for (E entrypoint : sourceList) {
                    destList.add(new FinalEntrypointV1(entrypoint, errors));
                }
                out.put(entry.getKey(), Collections.unmodifiableList(destList));
            }

            this.map = Collections.unmodifiableMap(out);
        }

        FinalEntrypointContainerV1(CustomJsonValue source, ErrorBuilder errors) {
            if (source instanceof CustomJsonObject) {
                CustomJsonObject obj = (CustomJsonObject) source;
                Map<String, List<FinalEntrypointV1>> out = new LinkedHashMap<>();

                for (Map.Entry<String, CustomJsonValue> entryList : obj.entrySet()) {
                    List<FinalEntrypointV1> outList = new ArrayList<>();

                    CustomJsonValue value = entryList.getValue();

                    if (value instanceof CustomJsonArray) {
                        for (CustomJsonValue val : (CustomJsonArray) value) {
                            outList.add(new FinalEntrypointV1(val, errors));
                        }
                    } else if (value instanceof CustomJsonString || value instanceof CustomJsonObject) {
                        outList.add(new FinalEntrypointV1(value, errors));
                    } else {
                        errors.appendWrongType(
                            "entrypoints." + entryList.getKey(), value, REASON_ENTRYPOINT_ENTRY, CustomJsonType.ARRAY,
                            CustomJsonType.STRING, CustomJsonType.OBJECT
                        );
                    }

                    out.put(entryList.getKey(), Collections.unmodifiableList(outList));
                }

                this.map = Collections.unmodifiableMap(out);
            } else {
                errors.appendWrongType("entrypoints", source, REASON_ENTRYPOINTS, CustomJsonType.OBJECT);
                this.map = Collections.emptyMap();
            }
        }

        public static <E extends EntrypointV1> FinalEntrypointContainerV1 from(EntrypointContainerV1<E> source) throws InvalidModJsonException {
            ErrorBuilder errors = new ErrorBuilder();
            FinalEntrypointContainerV1 result = new FinalEntrypointContainerV1(source, errors);
            errors.throwIfErrored();
            return result;
        }

        public static FinalEntrypointContainerV1 from(CustomJsonValue source) throws InvalidModJsonException {
            ErrorBuilder errors = new ErrorBuilder();
            FinalEntrypointContainerV1 result = new FinalEntrypointContainerV1(source, errors);

            InvalidModJsonException exception = errors.build();
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        @Override
        public Set<Map.Entry<String, List<FinalEntrypointV1>>> entrySet() {
            return map.entrySet();
        }
    }

    public static final class FinalEntrypointV1 extends Located implements EntrypointV1 {

        private static final String REASON_ENTRYPOINT = "[TODO:WHOLE_ENTRYPOINT]";
        private static final String REASON_ADAPTER = "[TODO:ADAPTER]";
        private static final String REASON_VALUE = "[TODO:VALUE]";

        private final String adapter;
        private final String value;

        FinalEntrypointV1(EntrypointV1 source, ErrorBuilder errors) {
            super(((Located) source).source);
            String location = this.source.location();

            this.adapter = source.adapter();
            this.value = source.value();

            if (value == null) {
                errors.appendMissingValue(location, "value", REASON_VALUE, CustomJsonType.STRING);
            }
        }

        FinalEntrypointV1(CustomJsonValue source, ErrorBuilder errors) {
            super(source);

            if (source instanceof CustomJsonString) {

                this.adapter = null;
                this.value = ((CustomJsonString) source).value();

            } else if (source instanceof CustomJsonObject) {
                CustomJsonObject obj = (CustomJsonObject) source;

                CustomJsonString adapter = errors.optionalString(obj, "adapter", REASON_ADAPTER);
                this.adapter = adapter != null ? adapter.value() : null;

                CustomJsonString value = errors.expectString(obj, "value", REASON_VALUE);
                this.value = value != null ? value.value() : null;

            } else {
                this.adapter = null;
                this.value = null;
                errors.appendWrongType(
                    "entrypoint", source, REASON_ENTRYPOINT, CustomJsonType.OBJECT, CustomJsonType.STRING
                );
            }
        }

        public static FinalEntrypointV1 from(EntrypointV1 source) throws InvalidModJsonException {
            ErrorBuilder errors = new ErrorBuilder();
            FinalEntrypointV1 result = new FinalEntrypointV1(source, errors);
            errors.throwIfErrored();
            return result;
        }

        public static FinalEntrypointV1 from(CustomJsonValue source) throws InvalidModJsonException {
            ErrorBuilder errors = new ErrorBuilder();
            FinalEntrypointV1 result = new FinalEntrypointV1(source, errors);

            InvalidModJsonException exception = errors.build();
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        FinalEntrypointV1(String adapter, String value) {
            super(USE_DEFAULT_SOURCE);
            this.adapter = adapter;
            this.value = value;
        }

        public static FinalEntrypointV1 of(String adapter, String value) throws InvalidModJsonException {
            FinalEntrypointV1 result = new FinalEntrypointV1(adapter, value);
            ErrorBuilder errors = new ErrorBuilder();
            errors.throwIfErrored();
            return result;
        }

        @Override
        public String adapter() {
            return adapter;
        }

        @Override
        public String value() {
            return value;
        }
    }
}
