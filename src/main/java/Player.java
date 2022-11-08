import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Player {
    private DatagramPacket sendPacket;
    //server will send to the address data it received from
    private DatagramSocket sendSocket;
    private DatagramPacket receivePacket;
    private String lastMessage;
    private int number = 0;
    private boolean isTurn = false;
    private ArrayList<String> dice = new ArrayList<>();
    private String fortuneCard;
    private boolean[] inChest = {false, false, false, false, false, false, false, false};
    private boolean skullIsland = false;

    public Player() {
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets, add timeout for receiving
            sendSocket = new DatagramSocket();
            sendSocket.setSoTimeout(Config.TIMEOUT);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }
    //constructor for testing
    public Player(int port) {
        try {
            sendSocket = new DatagramSocket(port);
            sendSocket.setSoTimeout(Config.TIMEOUT);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Player me = new Player();
        boolean joined = me.join();
        while (!joined) {
          joined = me.join();
        }
        //good connection, receive
        me.receive();
        //lobbyWait will wait until we get a Welcome message, and collect input from player 1 if necessary.
        me.lobbyWait();
        //do a welcome message
        Config.LOGGER.info(Config.WELCOME);
        System.out.println(Config.WELCOME);
        while (!me.getTurn()) {
            //wait for my turn
            me.waitTurn();
            //now it's my turn
            me.takeTurn();
            //now, end my turn
            me.endTurn();
            //print new score
            System.out.println(me.getLastMessage());
        }
    }

    public void rpc_send(String message) {
        //construct a packet
        //datagram sends and receives bytes, we will use a more complex system to send and receive event objects.
        byte msg[] = message.getBytes();

        // Construct a datagram packet that is to be sent to a specified port
        // on a specified host.
        // The arguments are:
        //  msg - the message contained in the packet (the byte array)
        //  msg.length - the length of the byte array
        //  InetAddress.getLocalHost() - the Internet address of the
        //     destination host.

        try {
            sendPacket = new DatagramPacket(msg, msg.length,
                    InetAddress.getLocalHost(), Config.SERVER_PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Config.LOGGER.info("Player" + number + ": sending message");
        System.out.println("Player" + number + ": sending message");

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Config.LOGGER.info("Player" + number + ": message sent\n");
        System.out.println("Player" + number + ": message sent\n");
        receive();
    }

    public void receive() {
        //wait for a reply with a timeout
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        Config.LOGGER.info("Player" + number + ": receiving message");
        try {
            // Block until a datagram packet is received from receiveSocket.
            sendSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            //set last message to timeout
            lastMessage = "Timeout";
            return;
        } catch (IOException e) {
            System.out.println("Player IO Exception.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        Config.LOGGER.info("Player" + number + ": message received");

        int len = receivePacket.getLength();
        // Form a String from the byte array, set to lastMessage
        lastMessage = new String(data,0,len);
    }

    public boolean join() {
        //send join request and receive
        rpc_send("Join Request");

        //parse response
        if (lastMessage.equals("Timeout")) {
            //server busy or whatever
            //Config.LOGGER.info("Player" + number + ": failed to join Server");
            System.out.println("Player" + number + ": failed to join Server");
            number = 0;
            return false;
        }
        int lastdigit = Integer.parseInt(String.valueOf(lastMessage.charAt(lastMessage.length() - 1)));
        //check that it's a return message
        if (lastMessage.equals(Config.SERVER_JOIN_MESSAGE(lastdigit))) {
            //Set the number to be the last digit of the message
            number = lastdigit;
            Config.LOGGER.info("Player" + number + ": successfully joined the Server");
            System.out.println("Player" + number + ": successfully joined the Server");
            return true;
        }

        return false;
    }

    public void lobbyWait() {
        //if we are player 1, receive until we get the lobby question
        if (number == 1) {
            String msg;
            Scanner input = new Scanner(System.in);
            while(!(lastMessage.equals("Welcome")) && !(lastMessage.equals("Timeout"))) {
                while (!lastMessage.equals("Would you like to close the Lobby?")) {
                    System.out.println(lastMessage);
                    receive();
                }
                Config.LOGGER.info(getLastMessage() + " {Y/N}");
                System.out.println(getLastMessage() + " {Y/N}");
                msg = input.nextLine();
                while (!(msg.equals("Y") || msg.equals("N"))) {
                    Config.LOGGER.info("Invalid input, try again");
                    System.out.println("Invalid input, try again");
                    msg = input.nextLine();
                }
                rpc_send(msg);
            }
        }else {
            //not lobby host, just wait for welcome card
            while (!lastMessage.equals("Welcome")) {
                System.out.println(lastMessage);
                receive();
            }
        }
    }

    public void waitTurn() {
        System.out.println("Waiting for my turn");
        receive();
        String response;
        try {
            response = lastMessage.substring(0, 16);
        } catch (StringIndexOutOfBoundsException e) {
            response = lastMessage;
        }
        while (!response.equals("It's you're turn")) {
            if (lastMessage != "Timeout") {
                System.out.println("SERVER: " + lastMessage);
            }
            receive();
            try {
                response = lastMessage.substring(0, 16);
            } catch (StringIndexOutOfBoundsException e) {
                response = lastMessage;
            }
        }
        isTurn = true;
        //set fortune card
        int index = Integer.parseInt("" + lastMessage.charAt(16));
        setFortune(Config.FORTUNE_CARDS.get(index));
    }

    public void takeTurn() {
        //roll for the player and display the dice
        System.out.println("Rolling Dice...");
        rollDice();
        displayDice();
        String response = "something";
        while ((Integer.parseInt("" + getDiceString().charAt(0)) < 3 || fortuneCard.equals("SORCERESS") || skullIsland) && !(response.equals(""))) {
            Config.LOGGER.info("Select dice you would like to re-roll");
            System.out.println("Select dice you would like to re-roll");
            System.out.println("You must select at least two die, and they may not be skulls");
            System.out.println("type response as an undivided sequence of indices ie: 037 , type nothing to end your turn.\n");
            Scanner input = new Scanner(System.in);
            response = input.nextLine();
            String skulls = getDiceString();
            if (response.length() > 1 && ((fortuneCard.equals("TREASURE") && ((response.charAt(0) == 'A' || response.charAt(0) == 'R'))))) {
                while ((response.charAt(0) == 'A' && !(addChest(response))) || (response.charAt(0) == 'R' && !(removeChest(response)))) {
                    Config.LOGGER.info("Invalid Treasure Chest input, try again");
                    System.out.println("Invalid Treasure Chest input, try again");
                    response = input.nextLine();
                }
            } else {
                while (!(reRoll(response))) {
                    Config.LOGGER.info("Invalid input, try again");
                    System.out.println("Invalid input, try again");
                    response = input.nextLine();
                }
            }
            //good response, already reRolled / added/ removed
            displayDice();
            if (skullIsland) {
                if (getDiceString().charAt(0) == skulls.charAt(0)) {
                    //skulls didn't increment, cancel
                    break;
                }
            }
        }
        //check if we died:
        if (!response.equals("")) {
            if (skullIsland) {
                Config.LOGGER.info("You didn't get any more skulls, no points for you");
                System.out.println("You didn't get any more skulls, no points for you");
            }else {
                Config.LOGGER.info("You have three Skulls and have died, maybe points for you");
                System.out.println("You have three Skulls and have died, maybe points for you");
            }
        }
        //that's all...
    }

    public void displayDice() {
        System.out.println("Dice shown as a list with numbers representing the position in the list.\n");
        if (fortuneCard.equals("TREASURE")) {
            System.out.println("Values surrounded by {} are in your Treasure Chest");
            System.out.println("Add things to you're chest by following the reRoll instructions, but with an A or R to add or remove items\n");
        }
        for (int i = 0; i < dice.size(); i++) {
            if (inChest[i]) {
                System.out.print("[" + i + "]: {" + dice.get(i) + "}, ");
            }else {
                System.out.print("[" + i + "]: " + dice.get(i) + ", ");
            }
        }
        System.out.print("\n");
        System.out.println("\n");
        System.out.println("Your Fortune Card is: " + fortuneCard);
        System.out.println(Config.FORTUNE_DESCRIPTION(fortuneCard));
    }

    public void endTurn() {
        //first off, not my turn anymore
        isTurn = false;
        //we are going to send dice in for scoring and get a reply
        rpc_send(getDiceString());
        //delete dice
        dice.clear();
    }

    public void rollDice() {
        Random rand = new Random();
        //roll 8 dice, set the dice data structure
        for (int i = 0; i < 8; i++) {
            //roll from 0-5
            int roll = rand.nextInt(6);
            //get the string that corresponds to the index from Config
            dice.add(Config.DICE.get(roll));
        }
        if (fortuneCard != Config.FORTUNE_CARDS.get(4) || fortuneCard != Config.FORTUNE_CARDS.get(5) || fortuneCard != Config.FORTUNE_CARDS.get(6)) {
            //sea battle means can't go to skull island.
            if (Integer.parseInt(getDiceString().charAt(0) + "") >= 3 && fortuneCard.equals("SKULL1")) {
                skullIsland = true;
                System.out.println("WELCOME TO THE ISLAND OF SKULLS");
            }
            if (Integer.parseInt(getDiceString().charAt(0) + "") >= 2 && fortuneCard.equals("SKULL2")) {
                skullIsland = true;
                System.out.println("WELCOME TO THE ISLAND OF SKULLS");
            }
            if (Integer.parseInt(getDiceString().charAt(0) + "") >= 4) {
                skullIsland = true;
                System.out.println("WELCOME TO THE ISLAND OF SKULLS");
            }
        }
    }

    public boolean reRoll(String indices) {
        //firstly, empty indices means we reRoll nothing.
        if (indices.equals("")) {
            return true;
        }
        //error checking, sorceress can reroll 1 die
        if (indices.length() > 8 || (indices.length() < 2 && !fortuneCard.equals("SORCERESS"))) {
            return false;
        }
        if (checkIndices(indices) == null) {
            //checkIndices will return null if it found something bad
            return false;
        }
        int[] reRolls = checkIndices(indices);
        //backup dice incase we are rerolling a skull
        ArrayList<String> backup = new ArrayList<>(dice);
        Random rand = new Random();
        int skullBackup = -1;
        //check if we reroll a skull
        for (int i : reRolls) {
            if (fortuneCard.equals("SORCERESS")) {
                if (dice.get(i).equals("SKULL")) {
                    //set it to a monkey so it gets rerolled.
                    dice.set(i, "MONKEY");
                    //now, reset the fortune card
                    fortuneCard = "Already Used SORCERESS";
                }
            }
            if (dice.get(i).equals("SKULL")) {
                //reset to the backup before leaving
                dice.clear();
                dice.addAll(backup);
                if (fortuneCard.equals("Already Used SORCERESS")) {
                    //asked to reroll two skulls, return false and keep the sorceress
                    fortuneCard = "SORCERESS";
                }
                return false;
            }else {
                //roll from 0-5
                int roll = rand.nextInt(6);
                //loop to insure it isn't the same value
                while (Config.DICE.get(roll).equals(backup.get(i))) {
                    roll = rand.nextInt(6);
                }
                dice.set(i, Config.DICE.get(roll));
            }
        }
        return true;
    }

    public boolean addChest(String indices) {
        //remove the A or R if there is one...
        if (indices.charAt(0) == 'A') {
            indices = indices.substring(1);
        }
        //error checking
        if (!fortuneCard.equals("TREASURE")) {
            return false;
        }
        if (indices.equals("")) {
            return true;
        }
        //error checking, sorceress can reroll 1 die
        if (indices.length() > 8) {
            return false;
        }
        if (checkIndices(indices) == null) {
            //checkIndices will return null if it found something bad
            return false;
        }
        int[] addTo = checkIndices(indices);
        //error free, now let's see
        for (int i : addTo) {
            //add each to the chest.
            inChest[i] = true;
        }
        return true;
    }

    public boolean removeChest(String indices) {
        //remove the A or R if there is one...
        if (indices.charAt(0) == 'R') {
            indices = indices.substring(1);
        }
        //error checking
        if (!fortuneCard.equals("TREASURE")) {
            return false;
        }
        if (indices.equals("")) {
            return true;
        }
        if (indices.length() > 8) {
            return false;
        }
        if (checkIndices(indices) == null) {
            //checkIndices will return null if it found something bad
            return false;
        }
        int[] removeFrom = checkIndices(indices);
        //error free, now let's see
        for (int i : removeFrom) {
            //remove it
            inChest[i] = false;
        }
        return true;
    }

    private int[] checkIndices(String indices) {
        int[] addTo = new int[indices.length()];
        //create an int array of indices
        for (int i =0; i < indices.length(); i++) {
            try {
                addTo[i] = Integer.parseInt("" + indices.charAt(i));
                for (int j =0; j < indices.length(); j++) {
                    if (i != j && addTo[i] == Integer.parseInt("" + indices.charAt(j))) {
                        //found a duplicate
                        return null;
                    }
                }
            } catch (NumberFormatException e){
                //happens if any character is not an int
                return null;
            }
            //ensure we don't get any non existent indices
            if (addTo[i] > 7) {
                return null;
            }
        }
        return addTo;
    }

    public String getDiceString() {
        int[] diceCount = new int[6];
        for (String s : dice) {
            diceCount[Config.DICE.indexOf(s)]++;
        }
        //add something for treasurechest and skull island
        StringBuilder chest = new StringBuilder();
        for (int i = 0; i < inChest.length; i++) {
            if (inChest[i]) {
                //seems complex, we are getting the string value of the dice, then converting it to a config reference for scoring
                chest.append(Config.DICE.indexOf(dice.get(i)));
            }
        }
        //this is how we tell the server and game that we are on skull island
        if(skullIsland) {
            chest.append(Config.DICE.indexOf("SKULL"));
        }
        //just like in tester.
        return "" + diceCount[0] + diceCount[1] + diceCount[2] + diceCount[3] + diceCount[4] + diceCount[5] + chest.toString();
    }

    public void close() {
        sendSocket.close();
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public int getNumber() {
        return number;
    }

    public ArrayList<String> getDice() {
        return dice;
    }

    public boolean getTurn() {
        return isTurn;
    }

    public void setFortune(String fortuneCard) {
        this.fortuneCard = fortuneCard;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public void setDice(ArrayList<String> dice) {
        boolean firstTurn = (this.dice.size() == 0);
        this.dice.clear();
        this.dice.addAll(dice);
        if (firstTurn || skullIsland) {
            //basically, if none of the following are true, skull island shouldn't be either.
            skullIsland = false;
            if (Integer.parseInt(getDiceString().charAt(0) + "")  >= 3 && fortuneCard.equals("SKULL1")) {
                skullIsland = true;
                System.out.println("WELCOME TO THE ISLAND OF SKULLS");
            }
            if (Integer.parseInt(getDiceString().charAt(0) + "")  >= 2 && fortuneCard.equals("SKULL2")) {
                skullIsland = true;
                System.out.println("WELCOME TO THE ISLAND OF SKULLS");
            }
            if (Integer.parseInt(getDiceString().charAt(0) + "")  >= 4) {
                skullIsland = true;
                System.out.println("WELCOME TO THE ISLAND OF SKULLS");
            }
        }
    }
}
