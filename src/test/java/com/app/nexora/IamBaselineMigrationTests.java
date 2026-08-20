package com.app.nexora;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IamBaselineMigrationTests {

    private static final String MIGRATION = "/db/migration/V1__iam_baseline.sql";

    @Test
    void containsOnlyTheSevenIamTablesAndNoDumpData() throws IOException {
        String sql = readMigration();
        Set<String> tables = matches(sql, "CREATE\\s+TABLE\\s+public\\.([a-z_]+)");

        assertEquals(Set.of(
                "apps", "organizations", "permissions", "role_permissions",
                "roles", "user_roles", "users"
        ), tables);
        assertFalse(sql.matches("(?is).*\\b(COPY|INSERT\\s+INTO|CREATE\\s+DATABASE|CREATE\\s+TABLESPACE|OWNER\\s+TO|GRANT)\\b.*"));
    }

    @Test
    void preservesDumpConstraintsIndexesAndTriggers() throws IOException {
        String sql = readMigration();

        assertEquals(2, count(sql, "CREATE\\s+EXTENSION\\s+IF\\s+NOT\\s+EXISTS"));
        assertEquals(1, count(sql, "CREATE\\s+FUNCTION\\s+public\\.fn_set_updated_at"));
        assertEquals(7, count(sql, "CONSTRAINT\\s+[a-z_]+_pkey\\s+PRIMARY\\s+KEY"));
        assertEquals(9, count(sql, "CONSTRAINT\\s+[a-z_]+\\s+CHECK"));
        assertEquals(8, count(sql, "FOREIGN\\s+KEY"));
        assertEquals(27, count(sql, "CREATE\\s+(?:UNIQUE\\s+)?INDEX"));
        assertEquals(7, count(sql, "CREATE\\s+TRIGGER"));
    }

    private static String readMigration() throws IOException {
        try (InputStream input = IamBaselineMigrationTests.class.getResourceAsStream(MIGRATION)) {
            assertNotNull(input, "Missing migration " + MIGRATION);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Set<String> matches(String value, String regex) {
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(value);
        Set<String> results = new HashSet<>();
        while (matcher.find()) {
            results.add(matcher.group(1));
        }
        return results;
    }

    private static int count(String value, String regex) {
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(value);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
