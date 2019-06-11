package main.concept;

import java.util.Map;

public class Option {
	protected Map<String, Double> resourceIDtoCostMap;

	public Option(Map<String, Double> resourceIDtoCostMap) {
		this.resourceIDtoCostMap = resourceIDtoCostMap;
	}

	public double getResourceCost(String resourceID) {
		Double cost = resourceIDtoCostMap.get(resourceID);
		if (cost == null) {
			return 0;
		}
		return cost;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (other instanceof Option) {
			Option otherOpt = (Option) other;
			return this.resourceIDtoCostMap.equals(otherOpt.resourceIDtoCostMap);
		}
		return false;

	}

	@Override
	public int hashCode() {
		return resourceIDtoCostMap.hashCode();
	}
}
