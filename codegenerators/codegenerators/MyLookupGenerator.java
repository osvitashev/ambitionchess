package codegenerators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

import javax.management.openmbean.OpenMBeanParameterInfoSupport;

import gamestate.Bitboard;

public class MyLookupGenerator {
	static int NUM_GROUPS =1024;
	
	MyLookupGenerator(){
		populateAttackCollection();
	}
	
	
	static void addToSelection(ArrayList<Character>  arr, char arg, int num) {
		for(int i=0; i<num; ++i)
			arr.add(arg);
	}
	
	static void generatePermutations(ArrayList<Character> arr, int index, HashSet<ArrayList<Character>> uniquePermutations) {
        if (index == arr.size() - 1) {
        	if(!uniquePermutations.contains(arr)) {
        		uniquePermutations.add(new ArrayList<Character>(arr));
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
	private ArrayList<AttackSequence> attackCollection=new ArrayList<>();
	
	boolean isFeasibleAttackCombo(AttackSequence ac) {
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
		ArrayList<ArrayList<Character>> pawnSets = new ArrayList<ArrayList<Character>>();
		pawnSets.add(new ArrayList<Character>());
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(1).add('P');
		
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(2).add('P');
		pawnSets.get(2).add('P');

		ArrayList<ArrayList<Character>> knightSets = new ArrayList<ArrayList<Character>>();
		knightSets.add(new ArrayList<Character>());
		knightSets.add(new ArrayList<Character>());
		knightSets.get(1).add('N');
		knightSets.add(new ArrayList<Character>());
		knightSets.get(2).add('N');
		knightSets.get(2).add('N');
		
		
		ArrayList<ArrayList<Character>> kingSets = new ArrayList<ArrayList<Character>>();
		kingSets.add(new ArrayList<Character>());
		kingSets.add(new ArrayList<Character>());
		kingSets.get(1).add('K');
		
		int maxBishops = 1;
		int maxRooks=2;
		int maxQueens=2;
		
		int maxLength = 5;
		ArrayList<Character> selection = new ArrayList<Character>(10);
		HashSet<ArrayList<Character>> uniqueSliderPermutations = new HashSet<ArrayList<Character>>();

		
		for(int b=0; b<=maxBishops; ++b)
			for(int r=0; r<=maxRooks; ++r)
				for(int q=0; q<=maxQueens; ++q) {
					if(b+r+q<=maxLength) {
						selection.clear();
						addToSelection(selection, 'B', b);
						addToSelection(selection, 'R', r);
						addToSelection(selection, 'Q', q);
						generatePermutations(selection, 0, uniqueSliderPermutations);
					}
				}
		
		ArrayList<ArrayList<Character>> sliderSets = new ArrayList<ArrayList<Character>>();
		sliderSets.add(new ArrayList<Character>());//HashSet<ArrayList<Character>> could not hold the empty set
		sliderSets.addAll(uniqueSliderPermutations);
		
		AttackSequence tempAS;
		HashSet<AttackSequence> uniqueAttackCombinations = new HashSet<AttackSequence>();
		
		for(ArrayList<Character> ps : pawnSets)
			for(ArrayList<Character> ns : knightSets)
				for(ArrayList<Character> ss : sliderSets)
					for(ArrayList<Character> ks : kingSets) {
						for(int numDirectAttackers=0; numDirectAttackers<=ss.size(); numDirectAttackers++) {//<= because we wan to execute the loop body even with empty set
							tempAS=new AttackSequence();
							tempAS.unconditionalAttackers.addAll(ps);
							tempAS.unconditionalAttackers.addAll(ns);
							for(int di=0;di<numDirectAttackers;++di)
								tempAS.unconditionalAttackers.add(ss.get(di));
							for(int di=numDirectAttackers;di<ss.size();++di)
								tempAS.attackersThroughEnemyPawn.add(ss.get(di));
							tempAS.unconditionalAttackers.addAll(ks);
							tempAS.setSerializedIntKey();
							if(tempAS.attackersThroughEnemyPawn.indexOf('R') == -1)
								uniqueAttackCombinations.add(tempAS);
						}
					}
		
		ArrayList<AttackSequence> tempholder = new ArrayList<>(uniqueAttackCombinations);
		
		
		for(AttackSequence ac : tempholder)
			if(isFeasibleAttackCombo(ac))
				attackCollection.add(ac);
		
		
		Collections.sort(attackCollection, (a, b) -> {
			
			
			if(a.unconditionalAttackers.size()!=b.unconditionalAttackers.size())
				return a.unconditionalAttackers.size()-b.unconditionalAttackers.size();
			for(int i=a.unconditionalAttackers.size()-1; i>=0; --i)
				if((a.unconditionalAttackers.get(i) - b.unconditionalAttackers.get(i)) < 0)
					return -1;
				else if((a.unconditionalAttackers.get(i) - b.unconditionalAttackers.get(i)) > 0)
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
//		for(AttackSequence gl : attackCollection)
//			System.out.println(gl.toString());
		System.out.println("pawnSets: " + pawnSets.size());
		System.out.println("knightSets: " + knightSets.size());
		System.out.println("sliderSets: " + sliderSets.size());
		System.out.println("kingSets: " + kingSets.size());
		System.out.println("grandAttackCollection.size: " + attackCollection.size());
		
		
		//maps short attack string to attack combo
		HashMap<String, AttackSequence> dedupe= new HashMap<>();
		for(AttackSequence ac : attackCollection) {
			if(!dedupe.containsKey(ac.toCompressedAttackString())) {
				dedupe.put(ac.toCompressedAttackString(), ac);
			}
		}
		
		attackCollection=new ArrayList<>(dedupe.values());
		attackCollection.trimToSize();
		System.out.println("deduped grandAttackCollection.size: " + attackCollection.size());
		
		int minKey=Integer.MAX_VALUE, maxKey=Integer.MIN_VALUE;
		Set<Integer> keysSet = new HashSet<>();
		for(AttackSequence ac : attackCollection) {
			keysSet.add(ac.serializedIntKey);
			if(ac.serializedIntKey<minKey)
				minKey=ac.serializedIntKey;
			if(ac.serializedIntKey>maxKey)
				maxKey=ac.serializedIntKey;
		}
		if(keysSet.size()==attackCollection.size())
			System.out.println("All good: the attack sequences all have unique keys.");
		else
			System.out.println("SOMETHING IS WRONG!!!!!!!!!!!!!!!!!!!!!!!! NOT ALL attack sequences all have unique keys!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("Keys of grandAttackCollection: min="+minKey + " max="+maxKey);
		
		generateMatchUpCollection();
	}
	
	
	/**
	 * 
	 * @param attacker
	 * @param defender
	 * @param occupier - PieceType
	 * @return
	 */
	static int calculateGain_pureAttacks(List<Character> attacker_unconditionalAttackers, ArrayList<Character> attacker_attackersThroughEnemyPawn,
			List<Character> defender_unconditionalAttackers, ArrayList<Character> defender_attackersThroughEnemyPawn,int targetCost) {
//		System.out.println(attacker.toString() + " vs. " + defender.toString());
		Character occupier;
		ArrayDeque<Character> attacker_attacks = new ArrayDeque<>(attacker_unconditionalAttackers);
		ArrayDeque<Character> attacker_conditionalAttacks = new ArrayDeque<>(attacker_attackersThroughEnemyPawn);
		ArrayDeque<Character> defender_attacks = new ArrayDeque<>(defender_unconditionalAttackers);
		ArrayDeque<Character> defender_conditionalAttacks = new ArrayDeque<>(defender_attackersThroughEnemyPawn);
		
		ArrayDeque<Character> attacks, conditionalAttacks;
		
		//Follow static exchange evaluation algorithm here.
		boolean attacker_opposite_pawn_condition_met = false, defender_opposite_pawn_condition_met = false, opposite_pawn_condition_met;
		ArrayList<Integer> occupationValueHistory = new ArrayList<Integer>();
		occupationValueHistory.add(-targetCost);//gain[d]     = value[target];
		boolean isAttackerTurn = false;
		do {
			isAttackerTurn^=true;
			//get next attacker
			occupier=null;
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
				if(AttackSequence.isLesserAttacker(attacks.peekFirst(), conditionalAttacks.peekFirst()))
					occupier=attacks.removeFirst();
				else
					occupier=conditionalAttacks.removeFirst();
			}

			
			if(occupier == null)
				break;
			else if(occupier == 'P')
				if(isAttackerTurn)
					defender_opposite_pawn_condition_met=true;
				else
					attacker_opposite_pawn_condition_met=true;
				
//			System.out.println("isAttackerTurn: "+ isAttackerTurn+ " and occupier: "+ PieceType.toString(occupier));
			
			occupationValueHistory.add((isAttackerTurn ? 1 : -1) *(AttackSequence.pieceCost(occupier)));//gain[d]  = value[aPiece] - gain[d-1]; // speculative store, if defended

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
	
	static boolean verifyMatch(AttackSequence a, AttackSequence b) {
		if(!a.attackersThroughEnemyPawn.isEmpty() && b.unconditionalAttackers.indexOf('P')==-1)
			return false;
		if(!b.attackersThroughEnemyPawn.isEmpty() && a.unconditionalAttackers.indexOf('P')==-1)
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
		System.out.println("Matchup sample: "+ matchups.get(0).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(1).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(2).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(3).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(342).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(856).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(2900).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(5556).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(10546).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(1856).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(24864).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(30654).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(36654).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(40654).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(45654).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(50654).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(1069689).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(1079689).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(1099689).toVerboseString());
		System.out.println("Matchup sample: "+ matchups.get(1119689).toVerboseString());
		
		Long minKey=Long.MAX_VALUE, maxKey=Long.MIN_VALUE;
		Set<Long> keysSet = new HashSet<>();
		for(ComboMatchUp cmu : matchups) {
			keysSet.add(cmu.matchupKey);
			if(cmu.matchupKey<minKey)
				minKey=cmu.matchupKey;
			if(cmu.matchupKey>maxKey)
				maxKey=cmu.matchupKey;
		}
		if(keysSet.size()==matchups.size())
			System.out.println("All good: the matchups all have unique keys.");
		else
			System.out.println("SOMETHING IS WRONG!!!!!!!!!!!!!!!!!!!!!!!! NOT ALL matchups all have unique keys!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("Keys of matchups: min="+minKey + " max="+maxKey);
		
//this is wrong!!!
//Matchup sample: long=[PPBRRQK|] short=[PPMRRQK|] serialized= 4322100 VS. long=[PQR|Q] short=[PQR|Q] serialized= 35230
//natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=215104000000035230
//  P M R Q  - attackers
//P x . . . 
//M x . . . 
//R x . x . 
//Q x . x x 
		
	}
	

	public static void main(String[] args) {
		String javaVersion = System.getProperty("java.specification.version");
        System.out.println("Java Version: " + javaVersion);
		
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		
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

		String costString;
		HashMap<String, Integer> htg = new HashMap<>();
		for(ComboMatchUp cmu : myGenerator.matchups) {
			costString=cmu.getStringNaturalAttacks();
			htg.put(costString, 1+htg.getOrDefault(costString, 0));
		}
		System.out.println("Attack natural payoff distribution: ["+htg.size()+"]");
		System.out.println(htg);

		
		HashMap<Integer, Integer> whatIfDistribution = new HashMap<>();
		for(ComboMatchUp cmu : myGenerator.matchups) {
			whatIfDistribution.put(cmu.whatIfMatrix, 1+whatIfDistribution.getOrDefault(cmu.whatIfMatrix, 0));
		}
		System.out.println("WhatIf distribution: ["+whatIfDistribution.size()+"]");
		System.out.println(whatIfDistribution);
		System.out.println("WhatIf values: " + whatIfDistribution.values());
		
		int histogram_inputs[]=new int[NUM_GROUPS];
		Set<Integer>[] histogram_unique_outputs = new HashSet[NUM_GROUPS];
		for(int i=0; i< NUM_GROUPS; ++i)
			histogram_unique_outputs[i]=new HashSet<Integer>();
		
		for(ComboMatchUp cmu : myGenerator.matchups) {
			histogram_inputs[ComboMatchUp.toGroupIndex(cmu.matchupKey)]++;
			histogram_unique_outputs[ComboMatchUp.toGroupIndex(cmu.matchupKey)].add(cmu.whatIfMatrix);
		}
			
		{
			int min_inputSize = Integer.MAX_VALUE, min_outputSize = Integer.MAX_VALUE;
	        int max_inputSize = Integer.MIN_VALUE, max_outputSize = Integer.MIN_VALUE;
	        int sum_inputSize = 0, sum_outputSize = 0;
	        for(int i=0;i<NUM_GROUPS;++i) {
	        	System.out.println("mod "+NUM_GROUPS+" = " + i+ " inputs: "+ histogram_inputs[i] + " outputs: " + histogram_unique_outputs[i].size());
	            min_inputSize = Math.min(min_inputSize, histogram_inputs[i]);
	            max_inputSize = Math.max(max_inputSize, histogram_inputs[i]);
	            sum_inputSize += histogram_inputs[i];
	            
	            min_outputSize = Math.min(min_outputSize, histogram_unique_outputs[i].size());
	            max_outputSize = Math.max(max_outputSize, histogram_unique_outputs[i].size());
	            sum_outputSize += histogram_unique_outputs[i].size();
	        }
	        double average_inputSize = (double) sum_inputSize / NUM_GROUPS;
	        double average_outputSize = (double) sum_outputSize / NUM_GROUPS;
	        double sumOfSquaredDifferences_inputSize = 0, sumOfSquaredDifferences_outputSize = 0;
	        for(int i=0;i<NUM_GROUPS;++i) {
	            double diff = histogram_inputs[i] - average_inputSize;
	            sumOfSquaredDifferences_inputSize += diff * diff;
	            
	            diff = histogram_unique_outputs[i].size() - average_outputSize;
	            sumOfSquaredDifferences_outputSize += diff * diff;
	        }
	        double standardDeviation_inputSize = Math.sqrt(sumOfSquaredDifferences_inputSize / NUM_GROUPS);
	        double standardDeviation_outputSize = Math.sqrt(sumOfSquaredDifferences_outputSize / NUM_GROUPS);
			System.out.println("mod "+NUM_GROUPS+" inputs== min: "+min_inputSize+ " max: "+max_inputSize+" avg: "+String.format("%.2f", average_inputSize)+ " stddev: "+String.format("%.2f", standardDeviation_inputSize));
			System.out.println("mod "+NUM_GROUPS+" outputs== min: "+min_outputSize+ " max: "+max_outputSize+" avg: "+String.format("%.2f", average_outputSize)+ " stddev: "+String.format("%.2f", standardDeviation_outputSize));

		}
		
		String filePath = "etc/payload.csv";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			for (ComboMatchUp value : myGenerator.matchups) {
				writer.write(Integer.toString(value.whatIfMatrix));
				//writer.write(",");
				writer.newLine();
			}
			//writer.newLine();
			System.out.println("Array payload written to CSV file successfully.");
		} catch (IOException e) {
			System.err.println("An error occurred: " + e.getMessage());
		}
		
		filePath = "etc/identifiers.csv";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			for (ComboMatchUp value : myGenerator.matchups) {
				writer.write(Long.toString(value.matchupKey));
				//writer.write(",");
				writer.newLine();
			}
			//writer.newLine();
			System.out.println("Array identifier written to CSV file successfully.");
		} catch (IOException e) {
			System.err.println("An error occurred: " + e.getMessage());
		}
		
        List<Integer> read_payload = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("etc/payload.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int value = Integer.parseInt(line);
                read_payload.add(value);
            }
            
            System.out.println("Payload loaded from CSV: " + read_payload.size());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        
        List<Long> read_identifier = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("etc/identifiers.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                long value = Long.parseLong(line);
                read_identifier.add(value);
            }
            
            System.out.println("Identifier loaded from CSV: " + read_identifier.size());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        
	}//main

}
