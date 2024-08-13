package org.quiltmc.mod_spec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonToken;

public class SpdxLicenseGenerator {
    private static final String FS_SEP = FileSystems.getDefault().getSeparator();
    private static final String AUTOGEN_COMMENT = "// EVERYTHING BELOW IS AUTOMATICALLY GENERATED";

    public static void main(String[] args) throws IOException {
        // Expect 1st arg to be the path to generate in

        String path = args[0];
        Path root = Paths.get(".").toRealPath();
        Path file = root.resolve("src/main/java/org/quiltmc/mod_spec/api/SpdxLicense.java".replace("/", FS_SEP));

        if (!Files.isRegularFile(file)) {
            throw new Error("Didn't find SpdxLicense.java! (checked '" + file + "')");
        }

        Path backup = root.resolve("backup/SpdxLicense.java.bak");
        Files.deleteIfExists(backup);
        Files.copy(file, backup);
        List<String> lines = Files.readAllLines(file);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            out.add(line);
            int beginIndex = line.indexOf(AUTOGEN_COMMENT);
            if (beginIndex >= 0) {
                break;
            }
        }

        // Is this the right URL?
        String url = "https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json";

        Map<String, LicenseEntry> licenses = new TreeMap<>();
        final String actualLicenseVersion;
        final String actualLicenseReleaseDate;
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();

            Path backupLicenseList = root.resolve("backup/license_list.json");
            try (InputStream stream = connection.getInputStream()) {
                BufferedReader r = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                List<String> licenseLines = new ArrayList<>();
                String line;
                while ((line = r.readLine()) != null) {
                    licenseLines.add(line);
                }
                // Save the json for debugging
                Files.write(backupLicenseList, licenseLines);
            }

            try (JsonReader jr = JsonReader.json(backupLicenseList)) {
                jr.beginObject();
                assertName("licenseListVersion", jr);
                actualLicenseVersion = jr.nextString();
                assertName("licenses", jr);
                jr.beginArray();
                while (jr.peek() == JsonToken.BEGIN_OBJECT) {
                    jr.beginObject();

                    String id = null;
                    String name = null;
                    String reference = null;
                    String licenseLocation = jr.path();

                    while (jr.peek() == JsonToken.NAME) {
                        switch (jr.nextName()) {
                            case "reference": {
                                reference = jr.nextString();
                                break;
                            }
                            case "name": {
                                name = jr.nextString();
                                break;
                            }
                            case "licenseId": {
                                id = jr.nextString();
                                break;
                            }
                            default: {
                                jr.skipValue();
                                break;
                            }
                        }
                    }

                    if (id == null || name == null || reference == null) {
                        throw new Error("Missing id, name, or reference from " + licenseLocation);
                    }

                    licenses.put(id, new LicenseEntry(id, name, reference));

                    jr.endObject();
                }
                jr.endArray();
                assertName("releaseDate", jr);
                actualLicenseReleaseDate = jr.nextString();
                jr.endObject();
            }

        } catch (IOException e) {
            throw e;
        }

        out.add("\tstatic {");
        out.add("\t\tLICENSE_LIST_COMMIT = \"" + actualLicenseVersion + "\";");
        out.add("\t\tLICENSE_LIST_DATE = \"" + actualLicenseReleaseDate + "\";");
        out.add("\t}");
        out.add("");

        for (LicenseEntry license : licenses.values()) {
            out.add("\t/** <a href=\"" + license.reference + "\">" + license.name + "</a> */");
            String javaFriendlyId = license.id.replace('-', '_')//
                .replace('.', '_')//
                .replace("+", "_plus");
            out.add(
                "\tpublic static final SpdxLicense LICENSE_" + javaFriendlyId + " = new SpdxLicense(\"" + license.id
                    + "\", \"" + license.name.replace("\"", "\\\"") + "\", \"" + license.reference + "\");"
            );
        }

        out.add("}");

        Files.write(file, out);
    }

    private static void assertName(String name, JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NAME) {
            throw new IOException("Expected name '" + name + "', but found " + reader.peek() + " at " + reader.path());
        }
        String actualName = reader.nextName();
        if (!name.equals(actualName)) {
            throw new IOException("Expected name '" + name + "', but found '" + actualName + "' at " + reader.path());
        }
    }

    static class LicenseEntry {
        final String id, name, reference;

        public LicenseEntry(String id, String name, String reference) {
            this.id = id;
            this.name = name;
            this.reference = reference;
        }
    }
}
