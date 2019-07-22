package dummy.context;

import dummy.simulator.GlobalVars;
import main.environment.Environment;
import repast.simphony.context.DefaultContext;

/**
 * Container for all locations
 * 
 * @author khoa_nguyen
 *
 */
public class LocationContext extends DefaultContext<Environment> {

	public LocationContext() {
		super(GlobalVars.CONTEXT_NAMES.LOCATION_CONTEXT);
	}

}