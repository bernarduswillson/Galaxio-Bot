package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    static int detonate = 0;
    static int currentTick = 0;
    static int teleporter = 1;
    static int shieldOn = 0;
    static int prevsize = 0;
    static int tickX = 1;
    static int activateShield = 0;
    static int teleTick = 0;
    static int teleActiveIn = 0;
    static int teleWasShot = 0;
    static int closeFire = 0;
    static int longFire = 0;
    static int center = 0;
    static int runAway = 0;
    static int chase = 0;
    static int attack = 0;
    static int squeezeL = 0;
    static int squeezeR = 0;
    static int runAwayF = 0;
    static int fixDetonate = 0;
    static int supernovaTick = 0;
    static int supernovaActiveIn = 0;
    static int supernovaWasShot = 0;
    static int isAbortD = 0;
    static int isAbortO = 0;

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
        if (this.gameState.world.getCurrentTick() != null && this.gameState.world.getCurrentTick() != 0 && this.gameState.world.getCurrentTick() > tickX) {
            currentTick = this.gameState.world.getCurrentTick();
            tickX = currentTick;
            double worldRadius = this.gameState.world.radius;

            //scan
            int headingPref = scanObject();

            // prevent action called twice
            int isAction = 0;

            //closest enemy var
            var closestEnemy = gameState.getPlayerGameObjects().stream()
                    .filter(bot -> bot.id != this.bot.id)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .orElse(null);
            
            //print info
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("TICK : " + currentTick);
            System.out.println("Current World Radius : " + this.gameState.world.getRadius());
            System.out.println("---------------------------");
            System.out.println("Bot Position : " + this.bot.position.x + " " + this.bot.position.y);
            System.out.println("Bot Size : " + this.bot.size);
            System.out.println("Bot Ammo: " + this.bot.torpedoSalvoCount);
            System.out.println("Bot Teleporter Ammo: " + this.bot.teleporterCount);
            System.out.println("Bot Shield: " + this.bot.shieldCount);
            System.out.println("");
            System.out.println("Closest Enemy Distance: " + getDistanceBetween(closestEnemy, this.bot));
            System.out.println("Closest Enemy Size: " + closestEnemy.size);
            System.out.println("");
            System.out.println("Action: ");


            //get this bot distance from world center
            int botX = this.bot.position.x;
            int botY = this.bot.position.y;
            double distanceFromWorldCenter = Math.sqrt(Math.pow((botX - 0), 2) + Math.pow((botY - 0), 2));


            //VARIABLES
            // is enemy smaller with no margin, for detonate
            var isEnemySmaller = gameState.getPlayerGameObjects().stream()
                    .filter(bot -> bot.id != this.bot.id)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .filter(bot -> bot.size < this.bot.size)
                    .orElse(null);
                    
            // is enemy smaller with margin, for chasing and offensive shooting
            var isEnemySmaller2 = gameState.getPlayerGameObjects().stream()
                    .filter(bot -> bot.id != this.bot.id)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .filter(bot -> bot.size*1.2 < this.bot.size)
                    .orElse(null);

            // is enemy bigger with margin, for defensive shooting
            var isEnemyBigger = gameState.getPlayerGameObjects().stream()
                    .filter(bot -> bot.id != this.bot.id)
                    .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .filter(bot -> bot.size > this.bot.size*0.9)
                    .orElse(null);
            
            // is enemy bigger with margin, for running away
            var isEnemyBigger2 = gameState.getPlayerGameObjects().stream()
            .filter(bot -> bot.id != this.bot.id)
            .filter(bot -> bot.size*1.1 > this.bot.size)
            .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
            .orElse(null);
            
            // is enemy smaller with margin, and far, for teleporter
            var isEnemySmaller3 = gameState.getPlayerGameObjects().stream()
            .filter(bot -> bot.id != this.bot.id)
            .min(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
            .filter(bot -> bot.size*1.1 + 40 < this.bot.size)
            .orElse(null);

            // get biggest enemy size
            var isEnemyBiggest = gameState.getPlayerGameObjects().stream()
                    .filter(bot -> bot.id != this.bot.id)
                    .filter(bot -> bot.size*1.1 > this.bot.size)
                    .max(Comparator.comparing(bot -> getDistanceBetween(bot, this.bot) - bot.size - this.bot.size))
                    .orElse(null);
            
            // prevent bug
            var closestFood = gameState.getGameObjects().stream()
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.FOOD || gameObject.gameObjectType == ObjectTypes.SUPERFOOD)
                    .min(Comparator.comparing(gameObject -> getDistanceBetween(gameObject, this.bot)))
                    .orElse(null);
            
            // get food heading based on scanning
            var scanFood = gameState.getGameObjects().stream()
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.FOOD || gameObject.gameObjectType == ObjectTypes.SUPERFOOD)
                    .filter(gameObject -> (isInSight(headingPref, gameObject, 30)))
                    .collect(Collectors.toList());

            // get torpedoes heading to this bot
            var enemyTorpedoes = gameState.getGameObjects().stream()
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.TORPEDOSALVO)
                    .filter(gameObject -> isEnemyTorpedo(gameObject) == true)
                    .filter(gameObject -> getDistanceBetween(gameObject, this.bot) < 100 + this.bot.size)
                    .sorted(Comparator
                            .comparing(gameObject -> getDistanceBetween(this.bot, gameObject)))
                    .collect(Collectors.toList());  

            // check if there is an object that can block shooting (offensive)
            var isObjectInFrontO = gameState.getGameObjects().stream()
                .filter(gameObject -> isEnemySmaller2 != null)
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.WORMHOLE || gameObject.gameObjectType == ObjectTypes.ASTEROIDFIELD || gameObject.gameObjectType == ObjectTypes.GASCLOUD)
                    .filter(gameObject -> (isInSight(getHeadingBetween(isEnemySmaller2), gameObject, 30)))
                    .collect(Collectors.toList());  

            // check if there is an object that can block shooting (defensive)
            var isObjectInFrontD = gameState.getGameObjects().stream()
                    .filter(gameObject -> isEnemyBigger != null)
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.WORMHOLE || gameObject.gameObjectType == ObjectTypes.ASTEROIDFIELD || gameObject.gameObjectType == ObjectTypes.GASCLOUD)
                    .filter(gameObject -> (isInSight(getHeadingBetween(isEnemyBigger), gameObject, 30)))
                    .collect(Collectors.toList());  

            // check if there is an object that can block shooting (kamikaze)
            var isObjectInFrontK = gameState.getGameObjects().stream()
                    .filter(gameObject -> closestEnemy != null)
                    .filter(gameObject -> gameObject.gameObjectType == ObjectTypes.WORMHOLE || gameObject.gameObjectType == ObjectTypes.ASTEROIDFIELD || gameObject.gameObjectType == ObjectTypes.GASCLOUD)
                    .filter(gameObject -> (isInSight(getHeadingBetween(closestEnemy), gameObject, 30)))
                    .collect(Collectors.toList());  


            // BUG FIX
            // prevent detonate bug
            if (fixDetonate >= 1) {
                playerAction.action = PlayerActions.TELEPORT;
                System.out.println("DETONATE TELEPORTER");
                fixDetonate = 0;
                isAction = 1;
            }
            
            // cannot call shield when shield is active
            if (shieldOn >= 1) {
                shieldOn++;
                if (
                shieldOn > 20
                ) {
                    shieldOn = 0;
                }
            }
            
            // detonate supernova
            if (
            supernovaTick + supernovaActiveIn == currentTick && 
            supernovaWasShot >= 1 && 
            isAction == 0
            ) {
                playerAction.action = PlayerActions.DETONATESUPERNOVA;
                System.out.println("DETONATE SUPERNOVA");
                isAction = 1;
                supernovaWasShot = 0;
            }

            //AFTER TELE ACTION
            // activate shield after shooting teleporter
            if (
            activateShield >= 1 && 
            isAction == 0
            ) {
                playerAction.action = PlayerActions.ACTIVATESHIELD;
                System.out.println("OFFENSIVE SHIELD");
                activateShield = 0;
                shieldOn = 1;
                isAction = 1;
            }

            // detonate teleporter when the enemy is smaller
            if (
            teleTick + teleActiveIn == currentTick && 
            isEnemySmaller != null &&
            teleWasShot >= 1 && 
            isAction == 0
            ) {
                playerAction.action = PlayerActions.TELEPORT;
                System.out.println("DETONATE TELEPORTER");
                isAction = 1;
                teleWasShot = 0;
                fixDetonate++;
            }
            else if (
            teleTick + teleActiveIn == currentTick && 
            isEnemySmaller == null &&
            teleWasShot >= 1  && 
            isAction == 0
            ) {
                System.out.println("CANCEL DETONATE");
                teleWasShot = 0;
            }


            //GREEDY ALGORITHM BASED ON PRIORITIES
            //1. Shoot supernova when enemy is the biggest and far away
            if (
            isEnemyBiggest != null && 
            getDistanceBetween(isEnemyBiggest, this.bot) > (0.5*this.gameState.world.radius) + this.bot.size + bot.size && 
            this.bot.supernovaAvailable == 1 && 
            isAction == 0
            ) {
                playerAction.heading = getHeadingBetween(isEnemyBiggest);
                playerAction.action = PlayerActions.FIRESUPERNOVA;
                System.out.println("FIRE SUPERNOVA");
                supernovaTick = this.gameState.world.getCurrentTick();
                // predict when to detonate
                supernovaActiveIn = (int)(getDistanceBetween(isEnemyBiggest, this.bot)-(isEnemyBiggest.size*0.5)-this.bot.size)/20;
                supernovaWasShot++;
                isAction = 1;
            }

            //2. Shield when enemytorpedo is close
            if (
            this.bot.size > 50 &&
            enemyTorpedoes.size() > 1 &&
            shieldOn == 0 &&
            isAction == 0
            ) {
                playerAction.action = PlayerActions.ACTIVATESHIELD;
                System.out.println("DEFENSIVE SHIELD");
                isAction = 1;
                shieldOn = 1;
            }

            //3. Shoot enemy when enemy is bigger, defensive shooting
            if (
            isEnemyBigger != null && 
            getDistanceBetween(isEnemyBigger, this.bot) < 350 + this.bot.size + isEnemyBigger.size && 
            this.bot.size > 30 && 
            this.bot.torpedoSalvoCount > 0 &&
            teleWasShot == 0 && 
            isAction == 0
            ) {
                double minDistance = 1000;
                if (isObjectInFrontD.size() > 0) {
                    for (int i = 0; i < isObjectInFrontD.size(); i++) {
                        if (getDistanceBetween(isObjectInFrontD.get(i), this.bot) < minDistance) {
                            minDistance = getDistanceBetween(isObjectInFrontD.get(i), this.bot);
                        }
                    }
                }
                // check if there is an object that can block shooting
                if (minDistance - this.bot.size < getDistanceBetween(isEnemyBigger, this.bot) - this.bot.size - isEnemyBigger.size) {
                    System.out.println("OBJECT IN FRONT, ABORT SHOOTING");
                    minDistance = 1000;
                }
                // high fire rate when enemy is close
                else if (getDistanceBetween(isEnemyBigger, this.bot) < 150 + this.bot.size + isEnemyBigger.size) {
                    if (closeFire == 0) {
                    playerAction.heading = getHeadingBetween(isEnemyBigger);
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                    System.out.println("CLOSE RANGE FIRE");
                    closeFire++;
                    isAction = 1;
                    }
                    else if (closeFire == 1) {
                        closeFire = 0;
                    }
                }
                // low fire rate when enemy is far
                else if (getDistanceBetween(isEnemyBigger, this.bot) <= 350 + this.bot.size + isEnemyBigger.size) {
                    if (longFire == 0) {
                        playerAction.heading = getHeadingBetween(isEnemyBigger);
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                        System.out.println("LONG RANGE FIRE");
                        isAction = 1;
                        longFire++;
                    }
                    else if (
                    longFire == 1 || 
                    longFire == 2 || 
                    longFire == 3
                    ) {
                        longFire++;
                    }
                    else if (longFire >= 4) {
                        longFire = 0;
                    }
                }
            }

            //4. Go to center when edge is near
            if (
            distanceFromWorldCenter*1.2 + (1.7*this.bot.size) > worldRadius &&
            isAction == 0
            ) {
                // if chased by enemy in the edge, escape
                if (
                isEnemyBigger2 != null && 
                getDistanceBetween(isEnemyBigger2, this.bot) < 200 + this.bot.size + isEnemyBigger2.size
                ) {
                    if (squeezeL == 0) {
                        playerAction.heading = getHeadingBetween(isEnemyBigger2)+90;
                        playerAction.action = PlayerActions.FORWARD;
                        System.out.println("SQUEEZED");
                        squeezeL++;
                        isAction = 1;
                    }
                    else if (squeezeL == 1) {
                        squeezeL = 0;
                    }
                }
                // if not chased, go to center
                else {
                    if (center == 0) {
                        playerAction.heading = getHeadingToCenter();
                        playerAction.action = PlayerActions.FORWARD;
                        System.out.println("GO TO CENTER");
                        center++;
                        isAction = 1;
                    }
                    else if (center == 1) {
                        center = 0;
                    }
                }
            }
                    
            //5. Escape if enemy is bigger and close
            if (
            isEnemyBigger2 != null && 
            getDistanceBetween(isEnemyBigger2, this.bot) < 200 + this.bot.size + bot.size && 
            isAction == 0
            ) {
                if (runAway == 0) {
                    playerAction.heading = getHeadingBetween(isEnemyBigger2)+180;
                    playerAction.action = PlayerActions.FORWARD;
                    System.out.println("RUN AWAY");
                    runAwayF++;
                    runAway++;
                    isAction = 1;
                }
                else if (runAway == 1) {
                    runAwayF++;
                    runAway = 0;
                }
            }
            
            //6. Chase when enemy is smaller and close
            if (
            isEnemySmaller2 != null && 
            getDistanceBetween(isEnemySmaller2, this.bot) < 100 + this.bot.size + bot.size &&
            teleWasShot == 0 &&
            isAction == 0
            ) {
                if (chase == 0) {
                    playerAction.heading = getHeadingBetween(isEnemySmaller2);
                    playerAction.action = PlayerActions.FORWARD;
                    System.out.println("CHASE");
                    chase++;
                    runAwayF++;
                    isAction = 1;
                }
                else if (chase == 1) {
                    runAwayF++;
                    chase = 0;
                }
            }

            //7. Shoot teleport when enemy is smaller and in range
            if (
            isEnemySmaller3 != null && 
            getDistanceBetween(isEnemySmaller3, this.bot) >= 150 + this.bot.size + bot.size && 
            getDistanceBetween(isEnemySmaller3, this.bot) < 500 + this.bot.size + bot.size && 
            this.bot.size > 100 && 
            this.bot.teleporterCount > 0 && 
            this.bot.shieldCount > 0 &&
            shieldOn == 0 &&
            teleWasShot == 0 && 
            isAction == 0
            ) {
                playerAction.heading = getHeadingBetween(isEnemySmaller3);
                playerAction.action = PlayerActions.FIRETELEPORT;
                System.out.println("FIRE TELEPORT");
                teleTick = this.gameState.world.getCurrentTick();
                // predict when to detonate
                teleActiveIn = (int)(getDistanceBetween(isEnemySmaller3, this.bot)-(isEnemySmaller3.size*0.5)-this.bot.size)/20;
                // torpedoes disabled until tele is detonated
                teleWasShot++;
                activateShield = 1;
                isAction = 1;
            }
            
            // 8. Shoot torpedo when enemy is smaller and close, offensive shooting
            if (
            isEnemySmaller2 != null && 
            getDistanceBetween(isEnemySmaller2, this.bot) < 150 + this.bot.size + bot.size && 
            this.bot.torpedoSalvoCount > 0 &&
            this.bot.size > 50 &&
            teleWasShot == 0 && 
            isAction == 0
            ) {
                double minDistance = 1000;
                if (isObjectInFrontO.size() > 0) {
                    for (int i = 0; i < isObjectInFrontO.size(); i++) {
                        if (getDistanceBetween(isObjectInFrontO.get(i), this.bot) < minDistance) {
                            minDistance = getDistanceBetween(isObjectInFrontO.get(i), this.bot);
                        }
                    }
                }
                // check if there is an object that can block shooting
                if (minDistance - this.bot.size < getDistanceBetween(isEnemySmaller2, this.bot) - this.bot.size - isEnemySmaller2.size) {
                    System.out.println("OBJECT IN FRONT, ABORT SHOOTING");
                    minDistance = 1000;
                }
                else {
                    if (attack == 0) {
                    playerAction.heading = getHeadingBetween(isEnemySmaller2);
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                    System.out.println("OFFENSIVE FIRE");
                    attack++;
                    isAction = 1;
                    }
                    else if (attack == 1) {
                        attack = 0;
                    }
                }
            }

            // 10. Get food while being chased or chasing
            if (
            closestFood != null &&
            runAwayF > 0 &&
            isAction == 0
            ) {
                playerAction.action = PlayerActions.FORWARD;
                System.out.println("FOOD WHILE BEING CHASED / CHASING");
                runAwayF = 0;
                isAction = 1;
            }
        
            //7. Finding best area to move to
            if (
            (scanFood != null && 
            this.gameState.world.getCurrentTick() < 700 &&
            prevsize != this.bot.size &&
            runAwayF == 0 &&
            isAction == 0)
            ) {
                double minDistanceF = 1000;
                int index = 0;
                if (scanFood.size() > 0) {
                    for (int i = 0; i < scanFood.size(); i++) {
                        if (getDistanceBetween(scanFood.get(i), this.bot) < minDistanceF) {
                            minDistanceF = getDistanceBetween(scanFood.get(i), this.bot);
                            index = i;
                        }
                    }
                    var closestScanFood = scanFood.get(index);
                    playerAction.heading = getHeadingBetween(closestScanFood);
                    playerAction.action = PlayerActions.FORWARD;
                    System.out.println("FOOD");
                    minDistanceF = 1000;
                    isAction = 1;
                }
            }

            //8. Kamikaze when there is no more food
            if (
            closestFood == null && 
            isAction == 0
            ) {
                double minDistance = 1000;
                if (isObjectInFrontK.size() > 0) {
                    for (int i = 0; i < isObjectInFrontK.size(); i++) {
                        if (getDistanceBetween(isObjectInFrontK.get(i), this.bot) < minDistance) {
                            minDistance = getDistanceBetween(isObjectInFrontK.get(i), this.bot);
                        }
                    }
                }
                // check if there is an object that can block shooting
                if (minDistance - this.bot.size < getDistanceBetween(closestEnemy, this.bot) - this.bot.size - closestEnemy.size) {
                    System.out.println("OBJECT IN FRONT, ABORT SHOOTING");
                    minDistance = 1000;
                }
                else {
                    playerAction.heading = getHeadingBetween(closestEnemy);
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                    System.out.println("KAMIKAZE");
                    isAction = 1;
                }
            }

            //9. Default food prevent bug under 5 ticks
            if (
            currentTick < 5 && 
            closestFood != null
            ) {
                playerAction.heading = getHeadingBetween(closestFood);
                playerAction.action = PlayerActions.FORWARD;
                System.out.println("DEFAULT FOOD");
                isAction = 1;
            }

            //10. Default action
            if (isAction == 0) {
                playerAction.action = PlayerActions.FORWARD;
                System.out.println("DEFAULT");
                isAction = 1;
            }
            
            prevsize = this.bot.size;
            this.playerAction = playerAction;
            System.out.println("Bot Heading: " + this.playerAction.getHeading());
        }
    }

    // function if object is in sight or no
    public boolean isInSight(int heading, GameObject gameObject, int vision) { 
        vision /= 2;
        if (heading >= vision && heading <= 360 - vision) {
            if (getHeadingBetween(gameObject) >= (heading - vision)%360 && getHeadingBetween(gameObject) <= (heading + vision)%360) {
                return true;
            }
        }
        else if (heading < vision) {
            if ((getHeadingBetween(gameObject) <= (heading + vision) && getHeadingBetween(gameObject) >= 0) || (360 - vision + heading <= getHeadingBetween(gameObject) && getHeadingBetween(gameObject) <= 360)) {
                return true;
            }
        }
        else if (heading > 360 - vision) {
            if ((getHeadingBetween(gameObject) >= (heading - vision) && getHeadingBetween(gameObject) <= 360) || (360 - vision + heading <= getHeadingBetween(gameObject) && getHeadingBetween(gameObject) <= 360)) {
                return true;
            }
        }
        return false;
    }

    // detect enemy torpedo
    public boolean isEnemyTorpedo(GameObject gameObject) {
        //get our position
        int x = this.bot.getPosition().x;
        int y = this.bot.getPosition().y;
        //get torpedo position
        int x2 = gameObject.getPosition().x;
        int y2 = gameObject.getPosition().y;
        //get torpedo heading
        int heading = gameObject.currentHeading;
        
        if (x2 > x && y2 > y) {
            if (heading  >= 180 && heading <= 270) {
                return true;
            }
        }
        else if (x2 < x && y2 > y) {
            if (heading >= 270 && heading <= 360) {
                return true;
            }
        }
        else if (x2 < x && y2 < y) {
            if (heading >= 0 && heading <= 90) {
                return true;
            }
        }
        else if (x2 > x && y2 < y) {
            if (heading >= 90 && heading <= 180) {
                return true;
            }
        }
        else if (x2 > x && y2 == y) {
            if (heading == 180) {
                return true;
            }
        }
        else if (x2 < x && y2 == y) {
            if (heading == 0) {
                return true;
            }
        }
        else if (x2 == x && y2 > y) {
            if (heading == 270) {
                return true;
            }
        }
        else if (x2 == x && y2 < y) {
            if (heading == 90) {
                return true;
            }
        }
        return false;
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

    //scan list of object
    private int scanObject() {
        int vision = 30;
        List<Double> scoringList = new ArrayList<Double>();
        for (int i = 0; i < 360/vision; i++) {
            int headingStart = i * vision;
            int headingEnd = headingStart + vision;
            double scoreSum = 0;

            //collect food list
            List<GameObject> foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .filter(item -> getDistanceBetween(this.bot, item) < (this.gameState.world.radius*0.5) + this.bot.size + item.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < foodList.size(); j++) {
                scoreSum += 10 / getDistanceBetween(this.bot, foodList.get(j));
            }

            //collect superfood list
            List<GameObject> superfoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .filter(item -> getDistanceBetween(this.bot, item) < (this.gameState.world.radius*0.5) + this.bot.size + item.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < superfoodList.size(); j++) {
                scoreSum += 15 / getDistanceBetween(this.bot, superfoodList.get(j));
            }

            //collect obstacles list
            List<GameObject> gasCloudList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
                    .filter(item -> getDistanceBetween(this.bot, item) < (this.gameState.world.radius*0.5) + this.bot.size + item.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < gasCloudList.size(); j++) {
                scoreSum += -10 / getDistanceBetween(this.bot, gasCloudList.get(j));
            }
            
            //collect asteroid list
            List<GameObject> asteroidList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.ASTEROIDFIELD)
                    .filter(item -> getDistanceBetween(this.bot, item) < (this.gameState.world.radius*0.5) + this.bot.size + item.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < asteroidList.size(); j++) {
                scoreSum += -5 / getDistanceBetween(this.bot, asteroidList.get(j));
            }

            //collect wormhole list
            List<GameObject> wormholeList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .filter(item -> getDistanceBetween(this.bot, item) < (this.gameState.world.radius*0.5) + this.bot.size + item.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < wormholeList.size(); j++) {
                scoreSum += -10 / getDistanceBetween(this.bot, wormholeList.get(j));
            }

            // collect bigger enemy list
            List<GameObject> enemyBiggerList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                    .filter(item -> getDistanceBetween(this.bot, item) < (this.gameState.world.radius*0.5) + this.bot.size + item.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < enemyBiggerList.size(); j++) {
                scoreSum += -20 / getDistanceBetween(this.bot, enemyBiggerList.get(j));
            }

            // collect smaller enemy list
            List<GameObject> enemySmallerList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                    .filter(item -> getDistanceBetween(this.bot, item) < 300 + this.bot.size + bot.size)
                    .filter(item -> getHeadingBetween(item) >= headingStart && getHeadingBetween(item) <= headingEnd)
                    .filter(item -> item.size < this.bot.size)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(this.bot, item)))
                    .collect(Collectors.toList());
            for (int j = 0; j < enemySmallerList.size(); j++) {
                scoreSum += 10 / getDistanceBetween(this.bot, enemySmallerList.get(j));
            }

            scoringList.add(scoreSum);
        }
        //calculate the best direction
        double maxScore = 0;
        int maxScoreIndex = 0;
        for (int i = 0; i < scoringList.size(); i++) {
            if (scoringList.get(i) > maxScore) {
                maxScore = scoringList.get(i);
                maxScoreIndex = i;
            }
        }
        return (maxScoreIndex+1) * vision - (vision/2);
    }
}