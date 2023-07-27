package codegenerators;

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

import gamestate.Bitboard;
import gamestate.GlobalConstants.PieceType;

public class MyLookupGenerator {
	public static String attackerListToString(ArrayList<Integer> attackers) {
		String ret="attackers: ";
		for(Integer i : attackers)
			ret += PieceType.toString(i);
		return ret;
	}
	
	class AttackStack{
		//list of AttackSet.AttackSetType
		ArrayList<Integer> attackers=new ArrayList<Integer>();
		ArrayList<Integer> attackersThroughEnemyPawn=new ArrayList<Integer>();
		
		@Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        AttackStack other = (AttackStack) o;
	        return attackers.equals(other.attackers) && attackersThroughEnemyPawn.equals(other.attackersThroughEnemyPawn);
	    }
		
		@Override
	    public int hashCode() {
	        return Objects.hash(attackers, attackersThroughEnemyPawn);
	    }
	}
	
	MyLookupGenerator(){
		populateGrandAttackCollection();
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
	
	private ArrayList<AttackStack> grandAttackCollection;
	
	/**
	 * Is only concerned with pawn attacks. not pushes!
	 */
	private void populateGrandAttackCollection(){
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
		
		int maxKnights =2;
		int maxKings = 1;
		
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
		
		AttackStack tempAS;
		//ArrayList<Integer> temp = new ArrayList<Integer>();
		HashSet<AttackStack> attacksWithNoEnemyPawns = new HashSet<AttackStack>();
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
						tempAS=new AttackStack();
						tempAS.attackers.addAll(ps);
						tempAS.attackers.addAll(ns);
						tempAS.attackers.addAll(ss);
						tempAS.attackers.addAll(ks);
						attacksWithNoEnemyPawns.add(tempAS);
					}
		
		grandAttackCollection = new ArrayList<>(attacksWithNoEnemyPawns);
		
		Collections.sort(grandAttackCollection, (a, b) -> {
			
			
			if(a.attackers.size()!=b.attackers.size())
				return a.attackers.size()-b.attackers.size();
			for(int i=a.attackers.size()-1; i>=0; --i)
				if((a.attackers.get(i) - b.attackers.get(i)) < 0)
					return -1;
				else if((a.attackers.get(i) - b.attackers.get(i)) > 0)
					return 1;
            return 0;
        });
		
		System.out.println("grandAttackCollection.size: " + grandAttackCollection.size());
		for(AttackStack gl : grandAttackCollection)
			System.out.println(attackerListToString(gl.attackers));
		grandAttackCollection = grandAttackCollection;
	}
	
	
	/**
	 * Plays out the interactions of two attack stacks.
	 * The value returned is WHAT???
	 * 
	 */
	static byte calculatePowerDiff(AttackStack attacker, AttackStack defender) {
		return 0;
	}

	public static void main(String[] args) {
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		
		
			
		
	}//main

}
