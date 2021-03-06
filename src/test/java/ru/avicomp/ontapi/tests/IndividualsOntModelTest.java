/*
 * This file is part of the ONT API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2017, Avicomp Services, AO
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package ru.avicomp.ontapi.tests;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.*;

import ru.avicomp.ontapi.OntManagers;
import ru.avicomp.ontapi.OntologyManager;
import ru.avicomp.ontapi.OntologyModel;
import ru.avicomp.ontapi.jena.model.OntClass;
import ru.avicomp.ontapi.jena.model.OntGraphModel;
import ru.avicomp.ontapi.jena.model.OntIndividual;
import ru.avicomp.ontapi.jena.vocabulary.OWL;
import ru.avicomp.ontapi.jena.vocabulary.RDF;
import ru.avicomp.ontapi.utils.OntIRI;

/**
 * test individuals using jena-graph and owl-api
 * <p>
 * Created by @szuev on 08.10.2016.
 */
public class IndividualsOntModelTest extends OntModelTestBase {

    @Test
    public void test() throws OWLOntologyCreationException {
        OntIRI iri = OntIRI.create("http://test.test/add-class-individual");
        OntologyManager manager = OntManagers.createONT();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OntologyModel owl = manager.createOntology(iri.toOwlOntologyID());
        OntGraphModel jena = owl.asGraphModel();

        OntIRI class1 = iri.addFragment("ClassN1");
        OntIRI class2 = iri.addFragment("ClassN2");
        OntIRI individual1 = iri.addFragment("TestIndividualN1");
        OntIRI individual2 = iri.addFragment("TestIndividualN2");
        OntIRI individual3 = iri.addFragment("TestIndividualN3");
        int classesCount = 2;
        int individualsCount = 3;

        LOGGER.info("Add classes.");
        manager.applyChange(new AddAxiom(owl, factory.getOWLDeclarationAxiom(factory.getOWLClass(class1))));
        jena.add(class2.toResource(), RDF.type, OWL.Class);

        LOGGER.info("Add individuals.");
        LOGGER.debug("Add individuals using OWL");
        manager.applyChange(new AddAxiom(owl, factory.getOWLClassAssertionAxiom(factory.getOWLClass(class1), factory.getOWLNamedIndividual(individual1))));
        LOGGER.debug("Add individuals using ONT");
        jena.add(individual2.toResource(), RDF.type, class1.toResource());
        jena.add(individual2.toResource(), RDF.type, OWL.NamedIndividual);
        jena.getOntEntity(OntClass.class, class2.getIRIString()).createIndividual(individual3.getIRIString());

        debug(owl);

        Assert.assertEquals("OWL: incorrect classes count", classesCount + individualsCount, owl.axioms(AxiomType.DECLARATION).count());
        Assert.assertEquals("Jena: incorrect classes count.", classesCount, jena.ontEntities(OntClass.class).count());
        Assert.assertEquals("OWL: incorrect individuals count", individualsCount, owl.axioms(AxiomType.CLASS_ASSERTION).count());
        Assert.assertEquals("Jena: incorrect individuals count.", individualsCount, jena.ontObjects(OntIndividual.class).count());

        LOGGER.info("Remove individuals");
        // remove class assertion and declaration:
        jena.removeAll(individual3.toResource(), null, null);
        // remove class-assertion:
        manager.applyChange(new RemoveAxiom(owl, factory.getOWLClassAssertionAxiom(factory.getOWLClass(class1), factory.getOWLNamedIndividual(individual1))));
        // remove declaration:
        owl.remove(factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(individual1)));
        individualsCount = 1;

        debug(owl);

        Assert.assertEquals("OWL: incorrect individuals count after removing", individualsCount, owl.axioms(AxiomType.CLASS_ASSERTION).count());
        Assert.assertEquals("Jena: incorrect individuals count after removing.", individualsCount, jena.ontObjects(OntIndividual.class).count());
    }

}
