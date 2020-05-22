import models.Agent;
import models.Container;
import models.Game;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException {
        Container container = Container.getInstance();

        Game game = new Game();

        //I was sleep deprived and this seemed like a good idea at the time...
        Agent[] lowVhigh = {new Agent(0.25), new Agent(0.75)};
        Agent[] lowVmid = {new Agent(0.25), new Agent(0.50)};
        Agent[] highVmid = {new Agent(0.75), new Agent(0.50)};
        Agent[] highVhigh = {new Agent(0.75), new Agent(0.75)};
        Agent[] lowVlow = {new Agent(0.25), new Agent(0.25)};
        Agent[] midVmid = {new Agent(0.50), new Agent(0.50)};

        container.addAgents(lowVhigh);
        container.addAgents(lowVmid);
        container.addAgents(highVmid);
        container.addAgents(highVhigh);
        container.addAgents(lowVlow);
        container.addAgents(midVmid);

        game.playRound(lowVhigh);
        game.playRound(lowVmid);
        game.playRound(highVmid);
        game.playRound(lowVlow);
        game.playRound(highVhigh);
        game.playRound(midVmid);

        game.playRound(lowVhigh);
        game.playRound(lowVmid);
        game.playRound(highVmid);
        game.playRound(lowVlow);
        game.playRound(highVhigh);
        game.playRound(midVmid);



        for(int i =0; i<300; i++){
            Agent[] players = container.randomlySelectPlayers();
            game.playRound(players);
        }

    }
}
