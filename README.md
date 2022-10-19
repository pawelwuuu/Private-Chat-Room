# Private Peer to Peer chat room

Private chat room is an application that provides tools to create a Peer To Peer connection with another computers. You can connect to certain computer just by typing
it's ip in the app setup. Number of computers that are able to chat in one chat room is unlimited.

## How does the programme works
At first user have to specify initial setup for chat room. There are two options: client and server. When client option is choosen then client obejct is created, meanwhile construction of client object typed password is hashed and connection request with hashed password is sent to specified ip via socket. If server option has been choosen then server and client objects are created, server is created on local machine so other computers are connecting with it creating peer to peer connection.  Client imiedietly connects with server, server hashes specified password as well. Peer to peer connection that is created in this app is unique because of that computers connected to server do not have the same permissions, only server is the root.

## How to run

To run the Private Chatroom you need to double click on application jar or launch Gui class.

## How make chat room visible in public network

To do that you need to do port fowarding in your router admin panel. Add rule for fowarding of port 43839 to your local machine ip. Also you can use hamachi application for that purpose.

## Instruction

You can host the chat room or join one. If you want to host the chat room then you need to choose server radio button, then specify it's password and your nickname. User which have hosted the chat room is it's root.
If you rather want to connect to already existing chat room you have to choose the client radio button and specify three things: your nickname, ip of host, password to
the chat room.

### Commands
<p>There are also commands that you can use in chat room. The commands are following: </p>

#### Root commands
/ban [nickname]     - bans certain user. <br>
/kick [nickname]     - kicks certain user.
#### User commands
/disconnect - disconnects from the server. <br>
/help    - shows help for the commands. <br>
/list - shows list of connected users.

## License
Programme is released under GNU GPL license.

## Download JAR
To download the JAR file click [here](https://github.com/pawelwuuu/Private-Chat-Room/releases/download/App/Private-Chat.jar).
