package org.quiltmc.mod_spec.api;

import java.util.Collections;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonArray;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonBoolean;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonNumber;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonObject;
import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonString;

/** Utilities for getting values which are all optional, or will be ignored if they are of the wrong type. */
class MutableJsonUtil {

    static CustomJsonObject getObject(CustomJsonObject obj, String name) {
        CustomJsonValue value = obj.get(name);
        if (value instanceof CustomJsonObject) {
            return (CustomJsonObject) value;
        } else {
            return null;
        }
    }

    static CustomJsonObject getObjectOrEmpty(CustomJsonObject obj, String name) {
        CustomJsonObject object = getObject(obj, name);
        if (object != null) {
            return object;
        } else {
            String location = obj.location() + "." + name;
            return CustomJsonValue.createObject(location, Collections.emptyMap());
        }
    }

    static CustomJsonArray getArray(CustomJsonObject obj, String name) {
        CustomJsonValue value = obj.get(name);
        if (value instanceof CustomJsonArray) {
            return (CustomJsonArray) value;
        } else {
            return null;
        }
    }

    static CustomJsonArray getArrayOrEmpty(CustomJsonObject obj, String name) {
        CustomJsonArray array = getArray(obj, name);
        if (array != null) {
            return array;
        } else {
            String location = obj.location() + "." + name;
            return CustomJsonValue.createArray(location, Collections.emptyList());
        }
    }

    static Number getNumber(CustomJsonObject obj, String name) {
        CustomJsonValue value = obj.get(name);
        return value instanceof CustomJsonNumber ? ((CustomJsonNumber) value).value() : null;
    }

    static Double getDouble(CustomJsonObject obj, String name) {
        Number num = getNumber(obj, name);
        return num != null ? num.doubleValue() : null;
    }

    static double getDoubleOr0(CustomJsonObject obj, String name) {
        return getDoubleOr(obj, name, 0);
    }

    static double getDoubleOr(CustomJsonObject obj, String name, double defaultValue) {
        Double d = getDouble(obj, name);
        return d == null ? defaultValue : d;
    }

    static String getStringOrNull(CustomJsonObject obj, String name) {
        return getStringOr(obj, name, null);
    }

    static String getStringOrBlank(CustomJsonObject obj, String name) {
        return getStringOr(obj, name, "");
    }

    static String getStringOr(CustomJsonObject obj, String name, String defaultValue) {
        CustomJsonValue value = obj.get(name);
        return value instanceof CustomJsonString ? ((CustomJsonString) value).value() : defaultValue;
    }

    static boolean getBooleanOr(CustomJsonObject obj, String name, boolean defaultValue) {
        Boolean bool = getBooleanOrNull(obj, name);
        return bool != null ? bool : defaultValue;
    }

    static Boolean getBooleanOrNull(CustomJsonObject obj, String name) {
        CustomJsonValue value = obj.get(name);
        return value instanceof CustomJsonBoolean ? ((CustomJsonBoolean) value).value() : null;
    }
}
