# INFO1113: Lawn Layer Assignment

The following repository holds my INFO1113 2022 Semester 1 coding assignment. 

This assignment took about 30-40 hours of work. As INFO1113 is a beginner Java unit, this code is not extremely beautiful nor concise, nor does it utilise any rigorous software design theory. Rather, this work was focused on learning the essence of Java including OOP (encapsulation, inheritance, polymorphism), enums, simple data structures such as ArrayLists etc.

## Overview
The assignment was to create a game in the Java programming language using the Processing library for graphics and gradle as a dependency manager. The game centres around a player trying to capture as much area of the map as they can whilst avoiding enemies. The level is won once a certain percentage of the map area has been captured.

![game_demo1](https://user-images.githubusercontent.com/97012075/179744117-0910bd98-e277-48f2-b075-d1a3dbce9834.png)

![game_demo2](https://user-images.githubusercontent.com/97012075/179744080-563c2e57-3333-410f-9db1-6e5af87679f2.png)

The level, consisting of a map of 64x32 tiles each 20x20 pixels, is configured using the `level.txt` files. Each `.txt` file contains a series of crosses indicating where concrete (safe) tiles are. Other than concrete tiles the game consists of grass, dirt, and progress tiles.

The enemies can be configured in the `config.json` file. An admin can add as many enemies as they want and also choose how the enemy is spawned (either "random" or a specified coordinate value such as "10, 23"). Type 0 enemy is the standard worm. Type 1 enemy is the beetle. The beetle has the added feature that when it hits a grass tile, the tile is reverted back to soil.

I have also implemented two powerups into the game. The school zone tile slows down the enemies and the sonic tile speeds the player up, both for a short time period.

## Running the game
Download the repository and `cd` into the root directory before running `gradle run`. Ensure you're running Java 8 or earlier.

## Known issues
This assignment did not score full marks, albeit still scoring at a DI level. The primary reason for this was my 'flood fill' algorithm — the algorithm used to convert tiles the player has captured from dirt to grass. My algorithm only worked as intended for the instances where the captured area was connected to the top left portion of the map. This is because I begin the algorithm at the (0, 20) tile. If the captured area does not include this portion of the map, then it fills in the other half of the map. This area is almost always greater than the required percentage for success, moving the player on to the next level.

Other issues include glitches upon the activation of a powerup and the lack of red tile propagation — the player's entire progress path turns red instantly. Testing was definitely not done to a high enough standard, evident by the low test coverage. 

## Code design

![UMLDiagram](https://user-images.githubusercontent.com/97012075/179748738-2e26bd02-2007-478c-b995-22456a4de583.png)

```bash 
Mover/
├─ Player
├─ Enemy/
│  ├─ Beetle
│  ├─ Worm
Powerup/
├─ Slow
├─ Speed
Level/
Trail/
App/
```

My code for this assignment consists of 10 classes and 2 enumerations (Material and Direction). The 10 classes are split up into 2 hierarchies (Mover and Powerup) and 3 lone classes.

The abstract Mover hierarchy deals with all objects which have any sort of movement on the screen. Hence, the two subclasses for Mover are Player and Enemy. Through the use of protected attributes, this allows all basic movement attributes to be inherited by all moving objects, including x and y coordinates, the sprite which is to be moved on screen, and the current direction of movement. 

The Player subclass adds methods and logic to deal with player movement, including registering keystrokes and smoothly transitioning from one tile to another. The abstract Enemy class focuses on how enemies spawn in the game (at randomised positions or at certain coordinates) as well as how enemy sprites "bounce" or "reflect" off certain objects. As this "reflecting" is a necessary feature for all Enemy objects to have, but whose implementation logic may differ from enemy to enemy, the 'checkReflections()' method is abstracted. Finally, in the third level of hierarchy, we have two separate enemies — the Worm and the Beetle. These both implement the 'checkReflections()' method different, the Beetle with the added functionality that it converts any grass it hits back into soil.

I have implemented two powerups, which required some inheritance design. The abstracted Powerup class sets up logic for how powerups spawn on a random soil tile across the level map (hence why a Level object is a necessary parameter to initiate a Powerup object). All powerups must display some behaviour when activated, and hence we abstract the 'activate()' and 'deactivate()' methods. I could have made Powerup a subclass of Mover, considering that these two classes do share certain parameters and methods such as x, y, sprite, and draw(), however, as a Powerup does not actually need to be 'ticked'/updated every single frame (as it does not move), I deemed it confusing and unnecessary for this inheritance to occur.

The last 3 classes (App, Level and Trail) deal with very specific, individual features, and hence do not exhibit inheritance.

