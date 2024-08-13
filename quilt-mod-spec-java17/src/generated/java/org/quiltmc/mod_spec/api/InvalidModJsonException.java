package org.quiltmc.mod_spec.api;

import java.util.List;
import java.util.Set;

import org.quiltmc.mod_spec.api.CustomJsonValue.CustomJsonType;

/** Thrown when building a final mod json, or when testing a mutable mod json with */
public class InvalidModJsonException extends Exception {

    private final List<ModJsonProblem> problems;

    InvalidModJsonException(List<ModJsonProblem> problems) {
        super(buildDescription(problems));
        this.problems = problems;
    }

    private static String buildDescription(List<ModJsonProblem> from) {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    public sealed interface ModJsonProblem {
        String describe();
    }

    static String describeExpectedType(Set<CustomJsonType> types) {

    }

    /** Indicates that a value is missing. */
    public static final class MissingValueProblem implements ModJsonProblem {
        private final String location;
        private final String valueName;
        private final Set<CustomJsonType> expectedTypes;
        private final String reason;

        MissingValueProblem(String location, String valueName, Set<CustomJsonType> expectedTypes, String reason) {
            this.location = location;
            this.valueName = valueName;
            this.expectedTypes = expectedTypes;
            this.reason = reason;
        }

        /** @return Where the value should be. */
        public String location() {
            return location;
        }

        /** @return The name of the value that should be present. */
        public String valueName() {
            return valueName;
        }

        /** @return The valid types that could be present. */
        public Set<CustomJsonType> expectedTypes() {
            return expectedTypes;
        }

        /** A description of what should be present. */
        public String reason() {
            return reason;
        }

        @Override
        public String describe() {
            return "Missing value at " + location + ": expected " + valueName + " to be "
                + describeExpectedType(expectedTypes) + " but was missing! " + reason;
        }
    }

    /** Indicates that a value is of the wrong type. */
    public static final class WrongTypeProblem implements ModJsonProblem {
        private final String location;
        private final String valueName;
        private final Set<CustomJsonType> expectedTypes;
        private final CustomJsonValue actualValue;
        private final String reason;

        WrongTypeProblem(
            String valueName, Set<CustomJsonType> expectedTypes, CustomJsonValue actualValue, String reason
        ) {
            this.location = actualValue.location();
            this.valueName = valueName;
            this.expectedTypes = expectedTypes;
            this.actualValue = actualValue;
            this.reason = reason;
        }

        /** @return Where the value should be. */
        public String location() {
            return location;
        }

        /** @return The name of the value that should be present. */
        public String valueName() {
            return valueName;
        }

        /** @return The valid types that could be present. */
        public Set<CustomJsonType> expectedTypes() {
            return expectedTypes;
        }

        /** @return The value found. */
        public CustomJsonValue actualValue() {
            return actualValue;
        }

        /** A description of what should be present. */
        public String reason() {
            return reason;
        }
    }

    /** Indicates that a value is present and of the correct type, but is invalid or fails validation checks, like if
     * {@link QuiltModJsonV1#id()} doesn't match {@link QuiltModJsonV1#ID_PATTERN} */
    public static final class InvalidValueProblem implements ModJsonProblem {

        private final String location;
        private final String valueName;
        private final CustomJsonValue actualValue;
        private final String reason;

        InvalidValueProblem(String valueName, CustomJsonValue actualValue, String reason) {
            this.location = actualValue.location();
            this.valueName = valueName;
            this.actualValue = actualValue;
            this.reason = reason;
        }

        /** @return Where the value should be. */
        public String location() {
            return location;
        }

        /** @return The name of the value that should be present. */
        public String valueName() {
            return valueName;
        }

        /** @return The value found. */
        public CustomJsonValue actualValue() {
            return actualValue;
        }

        /** A description of what should be present. */
        public String reason() {
            return reason;
        }
    }
}
