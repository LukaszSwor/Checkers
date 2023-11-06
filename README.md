# Checkers Game

## Overview
This is a classic game of Checkers (also known as Draughts) built with JavaFX. It features a graphical user interface that allows two players to compete in this timeless strategy game on a single computer.

## Features
- Full checkers game logic including kinging, jumping, and multiple jumps.
- Score tracking for both players.
- A user-friendly graphical interface.

## Installation
To run this game, you will need Java 11 or later and the JavaFX SDK. Follow these steps to set up and run the game:

Clone the repository to your local machine:

1. Navigate to the directory where you cloned the repository.

2. Compile the code using your preferred Java compiler, or open it in an IDE like IntelliJ IDEA or Eclipse and build the project.


Running the Game
After compiling the source code, run the game with the following command (make sure to set the correct path to your JavaFX lib directory):
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar checkers-game.jar

Alternatively, if you are using an IDE, you can run the Main class directly.

How to Play
Players take turns moving their pieces diagonally forward towards the opponent's side. A piece is crowned as a "King" upon reaching the opposite end of the board, gaining the ability to move backward as well. Capturing opponent pieces by jumping over them is mandatory when possible, and chain jumps can be made in a single move.

Architecture
The project is divided into several packages:

com.checkers: Contains the Main application class that launches the game.
com.checkers.game: Holds game logic and controller classes.
com.checkers.model: Defines the data models, such as piece positions and states.
com.checkers.view: Manages the GUI elements of the game.

Contributions
Contributions are welcome! If you have any improvements or bug fixes, please fork the repository and submit a pull request.

Contact
Name: Łukasz Sworacki
Email: lukasz.sworacki@gmail.com
GitHub: https://github.com/LukaszSwor

Acknowledgments
JavaFX for providing the GUI toolkit.
The open-source community for continuous inspiration and support.





