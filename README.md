## Description
A web application that allows 4 players to play a game of Crazy 8's on four different browser tabs. Communication between browser clients is established through STOMP. The frontend of this application is made using React. The backend comprises Spring and Spring Boot. Dependency injection is used in conjunction with Selenium to facilitate automated testing of specific scenarios in the game that could ordinarily only happen by chance.

## Server Instructions
1. Run "mvn clean install"
2. Once the tests start running, press Ctrl + C and type "Y" to stop them.
3. Run Application.java
4. Open 4 browser tabs and navigate to localhost:8080 on each of them
5. Enter a different name for each player in the textbox provided and click on the "Join" button.
6. Once all 4 players have joined, the game commences and players are given 5 starting cards.


## Crazy 8 Rules
The rules for the game can be found [here](https://www.aquatennial.com/wp-content/uploads/2020/07/Card-Games_Crazy-8s.pdf)


## Images
### Sample: Player 1 view
<img width="865" alt="image" src="https://user-images.githubusercontent.com/51683551/212390681-f4beadd3-217e-4c26-a91f-d494e2ea4981.png">

## Video Demo
Click [here](https://youtu.be/toV59FuZVkk) for a live demo of the project.
