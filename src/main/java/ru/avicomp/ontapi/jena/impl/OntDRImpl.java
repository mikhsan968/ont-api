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

package ru.avicomp.ontapi.jena.impl;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

import ru.avicomp.ontapi.jena.OntJenaException;
import ru.avicomp.ontapi.jena.impl.configuration.*;
import ru.avicomp.ontapi.jena.model.OntDR;
import ru.avicomp.ontapi.jena.model.OntDT;
import ru.avicomp.ontapi.jena.model.OntFR;
import ru.avicomp.ontapi.jena.model.OntStatement;
import ru.avicomp.ontapi.jena.vocabulary.OWL;
import ru.avicomp.ontapi.jena.vocabulary.RDF;

/**
 * Implementation for Data Range Expressions.
 * <p>
 * Created by @szuev on 16.11.2016.
 */
public class OntDRImpl extends OntObjectImpl implements OntDR {
    private static final OntFinder DR_FINDER = new OntFinder.ByType(RDFS.Datatype);
    private static final OntFilter DR_FILTER = OntFilter.BLANK.and(new OntFilter.HasType(RDFS.Datatype));

    public static Configurable<OntObjectFactory> oneOfDRFactory = m ->
            new CommonOntObjectFactory(new OntMaker.Default(OneOfImpl.class), DR_FINDER, DR_FILTER.and(new OntFilter.HasPredicate(OWL.oneOf)));
    public static Configurable<OntObjectFactory> restrictionDRFactory = m ->
            new CommonOntObjectFactory(new OntMaker.Default(RestrictionImpl.class), DR_FINDER, DR_FILTER.and(new OntFilter.HasPredicate(OWL.onDatatype)).and(new OntFilter.HasPredicate(OWL.withRestrictions)));
    public static Configurable<OntObjectFactory> complementOfDRFactory = m ->
            new CommonOntObjectFactory(new OntMaker.Default(ComplementOfImpl.class), DR_FINDER, DR_FILTER.and(new OntFilter.HasPredicate(OWL.datatypeComplementOf)));
    public static Configurable<OntObjectFactory> unionOfDRFactory = m ->
            new CommonOntObjectFactory(new OntMaker.Default(UnionOfImpl.class), DR_FINDER, DR_FILTER.and(new OntFilter.HasPredicate(OWL.unionOf)));
    public static Configurable<OntObjectFactory> intersectionOfDRFactory = m ->
            new CommonOntObjectFactory(new OntMaker.Default(IntersectionOfImpl.class), DR_FINDER, DR_FILTER.and(new OntFilter.HasPredicate(OWL.intersectionOf)));

    public static Configurable<MultiOntObjectFactory> abstractAnonDRFactory = createMultiFactory(DR_FINDER,
            oneOfDRFactory, restrictionDRFactory, complementOfDRFactory, unionOfDRFactory, intersectionOfDRFactory);

    public static Configurable<MultiOntObjectFactory> abstractDRFactory = createMultiFactory(DR_FINDER, Entities.DATATYPE, abstractAnonDRFactory);

    public OntDRImpl(Node n, EnhGraph m) {
        super(n, m);
    }

    private static Resource create(OntGraphModelImpl model) {
        Resource res = model.createResource();
        model.add(res, RDF.type, RDFS.Datatype);
        return res;
    }

    public static OneOf createOneOf(OntGraphModelImpl model, Stream<Literal> values) {
        OntJenaException.notNull(values, "Null values stream.");
        Resource res = create(model);
        model.add(res, OWL.oneOf, model.createList(values.iterator()));
        return model.getNodeAs(res.asNode(), OneOf.class);
    }

    public static Restriction createRestriction(OntGraphModelImpl model, OntDR property, Stream<OntFR> values) {
        OntJenaException.notNull(property, "Null property.");
        OntJenaException.notNull(values, "Null values stream.");
        Resource res = create(model);
        model.add(res, OWL.onDatatype, property);
        model.add(res, OWL.withRestrictions, model.createList(values.iterator()));
        return model.getNodeAs(res.asNode(), Restriction.class);
    }

    public static ComplementOf createComplementOf(OntGraphModelImpl model, OntDR other) {
        OntJenaException.notNull(other, "Null data range.");
        Resource res = create(model);
        model.add(res, OWL.datatypeComplementOf, other);
        return model.getNodeAs(res.asNode(), ComplementOf.class);
    }

    public static UnionOf createUnionOf(OntGraphModelImpl model, Stream<OntDR> values) {
        OntJenaException.notNull(values, "Null values stream.");
        Resource res = create(model);
        model.add(res, OWL.unionOf, model.createList(values.iterator()));
        return model.getNodeAs(res.asNode(), UnionOf.class);
    }

    public static IntersectionOf createIntersectionOf(OntGraphModelImpl model, Stream<OntDR> values) {
        OntJenaException.notNull(values, "Null values stream.");
        Resource res = create(model);
        model.add(res, OWL.intersectionOf, model.createList(values.iterator()));
        return model.getNodeAs(res.asNode(), IntersectionOf.class);
    }

    Stream<OntStatement> listStatements(Property predicate) {
        return Stream.of(statements(predicate), rdfListContent(predicate)).flatMap(Function.identity());
    }

    public static class OneOfImpl extends OntDRImpl implements OneOf {
        public OneOfImpl(Node n, EnhGraph m) {
            super(n, m);
        }

        @Override
        public Stream<Literal> values() {
            return rdfListMembers(OWL.oneOf, Literal.class);
        }

        @Override
        public Stream<OntStatement> content() {
            return Stream.concat(super.content(), listStatements(OWL.oneOf));
        }
    }

    public static class RestrictionImpl extends OntDRImpl implements Restriction {
        public RestrictionImpl(Node n, EnhGraph m) {
            super(n, m);
        }

        @Override
        public Class<Restriction> getActualClass() {
            return Restriction.class;
        }

        @Override
        public OntDT getDatatype() {
            return getRequiredObject(OWL.onDatatype, OntDT.class);
        }

        @Override
        public Stream<OntFR> facetRestrictions() {
            return rdfListMembers(OWL.withRestrictions, OntFR.class);
        }

        @Override
        public Stream<OntStatement> content() {
            return Stream.of(super.content(),
                    statement(OWL.onDatatype).map(Stream::of).orElse(Stream.empty()),
                    listStatements(OWL.withRestrictions)).flatMap(Function.identity());
        }
    }

    public static class ComplementOfImpl extends OntDRImpl implements ComplementOf {
        public ComplementOfImpl(Node n, EnhGraph m) {
            super(n, m);
        }

        @Override
        public OntDR getDataRange() {
            return getRequiredObject(OWL.datatypeComplementOf, OntDR.class);
        }

        @Override
        public Stream<OntStatement> content() {
            return Stream.concat(super.content(), statement(OWL.datatypeComplementOf).map(Stream::of).orElse(Stream.empty()));
        }
    }

    public static class UnionOfImpl extends OntDRImpl implements UnionOf {
        public UnionOfImpl(Node n, EnhGraph m) {
            super(n, m);
        }

        @Override
        public Stream<OntDR> dataRanges() {
            return rdfListMembers(OWL.unionOf, OntDR.class);
        }

        @Override
        public Stream<OntStatement> content() {
            return Stream.concat(super.content(), listStatements(OWL.unionOf));
        }
    }

    public static class IntersectionOfImpl extends OntDRImpl implements IntersectionOf {
        public IntersectionOfImpl(Node n, EnhGraph m) {
            super(n, m);
        }

        @Override
        public Stream<OntDR> dataRanges() {
            return rdfListMembers(OWL.intersectionOf, OntDR.class);
        }

        @Override
        public Stream<OntStatement> content() {
            return Stream.concat(super.content(), listStatements(OWL.intersectionOf));
        }

    }
}
