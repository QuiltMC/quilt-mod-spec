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
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArray;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonBoolean;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonNull;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonNumber;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObject;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonString;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.ParseException;

/** A general-purpose object representation of json, normally easier to use than {@link JsonReader} / {@link JsonWriter}
 * directly.
 * <p>
 * This is also used directly in mod jsons to represent unknown data, like "custom" values and possibly plugin defined
 * values.
 * <p>
 * This is recommended to be used with "instanceof" rather than checking {@link #type()} and then casting based on
 * that. */
public sealed interface CustomJsonValue extends JsonWritable {

    public static final String DEFAULT_LOCATION = "<created through code>";

    String location();

    CustomJsonType type();

    @Override
    default CustomJsonValue toJson() {
        return this;
    }

    @Override
    void write(JsonWriter writer) throws IOException;

    public static CustomJsonValue read(JsonReader reader) throws IOException, ParseException {

        switch (reader.peek()) {
        case BEGIN_ARRAY: {
            String location = reader.locationString();

            reader.beginArray();

            List<LoaderValue> elements = new ArrayList<>();

            while (reader.hasNext()) {
                elements.add(read(reader));
            }

            reader.endArray();

            return new ArrayImpl(location, elements);
        }
        case BEGIN_OBJECT: {
            String location = reader.locationString();

            reader.beginObject();

            Map<String, LoaderValue> elements = new LinkedHashMap<>();

            while (reader.hasNext()) {
                if (reader.peek() != JsonToken.NAME) {
                    throw new MalformedSyntaxException(reader, "Entry in object had an entry with no key");
                }

                String key = reader.nextName();
                elements.put(key, read(reader));
            }

            reader.endObject();

            return new ObjectImpl(location, elements);
        }
        case STRING:
            return new StringImpl(reader.locationString(), reader.nextString());
        case NUMBER:
            return new NumberImpl(reader.locationString(), reader.nextNumber());
        case BOOLEAN:
            return new BooleanImpl(reader.locationString(), reader.nextBoolean());
        case NULL:
            String location = reader.locationString();
            reader.nextNull();
            return new NullImpl(location);
        // Invalid
        case NAME:
            throw new MalformedSyntaxException(reader, "Unexpected name encountered");
        case END_ARRAY:
            throw new MalformedSyntaxException(reader, "Unexpected array end encountered");
        case END_OBJECT:
            throw new MalformedSyntaxException(reader, "Unexpected object end encountered");
        case END_DOCUMENT:
            throw new ParseException(reader, "Encountered end of document");
        }

        throw new UnsupportedOperationException("Encountered unreachable state");
    }

    public static CustomJsonObject createObject(Map<String, CustomJsonValue> map) {
        return createObject(DEFAULT_LOCATION, map);
    }

    public static CustomJsonObject createObject(String location, Map<String, CustomJsonValue> map) {
        return new CustomObject(location, Collections.unmodifiableMap(new LinkedHashMap<>(map)));
    }

    public static CustomJsonArray createArray(List<CustomJsonValue> list) {
        return createArray(DEFAULT_LOCATION, list);
    }

    public static CustomJsonArray createArray(String location, List<CustomJsonValue> list) {
        return new CustomArray(location, Collections.unmodifiableList(new ArrayList<>(list)));
    }

    public static CustomJsonNumber createNumber(Number number) {
        return createNumber(DEFAULT_LOCATION, number);
    }

    public static CustomJsonNumber createNumber(String location, Number number) {
        return new CustomNumber(location, number);
    }

    public static CustomJsonString createString(String value) {
        return createString(DEFAULT_LOCATION, value);
    }

    public static CustomJsonString createString(String location, String value) {
        return new CustomString(location, value);
    }

    public static CustomJsonBoolean createBoolean(boolean value) {
        return createBoolean(DEFAULT_LOCATION, value);
    }

    public static CustomJsonBoolean createBoolean(String location, boolean value) {
        return new CustomBoolean(location, value);
    }

    public enum CustomJsonType {
        OBJECT,
        ARRAY,
        NUMBER,
        STRING,
        BOOLEAN,
        NULL;
    }

    public sealed interface CustomJsonObject extends CustomJsonValue, Map<String, CustomJsonValue> {
        @Override
        default CustomJsonType type() {
            return CustomJsonType.OBJECT;
        }
    }

    public sealed interface CustomJsonArray extends CustomJsonValue, List<CustomJsonValue> {
        @Override
        default CustomJsonType type() {
            return CustomJsonType.ARRAY;
        }
    }

    public sealed interface CustomJsonNumber extends CustomJsonValue {
        Number value();

        @Override
        default CustomJsonType type() {
            return CustomJsonType.NUMBER;
        }
    }

    public sealed interface CustomJsonString extends CustomJsonValue {
        String value();

        @Override
        default CustomJsonType type() {
            return CustomJsonType.STRING;
        }
    }

    public sealed interface CustomJsonBoolean extends CustomJsonValue {
        boolean value();

        @Override
        default CustomJsonType type() {
            return CustomJsonType.BOOLEAN;
        }
    }

    public sealed interface CustomJsonNull extends CustomJsonValue {
        @Override
        default CustomJsonType type() {
            return CustomJsonType.NULL;
        }
    }

    public static final class CustomJsonObjectBuilder {
        private final Map<String, CustomJsonValue> map = new LinkedHashMap<>();

        public void put(String name, CustomJsonValue value) {
            map.put(name, value);
        }

        public void put(String name, CustomJsonObjectBuilder value) {
            put(name, value.build());
        }

        public void put(String name, CustomJsonArrayBuilder value) {
            put(name, value.build());
        }

        public void put(String name, JsonWritable value) {
            map.put(name, value.toJson());
        }

        public void put(String name, Number value) {
            map.put(name, createNumber(value));
        }

        public void put(String name, String value) {
            map.put(name, createString(value));
        }

        public void put(String name, boolean value) {
            map.put(name, createBoolean(value));
        }

        public CustomJsonObject build() {
            return createObject(map);
        }
    }

    public static final class CustomJsonArrayBuilder {
        private final List<CustomJsonValue> list = new ArrayList<>();

        public void add(CustomJsonValue value) {
            list.add(value);
        }

        public void add(CustomJsonObjectBuilder value) {
            add(value.build());
        }

        public void add(CustomJsonArrayBuilder value) {
            add(value.build());
        }

        public void add(JsonWritable value) {
            list.add(value.toJson());
        }

        public void add(Number value) {
            list.add(createNumber(value));
        }

        public void add(String value) {
            list.add(createString(value));
        }

        public void add(boolean value) {
            list.add(createBoolean(value));
        }

        public CustomJsonArray build() {
            return createArray(list);
        }
    }

}

final class CustomObject extends AbstractMap<String, CustomJsonValue> implements CustomJsonObject {

    final String location;
    final Map<String, CustomJsonValue> map;

    CustomObject(String location, Map<String, CustomJsonValue> map) {
        this.location = location;
        this.map = map;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public Set<Entry<String, CustomJsonValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        for (Map.Entry<String, CustomJsonValue> entry : entrySet()) {
            writer.name(entry.getKey());
            entry.getValue().write(writer);
        }
        writer.endObject();
    }
}

final class CustomArray extends AbstractList<CustomJsonValue> implements CustomJsonArray {

    final String location;
    final List<CustomJsonValue> list;

    CustomArray(String location, List<CustomJsonValue> list) {
        this.location = location;
        this.list = list;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public CustomJsonValue get(int index) {
        return list.get(index);
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.beginArray();
        for (CustomJsonValue value : this) {
            value.write(writer);
        }
        writer.endArray();
    }
}

final class CustomNumber implements CustomJsonNumber {
    final String location;
    final Number value;

    CustomNumber(String location, Number value) {
        this.location = location;
        this.value = value;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public Number value() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomNumber && value.equals(((CustomNumber) obj).value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.value(value);
    }
}

final class CustomString implements CustomJsonString {
    final String location;
    final String value;

    CustomString(String location, String value) {
        this.location = location;
        this.value = value;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomString && value.equals(((CustomString) obj).value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.value(value);
    }
}

final class CustomBoolean implements CustomJsonBoolean {
    final String location;
    final boolean value;

    CustomBoolean(String location, boolean value) {
        this.location = location;
        this.value = value;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public boolean value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomString && value == ((CustomBoolean) obj).value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.value(value);
    }
}

final class CustomNull implements CustomJsonNull {
    final String location;

    CustomNull(String location) {
        this.location = location;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomNull;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.nullValue();
    }
}
