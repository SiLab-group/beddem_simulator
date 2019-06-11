package main.concept;

public abstract class Resource implements Cloneable {

	protected String id;
	protected double quantity;

	public Resource(String id) {
		this.id = id;
	}

	/**
	 * Get the ID of the resource.
	 * 
	 * @return The ID of the resource.
	 */
	public String getID() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Resource.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Resource other = (Resource) obj;
		if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Check if the resource is expired.
	 * 
	 * @return True if the resource is expired. False otherwise.
	 */
	public abstract boolean isExpired();

}
