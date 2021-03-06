package org.semanticweb.owlapi.api.test;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.StructuralTransformation;

import static org.junit.Assert.assertEquals;

/**
 * Copy-paste from <a href='https://github.com/owlcs/owlapi'>OWL-API, ver. 5.1.1</a>
 */
@SuppressWarnings({"javadoc"})
@RunWith(Parameterized.class)
public class StructuralTransformationTestCase {

    private final OWLAxiom object;
    private final String expected;

    public StructuralTransformationTestCase(OWLAxiom object, String expected) {
        this.object = object;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> getData() {
        Builder b = new Builder();
        Map<OWLAxiom, String> map = new LinkedHashMap<>();
        map.put(b.dRange(),
                "[SubClassOf(owl:Thing DataAllValuesFrom(<urn:test#dp> <urn:test#datatype>))]");
        map.put(b.dDef(),
                "[DatatypeDefinition(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#datatype> xsd:double)]");
        map.put(b.decC(),
                "[Declaration(Annotation(<urn:test#ann> \"test\"^^xsd:string) Class(<urn:test#c>))]");
        map.put(b.decOp(),
                "[Declaration(Annotation(<urn:test#ann> \"test\"^^xsd:string) ObjectProperty(<urn:test#op>))]");
        map.put(b.decDp(),
                "[Declaration(Annotation(<urn:test#ann> \"test\"^^xsd:string) DataProperty(<urn:test#dp>))]");
        map.put(b.decDt(),
                "[Declaration(Annotation(<urn:test#ann> \"test\"^^xsd:string) Datatype(<urn:test#datatype>))]");
        map.put(b.decAp(),
                "[Declaration(Annotation(<urn:test#ann> \"test\"^^xsd:string) AnnotationProperty(<urn:test#ann>))]");
        map.put(b.decI(),
                "[Declaration(Annotation(<urn:test#ann> \"test\"^^xsd:string) NamedIndividual(<urn:test#i>))]");
        map.put(b.dDp(), "[DisjointDataProperties(<urn:test#dp> <urn:test#iri> )]");
        map.put(b.dOp(), "[DisjointObjectProperties(<urn:test#iri> <urn:test#op> )]");
        map.put(b.eDp(), "[EquivalentDataProperties(<urn:test#dp> <urn:test#iri> )]");
        map.put(b.eOp(), "[EquivalentObjectProperties(<urn:test#iri> <urn:test#op> )]");
        map.put(b.fdp(),
                "[SubClassOf(owl:Thing DataMaxCardinality(1 <urn:test#dp> rdfs:Literal))]");
        map.put(b.fop(), "[SubClassOf(owl:Thing ObjectMaxCardinality(1 <urn:test#op> owl:Thing))]");
        map.put(b.ifp(),
                "[SubClassOf(owl:Thing ObjectMaxCardinality(1 ObjectInverseOf(<urn:test#op>) owl:Thing))]");
        map.put(b.iop(), "[SubObjectPropertyOf(<urn:test#op> ObjectInverseOf(<urn:test#op>))]");
        map.put(b.irr(),
                "[IrreflexiveObjectProperty(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op>)]");
        map.put(b.opa(),
                "[ObjectPropertyAssertion(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op> <urn:test#i> <urn:test#i>)]");
        map.put(b.opaInv(),
                "[ObjectPropertyAssertion(Annotation(<urn:test#ann> \"test\"^^xsd:string) ObjectInverseOf(<urn:test#op>) <urn:test#i> <urn:test#i>)]");
        map.put(b.opaInvj(),
                "[ObjectPropertyAssertion(Annotation(<urn:test#ann> \"test\"^^xsd:string) ObjectInverseOf(<urn:test#op>) <urn:test#i> <urn:test#j>)]");
        map.put(b.oDom(),
                "[SubClassOf(<http://www.semanticweb.org/ontology#X0> <urn:test#c>), SubClassOf(<http://www.semanticweb.org/ontology#X1> ObjectAllValuesFrom(<urn:test#op> owl:Nothing)), SubClassOf(owl:Thing ObjectUnionOf(<http://www.semanticweb.org/ontology#X0> <http://www.semanticweb.org/ontology#X1>))]");
        map.put(b.oRange(),
                "[SubClassOf(<http://www.semanticweb.org/ontology#X0> <urn:test#c>), SubClassOf(owl:Thing ObjectAllValuesFrom(<urn:test#op> <http://www.semanticweb.org/ontology#X0>))]");
        map.put(b.chain(),
                "[SubObjectPropertyOf(Annotation(<urn:test#ann> \"test\"^^xsd:string) ObjectPropertyChain( <urn:test#iri> <urn:test#op> ) <urn:test#op>)]");
        map.put(b.ref(),
                "[ReflexiveObjectProperty(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op>)]");
        map.put(b.same(), "[]");
        map.put(b.subAnn(),
                "[SubAnnotationPropertyOf(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#ann> rdfs:label)]");
        map.put(b.subClass(),
                "[SubClassOf(<http://www.semanticweb.org/ontology#X0> owl:Thing), SubClassOf(<http://www.semanticweb.org/ontology#X1> ObjectComplementOf(<urn:test#c>)), SubClassOf(owl:Thing ObjectUnionOf(<http://www.semanticweb.org/ontology#X0> <http://www.semanticweb.org/ontology#X1>))]");
        map.put(b.subData(), "[SubDataPropertyOf(<urn:test#dp> owl:topDataProperty)]");
        map.put(b.subObject(),
                "[SubObjectPropertyOf(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op> owl:topObjectProperty)]");
        map.put(b.rule(),
                "[DLSafeRule( Body(BuiltInAtom(<urn:swrl#v1> Variable(<urn:swrl#var3>) Variable(<urn:swrl#var4>) )) Head(BuiltInAtom(<urn:swrl#v2> Variable(<urn:swrl#var5>) Variable(<urn:swrl#var6>) )) )]");
        map.put(b.symm(),
                "[SymmetricObjectProperty(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op>)]");
        map.put(b.trans(),
                "[TransitiveObjectProperty(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op>)]");
        map.put(b.hasKey(),
                "[HasKey(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#c> (<urn:test#iri> <urn:test#op> ) (<urn:test#dp> ))]");
        map.put(b.bigRule(),
                "[DLSafeRule(Annotation(<urn:test#ann> \"test\"^^xsd:string)  Body(BuiltInAtom(<urn:swrl#v1> Variable(<urn:swrl#var3>) Variable(<urn:swrl#var4>) ) ClassAtom(<urn:test#c> Variable(<urn:swrl#var2>)) DataRangeAtom(<urn:test#datatype> Variable(<urn:swrl#var1>)) BuiltInAtom(<urn:test#iri> Variable(<urn:swrl#var1>) ) DifferentFromAtom(Variable(<urn:swrl#var2>) <urn:test#i>) SameAsAtom(Variable(<urn:swrl#var2>) <urn:test#iri>)) Head(BuiltInAtom(<urn:swrl#v2> Variable(<urn:swrl#var5>) Variable(<urn:swrl#var6>) ) DataPropertyAtom(<urn:test#dp> Variable(<urn:swrl#var2>) \"false\"^^xsd:boolean) ObjectPropertyAtom(<urn:test#op> Variable(<urn:swrl#var2>) Variable(<urn:swrl#var2>))) )]");
        map.put(b.ann(),
                "[AnnotationAssertion(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#ann> <urn:test#iri> \"false\"^^xsd:boolean)]");
        map.put(b.asymm(),
                "[AsymmetricObjectProperty(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#op>)]");
        map.put(b.annDom(), "[AnnotationPropertyDomain(<urn:test#ann> <urn:test#iri>)]");
        map.put(b.annRange(), "[AnnotationPropertyRange(<urn:test#ann> <urn:test#iri>)]");
        map.put(b.dRangeAnd(),
                "[SubClassOf(owl:Thing DataAllValuesFrom(<urn:test#dp> DataIntersectionOf(<urn:test#datatype> DataOneOf(\"false\"^^xsd:boolean ) )))]");
        map.put(b.dRangeOr(),
                "[SubClassOf(owl:Thing DataAllValuesFrom(<urn:test#dp> DataUnionOf(<urn:test#datatype> DataOneOf(\"false\"^^xsd:boolean ) )))]");
        map.put(b.dOneOf(),
                "[SubClassOf(owl:Thing DataAllValuesFrom(<urn:test#dp> DataOneOf(\"false\"^^xsd:boolean )))]");
        map.put(b.dNot(),
                "[SubClassOf(owl:Thing DataAllValuesFrom(<urn:test#dp> DataComplementOf(DataOneOf(\"false\"^^xsd:boolean ))))]");
        map.put(b.dRangeRestrict(),
                "[SubClassOf(owl:Thing DataAllValuesFrom(<urn:test#dp> DataRangeRestriction(xsd:double facetRestriction(minExclusive \"5.0\"^^xsd:double) facetRestriction(maxExclusive \"6.0\"^^xsd:double))))]");
        map.put(b.assD(),
                "[DataPropertyAssertion(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#dp> <urn:test#i> \"false\"^^xsd:boolean)]");
        map.put(b.assDPlain(),
                "[DataPropertyAssertion(Annotation(<urn:test#ann> \"test\"^^xsd:string) <urn:test#dp> <urn:test#i> \"string\"@en)]");
        map.put(b.dDom(),
                "[SubClassOf(<http://www.semanticweb.org/ontology#X0> <urn:test#c>), SubClassOf(<http://www.semanticweb.org/ontology#X1> DataAllValuesFrom(<urn:test#dp> DataComplementOf(rdfs:Literal))), SubClassOf(owl:Thing ObjectUnionOf(<http://www.semanticweb.org/ontology#X0> <http://www.semanticweb.org/ontology#X1>))]");
        map.put(b.dc(),
                "[SubClassOf(owl:Thing ObjectComplementOf(<urn:test#iri>)), SubClassOf(owl:Thing ObjectComplementOf(<urn:test#c>))]");
        map.put(b.du(),
                "[SubClassOf(<http://www.semanticweb.org/ontology#X0> <urn:test#c>), SubClassOf(owl:Thing ObjectComplementOf(<urn:test#iri>)), SubClassOf(<http://www.semanticweb.org/ontology#X1> <urn:test#iri>), SubClassOf(owl:Thing <urn:test#c>), SubClassOf(owl:Thing ObjectComplementOf(<urn:test#c>)), SubClassOf(owl:Thing ObjectUnionOf(<http://www.semanticweb.org/ontology#X0> <http://www.semanticweb.org/ontology#X1>))]");
        map.put(b.ec(),
                "[SubClassOf(owl:Thing <urn:test#c>), SubClassOf(owl:Thing <urn:test#iri>)]");
        Collection<Object[]> toReturn = new ArrayList<>();
        map.forEach((k, v) -> toReturn.add(new Object[]{k, v}));
        return toReturn;
    }

    @Test
    public void testAssertion() {
        StructuralTransformation testsubject =
                new StructuralTransformation(OWLManager.getOWLDataFactory());
        Set<OWLAxiom> singleton = Collections.singleton(object);
        String result = testsubject.getTransformedAxioms(singleton).toString();
        assertEquals(expected, result);
    }
}