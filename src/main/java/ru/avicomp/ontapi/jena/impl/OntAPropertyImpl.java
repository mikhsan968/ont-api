package ru.avicomp.ontapi.jena.impl;

import java.util.stream.Stream;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;

import ru.avicomp.ontapi.jena.JenaUtils;
import ru.avicomp.ontapi.jena.model.OntNAP;

/**
 * owl:AnnotationProperty
 * <p>
 * Created by szuev on 03.11.2016.
 */
public class OntAPropertyImpl extends OntEntityImpl implements OntNAP {

    public OntAPropertyImpl(Node n, EnhGraph g) {
        super(OntEntityImpl.checkNamed(n), g);
    }

    @Override
    public Class<OntNAP> getActualClass() {
        return OntNAP.class;
    }

    @Override
    public Resource getRDFType() {
        return OWL2.AnnotationProperty;
    }

    @Override
    public Stream<Resource> domain() {
        return JenaUtils.asStream(getModel().listObjectsOfProperty(this, RDFS.domain).mapWith(RDFNode::asResource));
    }

    @Override
    public Stream<Resource> range() {
        return JenaUtils.asStream(getModel().listObjectsOfProperty(this, RDFS.range).mapWith(RDFNode::asResource));
    }

    @Override
    public boolean isBuiltIn() {
        return BUILT_IN_ANNOTATION_PROPERTIES.contains(this);
    }
}
