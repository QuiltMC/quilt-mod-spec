package org.quiltmc.mod_spec.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArray;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonBoolean;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonNumber;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObject;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonString;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonType;
import org.quiltmc.mod_spec.api.InvalidModJsonException.InvalidValueProblem;
import org.quiltmc.mod_spec.api.InvalidModJsonException.MissingValueProblem;
import org.quiltmc.mod_spec.api.InvalidModJsonException.ModJsonProblem;
import org.quiltmc.mod_spec.api.InvalidModJsonException.WrongTypeProblem;

final class ErrorBuilder {

    private final List<ModJsonProblem> problems = new ArrayList<>();

    void appendMissingValue(String location, String name, String reason, CustomJsonType... validTypes) {
        problems.add(new MissingValueProblem(location, name, setOf(validTypes), reason));
    }

    void appendWrongType(String name, CustomJsonValue actualValue, String reason, CustomJsonType... validTypes) {
        problems.add(new WrongTypeProblem(name, setOf(validTypes), actualValue, reason));
    }

    void appendInvalidValue(String name, CustomJsonValue actualValue, String reason) {
        problems.add(new InvalidValueProblem(name, actualValue, reason));
    }

    CustomJsonObject expectObject(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            problems.add(new MissingValueProblem(obj.location(), name, setOf(CustomJsonType.OBJECT), reason));
            return null;
        }

        return expectObject(value, name, reason);
    }

    CustomJsonObject optionalObject(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            return null;
        }
        return expectObject(value, name, reason);
    }

    CustomJsonObject expectObject(CustomJsonValue value, String name, String reason) {
        if (value instanceof CustomJsonObject) {
            return (CustomJsonObject) value;
        } else {
            problems.add(new WrongTypeProblem(name, setOf(CustomJsonType.OBJECT), value, reason));
        }
        return null;
    }

    CustomJsonArray expectArray(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            problems.add(new MissingValueProblem(obj.location(), name, setOf(CustomJsonType.ARRAY), reason));
            return null;
        }

        return expectArray(value, name, reason);
    }

    CustomJsonArray optionalArray(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            return null;
        }
        return expectArray(value, name, reason);
    }

    CustomJsonArray expectArray(CustomJsonValue value, String name, String reason) {
        if (value instanceof CustomJsonArray) {
            return (CustomJsonArray) value;
        } else {
            problems.add(new WrongTypeProblem(name, setOf(CustomJsonType.ARRAY), value, reason));
        }
        return null;
    }

    CustomJsonNumber expectNumber(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            problems.add(new MissingValueProblem(obj.location(), name, setOf(CustomJsonType.NUMBER), reason));
            return null;
        }
        return expectNumber(value, name, reason);
    }

    CustomJsonNumber optionalNumber(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            return null;
        }
        return expectNumber(value, name, reason);
    }

    CustomJsonNumber expectNumber(CustomJsonValue value, String name, String reason) {
        if (value instanceof CustomJsonNumber) {
            return (CustomJsonNumber) value;
        } else {
            problems.add(new WrongTypeProblem(name, setOf(CustomJsonType.NUMBER), value, reason));
        }
        return null;
    }

    CustomJsonString expectString(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            problems.add(new MissingValueProblem(obj.location(), name, setOf(CustomJsonType.STRING), reason));
            return null;
        }

        return expectString(value, name, reason);
    }

    CustomJsonString optionalString(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            return null;
        }

        return expectString(value, name, reason);
    }

    CustomJsonString expectString(CustomJsonValue value, String name, String reason) {
        if (value instanceof CustomJsonString) {
            return (CustomJsonString) value;
        } else {
            problems.add(new WrongTypeProblem(name, setOf(CustomJsonType.STRING), value, reason));
        }
        return null;
    }

    CustomJsonBoolean expectBoolean(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            problems.add(new MissingValueProblem(obj.location(), name, setOf(CustomJsonType.BOOLEAN), reason));
            return null;
        }

        return expectBoolean(value, name, reason);
    }

    CustomJsonBoolean optionalBoolean(CustomJsonObject obj, String name, String reason) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            return null;
        }

        return expectBoolean(value, name, reason);
    }

    CustomJsonBoolean expectBoolean(CustomJsonValue value, String name, String reason) {
        if (value instanceof CustomJsonBoolean) {
            return (CustomJsonBoolean) value;
        } else {
            problems.add(new WrongTypeProblem(name, setOf(CustomJsonType.BOOLEAN), value, reason));
        }
        return null;
    }

    CustomJsonValue expect(CustomJsonObject obj, String name, String reason, CustomJsonType... validTypes) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            problems.add(new MissingValueProblem(obj.location(), name, setOf(validTypes), reason));
            return null;
        }

        return expect(name, reason, value, validTypes);
    }

    CustomJsonValue optional(CustomJsonObject obj, String name, String reason, CustomJsonType... validTypes) {
        CustomJsonValue value = obj.get(name);
        if (value == null) {
            return null;
        }

        return expect(name, reason, value, validTypes);
    }

    CustomJsonValue expect(String name, String reason, CustomJsonValue value, CustomJsonType... validTypes) {
        CustomJsonType actualType = value.type();
        for (CustomJsonType allowed : validTypes) {
            if (actualType == allowed) {
                return value;
            }
        }

        problems.add(new WrongTypeProblem(name, setOf(validTypes), value, reason));
        return null;
    }

    private static Set<CustomJsonType> setOf(CustomJsonType... values) {
        return Collections.unmodifiableSet(EnumSet.of(values[0], values));
    }

    InvalidModJsonException build() {
        if (problems.isEmpty()) {
            return null;
        } else {
            return makeError();
        }
    }

    private InvalidModJsonException makeError() {
        return new InvalidModJsonException(Collections.unmodifiableList(new ArrayList<>(problems)));
    }

    void throwIfErrored() throws InvalidModJsonException {
        if (!problems.isEmpty()) {
            throw makeError();
        }
    }
}
