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
package org.semanticweb.owlapi.api.test.ontology;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;
import org.semanticweb.owlapi.model.*;

/**
 * @author Matthew Horridge, The University Of Manchester, Information
 *         Management Group
 * @since 2.2.0
 */
@ru.avicomp.ontapi.utils.ModifiedForONTApi
@SuppressWarnings("javadoc")
public class OntologyURITestCase extends TestBase {

    /**
     * ONT-API: I changed behaviour of {@link OWLOntology#toString()} since don't want any calculation on this operation.
     * In ONT-API we are working with graph, while in OWL-API there is a collections.
     * Beyond that the solution when you provide some complex info during toString seems strange to me.
     *
     * @throws OWLOntologyCreationException
     */
    @Test
    public void testNamedOntologyToString() throws OWLOntologyCreationException {
        IRI ontIRI = OWLFunctionalSyntaxFactory.IRI("http://owlapi.sourceforge.net/", "ont");
        OWLOntology ont = m.createOntology(ontIRI);
        String s = ont.toString();
        String suffix = String.format("[Axioms: %d Logical Axioms: %d] First 20 axioms: {}", ont.getAxiomCount(), ont.getLogicalAxiomCount());
        String expected = DEBUG_USE_OWL ? String.format("Ontology(%s) %s", ont.getOntologyID(), suffix) : String.format("Ontology(%s)", ont.getOntologyID());
        Assert.assertEquals(expected, s);
    }

    @Test
    public void testOntologyID() {
        IRI iriA = OWLFunctionalSyntaxFactory.IRI("http://www.another.com/", "ont");
        IRI iriB = OWLFunctionalSyntaxFactory.IRI("http://www.another.com/ont/", "version");
        OWLOntologyID ontIDBoth = new OWLOntologyID(Optional.of(iriA), Optional.of(iriB));
        OWLOntologyID ontIDBoth2 = new OWLOntologyID(Optional.of(iriA), Optional.of(iriB));
        Assert.assertEquals(ontIDBoth, ontIDBoth2);
        OWLOntologyID ontIDURIOnly = new OWLOntologyID(Optional.of(iriA), Optional.empty());
        Assert.assertFalse(ontIDBoth.equals(ontIDURIOnly));
        OWLOntologyID ontIDNoneA = new OWLOntologyID();
        OWLOntologyID ontIDNoneB = new OWLOntologyID();
        Assert.assertFalse(ontIDNoneA.equals(ontIDNoneB));
    }

    @Test
    public void testOntologyURI() throws OWLOntologyCreationException {
        IRI iri = OWLFunctionalSyntaxFactory.IRI("http://www.another.com/", "ont");
        OWLOntology ont = getOWLOntology(iri);
        Assert.assertEquals(iri, ont.getOntologyID().getOntologyIRI().orElseThrow(() -> new AssertionError("No IRI")));
        Assert.assertTrue(m.contains(iri));
        Assert.assertTrue(m.ontologies().anyMatch(ont::equals));
        OWLOntologyID ontID = new OWLOntologyID(Optional.of(iri), Optional.empty());
        Assert.assertEquals(ont.getOntologyID(), ontID);
    }

    @Test(expected = OWLOntologyAlreadyExistsException.class)
    public void testDuplicateOntologyURI() throws Throwable {
        IRI uri = IRI.getNextDocumentIRI("http://www.another.com/ont");
        getOWLOntology(uri);
        try {
            getOWLOntology(uri);
        } catch (ru.avicomp.ontapi.OntApiException e) {
            LOGGER.debug("Exception " + e);
            throw e.getCause();
        }
    }

    @Test
    public void testSetOntologyURI() throws OWLOntologyCreationException {
        IRI iri = IRI.getNextDocumentIRI("http://www.another.com/ont");
        OWLOntology ont = getOWLOntology(iri);
        IRI newIRI = IRI.getNextDocumentIRI("http://www.another.com/newont");
        SetOntologyID sou = new SetOntologyID(ont, new OWLOntologyID(Optional.of(newIRI), Optional.empty()));
        ont.applyChange(sou);
        Assert.assertFalse(m.contains(iri));
        Assert.assertTrue(m.contains(newIRI));
        Assert.assertEquals(newIRI, ont.getOntologyID().getOntologyIRI().orElseThrow(() -> new AssertionError("No IRI")));
    }

    @Test
    public void testVersionURI() throws OWLOntologyCreationException {
        IRI ontIRI = IRI.getNextDocumentIRI("http://www.another.com/ont");
        IRI verIRI = IRI.getNextDocumentIRI("http://www.another.com/ont/versions/1.0.0");
        OWLOntology ont = getOWLOntology(new OWLOntologyID(Optional.of(ontIRI), Optional.of(verIRI)));
        Assert.assertEquals(ontIRI, ont.getOntologyID().getOntologyIRI().orElseThrow(() -> new AssertionError("No IRI")));
        Assert.assertEquals(verIRI, ont.getOntologyID().getVersionIRI().orElseThrow(() -> new AssertionError("No ver IRI")));
    }

    @Test
    public void testNullVersionURI() throws OWLOntologyCreationException {
        IRI ontIRI = IRI.getNextDocumentIRI("http://www.another.com/ont");
        OWLOntology ont = getOWLOntology(new OWLOntologyID(Optional.of(ontIRI), Optional.empty()));
        Assert.assertEquals(ontIRI, ont.getOntologyID().getOntologyIRI().orElseThrow(() -> new AssertionError("No IRI")));
        Assert.assertFalse(ont.getOntologyID().getVersionIRI().isPresent());
    }
}
