package Services;

import Enums.*;
import Models.*;
import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    private GameObject prevTarget;

    private int prevSize;

    private boolean first;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
        this.first = true;
        this.prevSize = bot.getSize();
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }







   //*********************************************
    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD;
//        playerAction.heading = new Random().nextInt(360);
//
//        this.playerAction = playerAction;


        if (!gameState.getGameObjects().isEmpty()) {

            System.out.println("calculating...");
            if(first || !isLastTargetActive(prevTarget)) {
                first = false;
//                playerAction = gotoBestFoodHorde(playerAction);
                prevTarget = getBestFoodHorde();
                playerAction.heading = getHeadingBetween(prevTarget);
                System.out.println(getDistanceBetween(bot,prevTarget));
                System.out.println("heading to food.");
                prevSize = bot.getSize();
            }
            System.out.println(prevSize);

            System.out.println("Done!");
        }

        this.playerAction = playerAction;
    }

    //*********************************************


    public boolean isLastTargetActive(GameObject gameObject){
        var temp = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .collect(Collectors.toList());
        for(int i = 0; i < temp.size(); i++) {
            if(gameObject.getId() == temp.get(i).getId()) {
                return true;
            }
        }
        System.out.println(false);
        return false;
    }


    public PlayerAction gotoNearShip(PlayerAction playerAction) {

        playerAction.heading = getHeadingBetween(getNearestShip());

        return playerAction;
    }
    public PlayerAction gotoNearFood(PlayerAction playerAction) {

        playerAction.heading = getHeadingBetween(getNearestFood());

        return playerAction;
    }

    public PlayerAction gotoBestFoodHorde(PlayerAction playerAction) {

        playerAction.heading = getHeadingBetween(getBestFoodHorde());

        return playerAction;
    }

    public GameObject getNearestFood(){
        var foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        return foodList.get(0);
    }

    public GameObject getBestFoodHorde(){
        var foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        List<Map.Entry<GameObject, Double>> bestFoodList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            GameObject currentFood = foodList.get(i);

            var temp = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(currentFood, item)))
                    .collect(Collectors.toList());

            double oneOverDistances = 0;

            for (int j = 0; j < 20; j++) {
                double distances = getDistanceBetween(currentFood, temp.get(j));
                oneOverDistances += Math.pow(distances,-2);
            }

            bestFoodList.add(new AbstractMap.SimpleEntry<>(currentFood,
                    oneOverDistances * Math.pow(getDistanceBetween(currentFood,bot),-2)));
        }

        var bestFood = bestFoodList.get(0);

        for (int i = 0; i < bestFoodList.size(); i++){
            if(bestFoodList.get(i).getValue() > bestFood.getValue()){
                bestFood = bestFoodList.get(i);
            }
        }

        return bestFood.getKey();
    }

    public GameObject getNearestShip(){

        var shipList = gameState.getPlayerGameObjects()
                .stream().sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        if(gameState.getPlayerGameObjects().size() > 1) { return shipList.get(1); } else { return shipList.get(0); }

    }
    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}
