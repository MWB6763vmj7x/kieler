/*
 * generated by Xtext 2.10.0
 */
package de.cau.cs.kieler.kgraph.text.serializer;

import com.google.inject.Inject;
import de.cau.cs.kieler.kgraph.text.grandom.Configuration;
import de.cau.cs.kieler.kgraph.text.grandom.DoubleQuantity;
import de.cau.cs.kieler.kgraph.text.grandom.Edges;
import de.cau.cs.kieler.kgraph.text.grandom.Flow;
import de.cau.cs.kieler.kgraph.text.grandom.GrandomPackage;
import de.cau.cs.kieler.kgraph.text.grandom.Hierarchy;
import de.cau.cs.kieler.kgraph.text.grandom.Nodes;
import de.cau.cs.kieler.kgraph.text.grandom.Ports;
import de.cau.cs.kieler.kgraph.text.grandom.RandGraph;
import de.cau.cs.kieler.kgraph.text.grandom.Size;
import de.cau.cs.kieler.kgraph.text.services.GRandomGrammarAccess;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("all")
public class GRandomSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private GRandomGrammarAccess grammarAccess;
	
	@Override
	public void sequence(ISerializationContext context, EObject semanticObject) {
		EPackage epackage = semanticObject.eClass().getEPackage();
		ParserRule rule = context.getParserRule();
		Action action = context.getAssignedAction();
		Set<Parameter> parameters = context.getEnabledBooleanParameters();
		if (epackage == GrandomPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case GrandomPackage.CONFIGURATION:
				sequence_Configuration(context, (Configuration) semanticObject); 
				return; 
			case GrandomPackage.DOUBLE_QUANTITY:
				sequence_DoubleQuantity(context, (DoubleQuantity) semanticObject); 
				return; 
			case GrandomPackage.EDGES:
				sequence_Edges(context, (Edges) semanticObject); 
				return; 
			case GrandomPackage.FLOW:
				sequence_Flow(context, (Flow) semanticObject); 
				return; 
			case GrandomPackage.HIERARCHY:
				sequence_Hierarchy(context, (Hierarchy) semanticObject); 
				return; 
			case GrandomPackage.NODES:
				sequence_Nodes(context, (Nodes) semanticObject); 
				return; 
			case GrandomPackage.PORTS:
				sequence_Ports(context, (Ports) semanticObject); 
				return; 
			case GrandomPackage.RAND_GRAPH:
				sequence_RandGraph(context, (RandGraph) semanticObject); 
				return; 
			case GrandomPackage.SIZE:
				sequence_Size(context, (Size) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     Configuration returns Configuration
	 *
	 * Constraint:
	 *     (
	 *         samples=INT 
	 *         form=Form 
	 *         (
	 *             (
	 *                 nodes=Nodes | 
	 *                 edges=Edges | 
	 *                 hierarchy=Hierarchy | 
	 *                 seed=Integer | 
	 *                 format=Formats | 
	 *                 filename=STRING
	 *             )? 
	 *             (mW?='maxWidth' maxWidth=Integer)? 
	 *             (mD?='maxDegree' maxDegree=Integer)? 
	 *             (pF?='partitionFraction' fraction=DoubleQuantity)?
	 *         )+
	 *     )
	 */
	protected void sequence_Configuration(ISerializationContext context, Configuration semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     DoubleQuantity returns DoubleQuantity
	 *
	 * Constraint:
	 *     (quant=Double | (min=Double minMax?='to' max=Double) | (mean=Double gaussian?=Pm stddv=Double))
	 */
	protected void sequence_DoubleQuantity(ISerializationContext context, DoubleQuantity semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Edges returns Edges
	 *
	 * Constraint:
	 *     (
	 *         (density?='density' | total?='total' | relative?='relative' | outbound?='outgoing') 
	 *         nEdges=DoubleQuantity 
	 *         (labels?='labels' | selfLoops?='self loops')*
	 *     )
	 */
	protected void sequence_Edges(ISerializationContext context, Edges semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Flow returns Flow
	 *
	 * Constraint:
	 *     (flowType=FlowType side=Side amount=DoubleQuantity)
	 */
	protected void sequence_Flow(ISerializationContext context, Flow semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, GrandomPackage.Literals.FLOW__FLOW_TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GrandomPackage.Literals.FLOW__FLOW_TYPE));
			if (transientValues.isValueTransient(semanticObject, GrandomPackage.Literals.FLOW__SIDE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GrandomPackage.Literals.FLOW__SIDE));
			if (transientValues.isValueTransient(semanticObject, GrandomPackage.Literals.FLOW__AMOUNT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GrandomPackage.Literals.FLOW__AMOUNT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getFlowAccess().getFlowTypeFlowTypeEnumRuleCall_0_0(), semanticObject.getFlowType());
		feeder.accept(grammarAccess.getFlowAccess().getSideSideEnumRuleCall_1_0(), semanticObject.getSide());
		feeder.accept(grammarAccess.getFlowAccess().getAmountDoubleQuantityParserRuleCall_3_0(), semanticObject.getAmount());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Hierarchy returns Hierarchy
	 *
	 * Constraint:
	 *     (levels=DoubleQuantity | edges=DoubleQuantity | numHierarchNodes=DoubleQuantity | crossHierarchRel=DoubleQuantity)*
	 */
	protected void sequence_Hierarchy(ISerializationContext context, Hierarchy semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Nodes returns Nodes
	 *
	 * Constraint:
	 *     (nNodes=DoubleQuantity (ports=Ports | labels?=Labels | size=Size | removeIsolated?='remove isolated')*)
	 */
	protected void sequence_Nodes(ISerializationContext context, Nodes semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Ports returns Ports
	 *
	 * Constraint:
	 *     (labels?=Labels | reUse=DoubleQuantity | size=Size | constraint=ConstraintType | flow+=Flow)*
	 */
	protected void sequence_Ports(ISerializationContext context, Ports semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     RandGraph returns RandGraph
	 *
	 * Constraint:
	 *     configs+=Configuration+
	 */
	protected void sequence_RandGraph(ISerializationContext context, RandGraph semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Size returns Size
	 *
	 * Constraint:
	 *     (height=DoubleQuantity | width=DoubleQuantity)*
	 */
	protected void sequence_Size(ISerializationContext context, Size semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
}
