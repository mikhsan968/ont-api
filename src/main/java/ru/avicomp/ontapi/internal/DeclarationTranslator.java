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

package ru.avicomp.ontapi.internal;

import java.util.Objects;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import ru.avicomp.ontapi.jena.impl.Entities;
import ru.avicomp.ontapi.jena.model.OntEntity;
import ru.avicomp.ontapi.jena.model.OntGraphModel;
import ru.avicomp.ontapi.jena.model.OntObject;
import ru.avicomp.ontapi.jena.model.OntStatement;
import ru.avicomp.ontapi.jena.vocabulary.RDF;

/**
 * Declaration of OWLEntity.
 * Simple triplet with rdf:type predicate.
 * <p>
 * Created by @szuev on 28.09.2016.
 */
public class DeclarationTranslator extends AxiomTranslator<OWLDeclarationAxiom> {
    @Override
    public void write(OWLDeclarationAxiom axiom, OntGraphModel model) {
        WriteHelper.writeDeclarationTriple(model, axiom.getEntity(), RDF.type, WriteHelper.getType(axiom.getEntity()), axiom.annotations());
    }

    @Override
    public Stream<OntStatement> statements(OntGraphModel model) {
        if (!getConfig(model).loaderConfig().isAllowReadDeclarations()) return Stream.empty();
        return model.ontEntities().map(OntObject::getRoot).filter(Objects::nonNull).filter(OntStatement::isLocal);
    }

    @Override
    public boolean testStatement(OntStatement statement) {
        return statement.getPredicate().equals(RDF.type)
                && statement.getSubject().isURIResource()
                && Stream.of(Entities.values()).map(Entities::type).anyMatch(t -> statement.getObject().equals(t));
    }

    @Override
    public InternalObject<OWLDeclarationAxiom> asAxiom(OntStatement statement) {
        ConfigProvider.Config conf = getConfig(statement);
        InternalObject<? extends OWLEntity> entity = ReadHelper.wrapEntity(statement.getSubject().as(OntEntity.class), conf.dataFactory());
        InternalObject.Collection<OWLAnnotation> annotations = ReadHelper.getStatementAnnotations(statement, conf.dataFactory(), conf.loaderConfig());
        OWLDeclarationAxiom res = conf.dataFactory().getOWLDeclarationAxiom(entity.getObject(), annotations.getObjects());
        return InternalObject.create(res, statement).add(annotations.getTriples());
    }
}
