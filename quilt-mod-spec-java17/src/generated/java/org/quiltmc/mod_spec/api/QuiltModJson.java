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

/** A "quilt.mod.json" file, inside of a {@link QuiltMod}.
 * <p>
 * Currently there is only one major version of a {@link QuiltModJson}: {@link QuiltModJsonV1}. In the future there may
 * be more, so this is a separate interface. */
public sealed interface QuiltModJson extends JsonWritable permits QuiltModJsonV1, FutureQuiltModJson {

    /** The quilt mod file schema version used for parsing this file. Currently the only valid version is 1. */
    int schema_version();
}

/** Used to indicate that additional mod json versions will be added, and any switch statements on them will need to
 * take into account future versions. This is package private to prevent code outside of this package from using it in a
 * switch statement, requiring a "default" section and handling unknown versions. */
final class FutureQuiltModJson implements QuiltModJson {
    private FutureQuiltModJson() {
        throw new Error("Future Quilt Mod Json isn't intended to be created!");
    }

    @Override
    public int schema_version() {
        return Integer.MAX_VALUE;
    }

    @Override
    public CustomJsonValue toJson() {
        throw new Error("Future Quilt Mod Json isn't intended to be created!");
    }
}
