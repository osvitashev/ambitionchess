package codegenerators;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

import exchange.control.AttackSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
	private ArrayList<AttackCombo> attackCollection=new ArrayList<>();
	
	boolean isFeasibleAttackCombo(AttackCombo ac) {
		if(ac.toString().indexOf("RB") != -1)
			return false;
		if(ac.toString().indexOf("QBR") != -1)
			return false;
		if(ac.toString().indexOf("QRQB") != -1)//in reality this would be QBQR!
			return false;
		if(ac.toString().indexOf("QRRQB") != -1)
			return false;
		if(ac.toString().indexOf("RQBQ") != -1)
			return false;
		
		return true;
	}
	
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
		HashSet<AttackCombo> uniqueAttackCombinations = new HashSet<AttackCombo>();
		
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
		
		ArrayList<AttackCombo> tempholder = new ArrayList<>(uniqueAttackCombinations);
		
		
		for(AttackCombo ac : tempholder)
			if(isFeasibleAttackCombo(ac))
				attackCollection.add(ac);
		
		
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
		for(AttackCombo gl : attackCollection)
			System.out.println(gl.toString());
		System.out.println("pawnSets: " + pawnSets.size());
		System.out.println("knightSets: " + knightSets.size());
		System.out.println("sliderSets: " + sliderSets.size());
		System.out.println("kingSets: " + kingSets.size());
		System.out.println("grandAttackCollection.size: " + attackCollection.size());
		
		
		//maps short attack string to attack combo
		HashMap<String, AttackCombo> dedupe= new HashMap<>();
		for(AttackCombo ac : attackCollection) {
			if(!dedupe.containsKey(ac.toCompressedAttackString())) {
				dedupe.put(ac.toCompressedAttackString(), ac);
			}
		}
		
		attackCollection=new ArrayList<>(dedupe.values());
		attackCollection.trimToSize();
		System.out.println("deduped grandAttackCollection.size: " + attackCollection.size());
	}
	
	
	static int pieceCost(int pt) {
		assert PieceType.validate(pt);
		switch (pt) {
		case PieceType.PAWN:
			return 100;
		case PieceType.KNIGHT:
			return 300;
		case PieceType.BISHOP:
			return 300;
		case PieceType.ROOK:
			return 500;
		case PieceType.QUEEN:
			return 1000;
		case PieceType.KING:
			return 1000000;
		}
		throw new RuntimeException("Unexpected value!");
	}
	
	
	/**
	 * 
	 * @param attacker
	 * @param defender
	 * @param occupier - PieceType
	 * @return
	 */
	static int calculateGain_pureAttacks(AttackCombo attacker, AttackCombo defender, int targetCost) {
//		System.out.println(attacker.toString() + " vs. " + defender.toString());
		int occupier;
		ArrayDeque<Integer> attacker_attacks = new ArrayDeque<>(attacker.attackers);
		ArrayDeque<Integer> attacker_conditionalAttacks = new ArrayDeque<>(attacker.attackersThroughEnemyPawn);
		ArrayDeque<Integer> defender_attacks = new ArrayDeque<>(defender.attackers);
		ArrayDeque<Integer> defender_conditionalAttacks = new ArrayDeque<>(defender.attackersThroughEnemyPawn);
		
		ArrayDeque<Integer> attacks, conditionalAttacks;
		
		//Follow static exchange evaluation algorithm here.
		boolean attacker_opposite_pawn_condition_met = false, defender_opposite_pawn_condition_met = false, opposite_pawn_condition_met;
		ArrayList<Integer> occupationValueHistory = new ArrayList<Integer>();
		occupationValueHistory.add(-targetCost);//gain[d]     = value[target];
		boolean isAttackerTurn = false;
		do {
			isAttackerTurn^=true;
			//get next attacker
			occupier=-1;
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
				occupier=attacks.removeFirst();
			else if(attacks.isEmpty() && opposite_pawn_condition_met && !conditionalAttacks.isEmpty())
				occupier=conditionalAttacks.removeFirst();
			else if(!attacks.isEmpty() && opposite_pawn_condition_met && !conditionalAttacks.isEmpty()) {
				if(attacks.peekFirst().intValue() < conditionalAttacks.peekFirst().intValue())
					occupier=attacks.removeFirst();
				else
					occupier=conditionalAttacks.removeFirst();
			}

			
			if(occupier == -1)
				break;
			else if(occupier == PieceType.PAWN)
				if(isAttackerTurn)
					defender_opposite_pawn_condition_met=true;
				else
					attacker_opposite_pawn_condition_met=true;
				
//			System.out.println("isAttackerTurn: "+ isAttackerTurn+ " and occupier: "+ PieceType.toString(occupier));
			
			occupationValueHistory.add((isAttackerTurn ? 1 : -1) *(pieceCost(occupier)));//gain[d]  = value[aPiece] - gain[d-1]; // speculative store, if defended

		}while(true);
		
//		System.out.println("Occupation sequence: "+ occupationValueHistory);
		ArrayList<Integer>gains=new ArrayList<Integer>();
		int currentGain=0;
		gains.add(currentGain);
		for(int i=0; i<occupationValueHistory.size()-1;++i) {
			currentGain-=occupationValueHistory.get(i);
			gains.add(currentGain);
		}
//		System.out.println("gains: "+ gains);
		int ret = helper_minimax_sequence(gains, 0);
//		System.out.println("returning: " + ret);
		return ret;
	}
	
	static int helper_minimax_sequence(ArrayList<Integer> arg, int i) {
		int temp;
		if(i == arg.size()-1) {
			temp=arg.get(i);
//			System.out.println("i="+i+ " returning leaf: "+temp);
			return temp;//the value of the last possible capturer does not affect the minimax calculation.
		}
			
		if(i%2 ==0) {
			temp=helper_minimax_sequence(arg, i+1);
//			System.out.println("i="+i+ " max("+arg.get(i)+", "+temp+")");
			temp=Math.max(arg.get(i), temp);
			return temp;
		}
			
		else {
			temp=helper_minimax_sequence(arg, i+1);
//			System.out.println("i="+i+ " min("+arg.get(i)+", "+temp+")");
			temp=Math.min(arg.get(i), temp);
			return temp;
		}
			
	}
	
	static boolean verifyMatch(AttackCombo a, AttackCombo b) {
		if(!a.attackersThroughEnemyPawn.isEmpty() && b.attackers.indexOf(PieceType.PAWN)==-1)
			return false;
		if(!b.attackersThroughEnemyPawn.isEmpty() && a.attackers.indexOf(PieceType.PAWN)==-1)
			return false;
		return true;
	}
	
	ArrayList<ComboMatchUp> matchups= new ArrayList<>();
	
	void generateMatchUpCollection() {
		ComboMatchUp matchup;
		
		for(int i=0;i<attackCollection.size();++i)
			for(int j=0;j<attackCollection.size();++j)
				if(verifyMatch(attackCollection.get(i), attackCollection.get(j))){
					matchup=new ComboMatchUp(attackCollection.get(i), attackCollection.get(j));
					matchups.add(matchup);
				}
		
		matchups.trimToSize();
		Collections.sort(matchups);
		System.out.println("ArrayList<ComboMatchUp> is generated with this many records: " + matchups.size());
		System.out.println("Matchup sample: "+ matchups.get(0).toStringNaturalAttacks());
		
		System.out.println("Matchup sample: "+ matchups.get(1).toStringNaturalAttacks());
		
		System.out.println("Matchup sample: "+ matchups.get(2).toStringNaturalAttacks());
		
		System.out.println("Matchup sample: "+ matchups.get(7856).toStringNaturalAttacks());
		
		System.out.println("Matchup sample: "+ matchups.get(45654).toStringNaturalAttacks());
		
		System.out.println("Matchup sample: "+ matchups.get(1099689).toStringNaturalAttacks());
		
	}
	
	
	/// turns out that file read/write operations are VERY slow.
	void writeMatchupCollection() {
	       // Write the ArrayList to a file using object serialization
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("naturalMatchupOutcomes.dat"))) {
            outputStream.writeObject(matchups);
            System.out.println("ArrayList<ComboMatchUp> written to the file successfully. With this many records: " + matchups.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	void loadMatchupCollection() {
	       try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("naturalMatchupOutcomes.dat"))) {
	    	   matchups = (ArrayList<ComboMatchUp>) inputStream.readObject();
	            matchups.trimToSize();
	            // Print the contents of the read ArrayList
	            System.out.println("Read ArrayList<ComboMatchUp> with this many records: " + matchups.size());
	        } catch (IOException | ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	}
	
	

	
	public static void main(String[] args) {
		MyLookupGenerator myGenerator = new MyLookupGenerator();

		//myGenerator.generateAndWriteMatchupCollection();
		//myGenerator.loadMatchupCollection();
		
		myGenerator.generateMatchUpCollection();
		
		System.out.println("Some matchup stats:");
		int pawnPos=0, minorPos=0, rookPos=0, queenPos=0;
		for(ComboMatchUp cmu : myGenerator.matchups) {
			if(cmu.naturalOutcomeTargetPawn>0)
				pawnPos++;
			if(cmu.naturalOutcomeTargetMinor>0)
				minorPos++;
			if(cmu.naturalOutcomeTargetRook>0)
				rookPos++;
			if(cmu.naturalOutcomeTargetQueen>0)
				queenPos++;
		}
		
		minorPos = myGenerator.matchups.size()-minorPos;
		rookPos = myGenerator.matchups.size()-rookPos;
		queenPos = myGenerator.matchups.size()-queenPos;
		
		System.out.println("Total number of matchups: "+myGenerator.matchups.size());
		System.out.println("Matchups where outcome is positive when target is pawn: "+pawnPos + " or " + String.format("%.2f", (double)pawnPos/(double)myGenerator.matchups.size()));
		System.out.println("Matchups where outcome is NOT positive when target is minor: "+minorPos+ " or " + String.format("%.2f", (double)minorPos/(double)myGenerator.matchups.size()));
		System.out.println("Matchups where outcome is NOT positive when target is rook: "+rookPos+ " or " + String.format("%.2f", (double)rookPos/(double)myGenerator.matchups.size()));
		System.out.println("Matchups where outcome is NOT positive when target is queen: "+queenPos+ " or " + String.format("%.2f", (double)queenPos/(double)myGenerator.matchups.size()));


    
        
	}//main

}
