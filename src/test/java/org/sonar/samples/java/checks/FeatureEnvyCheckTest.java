package org.sonar.samples.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

public class FeatureEnvyCheckTest {
    @Test
    void checkFeatureEnvy1() {
        // Verifies that the check will raise the adequate issues with the expected message.
        // In the test file, lines which should raise an issue have been commented out
        // by using the following syntax: "// Noncompliant {{EXPECTED_MESSAGE}}"
        CheckVerifier.newVerifier()
                .onFile("src/test/files/FeatureEnvyCheck1.java")
                .withCheck(new FeatureEnvyCheck())
                .verifyIssues();
    }

    @Test
    void checkFeatureEnvy2() {
        // Verifies that the check will raise the adequate issues with the expected message.
        // In the test file, lines which should raise an issue have been commented out
        // by using the following syntax: "// Noncompliant {{EXPECTED_MESSAGE}}"
        CheckVerifier.newVerifier()
                .onFile("src/test/files/FeatureEnvyCheck2.java")
                .withCheck(new FeatureEnvyCheck())
                .verifyIssues();
    }

    @Test
    void checkFeatureEnvy3() {
        // Verifies that the check will raise the adequate issues with the expected message.
        // In the test file, lines which should raise an issue have been commented out
        // by using the following syntax: "// Noncompliant {{EXPECTED_MESSAGE}}"
        CheckVerifier.newVerifier()
                .onFile("src/test/files/FeatureEnvyCheck3.java")
                .withCheck(new FeatureEnvyCheck())
                .verifyIssues();
    }
}

