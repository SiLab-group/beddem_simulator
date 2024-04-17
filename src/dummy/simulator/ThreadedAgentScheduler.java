package dummy.simulator;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import framework.agent.core.IAgent;

/**
 * This class can be used to step agents in different threads simultaneously. If
 * the <code>ContextManager</code> determines that this is a good idea (e.g. if
 * there will be no inter-agent communication) then, rather than using Repast to
 * schedule each agent's step() method directly, it will schedule the
 * agentStep() method (below) instead. This method is then responsible for
 * making the agents step by delegating the work do different threads depending
 * on how many CPU cores are free. As you can imagine, this leads to massive
 * decreases in computation time on multi-core computers.
 * 
 * <p>
 * It is important to note that there will be other side-effects from using
 * multiple threads, particularly agents simultaneously trying to access
 * Building methods or trying to write output data. So care needs to be taken
 * with the rest of the model to prevent problems. The (fairly) naive way that
 * I've tackled this is basically with the liberal use of
 * <code>synchronized</code>
 * </p>
 * 
 * @author khoa_nguyen
 * @see ContextManager
 * @see ThreadController
 * @see AgentThread
 */
public class ThreadedAgentScheduler {

	private static Logger LOGGER = Logger.getLogger(ThreadedAgentScheduler.class.getName());

	private List<IAgent> scheduledAgentsList = new ArrayList<IAgent>();
	private boolean agentsFinishedStepping;

	/**
	 * This is called once per iteration and goes through each agent calling their
	 * step method. This is done (instead of using Repast scheduler) to allow
	 * multi-threading (each step method can be executed on a free core). This
	 * method actually just starts a ThreadController thread (which handles spawning
	 * threads to step agents) and waits for it to finish
	 */
	public synchronized void agentStep() {
		LOGGER.log(Level.DEBUG, "" + ContextManager.getTicks());
		this.agentsFinishedStepping = false;
		(new Thread(new ThreadController(this))).start();
		while (!this.agentsFinishedStepping) {
			try {
				// Wait for the ThreadController to call
				// setAgentsFinishedStepping().
				this.wait();
			} catch (InterruptedException e) {
				LOGGER.log(Level.FATAL, "", e);
				ContextManager.stopSim(e, ThreadedAgentScheduler.class);
			}
			// Wait until the thread controller has finished
		}
	}

	/**
	 * Used to tell the ContextCreator that all agents have finished their step
	 * methods and it can continue doing whatever it was doing (it will be waiting
	 * while agents are stepping).
	 */
	public synchronized void setAgentsFinishedStepping() {
		this.agentsFinishedStepping = true;
		this.notifyAll();
	}

	public void addAgentToStart(IAgent agent) {
		scheduledAgentsList.add(agent);
	}

	public List<IAgent> getScheduledAgentList() {
		return this.scheduledAgentsList;
	}
}

/** Controls the allocation of <code>AgentThread</code>s to free CPUs */
class ThreadController implements Runnable {

	private static Logger LOGGER = Logger.getLogger(ThreadController.class.getName());

	// A pointer to the scheduler, used to inform it when it can wake up
	private ThreadedAgentScheduler cc;

	// The number of CPUs which can be utilised
	private int numCPUs;
	// Record which cpus are free (true) or busy (false)
	private boolean[] cpuStatus;

	public ThreadController(ThreadedAgentScheduler cc) {
		this.cc = cc;
		this.numCPUs = Runtime.getRuntime().availableProcessors();
		// Set all CPU status to 'free'
		this.cpuStatus = new boolean[this.numCPUs];
		for (int i = 0; i < this.numCPUs; i++) {
			this.cpuStatus[i] = true;
		}
	}

	/**
	 * Start the ThreadController. Iterate over all agents, starting
	 * <code>AgentThread</code>s on free CPUs. If no free CPUs then wait for a
	 * AgentThread to finish.
	 */
	@Override
	public void run() {

		for (IAgent b : this.cc.getScheduledAgentList()) {

			// Find a free cpu to exectue on
			boolean foundFreeCPU = false;
			// Determine if there are no free CPUs so thread can wait for one to
			// become free
			while (!foundFreeCPU) {
				synchronized (this) {
					cpus: for (int i = 0; i < this.numCPUs; i++) {
						if (this.cpuStatus[i]) {
							// Start a new thread on the free CPU and set it's
							// status to false
							foundFreeCPU = true;
							this.cpuStatus[i] = false;
							(new Thread(new AgentThread(this, i, b))).start();
							// Stop looping over CPUs, have found a free one for
							// this agent
							break cpus;
						}
					} // for cpus
					if (!foundFreeCPU) {
						this.waitForAgentThread();
					} // if !foundFreeCPU
				}
			} // while !freeCPU
		} // for agents

		// Have started stepping over all agents, now wait for all to finish.
		boolean allFinished = false;
		while (!allFinished) {
			allFinished = true;
			synchronized (this) {
				cpus: for (int i = 0; i < this.cpuStatus.length; i++) {
					if (!this.cpuStatus[i]) {
						allFinished = false;
						break cpus;
					}
				} // for cpus
				if (!allFinished) {
					this.waitForAgentThread();
				}
			}
		} // while !allFinished
			// Finished, tell the context creator to start up again.
		this.cc.setAgentsFinishedStepping();
	}

	/**
	 * Causes the ThreadController to wait for a AgentThred to notify it that it has
	 * finished and a CPU has become free.
	 */
	private synchronized void waitForAgentThread() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			LOGGER.log(Level.FATAL, "", e);
			ContextManager.stopSim(e, ThreadedAgentScheduler.class);
		} // Wait until the thread controller has finished

	}

	/**
	 * Tell this <code>ThreadController</code> that one of the CPUs is no free and
	 * it can stop waiting
	 * 
	 * @param cpuNumber The CPU which is now free
	 */
	public synchronized void setCPUFree(int cpuNumber) {
		this.cpuStatus[cpuNumber] = true;
		this.notifyAll();
	}

}

/** Single thread to call a Agent's step method */
class AgentThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(AgentThread.class.getName());

	// The agent to step
	private IAgent theAgent;
	private ThreadController tc;
	// The cpu that the thread is running on, used so that ThreadController
	private int cpuNumber;

	public AgentThread(ThreadController tc, int cpuNumber, IAgent b) {
		this.tc = tc;
		this.cpuNumber = cpuNumber;
		this.theAgent = b;
	}

	@Override
	public void run() {
		try {
			this.theAgent.step();
			LOGGER.log(Level.DEBUG, "Execute agent: " + theAgent.toString() + " step method");
		} catch (Exception ex) {
			LOGGER.log(Level.FATAL, "ThreadedAgentScheduler caught an error, telling model to stop", ex);
			ContextManager.stopSim(ex, this.getClass());
		}
		// Tell the ThreadController that this thread has finished
		tc.setCPUFree(this.cpuNumber);
	}

}
