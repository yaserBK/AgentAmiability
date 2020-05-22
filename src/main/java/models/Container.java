package models;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;


public class Container {
    //holds all agents. Used for sake of index values.
    ArrayList<Agent> allAgents;
    //used to select model agent
    TreeSet<Agent> leaderBoard;

    Random random = new Random();
    private static Container container;

    private Container(){
        allAgents = new ArrayList<>();
        leaderBoard = new TreeSet<>();
    }


    //gets and instance of the container...
    public static Container getInstance(){
        if (container == null){
            container = new Container();
        }
        return container;
    }

    public void addAgent(Agent a){
        allAgents.add(a);
        leaderBoard.add(a);
    }

    //returns a list of two randomly selected agents to caller.
    public Agent[] randomlySelectPlayers()
    {
        Agent[] players = new Agent[2];
        int agent1 = random.nextInt(allAgents.size()); //selects random agent
        int agent2 = random.nextInt(allAgents.size());
        //lets prevent agents from playing against themselves.
        while(agent1 == agent2)
        { //forcefully changing selection
            agent2 = random.nextInt(allAgents.size());
        }
        //adding to list that will be returned to caller.
        players[0]= allAgents.get(agent1);
        players[1]= allAgents.get(agent2);
        return players;
    }

    //returns lowest element in the leaderboard treeSet (aka the "Model Agent" for the round)
    public Agent getModelAgent()
    {
        return this.leaderBoard.last();
    }

    public void addAgents(Agent[] agents){
        for (Agent a : agents) {
            allAgents.add(a);
            leaderBoard.add(a);
        }
    }




}
