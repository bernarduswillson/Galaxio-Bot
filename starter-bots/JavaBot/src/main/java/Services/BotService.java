package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    static int kabur = 0;
    static int shoot = 0;
    static int center = 0;
    static int shoottele = 0;
    static int detonate = 0;
    static int tick = 0;
    static int ticksekarang = 0;
    static int activateshield = 0;
    static int firetele = 0;
    static int teleactivein = 0;
    
    

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
        if (this.gameState.world.getCurrentTick() != null) {
            tick = this.gameState.world.getCurrentTick();
            int count = 0;
            int botX = this.bot.position.x;
            int botY = this.bot.position.y;
            double distanceFromWorldCenter = Math.sqrt(Math.pow((botX - 0), 2) + Math.pow((botY - 0), 2));
            
            System.out.println("tick size " + tick + ": " + this.bot.size);

            //avoid gas clouds
            // var gasCloud = gameState.getGameObjects().stream()
            //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.GAS_CLOUD)
            //         .filter(gameObject -> getDistanceBetween(gameObject, bot) < 100)
            //         .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, bot)))
            //         .orElse(null);
            // if (gasCloud != null && count == 0 && this.bot.size < 50) {
            //     playerAction.action = PlayerActions.FORWARD;
            //     playerAction.heading = getHeadingBetween(gasCloud) + 90;
            //     System.out.println("tick " + tick + ":gas");
            //     count = 1;
            // }
            
            var enemy = gameState.getPlayerGameObjects().stream()
                    //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
                    .filter(bot -> bot.id != this.bot.id)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 1000)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .filter(bot -> bot.size*1.2 < this.bot.size)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    .orElse(null);

            if (activateshield >= 1) {
                playerAction.action = PlayerActions.ACTIVATESHIELD;
                System.out.println("tick " + tick + ": shield");
                count = 1;
                activateshield++;
                if (activateshield == 5) {
                    activateshield = 0;
                }
            }

            if (detonate >= 1) {
                playerAction.action = PlayerActions.TELEPORT;
                System.out.println("tick " + tick + ": detonate");
                count = 1;
                detonate++;
                if (detonate == 5) {
                    detonate = 0;
                }
            }

            if (firetele >= 1) {
                playerAction.action = PlayerActions.FIRETELEPORT;
                System.out.println("tick " + tick + ": fire tele");
                count = 1;
                firetele++;
                if (firetele >= 2) {
                    firetele = 0;
                }
            }

            //detonate teleporter
            if (ticksekarang + teleactivein == this.gameState.world.getCurrentTick() && shoottele >= 1) {
                playerAction.action = PlayerActions.TELEPORT;
                System.out.println("tick " + tick + ": detonate");
                count = 1;
                shoottele = 0;
                detonate++;
            }


            // nembak musuh
            var enemyshoot = gameState.getPlayerGameObjects().stream()
                    //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
                    .filter(bot -> bot.id != this.bot.id)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 1000)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .filter(bot -> bot.size > this.bot.size*0.9)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    .orElse(null);

            

                    if (enemyshoot != null && getDistanceBetween(enemyshoot, this.bot) < 500 + this.bot.size + enemyshoot.size && count == 0 && this.bot.size > 30 && shoottele == 0) {
                        // var isObject = gameState.getGameObjects().stream()
                        //         .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.WORMHOLE || gameObject.gameObjectType == ObjectTypes.ASTEROID_FIELD || gameObject.gameObjectType == ObjectTypes.GAS_CLOUD)
                        //         //.filter(bot -> getDistanceBetween(bot, this.bot) < 1000)
                        //         .min(Comparator.comparing(item -> getDistanceBetween(item, this.bot) - this.bot.size - item.size < getDistanceBetween(enemyshoot, this.bot) - this.bot.size - enemyshoot.size))
                        //         //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                        //         //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                        //         .orElse(null);
                        // if ((getHeadingBetween(enemyshoot) >= getHeadingBetween(isObject) - 25 && getHeadingBetween(enemyshoot) <= getHeadingBetween(isObject) + 25) && getDistanceBetween(isObject, this.bot) - this.bot.size < 100) {
                        //     playerAction.heading = getHeadingBetween(enemyshoot)+90;
                        //     playerAction.action = PlayerActions.FORWARD;
                        //     System.out.println("tick " + tick + ": gajaditembak");
                        //     count = 1;
                        // }
                        // else {
                            System.out.println("tick distance " + tick + ": " + getDistanceBetween(enemyshoot, this.bot));
                            if (shoot == 0 || shoot == 1) {
                                playerAction.heading = getHeadingBetween(enemyshoot);
                                playerAction.action = PlayerActions.FIRETORPEDOES;
                                System.out.println("tick " + tick + ": tembak");
                                count = 1;
                                shoot++;
                            }
                            else if (shoot >= 2) {
                                shoot = 0;
                            }
                        
                        
                    }
                    
                    var enemy2 = gameState.getPlayerGameObjects().stream()
                    //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
                            .filter(bot -> bot.id != this.bot.id)
                            // .filter(bot -> getDistanceBetween(bot, this.bot) < 100)
                            .filter(bot -> bot.size*1.1 > this.bot.size)
                            .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                            //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                            .orElse(null);
                            
                    //if distance from world center is greater than world radius, go to center
                    if (count == 0) {
                        double worldRadius = this.gameState.world.radius;
                        if (distanceFromWorldCenter*1.2 + (1.7*this.bot.size) > worldRadius) {
                            if (enemy2 != null && getDistanceBetween(enemy2, this.bot) < 200 + this.bot.size + enemy2.size) {
                                playerAction.heading = getHeadingBetween(enemy2)+90;
                                playerAction.action = PlayerActions.FORWARD;
                                System.out.println("tick " + tick + ": kejepit");
                                count = 1;
                                // if ((enemy2.currentHeading >= 0 && enemy2.currentHeading <= 90) || (enemy2.currentHeading >= 180 && enemy2.currentHeading <= 270)) {
                                //     playerAction.heading = getHeadingBetween(enemy2) - 90;
                                //     playerAction.action = PlayerActions.FORWARD;
                                //     System.out.println("tick " + tick + ": kejepit kiri");
                                //     count = 1;
                                // }
                                // else {
                                //     playerAction.heading = getHeadingBetween(enemy2) + 90;
                                //     playerAction.action = PlayerActions.FORWARD;
                                //     System.out.println("tick " + tick + ": kejepit kanan");
                                //     count = 1;
                                // }
                            }
                            else {
                                if (center == 0 || center == 1) {
                                    playerAction.heading = getHeadingToCenter();
                                    playerAction.action = PlayerActions.FORWARD;
                                    System.out.println("tick " + tick + ": center");
                                    count = 1;
                                    center++;
                                }
                                else if (center >= 2) {
                                    center = 0;
                                }
                            }
                        }
                    }
                    
            
                    
                    // ngindarin musuh
                if (enemy2 != null && getDistanceBetween(enemy2, this.bot) < 200 + this.bot.size + bot.size && count == 0) {
                    // var enemy3 = gameState.getPlayerGameObjects().stream()
                    //             //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
                    //             .filter(bot -> bot.id != this.bot.id)
                    //             // .filter(bot -> getDistanceBetween(bot, this.bot) < 100)
                    //             .filter(bot -> getDistanceBetween((bot), this.bot)> getDistanceBetween(enemy2, this.bot))
                    //             .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot)))
                    //             .filter(bot -> bot.size*1.2 >= this.bot.size)
                    //             //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    //             .orElse(null);
                    // if (tick < 100) {
                        // playerAction.heading = getHeadingBetween(enemy2)+180;
                        // playerAction.action = PlayerActions.FORWARD;
                        // System.out.println("tick " + tick + ": kabur");
                        // count = 1;
                        if (kabur == 0 || kabur == 1) {
                            playerAction.heading = getHeadingBetween(enemy2)+180;
                            playerAction.action = PlayerActions.FORWARD;
                            System.out.println("tick " + tick + ": kabur");
                            count = 1;
                            shoot++;
                        }
                        else if (kabur == 2) {
                            kabur = 0;
                        }
                    // }
                    // else if (tick >= 100) {
                        // double worldRadius = this.gameState.world.radius;
                        // if (distanceFromWorldCenter*1.2 + (1.7*this.bot.size) < worldRadius && tick < 10) {
                            // playerAction.heading = getHeadingBetween(enemy2)+180;
                            // playerAction.action = PlayerActions.FORWARD;
                            // System.out.println("tick " + tick + ": kabur");
                            // count = 1;
                            // playerAction.heading = getHeadingBetween(enemy2)+90;
                            // playerAction.action = PlayerActions.FORWARD;
                            // System.out.println("tick " + tick + ": kejepit");
                            // count = 1;
                        // }
                        // else if (enemy3!=null && getDistanceBetween(enemy3, this.bot) < 3*enemy3.size) {
                        //     playerAction.heading = ((getHeadingBetween(enemy3)+getHeadingBetween(enemy2))%360)/2;
                        //     playerAction.action = PlayerActions.FORWARD;
                        //     System.out.println("tick " + tick + ": kabur dari 2");
                        //     count = 1;
                        // }
                    // }
                }

            
            var enemytele = gameState.getPlayerGameObjects().stream()
                    //.filter(gameObject -> gameObject.gameObjectType == ObjectTypes.PLAYER)
                    .filter(bot -> bot.id != this.bot.id)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 1000)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .filter(bot -> bot.size*1.05 + 40 < this.bot.size)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    //.filter(bot -> getDistanceBetween(bot, this.bot) < 200)
                    .orElse(null);
            
            // ngejar musuh
                    if (enemytele != null && getDistanceBetween(enemy, this.bot) < 300 + this.bot.size + bot.size && this.bot.size > 100 && count == 0 && shoottele == 0) {
                        playerAction.heading = getHeadingBetween(enemy);
                        playerAction.action = PlayerActions.FIRETELEPORT;
                        System.out.println("tick " + tick + ": tembak tele");
                        ticksekarang = this.gameState.world.getCurrentTick();
                        shoottele++;
                        firetele++;
                        activateshield = 1;
                        teleactivein = (int)(getDistanceBetween(enemy, this.bot)-(enemy.size*0.5)-this.bot.size)/20;
                        count = 1;
                        // playerAction.heading = getHeadingBetween(enemy);
                        // playerAction.action = PlayerActions.FORWARD;
                        // System.out.println("tick " + tick + ": attack");
                        // count = 1;
                    }
                    else if (enemy != null && getDistanceBetween(enemy, this.bot) < 300 && count == 0) {
                        playerAction.heading = getHeadingBetween(enemy);
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                        System.out.println("tick " + tick + ": attack");
                        count = 1;
                    }
                    

            //get X and Y of bot
        

            //get X and Y of world center
            // double worldCenterX = this.gameState.world.radius;
            // double worldCenterY = this.gameState.world.radius;

            //get distance from world center
            
            // double worldRadius = this.gameState.world.radius;
            // var food = gameState.getGameObjects().stream()
            //     .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.FOOD)
            //     .filter(gameObject -> getDistanceBetween(gameObject, this.bot) - this.bot.size + distanceFromWorldCenter * 1.2 < worldRadius)
            //     // .filter(gameObject -> ((this.bot.currentHeading + getHeadingBetween(gameObject))%360 <= (this.bot.currentHeading + 45)%360 || (this.bot.currentHeading - getHeadingBetween(gameObject))%360 >= (this.bot.currentHeading - 45)%360))
            //     .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, this.bot) - this.bot.size))
            //     .orElse(null);

            // // go to food fix
            // if (food != null && count == 0) {
            //     playerAction.action = PlayerActions.FORWARD;
            //     playerAction.heading = getHeadingBetween(food);
            //     System.out.println("tick " + tick + ": food");
            //     count = 1;
            // }

            if (count == 0) {
                var foodList = gameState.getGameObjects()
                        .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                        .sorted(Comparator
                                .comparing(item -> getDistanceBetween(bot, item)))
                        .collect(Collectors.toList());

                playerAction.heading = getHeadingBetween(foodList.get(0));
                playerAction.action = PlayerActions.FORWARD;
                System.out.println("tick " + tick + ": food");
                count = 1;
            }

            
            
            
            
            
            
            
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

                // if (food != null && tick < 20) {
                
                //     playerAction.action = PlayerActions.FORWARD;
                //     playerAction.heading = getHeadingBetween(food);
                // }
                // else if (tick >= 20) {
                //     playerAction.action = PlayerActions.FORWARD;
                //     playerAction.heading = getHeadingBetween(food) + 180;
                // }
            // }
            
            this.playerAction = playerAction;
        }
    }

    // public boolean isInSight(GameObject gameObject) {
    //     if (this.bot.currentHeading >= 45 && this.bot.currentHeading <= 315) {
    //         if (getHeadingBetween(gameObject) >= (this.bot.currentHeading - 45)%360 && getHeadingBetween(gameObject) <= (this.bot.currentHeading + 45)%360) {
    //             return true;
    //         }
    //     }
    //     else if (this.bot.currentHeading < 45) {
    //         if ((getHeadingBetween(gameObject) <= (this.bot.currentHeading + 45) && getHeadingBetween(gameObject) >= 0) || (315 + this.bot.currentHeading <= 360)) {
    //             return true;
                
    //         }
    //     }
    //     else if (this.bot.currentHeading > 315) {
    //         if ((getHeadingBetween(gameObject) >= (this.bot.currentHeading - 45) && getHeadingBetween(gameObject) <= 360) || (45 - this.bot.currentHeading >= 0)) {
    //             return true;
  
    //         }
    //     }
    //     return false;
    // }

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