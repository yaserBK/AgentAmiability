package models;

/*
* Payoff Matrix for two competing agents (a,b)
* ---------------------------------------------
* (C == Cooperate)
* (D == Defect)
* (A == Abstain)
*
* if C or D:
*
*            bC           bD
*     aC   (+3,+3)      (-1,+5)
*                   +
*     aD   (+5,-1)      (00,00)
*
*   numberOfPlays for (a, b)++
*   numberOfRounds for (a, b) ++
*
* if A:
*
*     (00,00)
*    numberOfPlays for (a,b)+=0
*    numberOfRounds for (a,b)++
* */


import java.io.*;

public class Game
{
    private Container container;

    public Game()
    {
        this.container = Container.getInstance();
    }

    //takes an array consisting of two agents and pits them against one another
    //after the initial set of rounds agents will be passed in by means of the
    //get random agents method.
    public void playRound(Agent[] agents) throws IOException {

        Agent agentA = agents[0];
        Agent agentB = agents[1];

        //here the following is to be done:
        // compare decisions made by each agent
        // 0,1,2 -> D, A, C

        /*
        * x and y agent decisions (x,y)
        * if(A || A):
        *   playCount++
        *   roundCount = roundCount
        *   no payoffs.
        *   Record to CSV
        *
        * else {
        *   if(D&&C)
        *   payoff x and y
        *   record to csv
        *
        *   else if (C&&D)
        *
        *   else if (D&&D)
        *
        *   else if (C&&C)
        *
        * }
        *
        *
        * */

        int decA = agentA.decide();
        int decB = agentB.decide();

        if(decA == 1 || decB == 1){
            //incrementing number of rounds for both
            //but keeping number of plays along with all other data same.
            agentA.incRounds(false);
            agentB.incRounds(false);
            agentA.incPayoff(0);
            agentB.incPayoff(0);
        }
        else{
            //todo 1:  have all the amiability adjusted after each play
            //todo 2: have all data written to csv after each play


            if(decA == 0 && decB == 0){ //both defect
                agentA.incRounds(true);
                agentB.incRounds(true);

                agentA.incPayoff(0);
                agentB.incPayoff(0);


            }
            else if(decA == 2 && decB == 2){ //both cooperate
                agentA.incRounds(true);
                agentB.incRounds(true);

                agentA.incPayoff(+3);
                agentB.incPayoff(+3);



            }
            else if(decA == 0 && decB ==2){
                agentA.incRounds(true);
                agentB.incRounds(true);

                agentA.incPayoff(+5);
                agentB.incPayoff(-1);


            }
            else if(decA == 2 && decB == 0){
                agentA.incRounds(true);
                agentB.incRounds(true);

                agentA.incPayoff(-1);
                agentB.incPayoff(+5);


            }
        }

        agentA.writeToCSV();
        agentB.writeToCSV();
    }
}
