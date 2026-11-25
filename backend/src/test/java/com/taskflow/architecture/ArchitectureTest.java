package com.taskflow.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Enforces the Clean Architecture dependency rule at build time. */
@AnalyzeClasses(packages = "com.taskflow", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_is_framework_free = noClasses().that().resideInAPackage("com.taskflow.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.taskflow.application..", "com.taskflow.infrastructure..", "com.taskflow.web..",
                    "org.springframework..", "jakarta..", "com.fasterxml..");

    @ArchTest
    static final ArchRule application_depends_only_on_domain = noClasses().that().resideInAPackage("com.taskflow.application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.taskflow.infrastructure..", "com.taskflow.web..",
                    "org.springframework.web..", "org.springframework.data..", "org.springframework.security..",
                    "jakarta.persistence..");

    @ArchTest
    static final ArchRule web_does_not_touch_infrastructure = noClasses().that().resideInAPackage("com.taskflow.web..")
            .should().dependOnClassesThat().resideInAPackage("com.taskflow.infrastructure..");

    @ArchTest
    static final ArchRule infrastructure_does_not_depend_on_web = noClasses().that().resideInAPackage("com.taskflow.infrastructure..")
            .should().dependOnClassesThat().resideInAPackage("com.taskflow.web..");
}
