import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

import static java.lang.Math.random;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	private int pos_x;
	private int pos_y;
	private int energy_level;
	private static int IDNumber = 0;
	private int ID;
	private RabbitsGrassSimulationSpace cdSpace;

	public RabbitsGrassSimulationAgent(int basic_energy){
		energy_level = basic_energy;
		pos_x = -1;
		pos_y = -1;
		IDNumber++;
		ID = IDNumber;
	}

	public void setCarryDropSpace(RabbitsGrassSimulationSpace cds){
		cdSpace = cds;
	}

	public int getX() {
		return pos_x;
	}

	public int getY() {
		return pos_y;
	}

	public String getID(){
		return "A-" + ID;
	}

	public int getEnergyLevel(){
		return energy_level;
	}

	public void report(){
		System.out.println(getID() +
				" at " +
				pos_x + ", " + pos_y +
				" has " +
				getEnergyLevel() + " steps to live.");
	}

	public void setXY(int newX, int newY){
		pos_x = newX;
		pos_y = newY;
	}

	public void draw(SimGraphics arg0){
		arg0.drawFastRoundRect(Color.blue);
	}

	public void step(){
		energy_level--;
		boolean successMove=false;
		int count_attempts = 0;

		//we try to make a move in random directions until we find unoccupied space
		while(!successMove && count_attempts<10){
			int newX = pos_x;
			int newY = pos_y;
			int direction = (int) (random()*4);
			switch (direction){
				case 0:
					newX = newX + 1;
				case 1:
					newX = newX - 1;
				case 2:
					newY = newY + 1;
				case 3:
					newY = newY - 1;
			}

			Object2DGrid grid = cdSpace.getCurrentAgentSpace();
			//implement torus condition
			newX = (newX + grid.getSizeX()) % grid.getSizeX();
			newY = (newY + grid.getSizeY()) % grid.getSizeY();
			//System.out.println("delta " + (pos_y - newY) + "x " + (pos_x - newX));
			successMove = tryMove(newX, newY);
			//System.out.println("move nb " + count_attempts + "dir " + direction);

			++count_attempts;
		}
		energy_level += cdSpace.eatGrassAt(pos_x, pos_y);
	}

	private boolean tryMove(int newX, int newY){
		return cdSpace.moveAgentAt(pos_x, pos_y, newX, newY);
	}

	public boolean canReproduce(int thresh){
		return energy_level > thresh;
	}

}
