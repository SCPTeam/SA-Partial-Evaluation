package it.unige.ctheory;

import java.util.*;
import it.unige.automata.*;
import it.unige.lts.LTS;


public class NaturalProjection {
	private static ArrayList<ArrayList<State>> AlgC1(ArrayList<ArrayList<State>> Chi) {
		int i = 0;
		
		while(i < Chi.size()-1) {
			boolean flag = false;
			Set<State> Y = new HashSet<State>();
			ArrayList<Collection<State>> Chip = new ArrayList<Collection<State>>();
			int j = i + 1;
			while(j < Chi.size()) {
				Set<State> tmp = new HashSet<State>();
				tmp.addAll(Chi.get(i));
				tmp.retainAll(Chi.get(j));
				if(!tmp.isEmpty()) {
					flag = true;
					Y.addAll(Chi.get(j));
					Chi.remove(j);
				}
				else {
					Chip.add(Chi.get(j));
				}
				j++;
			}
			if(!flag) {
				i++;
			}
			else {
				// Ugly but it works
				Y.addAll(Chi.get(i));
				Chi.get(i).clear();
				Chi.get(i).addAll(Y);
			}
		}

		return Chi;
	}

	private static ArrayList<ArrayList<State>> AlgC2(ArrayList<ArrayList<ArrayList<State>>> P) {
		ArrayList<ArrayList<State>> Z = new ArrayList<ArrayList<State>>();
		for(ArrayList<ArrayList<State>> Pi : P) {
			Z.addAll(Pi);
		}

		return AlgC1(Z);
	}
	
	private static Set<State> getTransResult(Automaton A, State src, Set<String> Sigma){
		HashSet<State> result = new HashSet<>();
		for(String s : Sigma)
			result.addAll(A.trans(src, s));
		return result;
	}
	
	public static ArrayList<State> reachableStates(Automaton A, State init, Set<String> Sigma){
		HashSet<State> result = new HashSet<State>();
		Stack<State> stack = new Stack<State>();
		stack.push(init); 
		result.add(init);
		
		while(!stack.isEmpty()){
			State current = stack.pop();
			//Set<Transition> T = A.getTransitions(current, Sigma);
			Set<State> S = getTransResult(A, current, Sigma);
			for(State target : S){
				if(!result.contains(target)){
					stack.push(target);
					result.add(target);
				}
			}
		}
		
		return new ArrayList<>(result);
	}
	
	private static ArrayList<ArrayList<State>> AlgC3(Automaton A, Set<String> Sigma_B) {
		// Initial state
		State q0 = A.getInitial();
		// Symbols to be projected
		Set<String> Sigma_AmB = new HashSet<String>();
		Sigma_AmB.addAll(A.getAlphabet());
		Sigma_AmB.removeAll(Sigma_B);
		
		ArrayList<State> Xq0 = reachableStates(A, q0, Sigma_AmB);
		
		ArrayList<ArrayList<State>> Chi = new ArrayList<>(); 
		Chi.add(Xq0);

		SortedSet<State> Y = new TreeSet<State>();   // SortedSet
		Y.addAll(A.getStates());
		Y.removeAll(Xq0);
		while(!Y.isEmpty()) {
			State q = Y.first();
			ArrayList<State> Xq = reachableStates(A, q, Sigma_AmB);
			Chi.add(Xq);
			Y.removeAll(Xq);
		}

		return AlgC1(Chi);
	}
	
	private static ArrayList<ArrayList<State>> AlgC4(LTS A, ArrayList<ArrayList<State>> Chi, ArrayList<String> Sigma_B) {
		ArrayList<ArrayList<ArrayList<State>>> PY = new ArrayList<ArrayList<ArrayList<State>>>();
		for(ArrayList<State> X_i : Chi){
			ArrayList<ArrayList<State>> Y_ij = new ArrayList<ArrayList<State>>();
			for(String sigma_j : Sigma_B){
				HashSet<State> X_ij = new HashSet<State>();
				for(State q : X_i){
					for(Transition Tq : A.getTransitions(q, sigma_j))
						X_ij.add(Tq.getDestination());
				}

				HashSet<State> Y = new HashSet<State>();
				Y_ij.addAll(Chi);

				for(ArrayList<State> X_k : Chi){
					HashSet<State> tmp = new HashSet<State>();
					tmp.addAll(X_ij);
					tmp.retainAll(X_k);
					if(!tmp.isEmpty()){
						Y.addAll(X_k);
						Y_ij.remove(X_k);
					}
				}
				ArrayList<State> tmp = new ArrayList<State>();
				tmp.addAll(Y);
				Y_ij.add(tmp);
			}
			PY.add(Y_ij);
		}
		return AlgC2(PY);
	}

	public static ArrayList<ArrayList<State>> computeRStar(Automaton A, Set<String> Sigma_B){
		return AlgC3(A, Sigma_B);
	}
	
	/* Implementation schema: 
	 * 1 - Compute R* through Alg3
	 * 2 - Compute R+ => fixed point of \omega(R*) 
	 * 3 - Compute canonical projection h : Q -> Y (see paper pag. 17)
	 * 4 - Compute transitions (see paper pag. 17)
	 */
	public static LTS proj(LTS A, Set<String> Sigma_B) {
		return null;
	}
	
	public static LTS proj(LTS spec, LTS A, Set<String> Sigma_B) {
		return null;
	}
}
