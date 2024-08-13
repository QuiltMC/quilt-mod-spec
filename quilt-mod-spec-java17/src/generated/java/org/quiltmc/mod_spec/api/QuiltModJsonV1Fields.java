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

import java.util.List;
import java.util.Map;

import org.quiltmc.mod_spec.api.QuiltModJsonV1.DependencyV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.EntrypointV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.IconsV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.LicenseV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.LoadTypeV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.MixinEntryV1;
import org.quiltmc.mod_spec.api.QuiltModJsonV1.ProvidesV1;

/** Shared fields for {@link MutableQuiltModJsonV1} and */
abstract sealed class QuiltModJsonV1Fields permits FinalQuiltModJsonV1, MutableQuiltModJsonV1 {

    public String group() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public String id() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<? extends ProvidesV1> provides() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public String version() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public Map<String, List<? extends EntrypointV1>> entrypoints() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<String> jars() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public Map<String, String> language_adapters() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<? extends DependencyV1> depends() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<? extends DependencyV1> breaks() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public LoadTypeV1 load_type() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public String intermediate_mappings() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public String name() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public String description() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public Map<String, String> contributors() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public Map<String, String> contact() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<? extends LicenseV1> license() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public IconsV1 icon() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<? extends MixinEntryV1> mixin() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }

    public List<String> access_widener() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("// TODO: Implement this!");
    }
}
