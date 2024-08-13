# Quilt Loader Mod Json Specification

This is a work in progress possible replacement location for the quilt.mod.json specification, and some new Java APIs to make reading and writing these formats easier.

This repository contains two main things:

1. The specification for all the metadata files inside a jar that Quilt Loader uses.
    (Currently just `quilt.mod.json`)
2. A java API for reading and writing those files.

## Specification

Currently there is only one version of the `quilt.mod.json` file, and its specification is found in `src/main/resources/quilt_mod_spec/quilt.mod.json.v1.md`

## Java API

All public API is in the `org.quiltmc.mod_spec.api` package. For the sake of simplicity, there are no non-api packages - every internal class and method is package-private.

Almost all API types have exactly two implementations: a `Mutable` variant and a `Final` variant. The mutable variant is modifiable, and contains no validation, while the final variant is immutable, and performs strict checks during construction to validate that every field is a correct value.

The root type for `quilt.mod.json` files is `QuiltModJson`, with (currently) one subtype: `QuiltModJsonV1`.

All QuiltModJson based types are designed to map as closely to the json specification as possible, and as such they more useful when writing tools that modify specific schema versions of the jsons.
This repository WILL IN THE FUTURE [TODO] contain(s) an additional api type: `ExposedModJson`, which is designed to closely map to Quilt Loader's `ModMetadata` class. This is intended for tooling which doesn't care about modifying a mod json, but instead just needs to read some common information from it.

### Mutable implementations

All mutable implementations have 3 constructors (or static factories):

1. A no-args default constructor, where every value starts off as null
2. At least one constructor that takes a CustomJsonValue (or subtype) to read it from json.
    - If any invalid values are in the json, they are ignored.
3. A constructor that takes the base type, useful for copying an existing mutable or final implementation. This constructor always deep-copies all objects, ensuring that no references remain to the original object.

Most values of mutable implementations may be null, or set to null, even if they are mandatory fields. The normal exception is if the api class only stores a single field, like an array, or the field is not part of the json output.


### Final implementations

All final implementations only have static factories, normally named `from` or `of`:

1. A copy factory that acts in the same way as the mutable copy constructor.
2. At least one constructor that takes a CustomJsonValue (or subtype) to read it from json.
3. For simple types, a factory that accepts the values directly.

All of these factories may throw an `InvalidModJsonException` if there is anything wrong with the arguments.

### Json

This (de)serialises json using the quilt-parsers json library, often storing in an object form `CustomJsonValue` (and subtypes), which saves needing to read a JsonReader manually.

### Sealed types

Although this library targets Java 8, this is complied into a Multi-Release Jar, with `@Sealed` annotations being converted into proper sealed types. (This is only useful on Java 17 and above, though is most useful in Java 21 with "Pattern Matching for Switch".

For example, icons are referenced in two ways: either a single path to the icon file, or an integer to string map, where the keys are the size of that image. This is represented in this API with this:

```java
@Sealed
public interface IconsV1 {

    public interface SingleIconV1 extends IconsV1 {
        String icon();
    }

    public interface MultiIconV1 extends IconsV1 {
        SortedMap<Integer, String> icons();
    }
}
```

then the resulting switch statement needs no default:

```java
public Collection<String> getAllIcons(IconsV1 icons) {
    return switch (icons) {
        case null -> Collections.emptySet();
        case SingleIconV1 single -> Collections.singleton(single.icon());
        case MultiIconV1 multi -> multi.icons().values();
    };
}
```

