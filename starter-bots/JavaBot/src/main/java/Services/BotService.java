package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    static int index = 1;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        int count = 0;
        //get X and Y of bot
        int botX = this.bot.position.x;
        int botY = this.bot.position.y;

        //get X and Y of world center
        // double worldCenterX = this.gameState.world.radius;
        // double worldCenterY = this.gameState.world.radius;

        //get distance from world center
        double distanceFromWorldCenter = Math.sqrt(Math.pow((botX - 0), 2) + Math.pow((botY - 0), 2));

        //if distance from world center is greater than world radius, go to center
        if (index > 10) {
            double worldRadius = this.gameState.world.radius;
            if(distanceFromWorldCenter + (1.5*this.bot.size)>worldRadius) {
            
                playerAction.heading = getHeadingToCenter();
                playerAction.action = PlayerActions.FORWARD;
                System.out.println("tick " + index + ": center");
                count = 1;
            }
        }

        // ngejar musuh
        // var enemy = gameState.getPlayerGameObjects().stream()
        //         //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
        //         .filter(bot -> bot.id != this.bot.id)
        //         //.filter(bot -> getDistanceBetween(bot, this.bot) < 1000)
        //         .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot)))
        //         .filter(bot -> bot.size< this.bot.size)
        //         //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
        //         //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
        //         .orElse(null);

        // if (enemy != null && getDistanceBetween(enemy, this.bot) < 500) {
        //     playerAction.heading = getHeadingBetween(enemy);
        //     playerAction.action = PlayerActions.FORWARD;
        //     System.out.println("attack");
        // }

        // ngindarin musuh

        var enemy2 = gameState.getPlayerGameObjects().stream()
                //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
                .filter(bot -> bot.id != this.bot.id)
                // .filter(bot -> getDistanceBetween(bot, this.bot) < 100)
                .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot)))
                .filter(bot -> bot.size > this.bot.size)
                //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                .orElse(null);

        if (enemy2 != null && getDistanceBetween(enemy2, this.bot) < 100 && count == 0) {
            playerAction.heading = getHeadingBetween(enemy2)+180;
            playerAction.action = PlayerActions.FORWARD;
            System.out.println("tick " + index + ": kabur");
        }

        
        // go to food fix
        else {
            var food = gameState.getGameObjects().stream()
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.FOOD)
                    //.filter(gameObject -> getHeadingBetween(gameObject) > this.bot.currentHeading - 60 && getHeadingBetween(gameObject) < this.bot.currentHeading + 60)
                    .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
                    .orElse(null);
            if (food != null && count == 0) {
                playerAction.action = PlayerActions.FORWARD;
                playerAction.heading = getHeadingBetween(food);
                System.out.println("tick " + index + ": food");
            }
        }

        //avoid gas clouds
        // var gasCloud = gameState.getGameObjects().stream()
        //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.GAS_CLOUD)
        //         .filter(gameObject -> getDistanceBetween(gameObject, bot) < 50)
        //         .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
        //         .orElse(null);
        // if (gasCloud != null) {
        //     playerAction.action = PlayerActions.FORWARD;
        //     playerAction.heading = getHeadingBetween(gasCloud) + 180;
        //     System.out.println("gas");
        // }

        
        

        // var enemy = gameState.getGameObjects().stream()
        //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
        //         .filter(gameObject -> gameObject.id != bot.id)
        //         .filter(gameObject -> getDistanceBetween(gameObject, bot) < gameObject.size*2)
        //         .filter(gameObject -> gameObject.size * 1.1 < bot.size)
        //         .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
        //         .orElse(null);

        // if (enemy != null) {
        //     playerAction.heading = getHeadingBetween(enemy)+180;
        //     playerAction.action = PlayerActions.FORWARD;
        // }

        // var enemy = gameState.getGameObjects().stream()
        //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
        //         .filter(gameObject -> gameObject.id != bot.id)
        //         .filter(gameObject -> getDistanceBetween(gameObject, bot) < gameObject.size*2)
        //         .filter(gameObject -> gameObject.size * 1.1 < bot.size)
        //         .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
        //         .orElse(null);
        
        // if (enemy != null) {
        //     playerAction.heading = getHeadingBetween(enemy)+180;
        //     playerAction.action = PlayerActions.FORWARD;
        // }
        // var enemy2 = gameState.getGameObjects().stream()
        //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
        //         .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
        //         .filter(gameObject -> gameObject.id != bot.id)
        //         .filter(gameObject -> getDistanceBetween(gameObject, bot) < 100)
        //         .filter(gameObject -> gameObject.size > bot.size * 1.2)
        //         .orElse(null);
        // if (enemy2 != null) {
        //     playerAction.heading = getOppositeDirection(bot, enemy2);
        //     playerAction.action = PlayerActions.FORWARD;
        // }

        // else if (gameState.getGameObjects().stream()
        //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.GAS_CLOUD)
        //         .anyMatch(gameObject -> getDistanceBetween(gameObject, bot) < 50)) {
        //     playerAction.action = PlayerActions.FORWARD;
        //     playerAction.heading = bot.currentHeading + 180;
        // }
        
        // else if (enemy2 == null) {
            // var food = gameState.getGameObjects().stream()
            //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.WORMHOLE)
            //         .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
            //         .orElse(null);

            // if (food != null && index < 20) {
            ;
            //     playerAction.action = PlayerActions.FORWARD;
            //     playerAction.heading = getHeadingBetween(food);
            // }
            // else if (index >= 20) {
            //     playerAction.action = PlayerActions.FORWARD;
            //     playerAction.heading = getHeadingBetween(food) + 180;
            // }
        // }
        
        index++;
        this.playerAction = playerAction;
    }

    //direction to center
    

    private int getOppositeDirection(GameObject gameObject1, GameObject gameObject2) {
        return (int) Math.toDegrees(Math.atan2(gameObject2.getPosition().getY() - gameObject1.getPosition().getY(),
                gameObject2.getPosition().getX() - gameObject1.getPosition().getX()));
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

    private int getHeadingToCenter() {
        var direction = toDegrees(Math.atan2(0 - bot.getPosition().y,
                0 - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}
