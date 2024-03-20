# Behavior-driven Demand Model (BedDeM)
Behavior-driven Demand Model using Repast Simphony as a based framework library implementing the Triandis' Theory of Interpersonal Behavior.

![Beddem Model Theory of Interpersonal Behavior](docs/beddemTIB.PNG)

## Run individual simulation
Install Repast Simphony and compatible `eclipse committers IDE (2022-06)` the official instruction page can be found [here](https://repast.github.io/download.html)

1. Install Groovy in Eclipse: Help -> Install New Software -> Add new repository ->  `https://groovy.jfrog.io/artifactory/plugins-release/e4.24` -> Main package -> Eclipse Groovy Development tools
2. Install Repast Simphony: Help -> Install New Software -> Add new repository -> `https://web.cels.anl.gov/projects/Repast/update_site` -> Repast Simphony
3. Check Groovy compiler version is > than 3.0.x: Window -> Preferences -> Groovy compiler
4. `Note:` If Surrogate issue UTF16 appears: Menu Window -> Preferences -> Java -> Mark occurences -> Untick
5. Install openjdk-11 [here](https://jdk.java.net/archive/): Preferences -> Installed JREs -> Pick folder and Preferences -> Java -> Compiler-> Java 11
6. Clone the beddem_simulator repository `git clone https://github.com/SiLab-group/beddem_simulator.git`
7. Import project to eclipse: File -> New -> Other -> Repast Simphony -> Repast Simphony Project -> Select the folder of cloned repo (beddem) -> Name the project beddem -> Finish
8. Repast parameters, context, scenarios should be stored in `<project name>.rs` folder. If you name project differently it is needed to copy the xml files or in `Repast.settings`
9. Start Repast interface by click the small arrow next to the run button and select "your_project_name Model" (you can click the run button for the next run).
10. Click the run button to run the project. After it finishes, you should see a new output file in the project folder.
 
 ## Example simulation run
 This example simulation contains one agent with the simple schedule of two tasks. When changing the weight of the `time_weight` or `cost_weigt` in the `agent.csv` file. 
 The output changes based on the agent priorities `weights`. When the cost is more important agent walks at `6am`, when the time is more important agent drives.
 This properties can be adjusted in the example csv files located in the `data` folder.
 1. The properties file in `data/beddem_simulator.properties` should contain the files to be parsed 
```bash
AgentCSVfile=agent
LocationCSVfile=location
ScheduleCSVfile=schedule
VehicleCSVfile=vehicle
```
2. Run the simulation with run green button `beddem_simulator Model`
3. This opens Repast Simphony UI. Initialize context with `Initialize run` button.
4. Run the simulation with `Start Run` button.
![Repast Simphony UI](docs/contextSimphony.PNG)
5. Output files should be created in the root folder and the output should be print into the console. Such as
```bash
printReport: "
agentID,start_time,km,vehicle
1,6.0,2.0,car
1,12.0,10.0,car
"
```

## Project structure
In this case we have example `<scenario>` called `dummy`. The directory structure:
* src
	- `<scenario>.agent`: specific implementation of all agents for scenario and external inputs for processing
	   + CommunicationComponent: decision communication
	   + DecisionComponent: decision determinants definition
	   + MemoryComponent: memory definition and feelings
	   + PerceptionComponent: perception component of possibilities
	   + StandardDummyAgent: definition of the agent and its functions
	- `<scenario>.concept`: basic concepts used around model such as Task and Option available for agent
	   + EnvironmentalState: state of the environment and availability
	   + Feedback: feedback of the environment
	   + InternalState: state of the environment
	   + Option: environment options
	   + Task: one task in the agent schedule with all its constraints
	   + Vehicle: vehicle definition and properties
	- `<scenario>.context`: context definitions usually taken from the GlobVariables file
	   + AgentContext: context definition for agent
	   + LocationContext: context definition for location
	- `<scenario>.database`: data retrievers
	   + CSVReader: reader for csv files
	- `<scenario>.environment`: definitions of the environment
	- `<scenario>.report`: contains scenario specific reporter classes.
		+ Reporter classes: describes how what we want from the model. We can select which method to be run in Repast interface.
	- `<scenario>.simulator`: contain the Repast controller, scheduler and logger of the project. This is the core function of Repast and you should not modify it. The two simulator classes here are ContextManager (the simulator controller and entry points of the simulator) and ThreadAgentScheduler (schedules the Tasks and execute them).
		+ ContextManager: describes how the input should be read and can be modified to fit the project. Container of all the agents and locations.
	- `framework.agent.core`: anything that is related to the agent's operation
		+ IAgent: the Interface of agent. Contains some methods that an agent should have. 
		+ DefaultAgent: the standard agent that can be extended depend on the type of project. Its simulator operation is described in the step() method which is controlled by the Scheduler mentioned above.
		+ StandardTraveller: the class used specific for transportation demand. Contains the logic to evaluate an option. 
	- `framework.agent.reasoning`: containing the TPB and TIB model and determinant interfaces
		+ Determinant: standard determinant for evaluation of options
		+ LeafDeterminant: base determinant node in decision making 
		+ ParentDeterminant: parent determinant in decision making model defines ranking options
		+ TIBModel: Triandis decision making model, definition all base determinants according to this model
		+ TPBModel: Theory of planned behavior model
	- `framework.concept`: some basic concepts that are used around the model (i.e Task and Option available for the agent). Note that at the moment time is setup as hour and distance is in km. Also some classes that are implemented specifically for transportation demand.
	    + EnvironmentalState: state of environment
	    + Feedback: interface definition of feedback
	    + InternalState: interface for agent state 
	    + Opinion: interface for agent opinion
	    + Option: option interface
	    + Task: task interface
	- `framework.environment`: information of the environment where the agents reside in. (ex: available transportations, total demand, kms, spending for each mode, etc.).
	    + Environment: interface for location of the agent its environment
	- `framework.exception`: all the exceptions and how to handle them.
	
* data
	- `csv_files`:
		+ agents: list of all the agent and their parameters.
		+ transportation: list of all transportation mode and their parameters.
		+ schedule: list of all the events to be assigned to agents. At the moment, the price point of car is added at the end of the file name (ex: 0.0 mean car_price * 0.0). This is for when we want to run in batch mode where we want to define different price points in the demand curve.
	- testing: csv files for modular + system testing, will be generated if you run the testing classes.
	- beddem_simulator.properties: locations of all the inputs, parameters and outputs of the model.
* your_project_name.rs: Define the inputs, outputs, parameters and observers for the model 
