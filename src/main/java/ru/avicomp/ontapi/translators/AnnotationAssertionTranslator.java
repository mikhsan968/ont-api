package ru.avicomp.ontapi.translators;

import org.apache.jena.graph.Graph;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

/**
 * Examples:
 * foaf:LabelProperty vs:term_status "unstable" .
 * foaf:LabelProperty rdfs:isDefinedBy <http://xmlns.com/foaf/0.1/> .
 * pizza:UnclosedPizza rdfs:label "PizzaAberta"@pt .
 * <p>
 * Created by @szuev on 28.09.2016.
 */
class AnnotationAssertionTranslator extends AxiomTranslator<OWLAnnotationAssertionAxiom> {
    @Override
    public void write(OWLAnnotationAssertionAxiom axiom, Graph graph) {
        TranslationHelper.processAnnotatedTriple(graph, axiom.getSubject(), axiom.getProperty(), axiom.getValue(), axiom);
    }
}
