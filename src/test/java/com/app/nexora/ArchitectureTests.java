package com.app.nexora;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTests {

    private final JavaClasses classes = new ClassFileImporter().importPackages("com.app.nexora");

    @Test
    void verifiesModularMonolithBoundaries() {
        ApplicationModules.of(NexoraApplication.class).verify();
    }

    @Test
    void domainDoesNotDependOnFrameworksOrOuterLayers() {
        noClasses()
                .that().resideInAPackage("..iam.user.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "..iam.user.application..",
                        "..iam.user.web..",
                        "..iam.user.persistence..",
                        "..iam.user.config.."
                )
                .check(classes);
    }

    @Test
    void applicationDoesNotDependOnAdapters() {
        noClasses()
                .that().resideInAPackage("..iam.user.application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.springframework.web..",
                        "..iam.user.web..",
                        "..iam.user.persistence..",
                        "..iam.user.config.."
                )
                .check(classes);
    }
}
