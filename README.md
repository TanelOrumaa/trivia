# trivia



## Client types
1 - PresenterClient

2 - PlayerClient

3 - HostClient


## Message codes
####1xx codes are for general server-client messages, like sync.

#####101: Client -> Server "Hello, I am _____"
- Example: 101 clientType

#####102: Server -> Client "Acknowledged"
- Example: 102 uniqueHash

#####111: Client -> Server "Sync message 1"
- Example: 111 hash

#####112: Server -> Client "Sync message 2"
- Example: 112 hash servertime

#####121: Client -> Server "Login"
- Example: 121 hash username password

#####122: Server -> Client "Login successful"
- Example: 122 hash User_object_as_json

#####123: Client - Server "Register"
- Example: 123 hash username password nickanme

#####124: Server -> Client "Registration successful"
- Example: 124 hash

#####131: Client -> Server "Connecting to lobby"
- Example: 131 hash lobbyCode

#####132: Server -> Client "Connection to lobby successful"
- Example: 132 hash Lobby_object_as_json

#####134: Server -> Client ""
- Example: 132 hash 

#####135: Switching scene to LogIn
- Example: 135 

#####136: Switching scene to RegistrationScreen
- Example: 136

#####137: Switching scene to WaitingAfterQuestion
- Example: 137 

#####138: Switchign scene to QuestionFX
- Example: 138

#####199: Client -> Server "Quitting, please close socket."
- Example: 199 hash 

####2xx codes are for transferring data.

#####201: Client - > Server "User answered question"
- Example: 201 hash question_nr

#####202: Client -> Server "Request next question"
- Example: 202 hash

#####203: Server -> Client "Next question sent"
- Example: 203 hash Question_object_as_json



####4xx codes are for errors.

#####402 - Server -> Client "Unexpected message code"

#####404 - Server -> Client "Invalid hash"

#####422 - Server -> Client "Invalid login data"

#####423 - Server -> Client "Registration failed - username already exists"
 
#####432 - Server -> Client "Lobby does not exist"

#####434 - Server -> Client "Lobby is full"

#####436 - Server -> Client "Failed to send next question"
