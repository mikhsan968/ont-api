package ru.avicomp.ontapi.translators;

import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import ru.avicomp.ontapi.jena.model.OntOPE;
import ru.avicomp.ontapi.jena.model.OntStatement;
import ru.avicomp.ontapi.jena.vocabulary.OWL;
import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;

/**
 * Example:
 * gr:equal rdf:type owl:TransitiveProperty ;
 * <p>
 * Created by @szuev on 28.09.2016.
 */
class TransitiveObjectPropertyTranslator extends AbstractPropertyTypeTranslator<OWLTransitiveObjectPropertyAxiom, OntOPE> {
    @Override
    OWLTransitiveObjectPropertyAxiom create(OntStatement statement, Set<OWLAnnotation> annotations) {
        return new OWLTransitiveObjectPropertyAxiomImpl(ReadHelper.getObjectProperty(getSubject(statement)), annotations);
    }

    @Override
    Resource getType() {
        return OWL.TransitiveProperty;
    }

    @Override
    Class<OntOPE> getView() {
        return OntOPE.class;
    }
}
