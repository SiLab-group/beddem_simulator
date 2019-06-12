package dummy.agent;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.agent.core.CommunicationComponent;
import main.agent.core.MemoryComponent;
import main.concept.Option;
import main.concept.Task;
import main.environment.ILocation;

public class DummyCommunicationComponent extends CommunicationComponent {

	private static Logger LOGGER = Logger.getLogger(DummyCommunicationComponent.class.getName());

	@Override
	protected void sendUpdateToEnvironment(Task task, ILocation loc, Option pickedOpt,
			MemoryComponent memoryComponent) {
		// TODO Auto-generated method stub
		
	}

}
