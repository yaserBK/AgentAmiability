package models;


/*
 * will act as players for game
 * will have the following move set: Abstain, Cooperate, Defect
 *
 * in this system the road is "one way"
 * though as is in real life a driver may choose to deviate from this(defect)
 * or drive in the regular flow of the road(cooperate)
 *
 * the player may choose to not play at all (abstain)
 *
 * A,D,C = 0,1,2
 *
 *
 * */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Agent implements Comparable<Agent>{
    //container to select model player for sake of decision making
    public static Container allPrisoners;
    //used for agentID (reference sake)
    private static int agentCount = 0;
    private int agentID;
    private final double DELTA = 0.2; //amiability adjusted on the basis of this value
    private final double S_DELTA = 0.07; //arbitrarily chosen

    Random rand = new Random();

    private double totalPayoffs;
    //number of rounds started (includes rounds abstained in)
    private double roundCount;
    //number of plays made (excludes round abstained in) used to inform decisions...
    private double playCount;

    private double am; //amiability measure
    private double thresh; //threshold

    //used to inform decisions:
    private double cCount; //cooperates
    private double aCount; //abstain count used for statistics
    private double dCount; //defect
    int recentDecision;
    private Container container;

    //array list for each of keys contain data 1's and 0's
    //all array lists will contain the same number of
    //entries, each corresponding with the number of rounds the agent has played
    //probably do not need this as it is a little convoluted.
    //if i really want i could just have multiple arraylists, which would make far more sense.
    HashMap<String, ArrayList<Integer>> moves;

    public Agent(double thresh){
        this.am = 0; //starting threshold of zero for all agents.
        this.thresh = thresh; //setting threshold
        this.container = Container.getInstance();
        agentCount++;
        this.agentID = agentCount;
        moves = new HashMap<String, ArrayList<Integer>>();
        //initialising the set
        moves.put("A", new ArrayList<Integer>());
        moves.put("C", new ArrayList<Integer>());
        moves.put("D", new ArrayList<Integer>());
        moves.put("Net Payoff After Round", new ArrayList<Integer>());
        moves.put("Payoff for round", new ArrayList<Integer>());
    }

    //this method adjusts amiability and returns a decision from the agent to the game
    // The possible outcomes are as follows:
    // { 0, 1, 2 } -> { D, A, C }
    public int decide(){
        //pulling a model agent from the container.
        Agent modelAgent = container.getModelAgent();
        //getting relevant statistics from model agent for sake of amiability adjustment of current agent.
        double c = modelAgent.getcCount(); // number of co-operations for agent.
        double d = modelAgent.getdCount(); // number of defects for agent
        //double mRc = getRoundCount();  //round count for model agent

        //if it is the first round the agent is playing then the amiability will be randomly selected
        if (roundCount == 0){
            //this.am = this.am + (1-am)*delta;
            this.incAm(true, false); //random amiability with regular delta.
        }
        else { //for all consecutive rounds:


            //honestly it doesnt need to be this complex but i was going to use the difference
            //in ratio as a means by which to select the delta for the rounds adjustment.
            if(c/(c+d) > d/(c+d)){ //if c > d
                //amiability incremented by the regular delta
                this.incAm(false, false);
            }
            else if (c/(c+d) < d/(c+d)) { // d >  c
                //decrease amiability and return decision
                this.decAm(false);
            }
            else{ // if they are equal (maybe i should have an other else if statement and then a final else statement)

                //randomly chooses whether to increment, decrement or stay the same
                int incORdec = rand.nextInt(3); //0,1,2 (do nothing,increment, decrement )

                if(incORdec == 1){
                    this.incAm(false, false);
                }

                else if(incORdec == 2){
                    this.decAm(false);
                }
                else{
                    //amiability kept the same. Accounts for weird edge cases
                    //that are unlikely to happen
                    this.am = am*1;
                }

            }

        }
        //here comes the alternative logic:

        //if amiability greater than cooperation threshold
        if (this.am >= this.thresh){
            this.setRecentDecision(2);
            cCount++;
            return 2; //-> Cooperate
        }
        //if amiability is lower than threshold but greater than half the amiability threshold:
        else if(this.am < this.thresh && this.am >= (this.thresh)/2){
            this.setRecentDecision(1);
            aCount++;
            return 1; //-> Abstain
        }
        //thus leaving:
        //if the amiability is less than 1/2 of the amiability threshold
        else{
            this.setRecentDecision(0);
            dCount++;
            return 0; //-> Defect
        }
    }

    /*
    *  the following method compares agents on the basis of the following ratio:
    * totalPayoffs/roundsPlayed
    *
    * it is used to sort the treeset based leaderboard in which all the agents are stored
    */
    public int compareTo(Agent o) {
        if(this.totalPayoffs/this.roundCount > o.totalPayoffs/o.roundCount){
            return 1;
        }
        else if(this.totalPayoffs/this.roundCount < o.totalPayoffs/o.roundCount){
            return -1;
        }
        //if they they share the same value for the ration
        //then the one with more rounds played is selected.
        else{
            return Double.compare(this.roundCount, o.roundCount);
        }
    }

    //called during round of game after a decision is returned from the agent.
    public void incRounds(boolean play){
        roundCount++;
        if (play){ //only incriments play count if the player chooses C or D (not A)
            playCount++;
        }
    }

    //increases amiability by selected learning rate
    public void incAm(boolean random, boolean small)
    {
        if (random)
        {
            this.am = am + (1 - am) * (rand.nextDouble()*0.5);
        } else {

            if(small){
                this.am = am + (1-am)* S_DELTA;
            }
            else{
            this.am = am + ((1 - am) * DELTA);
        }
        }

        //ensuring that am stays within the bounds of 0.0 and 1.0
        if(this.am > 1){
            this.am = 1;
        }
    }

    //decreases amiability with selected learning rate
    //small only set to true for adjustments that occur after the round takes place...
    public void decAm(boolean small)
    {
        if(small){
            this.am = (am - S_DELTA)/(1 - S_DELTA);
        }
        else {
            this.am = (am - DELTA) / (1 - DELTA);
        }
        //ensuring that the amiability stays within the bounds of 0.0 to 1.0
        if(this.am < 0){
            this.am = 0;
        }
    }


    public int getAgentID() {
        return agentID;
    }

    public double getRoundCount() {
        return roundCount;
    }

    //returns number of times defected
    public double getdCount() {
        return dCount;
    }

    //returns number of times cooperated
    public double getcCount() {
        return cCount;
    }

    @Override
    public String toString() {
        //all this data will be appended to the relevant csv.
        return roundCount + "," + playCount + "," + am + "," + aCount + ","
                + cCount + "," + dCount + "," +totalPayoffs;
    }

    //called in the decide method.
    //holds most recent decision to be written into CSV...
    public void setRecentDecision(int recentDecision) {
        this.recentDecision = recentDecision;
    }

    public void incPayoff(double newPayoff){
        this.totalPayoffs = this.totalPayoffs+newPayoff;
    }
    
    public void writeToCSV() throws IOException {
        //specifying folder and filename:
        String path = "ThirdBatchData/agent"+this.agentID+".csv";

        FileWriter fw = new FileWriter(path,true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        if(roundCount == 1){
            pw.println("AgentID,"+agentID+",Threshold,"+thresh);
            pw.println("roundCount,playCount,amiability,aCount,cCount,dCount,totalPayoffs");
            pw.flush();
        }

       //writing line to csv:
        pw.println( this.toString() );
        pw.flush();

    }
    
    
}
