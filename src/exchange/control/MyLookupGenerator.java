package exchange.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyLookupGenerator {
	
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

	public static void main(String[] args) {
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
		
		System.out.println("pawnSets.size: " + pawnSets.size());
		for(ArrayList<Character> ps : pawnSets)
			System.out.println(ps);
		
		System.out.println("knightSets.size: " + knightSets.size());
		for(ArrayList<Character> ns : knightSets)
			System.out.println(ns);
		
		System.out.println("sliderPermutations.size: " + sliderPermutations.size());
		for(ArrayList<Character> sp : sliderPermutations)
			System.out.println(sp);
		
		System.out.println("kingSets.size: " + kingSets.size());
		for(ArrayList<Character> ks : kingSets)
			System.out.println(ks);
		
		ArrayList<Character> temp = new ArrayList<Character>();
		ArrayList<ArrayList<Character>> grandCollection = new ArrayList<ArrayList<Character>>();
		
		for(ArrayList<Character> ps : pawnSets)
			for(ArrayList<Character> ns : knightSets)
				for(ArrayList<Character> ss : sliderPermutations)
					for(ArrayList<Character> ks : kingSets) {
						temp.clear();
						temp.addAll(ps);
						temp.addAll(ns);
						temp.addAll(ss);
						temp.addAll(ks);
						grandCollection.add(new ArrayList<Character>(temp));
					}
		
		Collections.sort(grandCollection, Comparator.comparingInt(ArrayList::size));
		System.out.println("grandCollection.size: " + grandCollection.size());
		for(ArrayList<Character> gl : grandCollection)
			System.out.println(gl);
		
		
	}//main

}
