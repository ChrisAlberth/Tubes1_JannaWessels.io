package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    private GameObject firedTarget;

    private GameObject prevTarget;

    private int prevSize;

    private boolean first;

    private boolean justFired;
    private boolean normalShot;

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
        this.justFired = false;
        this.prevSize = bot.getSize();
        this.normalShot = true;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD;
////        playerAction.heading = new Random().nextInt(360);

        if (!gameState.getGameObjects().isEmpty()) {
            if (justFired) {
//                int area = BestHeading();
//                playerAction.heading = searchHeadInArea(area);
                playerAction = devilSurvivor(playerAction);

                justFired = false;
            }
            else {
                playerAction = deathUponYou(playerAction);
            }

            double distanceFromCentre = getDistanceBetween(bot, new GameObject());
            if (distanceFromCentre + bot.getSize() + 10 > gameState.world.getRadius()) {
                playerAction.heading = getHeadingBetween(new GameObject());
            }

            first = false;
        }

        this.playerAction = playerAction;
    }

    public PlayerAction devilSurvivor(PlayerAction playerAction){
        if (first || prevSize != this.bot.getSize()){
            int area = BestHeading();
            playerAction.heading = getBestFoodHorde(area);
            playerAction.action = PlayerActions.FORWARD;
            prevSize = this.bot.getSize();
        }

        return playerAction;
    }

    //    private Integer Targeting() {
//        int direction = 90;
//        boolean fight;
//        var foodList = gameState.getGameObjects()
//                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
//                .sorted(Comparator
//                        .comparing(item -> getDistanceBetween(bot, item)))
//                .collect(Collectors.toList());
//
//        var superFoodList = gameState.getGameObjects()
//                .stream().filter(sf -> sf.getGameObjectType() == ObjectTypes.SUPERFOOD)
//                .sorted(Comparator
//                        .comparing(sf -> getDistanceBetween(bot, sf)))
//                .collect(Collectors.toList());
//
//        var playerList = gameState.getPlayerGameObjects()
//                .stream()
//                .sorted(Comparator
//                        .comparing(player -> getDistanceBetween(bot, player)))
//                .collect(Collectors.toList());
//
//        double nearestFood = getDistanceBetween(bot, foodList.get(0));
//        double nearestSuperFood = getDistanceBetween(bot, superFoodList.get(0));
//
//        if (playerList.size() > 0) {
//            if (bot.getSize() > playerList.get(1).getSize()) {
//                fight = true;
//            }
//            else {
//                fight = false;
//            }
//
//            if (fight) {
//                direction = getHeadingBetween((playerList.get(1)));
//            }
//            else {
//                if (nearestSuperFood <= nearestFood * 2) {
//                    direction = getHeadingBetween(superFoodList.get(0));
//                }
//                else {
//                    direction = getHeadingBetween(foodList.get(0));
//                }
//            }
//        }
//
//        return direction;
//    }
    private Integer BestHeading() {
        var possibleCollision = gameState.getGameObjects()
                .stream().filter(obj -> getDistanceBetween(bot, obj) < bot.getSize() * 2.5 + bot.getSpeed() + 60)
                .sorted(Comparator
                        .comparing(obj -> getDistanceBetween(bot, obj)))
                .collect(Collectors.toList());

        List<Map.Entry<Integer, Integer>> headingValue = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            int minHead = i*30;
            int maxHead = minHead + 29;

            var objectInDirection = possibleCollision
                    .stream().filter(oid -> getHeadingBetween(oid) >= minHead && getHeadingBetween(oid) <= maxHead)
                    .collect(Collectors.toList());

            int headWeight = 0;
            int nObject = objectInDirection.size();

            for (int j = 0; j < nObject; j++) {
                ObjectTypes type = objectInDirection.get(j).getGameObjectType();
                if (type == ObjectTypes.GASCLOUD) {
                    headWeight += -3;
                }
                else if (type == ObjectTypes.PLAYER) {
                    headWeight += -5;
                }
                else if (type == ObjectTypes.ASTEROIDFIELD) {
                    headWeight += -1;
                }
                else if (type == ObjectTypes.TORPEDOSALVO) {
                    headWeight += -5;
                }
                else if (type == ObjectTypes.TELEPORTER) {
                    headWeight += -10;
                }
                else if (type == ObjectTypes.SUPERNOVABOMB) {
                    headWeight += -10;
                }
                else if (type == ObjectTypes.FOOD) {
                    headWeight += 5;
                }
                else if (type == ObjectTypes.SUPERFOOD) {
                    headWeight += 6;
                }
                else headWeight += 1;
            }

            headingValue.add(new AbstractMap.SimpleEntry<>(i, headWeight));
        }

        int bestArea = 0;
        int bestWeight = 0;
        for (int k = 0; k < 12; k++) {
            if (headingValue.get(k).getValue() > bestWeight) {
                bestWeight = headingValue.get(k).getValue();
                bestArea = k;
            }
        }

        return bestArea;
    }

    private Integer searchHeadInArea(int area) {

        int minDegree = area*30;
        int maxDegree = minDegree+29;

        var objectInArea = gameState.getGameObjects()
                .stream().filter(object -> getHeadingBetween(object) >= minDegree && getHeadingBetween(object) <= maxDegree && (object.getGameObjectType() == ObjectTypes.FOOD || object.getGameObjectType() == ObjectTypes.SUPERFOOD))
                .sorted(Comparator.comparing(object -> getDistanceBetween(bot, object)))
                .collect(Collectors.toList());

        if (objectInArea.size() > 1) {
            double distance1 = getDistanceBetween(bot, objectInArea.get(0));
            double distance2 = getDistanceBetween(bot, objectInArea.get(1));

            if (distance1 == distance2) {
                var objectEqualDistance = objectInArea.stream()
                        .filter(object -> getDistanceBetween(bot, object) == distance1)
                        .sorted(Comparator.comparing(object -> getHeadingBetween(object)))
                        .collect(Collectors.toList());

                return getHeadingBetween(objectEqualDistance.get(0));
            }
            else {
                return getHeadingBetween(objectInArea.get(0));
            }
        }
        else return getHeadingBetween(objectInArea.get(0));
    }

    public Integer getBestFoodHorde(int area) {
        int minDegree = area * 30;
        int maxDegree = minDegree + 29;

        var foodList = gameState.getGameObjects()
                .stream().filter(item -> getHeadingBetween(item) >= minDegree && getHeadingBetween(item) <= maxDegree &&
                        (item.getGameObjectType() == ObjectTypes.FOOD
                                || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        List<Map.Entry<GameObject, Double>> bestFoodList = new ArrayList<>();

        int nFoodInArea = foodList.size();

        for (int i = 0; i < nFoodInArea; i++) {
            GameObject currentFood = foodList.get(i);

            var temp = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD ||
                            item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(currentFood, item)))
                    .collect(Collectors.toList());

            double oneOverDistances = 0;

            for (int j = 0; j < 20; j++) {
                double distances = getDistanceBetween(currentFood, temp.get(j));
                oneOverDistances += Math.pow(distances, -2) * 1000000;
            }

            if (currentFood.getGameObjectType() == ObjectTypes.SUPERFOOD) {
                bestFoodList.add(new AbstractMap.SimpleEntry<>(currentFood,
                        oneOverDistances * Math.pow(getDistanceBetween(currentFood, bot), -1.05)
                                * 10));
            } else {
                bestFoodList.add(new AbstractMap.SimpleEntry<>(currentFood,
                        oneOverDistances * Math.pow(getDistanceBetween(currentFood, bot), -1.05)));
            }
        }

        var sortedBestFood = bestFoodList
                .stream().sorted(Comparator.comparing(item -> item.getValue()))
                .collect(Collectors.toList());

        if (sortedBestFood.size() > 1) {
            double weight1 = sortedBestFood.get(0).getValue();
            double weight2 = sortedBestFood.get(1).getValue();

            if (weight1 == weight2) {
                var objectEqualDistance = sortedBestFood.stream()
                        .filter(object -> object.getValue() == weight1)
                        .sorted(Comparator.comparing(object -> getHeadingBetween(object.getKey())))
                        .collect(Collectors.toList());

                return getHeadingBetween(objectEqualDistance.get(0).getKey());
            } else {
                return getHeadingBetween(sortedBestFood.get(0).getKey());
            }
        }
        else return getHeadingBetween(sortedBestFood.get(0).getKey());
    }

    public PlayerAction deathUponYou(PlayerAction playerAction) {

        GameObject bestPrey = getUrgentPrey();
        double distance = getDistanceBetween(bestPrey, this.bot);



        if(bestPrey != this.bot && (this.bot.getSize() - bestPrey.getSize() > 5 ||
                this.bot.getSize() - bestPrey.getSize() > -60 && distance < 70)){

            if(normalShot){
                playerAction.heading = getHeadingBetween(bestPrey);
                normalShot = false;
            } else {
                playerAction.heading = getFutureHeading(bestPrey);
                normalShot = true;
            }
            playerAction.action = PlayerActions.FIRETORPEDOES;
            System.out.println("\"death upon you\" called.");
        }

        justFired = true;
        firedTarget = bestPrey;


        return playerAction;
    }

    private int getFutureHeading(GameObject otherObject){
        double futureConstant = otherObject.getSpeed() * getDistanceBetween(this.bot, otherObject) / 60;

        double futureX = Math.cos(otherObject.currentHeading) * futureConstant;
        double futureY = Math.sin(otherObject.currentHeading) * futureConstant;

        var direction = toDegrees(Math.atan2(otherObject.getPosition().y + futureY - bot.getPosition().y,
                otherObject.getPosition().x + futureX - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    public GameObject getSpamablePrey(){

        var preyList = gameState.getPlayerGameObjects()
                .stream().filter(item -> item != this.bot).sorted(Comparator
                        .comparing(item -> Math.pow(item.getSpeed(), 1.2)
                                * Math.pow(getDistanceBetween(bot, item), 0.5)))
                .collect(Collectors.toList());

        return preyList.get(0);
    }
    public GameObject getUrgentPrey(){
        GameObject bestPrey = getNearestShip();
        double bestPreyDistance = getHeadingBetween(bestPrey);
        if(bestPreyDistance - this.bot.getSize() < Math.pow(bestPreyDistance,0.5) * 60 / bestPrey.getSpeed()){
            return bestPrey;
        } else {
            return this.bot;
        }
    }

    public GameObject getNearestShip(){

        var shipList = gameState.getPlayerGameObjects()
                .stream().sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        if(gameState.getPlayerGameObjects().size() > 1) { return shipList.get(1); } else { return shipList.get(0); }

    }

    public PlayerAction greedyArentYou(PlayerAction playerAction) {
        var shipList = gameState.getPlayerGameObjects();
        double avgSpd = 0;
        for(int i = 0; i < shipList.size(); i++){
            avgSpd += shipList.get(i).getSpeed();
        }

        avgSpd *= 1/ shipList.size();

        GameObject bestPrey = getSpamablePrey();

        if(bestPrey.getSpeed() < avgSpd * 3 / 2 && this.bot.getSize() > 30) {
            if(normalShot){
                playerAction.heading = getHeadingBetween(bestPrey);
                normalShot = false;
            } else {
                playerAction.heading = getFutureHeading(bestPrey);
                normalShot = true;
            }
            playerAction.action = PlayerActions.FIRETORPEDOES;

            justFired = true;
            firedTarget = bestPrey;
            System.out.println("\"greedy arent you\" called.");

        }


        return playerAction;
    }
//    private GameObject NearestObject() {
//        var objectList = gameState.getGameObjects()
//                .stream().sorted(Comparator
//                        .comparing(obj -> getDistanceBetween(bot, obj)))
//                .collect(Collectors.toList());
//    }
//    private GameObject NearestFood() {
//        var foodList = gameState.getGameObjects()
//                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
//                .sorted(Comparator
//                        .comparing(item -> getDistanceBetween(bot, item)))
//                .collect(Collectors.toList());
//
//        return foodList.get(0);
//    }

//    private GameObject NearestSuperFood() {
//        var superFoodList = gameState.getGameObjects()
//                .stream().filter(sf -> sf.getGameObjectType() == ObjectTypes.SUPERFOOD)
//                .sorted(Comparator
//                        .comparing(sf -> getDistanceBetween(bot, sf)))
//                .collect(Collectors.toList());
//    }
//    private GameObject NearestPlayer() {
//        var playerList = gameState.getPlayerGameObjects()
//                .stream()
//                .sorted(Comparator
//                        .comparing(player -> getDistanceBetween(bot, player)))
//                .collect(Collectors.toList());
//
//        if (playerList.size() > 0) {
//            return playerList.get(1);
//        }
//        return null;
//    }

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
