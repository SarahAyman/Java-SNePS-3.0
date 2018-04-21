/**
 * @className CaseFrame.java
 * 
 * @ClassDescription Case Frames are the highlight of the semantics in SNePS as they define sets
 * 	of relations to be used together to give a precise meaning and define the semantic class for 
 *  the nodes based on the case frame implemented by the nodes. In the current implementation
 *  (version) of Java SNePS, the case frame is implemented as a 5-tuple (semanticClass, relations, 
 *  aignatureIDs, signatures and id).  
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

import sneps.exceptions.CustomException;
import sneps.network.Network;

public class RelationsRestrictedCaseFrame extends CaseFrame {
	public static CaseFrame andRule, orRule, andOrRule, threshRule, numericalRule, act, act1, act2, act3, act4, act5,
			act6, act7, act8, act9, preconditionAct, whenDo, wheneverDo, doIf, actEffect, planAct, planGoal, withSome;

	// /**
	// * The name of the semantic class that represents the default semantic type
	// * of any node with a down-cable set implementing this case frame.
	// */
	// private String semanticClass;

	/**
	 * The relations included in the current case frame along with their
	 * adjustability and limit constraints within this case frame.
	 */
	private Hashtable<String, RCFP> relationsWithConstraints;

	/**
	 * A list of strings representing the IDs of the case frame signatures included
	 * in the current case frame. The main aim of this is to enforce priority while
	 * checking the case frame signatures during the process of building a node.
	 */
	private LinkedList<String> signatureIDs;

	/**
	 * The case frame signatures included in the current case frame. A case frame
	 * can have more than one signature and thus the signatures are prioritized.
	 */
	private Hashtable<String, CFSignature> signatures;

	/**
	 * A string id that is automatically generated by concatenating all names of the
	 * relations included in the case frame separated by commas after sorting them
	 * according to their lexicographical order. Each case frame has a unique set of
	 * relations and that is why the string id composed of the relations included in
	 * the case frame will be unique for each case frame.
	 */
	// private String id;

	// TODO add optional name field (can be null)

	/**
	 * The first constructor of this class.
	 * 
	 * @param semantic
	 *            the name of the semantic class representing the semantic type
	 *            specified by the current case frame.
	 * @param r
	 *            a linked list of RCFP (Relation case frame properties) for all the
	 *            relations included in the current case frame.
	 */
	public RelationsRestrictedCaseFrame(String semantic, LinkedList<RCFP> r) {
		super(semantic, getRelationsFromRCFP(r));
		super.setId(createId(r));
		this.relationsWithConstraints = generateRCFPHashtable(r);
		this.signatureIDs = new LinkedList<String>();
		this.signatures = new Hashtable<String, CFSignature>();

	}

	/**
	 * The second constructor of this class that discards some conceptual data to
	 * define the case frame and only constructs a case frame using a semantic type
	 * and a list of relations.
	 * 
	 * @param semantic
	 *            the name of the semantic class representing the semantic type
	 *            specified by the current case frame.
	 * 
	 * @
	 */

	// public CaseFrame(String semantic, LinkedList<Relation> relations) {
	// this.semanticClass = semantic;
	// this.relations = relations;
	//
	// }

	/**
	 * This method is invoked by the constructor to generate the hash table of
	 * relations (RCFP) from the linked list of relations (RCFP) that was passed to
	 * the constructor as a parameter. Each entry in the hash table generated has
	 * the relation name as the key and the RCFP of the corresponding relation as
	 * the value.
	 * 
	 * @param r
	 *            a linked list of RCFP representing the relations included in the
	 *            current case frame.
	 * 
	 * @return the hash table of the relations. (Each entry has the relation name as
	 *         the key and the RCFP of the corresponding relation as the value).
	 */
	private Hashtable<String, RCFP> generateRCFPHashtable(LinkedList<RCFP> r) {
		Hashtable<String, RCFP> relations = new Hashtable<String, RCFP>();
		for (int i = 0; i < r.size(); i++) {
			relations.put(r.get(i).getRelation().getName(), r.get(i));
		}
		return relations;
	}

	/**
	 * Adds a new signature to the current case frame.
	 * 
	 * @param sig
	 *            the case frame signature that will be added.
	 * @param priority
	 *            the priority of the new case frame signature.
	 * 
	 * @return true if the case frame signature was added successfully, and false
	 *         otherwise. (The same case frame signature (same id) cannot be added
	 *         twice).
	 */
	public boolean addSignature(CFSignature sig, Integer priority) {
		if (!this.signatures.containsKey(sig.getId())) {
			if (priority == null) {
				this.signatureIDs.add(sig.getId());
			} else {
				this.signatureIDs.add(priority.intValue(), sig.getId());
			}
			this.signatures.put(sig.getId(), sig);
			return true;
		}
		return false;
	}

	/**
	 * Removes a case frame signature by its ID.
	 * 
	 * @param id
	 *            the id of the case frame signature that will be removed.
	 * 
	 * @return true if the case frame signature was removed successfully, and false
	 *         otherwise.
	 */
	public boolean removeSignature(String id) {
		if (this.signatures.containsKey(id)) {
			this.signatures.remove(id);
			for (int i = 0; i < this.signatureIDs.size(); i++) {
				if (this.signatureIDs.get(i).equals(id)) {
					this.signatureIDs.remove(i);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes a case frame signature.
	 * 
	 * @param sig
	 *            the case frame signature that will be removed.
	 * 
	 * @return true if the case frame signature was removed successfully, and false
	 *         otherwise.
	 */
	public boolean removeSignature(CFSignature sig) {
		if (this.signatures.containsKey(sig.getId())) {
			this.signatures.remove(sig.getId());
			for (int i = 0; i < this.signatureIDs.size(); i++) {
				if (this.signatureIDs.get(i).equals(sig.getId())) {
					this.signatureIDs.remove(i);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @return the linked list that contains the IDs of the case frame signatures
	 *         ordered according to their priority.
	 */
	public LinkedList<String> getSignatureIDs() {
		return this.signatureIDs;
	}

	/**
	 * 
	 * @return the hash table that contains the case frame signatures.
	 */
	public Hashtable<String, CFSignature> getSignatures() {
		return this.signatures;
	}

	/**
	 * 
	 * @return the name of the default semantic class specified by the current case
	 *         frame.
	 */
	// public String getSemanticClass() {
	// return this.semanticClass;
	// }

	/**
	 * 
	 * @return the hash table that contains the relation names of all the relations
	 *         included in the case frame along with their corresponding RCFP.
	 */
	public Hashtable<String, RCFP> getrelationsWithConstraints() {
		return this.relationsWithConstraints;
	}

	/**
	 * 
	 * @return the ID of the case frame.
	 */
	// public String getId() {
	// return this.id;
	// }

	/**
	 * 
	 * @param relation
	 *            the relation that its corresponding RCFP is needed.
	 * 
	 * @return the RCFP of the given relation and null if the relation is not
	 *         included in the current case frame.
	 */
	public RCFP getRelationWithConstraints(Relation relation) {
		if (this.relationsWithConstraints.containsKey(relation.getName())) {
			return this.relationsWithConstraints.get(relation.getName());
		}
		return null;
	}

	public static LinkedList<Relation> getRelationsFromRCFP(LinkedList<RCFP> r) {

		LinkedList<Relation> rels = new LinkedList<Relation>();
		for (int i = 0; i < r.size(); i++) {
			rels.add(r.get(i).getRelation());
		}
		return rels;
	}

	private String createId(LinkedList<RCFP> r) {
		String id = "";
		LinkedList<String> relationNames = new LinkedList<String>();
		for (int i = 0; i < r.size(); i++) {
			relationNames.add(r.get(i).getRelation().getName());
		}
		Collections.sort(relationNames);
		for (int i = 0; i < relationNames.size(); i++) {
			if (i == 0) {
				id = id.concat(relationNames.get(i));
			} else {
				id = id.concat(",").concat(relationNames.get(i));
			}
		}
		return id;
	}

	public static void createDefaultCaseFrames() throws CustomException {
		if (RCFP.andAnt == null)
			RCFP.createDefaultProperties();
		LinkedList<RCFP> and = new LinkedList<RCFP>();
		and.add(RCFP.andAnt);
		and.add(RCFP.cq);
		andRule = Network.defineCaseFrameWithConstraints("Proposition", and);

		LinkedList<RCFP> or = new LinkedList<RCFP>();
		or.add(RCFP.ant);
		or.add(RCFP.cq);
		orRule = Network.defineCaseFrameWithConstraints("Proposition", or);

		LinkedList<RCFP> andOr = new LinkedList<RCFP>();
		andOr.add(RCFP.arg);
		andOr.add(RCFP.max);
		andOr.add(RCFP.min);
		andOrRule = Network.defineCaseFrameWithConstraints("Proposition", andOr);

		LinkedList<RCFP> thresh = new LinkedList<RCFP>();
		thresh.add(RCFP.arg);
		thresh.add(RCFP.threshMax);
		thresh.add(RCFP.thresh);
		threshRule = Network.defineCaseFrameWithConstraints("Proposition", thresh);

		LinkedList<RCFP> numerical = new LinkedList<RCFP>();
		numerical.add(RCFP.andAnt);
		numerical.add(RCFP.cq);
		numerical.add(RCFP.i);
		numericalRule = Network.defineCaseFrameWithConstraints("Proposition", numerical);

		LinkedList<RCFP> actCF = new LinkedList<RCFP>();
		actCF.add(RCFP.action);
		actCF.add(RCFP.obj);
		act = Network.defineCaseFrameWithConstraints("Act", actCF);

		LinkedList<RCFP> preAct = new LinkedList<RCFP>();
		preAct.add(RCFP.precondition);
		preAct.add(RCFP.act);
		preconditionAct = Network.defineCaseFrameWithConstraints("Proposition", preAct);

		LinkedList<RCFP> whendo = new LinkedList<RCFP>();
		whendo.add(RCFP.when);
		whendo.add(RCFP.doo);
		whenDo = Network.defineCaseFrameWithConstraints("Proposition", whendo);

		LinkedList<RCFP> wheneverdo = new LinkedList<RCFP>();
		wheneverdo.add(RCFP.whenever);
		wheneverdo.add(RCFP.doo);
		wheneverDo = Network.defineCaseFrameWithConstraints("Proposition", wheneverdo);

		LinkedList<RCFP> doif = new LinkedList<RCFP>();
		doif.add(RCFP.doo);
		doif.add(RCFP.iff);
		doIf = Network.defineCaseFrameWithConstraints("Prorposition", doif);

		LinkedList<RCFP> acteffect = new LinkedList<RCFP>();
		acteffect.add(RCFP.act);
		acteffect.add(RCFP.effect);
		actEffect = Network.defineCaseFrameWithConstraints("Proposition", acteffect);

		LinkedList<RCFP> planact = new LinkedList<RCFP>();
		planact.add(RCFP.plan);
		planact.add(RCFP.act);
		planAct = Network.defineCaseFrameWithConstraints("Proposition", planact);

		LinkedList<RCFP> plangoal = new LinkedList<RCFP>();
		plangoal.add(RCFP.plan);
		plangoal.add(RCFP.goal);
		planGoal = Network.defineCaseFrameWithConstraints("Proposition", plangoal);

		LinkedList<RCFP> withsome = new LinkedList<RCFP>();
		withsome.add(RCFP.withsome);
		withsome.add(RCFP.vars);
		withsome.add(RCFP.suchthat);
		withsome.add(RCFP.doo);
		withsome.add(RCFP.elsee);
		withSome = Network.defineCaseFrameWithConstraints("ControlAction", withsome);

		LinkedList<RCFP> actCF1 = new LinkedList<RCFP>();
		actCF1.add(RCFP.action);
		actCF1.add(RCFP.obj1);
		actCF1.add(RCFP.obj2);
		act1 = Network.defineCaseFrameWithConstraints("Act", actCF1);

		LinkedList<RCFP> actCF2 = new LinkedList<RCFP>();
		actCF2.add(RCFP.action);
		actCF2.add(RCFP.obj1);
		actCF2.add(RCFP.obj2);
		actCF2.add(RCFP.obj3);
		act2 = Network.defineCaseFrameWithConstraints("Act", actCF2);

		LinkedList<RCFP> actCF3 = new LinkedList<RCFP>();
		actCF3.add(RCFP.action);
		actCF3.add(RCFP.obj1);
		actCF3.add(RCFP.obj2);
		actCF3.add(RCFP.obj3);
		actCF3.add(RCFP.obj4);
		act3 = Network.defineCaseFrameWithConstraints("Act", actCF3);

		LinkedList<RCFP> actCF4 = new LinkedList<RCFP>();
		actCF4.add(RCFP.action);
		actCF4.add(RCFP.obj1);
		actCF4.add(RCFP.obj2);
		actCF4.add(RCFP.obj3);
		actCF4.add(RCFP.obj4);
		actCF4.add(RCFP.obj5);
		act4 = Network.defineCaseFrameWithConstraints("Act", actCF4);

		LinkedList<RCFP> actCF5 = new LinkedList<RCFP>();
		actCF5.add(RCFP.action);
		actCF5.add(RCFP.obj1);
		actCF5.add(RCFP.obj2);
		actCF5.add(RCFP.obj3);
		actCF5.add(RCFP.obj4);
		actCF5.add(RCFP.obj5);
		actCF5.add(RCFP.obj6);
		act5 = Network.defineCaseFrameWithConstraints("Act", actCF5);

		LinkedList<RCFP> actCF6 = new LinkedList<RCFP>();
		actCF6.add(RCFP.action);
		actCF6.add(RCFP.obj1);
		actCF6.add(RCFP.obj2);
		actCF6.add(RCFP.obj3);
		actCF6.add(RCFP.obj4);
		actCF6.add(RCFP.obj5);
		actCF6.add(RCFP.obj6);
		actCF6.add(RCFP.obj7);
		act6 = Network.defineCaseFrameWithConstraints("Act", actCF6);

		LinkedList<RCFP> actCF7 = new LinkedList<RCFP>();
		actCF7.add(RCFP.action);
		actCF7.add(RCFP.obj1);
		actCF7.add(RCFP.obj2);
		actCF7.add(RCFP.obj3);
		actCF7.add(RCFP.obj4);
		actCF7.add(RCFP.obj5);
		actCF7.add(RCFP.obj6);
		actCF7.add(RCFP.obj7);
		actCF7.add(RCFP.obj8);
		act7 = Network.defineCaseFrameWithConstraints("Act", actCF7);

		LinkedList<RCFP> actCF8 = new LinkedList<RCFP>();
		actCF8.add(RCFP.action);
		actCF8.add(RCFP.obj1);
		actCF8.add(RCFP.obj2);
		actCF8.add(RCFP.obj3);
		actCF8.add(RCFP.obj4);
		actCF8.add(RCFP.obj5);
		actCF8.add(RCFP.obj6);
		actCF8.add(RCFP.obj7);
		actCF8.add(RCFP.obj8);
		actCF8.add(RCFP.obj9);
		act8 = Network.defineCaseFrameWithConstraints("Act", actCF8);

		LinkedList<RCFP> actCF9 = new LinkedList<RCFP>();
		actCF9.add(RCFP.action);
		actCF9.add(RCFP.obj1);
		actCF9.add(RCFP.obj2);
		actCF9.add(RCFP.obj3);
		actCF9.add(RCFP.obj4);
		actCF9.add(RCFP.obj5);
		actCF9.add(RCFP.obj6);
		actCF9.add(RCFP.obj7);
		actCF9.add(RCFP.obj8);
		actCF9.add(RCFP.obj9);
		actCF9.add(RCFP.obj10);
		act9 = Network.defineCaseFrameWithConstraints("Act", actCF9);
	}
}