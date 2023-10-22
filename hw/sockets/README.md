# Chat system

## Description

Implement a chat system which supports multiple users. Every user is connected to what we call a room or channel.

To connect to the server, when starting the program, the user should use the following command

`/connect <ip of chat server>:<port>`

Every user must have a nickname. The nickname is given by using `/nick` Pesho upon joining.

Every user can send a message to the channel, this is done via the following command:

`/msg <message>`

When user named Pesho uses the command like that `/msg zdr`, all other clients connected see the following

`[10:33 AM] Pesho: zdr`

Every user can quit the room. This is done using the `/quit` command.

## Technical requirements

You should use ***Java Socket API***. Think what transport protocol we should use. We need to have exact ordering of the messages and we need to have guaranteed delivery of the messages.

You should be able to support multiple clients at once. Think how to handle new connections to the server.

## Submission

Submit the task as a pull request to the following repository

`https://github.com/arnaudoff/elsys-web-applications-course-2023-2024`

You should name the pull request in the following format

`<number in class> Chat system homework #1 - <firstName> <lastName>`

Replace number in class, firstName and lastName with your number in the class and names accordingly.

The task solution should be placed in the homeworks folder with sub folder named after your number in class, e.g 04. Inside the subfolder you should have a Java project named chat-system.

***Deadline Tue 24 Oct 2023, 23:59***
