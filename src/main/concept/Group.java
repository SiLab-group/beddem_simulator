package main.concept;

import java.util.HashMap;
import java.util.Map;

public abstract class Group {

	private String ID;
	private Map<Option,Double> optionToRefValMap; 
	private double totalRefSum;
	
	public Group(String id) {
		this.ID = id;
		this.optionToRefValMap = new HashMap<Option,Double>();
		this.totalRefSum = 0;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public void updateGroupRef(Option opt, Double value) {
		this.totalRefSum += value;
		if (this.optionToRefValMap.containsKey(opt)) {
			value += this.optionToRefValMap.get(opt);
		} 
		this.optionToRefValMap.put(opt, value);
	}
	
	public void resetRef() {
		this.optionToRefValMap = new HashMap<Option,Double>();
		this.totalRefSum = 0;
	}
	
	public Double getGroupRef(Option opt) {
		if (this.optionToRefValMap.containsKey(opt)) {
			return this.optionToRefValMap.get(opt)/totalRefSum;
		}
		return 0.0;
	}
}
