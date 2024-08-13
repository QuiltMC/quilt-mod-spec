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

import java.nio.file.Path;

/** Represents a quilt mod file, when it was read from a zip file. */
public interface QuiltMod {

    // TODO: Consider this!
    // Like, we don't actually want to move quilt loader's file systems into this

    /** @return A Path to the root of this quilt mod file */
    Path rootPath();

    /** @return The Quilt Mod Json file, as required for all quilt mods. */
    QuiltModJson quiltModJson();

}
