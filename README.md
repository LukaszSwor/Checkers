# Checkers Game

## Overview
This is a classic game of Checkers (also known as Draughts) built with JavaFX. It features a graphical user interface that allows two players to compete in this timeless strategy game on a single computer.

## Features
- Full checkers game logic including kinging, jumping, and multiple jumps.
- Score tracking for both players.
- A user-friendly graphical interface.

## Prerequisites
Before running the Checkers game, ensure that you have the following:
- Java SE Runtime Environment 11 or later.
- Maven (to manage dependencies and run the application).

## Installation
To set up the Checkers game on your local machine, follow these steps:

1. Clone the repository:
   
   `git clone https://github.com/LukaszSwor/Checkers.git`
2. Navigate to the directory where you cloned the repository.

## Running the Game

### With IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Ensure IntelliJ has recognized it as a Maven project and has indexed all dependencies.
3. Run the project by clicking 'Run' on the main class or using the Maven Projects window to execute the `javafx:run` goal.

### From the Terminal

1. Open a terminal and navigate to the project directory.
2. Run the following commands:
   `mvn clean install`
   `mvn javafx:run`

These commands will compile the source code and launch the application.


### How to Play
Players take turns moving their pieces diagonally forward towards the opponent's side. A piece is crowned as a "King" upon reaching the opposite end of the board, gaining the ability to move backward as well. Capturing opponent pieces by jumping over them is mandatory when possible, and chain jumps can be made in a single move.


### Contributions
Contributions are welcome! If you have any improvements or bug fixes, please fork the repository and submit a pull request.

### Contact
Name: Łukasz Sworacki
Email: lukasz.sworacki@gmail.com
GitHub: https://github.com/LukaszSwor

### Acknowledgments
JavaFX for providing the GUI toolkit.
The open-source community for continuous inspiration and support.





