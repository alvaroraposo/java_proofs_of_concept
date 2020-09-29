package br.edu.fas;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.Test;

import br.edu.fas.persistence.Dao;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Criação do Projeto Java por meio de Framework Maven 
 * Testes arquiteturais com Archunit
 * Instalar extensão Project Management for Java
 * Intalar extensão do Maven e do Java
 * Criar Maven Project
 * GroupId - estrutura de pacotes do projeto (br.edu.fas)
 * pom.xml - dependências do projeto. Verificar versão do jdk executado.
 * ******* maven.compiler.source
 * acrescentar dependência do Archunit no pom.xml
 */
public class FooArchitectTest {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("br.edu.fas");
    
    @Test
    public void verificarDependenciaParaCamadaPersistencia() {
        ArchRule rule = classes()
        .that().resideInAPackage("..persistence..")
        .should().onlyHaveDependentClassesThat().resideInAnyPackage("..persistence..", "..service..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarDependenciaDaCamadaPersistencia() {
        ArchRule rule = noClasses()
        .that().resideInAPackage("..persistence..")
        .should().dependOnClassesThat().resideInAnyPackage("..service..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarNomesClassesCamadaPersistencia() {
        ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Dao")
        .should().resideInAPackage("..persistence..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarImplementacaoInterfaceDao() {
        ArchRule rule = classes()
        .that().implement(Dao.class)
        .should().haveSimpleNameEndingWith("Dao");

        rule.check(importedClasses);
    }

    @Test
    public void verificarDependenciasCiclicas() {
        ArchRule rule = slices()
        .matching("br.edu.fas.(*)..").should().beFreeOfCycles();

        rule.check(importedClasses);
    }

    @Test
    public void verificaViolacaoCamadas() {
        ArchRule rule = layeredArchitecture()
        .layer("Service").definedBy("..service..")
        .layer("Persistence").definedBy("..persistence..")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");

        rule.check(importedClasses);
    }
}
