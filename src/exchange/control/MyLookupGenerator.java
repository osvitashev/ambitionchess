package exchange.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gamestate.GlobalConstants.PieceType;

public class MyLookupGenerator {
	class AttackStack{
		ArrayList<Integer> attackers=new ArrayList<Integer>();
		int serializedAttackers;
		
	}
	
	MyLookupGenerator(){
		populateGrandAttackCollection();
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
	
	private ArrayList<AttackStack> grandAttackCollection;
	
	private static int characterToExtendedPieceType(Character ch) {
		if(ch.equals('i'))
			return PlayerAttackStack.ENEMY_PAWN;
		else if(ch.equals('P'))
			return PieceType.PAWN;
		else if(ch.equals('R'))
			return PieceType.ROOK;
		else if(ch.equals('N'))
			return PieceType.KNIGHT;
		else if(ch.equals('B'))
			return PieceType.BISHOP;
		else if(ch.equals('Q'))
			return PieceType.QUEEN;
		else if(ch.equals('K'))
			return PieceType.KING;
		throw new RuntimeException("encountered unexpected value!");
	}
	
	private void populateGrandAttackCollection(){
		ArrayList<ArrayList<Character>> pawnSets = new ArrayList<ArrayList<Character>>();
		pawnSets.add(new ArrayList<Character>());
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(1).add('P');
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(2).add('i');
		
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(3).add('P');
		pawnSets.get(3).add('i');
		
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(4).add('P');
		pawnSets.get(4).add('P');
		
		pawnSets.add(new ArrayList<Character>());
		pawnSets.get(5).add('i');
		pawnSets.get(5).add('i');
		
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
		
		int maxKnights =2;
		int maxKings = 1;
		
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
		
		ArrayList<ArrayList<Character>> sliderPermutations = new ArrayList<ArrayList<Character>>();
		sliderPermutations.add(new ArrayList<Character>());//HashSet<ArrayList<Character>> could not hold the empty set
		sliderPermutations.addAll(uniqueSliderPermutations);
		
		ArrayList<Character> temp = new ArrayList<Character>();
		ArrayList<ArrayList<Character>> grandCollectionWithChars = new ArrayList<ArrayList<Character>>();
		
		for(ArrayList<Character> ps : pawnSets)
			for(ArrayList<Character> ns : knightSets)
				for(ArrayList<Character> ss : sliderPermutations)
					for(ArrayList<Character> ks : kingSets) {
						temp.clear();
						temp.addAll(ps);
						temp.addAll(ns);
						temp.addAll(ss);
						temp.addAll(ks);
						grandCollectionWithChars.add(new ArrayList<Character>(temp));
					}
		
		Collections.sort(grandCollectionWithChars, Comparator.comparingInt(ArrayList::size));
		ArrayList<AttackStack> grandCollection = new ArrayList<AttackStack>();
		AttackStack tempAS;
		for(ArrayList<Character> attackers : grandCollectionWithChars) {
			tempAS=new AttackStack();
			int tempSerialized = PlayerAttackStack.initialize();
			for(Character character : attackers) {
				tempAS.attackers.add(characterToExtendedPieceType(character));
				if(character.equals('i'))
					tempSerialized = PlayerAttackStack.addEnemyPawn(tempSerialized);
				else if(character.equals('P'))
					tempSerialized = PlayerAttackStack.addRegularPiece(tempSerialized, PieceType.PAWN);
				else if(character.equals('R'))
					tempSerialized = PlayerAttackStack.addRegularPiece(tempSerialized, PieceType.ROOK);
				else if(character.equals('N'))
					tempSerialized = PlayerAttackStack.addRegularPiece(tempSerialized, PieceType.KNIGHT);
				else if(character.equals('B'))
					tempSerialized = PlayerAttackStack.addRegularPiece(tempSerialized, PieceType.BISHOP);
				else if(character.equals('Q'))
					tempSerialized = PlayerAttackStack.addRegularPiece(tempSerialized, PieceType.QUEEN);
				else if(character.equals('K'))
					tempSerialized = PlayerAttackStack.addRegularPiece(tempSerialized, PieceType.KING);
				else
					throw new RuntimeException("encountered unexpected value!");
			}
			tempAS.serializedAttackers=tempSerialized;
			grandCollection.add(tempAS);
		}
			
		System.out.println("grandCollection.size: " + grandCollection.size());
		for(AttackStack gl : grandCollection)
			System.out.println(gl.attackers + " \t\t\t "+ Integer.toOctalString(gl.serializedAttackers));
		grandAttackCollection = grandCollection;
	}

	public static void main(String[] args) {
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		
		
		for (int i = 0; i < 11; ++i) {
			final int c=i;
			System.out.println("AttackStacks with " + c+" component(s): "
					+ myGenerator.grandAttackCollection.stream().filter(item -> item.attackers.size() == c).count() + " "
					+ myGenerator.grandAttackCollection.stream().filter(item -> item.attackers.size() <= c).count());
		}
			
		
	}//main

}
