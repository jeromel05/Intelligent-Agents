import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;


/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {

	// Default Values
		private static final int NUMAGENTS = 20;
		private static final int WORLDXSIZE = 20;
		private static final int WORLDYSIZE = 20;
		private static final int NUMGRASS = 20;
		private static final int INITENERGY = 200;
		private static final int GRASSGROWTHRATE = 30;
		private static final int BIRTHTHRESH = 230;

		private int numInitRabbits = NUMAGENTS;
		private int worldXSize = WORLDXSIZE;
		private int worldYSize = WORLDYSIZE;
		private int numInitGrass = NUMGRASS;
		private int initEnergyLevel = INITENERGY;
		private int grassGrowthRate = GRASSGROWTHRATE;
		private int birthThreshold = BIRTHTHRESH;

		private ArrayList agentList;
		private DisplaySurface displaySurf;
		private Schedule schedule;
		private RabbitsGrassSimulationSpace cdSpace;

		public static void main(String[] args) {
			
			System.out.println("Rabbit skeleton");

			SimInit init = new SimInit();
			RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
			// Do "not" modify the following lines of parsing arguments
			if (args.length == 0) // by default, you don't use parameter file nor batch mode 
				init.loadModel(model, "", false);
			else
				init.loadModel(model, args[0], Boolean.parseBoolean(args[1]));
			
		}

		public void setup(){
			System.out.println("Running setup");
			cdSpace = null;
			agentList = new ArrayList();
			schedule = new Schedule(1);

			if (displaySurf != null){
				displaySurf.dispose();
			}
			System.out.println("Surf tej ");
			displaySurf = null;
			displaySurf = new DisplaySurface(this, "Model Window 1");
			registerDisplaySurface("Model Window 1", displaySurf);
			System.out.println("terminé setup ");

		}

		public void begin() {
			// TODO Auto-generated method stub
			buildModel();
    		buildSchedule();
    		buildDisplay();
			displaySurf.display();
			
		}

		public String[] getInitParam() {
			// TODO Auto-generated method stub
			// Parameters to be set by users via the Repast UI slider bar
			// Do "not" modify the parameters names provided in the skeleton code, you can add more if you want 
			String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold", "InitialEnergy"};
			return params;
		}

		public String getName() {
			return null;
		}

		public Schedule getSchedule() {
			return schedule;
		}

		public void buildModel(){
			System.out.println("Running BuildModel");
			cdSpace = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
			cdSpace.spreadGrass(numInitGrass);
			for(int i = 0; i < numInitRabbits; i++){
				addNewAgent();
			}
			for(int i = 0; i < agentList.size(); i++){
				RabbitsGrassSimulationAgent agent1 = (RabbitsGrassSimulationAgent)agentList.get(i);
				agent1.report();
			}
		}

		public void buildSchedule(){
			System.out.println("Running BuildSchedule");
			class RabbitStep extends BasicAction {
				public void execute() {
					SimUtilities.shuffle(agentList);
					//every agent moves at each step
					for(int i =0; i < agentList.size(); i++) {
						RabbitsGrassSimulationAgent agent1 = (RabbitsGrassSimulationAgent) agentList.get(i);
						agent1.step();
					}
				}
			}
			class RemoveDeadAgents extends BasicAction {
				public void execute(){
					reapDeadAgents();
					countLivingAgents();
				}
			}
			class GrowthAndReproduction extends BasicAction {
				public void execute(){
					cdSpace.spreadGrass(grassGrowthRate);
					//check each rabbit to see if it can reproduce
					for(int i =0; i < agentList.size(); i++){
						RabbitsGrassSimulationAgent agent1 = (RabbitsGrassSimulationAgent)agentList.get(i);
						if(agent1.canReproduce(birthThreshold)){
							addNewAgent();
						}
					}
				}
			}
			class updateDisplay extends BasicAction {
				public void execute() {
					displaySurf.updateDisplay();
				}
			}

			schedule.scheduleActionAtInterval(2, new RemoveDeadAgents());
			schedule.scheduleActionAtInterval(1, new RabbitStep());
			schedule.scheduleActionAtInterval(1, new GrowthAndReproduction());
			schedule.scheduleActionAtInterval(1, new updateDisplay());
		}

		public void buildDisplay(){
			System.out.println("Running BuildDisplay");
			ColorMap map = new ColorMap();

			for(int i = 1; i<16; i++){
				map.mapColor(i, new Color((int)(i * 8 + 127), 0, 0));
			}
			map.mapColor(0, Color.white);

			Object2DDisplay displayAgents = new Object2DDisplay(cdSpace.getCurrentAgentSpace());
			displayAgents.setObjectList(agentList);
			Value2DDisplay displayGrass = new Value2DDisplay(cdSpace.getCurrentGrassSpace(), map);

			displaySurf.addDisplayableProbeable(displayGrass, "Grass");
			displaySurf.addDisplayableProbeable(displayAgents, "Agents");
		}

		private void addNewAgent(){
			RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(initEnergyLevel);
			agentList.add(a);
			cdSpace.addAgent(a);
		}

		private int countLivingAgents(){
			int livingAgents = 0;
			for(int i = 0; i < agentList.size(); i++){
				RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent)agentList.get(i);
				if(cda.getEnergyLevel() > 0) livingAgents++;
			}
			System.out.println("Number of living agents is: " + livingAgents);

			return livingAgents;
		}

		private int reapDeadAgents(){
			int count = 0;
			for(int i = (agentList.size() - 1); i >= 0 ; i--){
				RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent)agentList.get(i);
				if(cda.getEnergyLevel() < 1){
					cdSpace.removeAgentAt(cda.getX(), cda.getY());
					agentList.remove(i);
					count++;
				}
			}
			return count;
		}

		public int getNumAgents(){
			return numInitRabbits;
		}

		public void setNumAgents(int na){
			numInitRabbits = na;
		}

		public int getNumGrass(){
		return numInitGrass;
	}

		public void setNumGrass(int ng){
			numInitGrass = ng;
	}

		public int getWorldXSize(){
			return worldXSize;
		}

		public void setWorldXSize(int wxs){
			worldXSize = wxs;
		}

		public int getWorldYSize(){
			return worldYSize;
		}

		public void setWorldYSize(int wys){
			worldYSize = wys;
		}

		public int getInitEnergyLevel(){
		return initEnergyLevel;
	}

		public void setInitEnergyLevel(int elev){
			initEnergyLevel = elev;
	}
}
