package quiltmc.test;

import org.quiltmc.mod_spec.api.QuiltModJsonV1.ModConstraintV1.VersionConstraintV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.ModConstraintV1.VersionConstraintV1.ArrayVersionConstraintV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.ModConstraintV1.VersionConstraintV1.NewVersionConstraintV1;

public class Test {

    public static int test(VersionConstraintV1 constraint) {
        /// This is a compile test to ensure that we don't need to switch on MutableVersionConstraint
        return switch (constraint) {
            case NewVersionConstraintV1 n -> 0;
            case ArrayVersionConstraintV1 a -> 1;
        };
    }
}
