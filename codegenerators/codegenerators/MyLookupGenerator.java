package codegenerators;

import java.util.ArrayDeque;
import java.util.ArrayList;

import exchange.control.AttackSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.management.openmbean.OpenMBeanParameterInfoSupport;

import gamestate.Bitboard;
import gamestate.GlobalConstants.PieceType;

public class MyLookupGenerator {
	public static String attackerListToString(ArrayList<Integer> attackers) {
		String ret="";
		for(Integer i : attackers)
			ret += PieceType.toString(i);
		return ret;
	}
	

	
	MyLookupGenerator(){
		populateAttackCollection();
	}
	
	
	static void addToSelection(ArrayList<Integer>  arr, int arg, int num) {
		for(int i=0; i<num; ++i)
			arr.add(arg);
	}
	
	static void generatePermutations(ArrayList<Integer> arr, int index, HashSet<ArrayList<Integer>> uniquePermutations) {
        if (index == arr.size() - 1) {
        	if(!uniquePermutations.contains(arr)) {
        		uniquePermutations.add(new ArrayList<Integer>(arr));
        		//System.out.println(arr.toString());
        	}
            return;
        }
        for (int i = index; i < arr.size(); i++) {
            Collections.swap(arr, index, i);
            generatePermutations(arr, index + 1, uniquePermutations);
            Collections.swap(arr, index, i);
        }
    }
	
	/**
	 * Considers up to one mismatched pawn per attack combination, thus intentionally does not make a distinctions between
	 * 3k4/7K/8/3r4/2P1P3/1q3b2/8/8 w - - 0 1
	 * and
	 * 3k4/7K/8/3r4/2P1P3/5b2/6q1/8 w - - 0 1
	 */
	private ArrayList<AttackCombo> attackCollection;
	
	/**
	 * Is only concerned with pawn attacks. not pushes!
	 */
	private void populateAttackCollection(){
		ArrayList<ArrayList<Integer>> pawnSets = new ArrayList<ArrayList<Integer>>();
		pawnSets.add(new ArrayList<Integer>());
		pawnSets.add(new ArrayList<Integer>());
		pawnSets.get(1).add(PieceType.PAWN);
		
		pawnSets.add(new ArrayList<Integer>());
		pawnSets.get(2).add(PieceType.PAWN);
		pawnSets.get(2).add(PieceType.PAWN);

		ArrayList<ArrayList<Integer>> knightSets = new ArrayList<ArrayList<Integer>>();
		knightSets.add(new ArrayList<Integer>());
		knightSets.add(new ArrayList<Integer>());
		knightSets.get(1).add(PieceType.KNIGHT);
		knightSets.add(new ArrayList<Integer>());
		knightSets.get(2).add(PieceType.KNIGHT);
		knightSets.get(2).add(PieceType.KNIGHT);
		
		
		ArrayList<ArrayList<Integer>> kingSets = new ArrayList<ArrayList<Integer>>();
		kingSets.add(new ArrayList<Integer>());
		kingSets.add(new ArrayList<Integer>());
		kingSets.get(1).add(PieceType.KING);
		
		int maxBishops = 1;
		int maxRooks=2;
		int maxQueens=2;
		
		int maxLength = 5;
		ArrayList<Integer> selection = new ArrayList<Integer>(10);
		HashSet<ArrayList<Integer>> uniqueSliderPermutations = new HashSet<ArrayList<Integer>>();

		
		for(int b=0; b<=maxBishops; ++b)
			for(int r=0; r<=maxRooks; ++r)
				for(int q=0; q<=maxQueens; ++q) {
					if(b+r+q<=maxLength) {
						selection.clear();
						addToSelection(selection, PieceType.BISHOP, b);
						addToSelection(selection, PieceType.ROOK, r);
						addToSelection(selection, PieceType.QUEEN, q);
						generatePermutations(selection, 0, uniqueSliderPermutations);
					}
				}
		
		ArrayList<ArrayList<Integer>> sliderSets = new ArrayList<ArrayList<Integer>>();
		sliderSets.add(new ArrayList<Integer>());//HashSet<ArrayList<Character>> could not hold the empty set
		sliderSets.addAll(uniqueSliderPermutations);
		
		AttackCombo tempAS;
		//ArrayList<Integer> temp = new ArrayList<Integer>();
		HashSet<AttackCombo> uniqueAttackCombinations = new HashSet<AttackCombo>();
		//ArrayList<ArrayList<Integer>> attacksWithNoEnemyPawns = new ArrayList<ArrayList<Integer>>();
		
		System.out.println("pawnSets: " + pawnSets.size());
		//for(ArrayList<Integer> ps : pawnSets)
		//	System.out.println(attackerListToString(ps));
		
		System.out.println("knightSets: " + knightSets.size());
		//for(ArrayList<Integer> ps : knightSets)
		//	System.out.println(attackerListToString(ps));
		
		System.out.println("sliderSets: " + sliderSets.size());
		//for(ArrayList<Integer> ps : sliderSets)
		//	System.out.println(attackerListToString(ps));
		
		System.out.println("kingSets: " + kingSets.size());
		//for(ArrayList<Integer> ps : kingSets)
		//	System.out.println(attackerListToString(ps));
		
		
		for(ArrayList<Integer> ps : pawnSets)
			for(ArrayList<Integer> ns : knightSets)
				for(ArrayList<Integer> ss : sliderSets)
					for(ArrayList<Integer> ks : kingSets) {
						for(int numDirectAttackers=0; numDirectAttackers<=ss.size(); numDirectAttackers++) {//<= because we wan to execute the loop body even with empty set
							tempAS=new AttackCombo();
							tempAS.attackers.addAll(ps);
							tempAS.attackers.addAll(ns);
							
							for(int di=0;di<numDirectAttackers;++di)
								tempAS.attackers.add(ss.get(di));
							for(int di=numDirectAttackers;di<ss.size();++di)
								tempAS.attackersThroughEnemyPawn.add(ss.get(di));
							tempAS.attackers.addAll(ks);
							tempAS.setSerialized();
							if(tempAS.attackersThroughEnemyPawn.indexOf(PieceType.ROOK) == -1)
								uniqueAttackCombinations.add(tempAS);
						}
					}
		
		attackCollection = new ArrayList<>(uniqueAttackCombinations);
		
		Collections.sort(attackCollection, (a, b) -> {
			
			
			if(a.attackers.size()!=b.attackers.size())
				return a.attackers.size()-b.attackers.size();
			for(int i=a.attackers.size()-1; i>=0; --i)
				if((a.attackers.get(i) - b.attackers.get(i)) < 0)
					return -1;
				else if((a.attackers.get(i) - b.attackers.get(i)) > 0)
					return 1;
			
			if(a.attackersThroughEnemyPawn.size()!=b.attackersThroughEnemyPawn.size())
				return a.attackersThroughEnemyPawn.size()-b.attackersThroughEnemyPawn.size();
			for(int i=a.attackersThroughEnemyPawn.size()-1; i>=0; --i)
				if((a.attackersThroughEnemyPawn.get(i) - b.attackersThroughEnemyPawn.get(i)) < 0)
					return -1;
				else if((a.attackersThroughEnemyPawn.get(i) - b.attackersThroughEnemyPawn.get(i)) > 0)
					return 1;
            return 0;
        });
		
		System.out.println("grandAttackCollection.size: " + attackCollection.size());
		for(AttackCombo gl : attackCollection)
			System.out.println(gl.toString());
	}
	
	
	static int pieceCost(int pt) {
		assert PieceType.validate(pt);
		switch (pt) {
		case PieceType.PAWN:
			return 1;
		case PieceType.KNIGHT:
			return 3;
		case PieceType.BISHOP:
			return 3;
		case PieceType.ROOK:
			return 5;
		case PieceType.QUEEN:
			return 9;
		}
		throw new RuntimeException("Unexpected value!");
	}
	
	/**
	 * 
	 * @return index in ac.attackers
	 */
	static int getNextAttackerIndex(AttackCombo ac, int attacker_index, int conditional_attacker_index, boolean isConditionMet) {
		if(!isConditionMet)
			if((attacker_index+1) < ac.attackers.size())
				return attacker_index+1;
			else
				return -1;
		
		return -1;
	}
	
	static int calculateGain_pureAttacks(AttackCombo attacker, AttackCombo defender, int victim) {
		assert PieceType.validate(victim);
		ArrayDeque<Integer> attacker_attacks = new ArrayDeque<>(attacker.attackers);
		ArrayDeque<Integer> attacker_conditionalAttacks = new ArrayDeque<>(attacker.attackersThroughEnemyPawn);
		ArrayDeque<Integer> defender_attacks = new ArrayDeque<>(defender.attackers);
		ArrayDeque<Integer> defender_conditionalAttacks = new ArrayDeque<>(defender.attackersThroughEnemyPawn);
		
		ArrayDeque<Integer> attacks, conditionalAttacks;
		
		//Follow static exchange evaluation algorithm here.
		boolean attacker_opposite_pawn_condition_met = false, defender_opposite_pawn_condition_met = false, opposite_pawn_condition_met;
		ArrayList<Integer> gain = new ArrayList<Integer>();
		int d=0, nextAttacker, costOfNextAttacker=0;
		gain.add(pieceCost(victim));//gain[d]     = value[target];
		boolean isAttackerTurn = false;
		do {
			isAttackerTurn^=true;
			//get next attacker
			nextAttacker=-1;
			if(isAttackerTurn) {
				attacks = attacker_attacks;
				conditionalAttacks=attacker_conditionalAttacks;	
				opposite_pawn_condition_met=attacker_opposite_pawn_condition_met;
			}
			else {
				attacks = defender_attacks;
				conditionalAttacks=defender_conditionalAttacks;	
				opposite_pawn_condition_met=defender_opposite_pawn_condition_met;
			}
			if(!attacks.isEmpty() && (!opposite_pawn_condition_met || conditionalAttacks.isEmpty()))
				nextAttacker=attacks.removeFirst();
			else if(attacks.isEmpty() && opposite_pawn_condition_met && !conditionalAttacks.isEmpty())
				nextAttacker=conditionalAttacks.removeFirst();
			else if(!attacks.isEmpty() && opposite_pawn_condition_met && !conditionalAttacks.isEmpty()) {
				if(attacks.peekFirst().intValue() < conditionalAttacks.peekFirst().intValue())
					nextAttacker=attacks.removeFirst();
				else
					nextAttacker=conditionalAttacks.removeFirst();
			}

			
			if(nextAttacker == -1)
				break;
			else if(nextAttacker == PieceType.PAWN)
				if(isAttackerTurn)
					defender_opposite_pawn_condition_met=true;
				else
					attacker_opposite_pawn_condition_met=true;
				
			d++;
			
			costOfNextAttacker = pieceCost(nextAttacker);//placeholder
			gain.add(costOfNextAttacker - gain.get(d-1));//gain[d]  = value[aPiece] - gain[d-1]; // speculative store, if defended
			if (Math.max (-1*gain.get(d-1), gain.get(d)) < 0) break;//if (max (-gain[d-1], gain[d]) < 0) break; // pruning does not influence the result

		}while(true);
		while ((--d)!=0)
		      gain.set(d-1,  -Math.max (-1*gain.get(d-1), gain.get(d)));
		return gain.get(0);
	}

	public static void main(String[] args) {
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		
		
	}//main

}
