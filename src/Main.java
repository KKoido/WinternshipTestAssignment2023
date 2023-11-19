import java.io.*;
import java.util.*;

import static java.lang.Double.parseDouble;

public class Main {

    public static void main(String[] args) {

        long casinoBalance = 0;

        List<Player> players = new ArrayList<>();
        List<String[]> illegitimatePlayers = new ArrayList<>();

        Map<UUID, Match> matches = new HashMap<>();
        Map<UUID, List<String>> playerActions = new HashMap<>();

        // Read player data
        try (BufferedReader br = new BufferedReader(new FileReader("src/resources/player_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] playerData = line.split(",");
                UUID playerId = UUID.fromString(playerData[0]);
                if (!playerActions.containsKey(playerId))
                    playerActions.put(playerId, new ArrayList<>());
                playerActions.get(playerId).add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read match data
        try (BufferedReader br = new BufferedReader(new FileReader("src/resources/match_data.txt"))){
            String line;
            while ((line = br.readLine()) != null){
                String[] matchData = line.split(",");
                UUID matchId = UUID.fromString(matchData[0]);
                double rateA = parseDouble(matchData[1]);
                double rateB = parseDouble(matchData[2]);
                char result = matchData[3].charAt(0);

                matches.put(matchId, new Match(matchId, result, rateA, rateB));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Process each player's actions
        for (Map.Entry<UUID, List<String>> playerData : playerActions.entrySet()) {
            Player player = new Player(playerData.getKey());
            players.add(player);
            int casinoBalanceChange = 0;
            for (String action : playerData.getValue()){
                String[] actionParts = action.split(",");
                String operation = actionParts[1];
                int amount = Integer.parseInt(actionParts[3]);
                switch (operation){
                    case "DEPOSIT":
                        player.deposit(amount);
                        break;

                    case "WITHDRAW":
                        if (amount <= player.getBalance())
                            player.withdraw(amount);

                        // Illegal operation
                        else{
                            String[] illegitimateAction = new String[5];
                            String[] parts = action.split(",");
                            for (int i = 0; i < parts.length; i++) {
                                if (parts[i] != "")
                                    illegitimateAction[i] = parts[i];
                            }
                            illegitimatePlayers.add(illegitimateAction);
                            players.remove(player);
                            casinoBalanceChange = 0; // This players actions won't impact casino's balance
                        }
                        break;

                    case "BET":
                        if (amount <= player.getBalance()) {
                            // find the corresponding match
                            Match match = matches.get(UUID.fromString(actionParts[2]));
                            casinoBalanceChange -= player.bet(amount, actionParts[4].charAt(0), match);
                        }
                        // Illegal operation
                        else{
                            String[] illegitimateAction = new String[5];
                            String[] parts = action.split(",");
                            for (int i = 0; i < parts.length; i++) {
                                if (parts[i] != "")
                                    illegitimateAction[i] = parts[i];
                            }
                            illegitimatePlayers.add(illegitimateAction);
                            players.remove(player);
                            casinoBalanceChange = 0; // This players actions wont impact casino's balance
                        }
                        break;

                    default:
                        System.out.println("Unknown operation");
                        break;
                }
            }
            // Calculate host's balance change once all of a player's actions are processed.
            casinoBalance += casinoBalanceChange;
        }
        
        // Sort the player lists
        players.sort(Comparator.comparing(Player::getPlayerId));
        illegitimatePlayers.sort(Comparator.comparing(playerData -> playerData[0]));

        // Output data
        try (PrintWriter writer = new PrintWriter("src/result.txt")) {
            // Legitimate players
            for (Player player : players)
                writer.println(player.toString());
            writer.println();
            // Illegitimate players
            for (String[] action: illegitimatePlayers) {
                writer.println(Arrays.toString(action).replace(",", "")
                        .replace("[","").replace("]", ""));
            }
            // Casino's final balance
            writer.println("\n" + casinoBalance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
