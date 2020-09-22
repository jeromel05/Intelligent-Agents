import uchicago.src.sim.space.Object2DGrid;
/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */

public class RabbitsGrassSimulationSpace {
    private Object2DGrid grassSpace;
    private Object2DGrid agentSpace;

    public RabbitsGrassSimulationSpace(int xSize, int ySize){
        grassSpace = new Object2DGrid(xSize, ySize);
        agentSpace = new Object2DGrid(xSize, ySize);
        for(int i = 0; i < xSize; i++){
            for(int j = 0; j < ySize; j++){
                grassSpace.putObjectAt(i,j, 0);
            }
        }
    }

    public void spreadGrass(int nbGrass){
        // Randomly place money in moneySpace
        for(int i = 0; i < nbGrass; i++){

            // Choose coordinates
            int x = (int)(Math.random()* grassSpace.getSizeX());
            int y = (int)(Math.random()*grassSpace.getSizeY());

            // Get the value of the object at those coordinates
            int I;
            I = getGrassAt(x,y);

            // Replace the Integer object with another one with the new value
            // If grass already occupies the chosen cell, we add 1 to its energy
            grassSpace.putObjectAt(x,y, I + 1);
        }
    }

    public int getGrassAt(int x, int y){
        int i;
        if(grassSpace.getObjectAt(x,y)!= null){
            i = ((Integer)grassSpace.getObjectAt(x,y)).intValue();
        }
        else{
            i = 0;
        }
        return i;
    }

    public boolean isCellOccupied(int x, int y){
        boolean isOccupied = false;
        if(agentSpace.getObjectAt(x, y)!=null) isOccupied = true;
        return isOccupied;
    }

    public boolean addAgent(RabbitsGrassSimulationAgent agent){
        boolean success = false;
        int count = 0;
        //countlimit = nb of attempts
        int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();

        while((success==false) && (count < countLimit)){
            int x = (int)(Math.random()*(agentSpace.getSizeX()));
            int y = (int)(Math.random()*(agentSpace.getSizeY()));
            if(isCellOccupied(x,y) == false){
                agentSpace.putObjectAt(x,y,agent);
                agent.setXY(x,y);
                agent.setCarryDropSpace(this);
                success = true;
            }
            count++;
        }

        return success;
    }

    public void removeAgentAt(int x, int y){
        agentSpace.putObjectAt(x, y, null);
    }

    public Object2DGrid getCurrentGrassSpace(){
        return grassSpace;
    }

    public Object2DGrid getCurrentAgentSpace(){
        return agentSpace;
    }

    public int eatGrassAt(int x, int y){
        int grass = getGrassAt(x, y);
        grassSpace.putObjectAt(x, y, 0);
        return grass;
    }

    public boolean moveAgentAt(int x, int y, int newX, int newY){
        boolean retVal = false;
        if(!isCellOccupied(newX, newY)){
            RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent)agentSpace.getObjectAt(x, y);
            removeAgentAt(x,y);
            cda.setXY(newX, newY);
            agentSpace.putObjectAt(newX, newY, cda);
            retVal = true;
        }
        return retVal;
    }
}
