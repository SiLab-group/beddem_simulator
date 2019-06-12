package dummy.context;

import dummy.simulator.GlobalVars;
import main.environment.ILocation;
import repast.simphony.context.DefaultContext;

/**
 * Container for all locations
 * 
 * @author khoa_nguyen
 *
 */
public class LocationContext extends DefaultContext<ILocation> {

	public LocationContext() {
		super(GlobalVars.CONTEXT_NAMES.LOCATION_CONTEXT);
	}

}