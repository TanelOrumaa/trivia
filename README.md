# trivia



## Client types
1 - PresenterClient

2 - PlayerClient

3 - HostClient


## Message codes
####1xx codes are for general server-client messages, like sync.

#####101: Client -> server.Server "Hello, I am _____"
- Example: 101 clientType

#####102: server.Server -> Client "Acknowledged"
- Example: 102 uniqueHash

#####111: Client -> server.Server "Sync message 1"
- Example: 111 hash

#####112: server.Server -> Client "Sync message 2"
- Example: 112 hash servertime

#####121: Client -> server.Server "Login"
- Example: 121 hash username password

#####122: server.Server -> Client "Login successful"
- Example: 122 hash User_object_as_json

#####123: Client - server.Server "Register"
- Example: 123 hash username password nickname

#####124: server.Server -> Client "Registration successful"
- Example: 124 hash

#####131: Client -> server.Server "Connecting to lobby"
- Example: 131 hash lobbyCode

#####132: server.Server -> Client "Connection to lobby successful"
- Example: 132 hash Lobby_object_as_json

#####133: Client -> server.Server "Create lobby"
- Example: 133 hash lobbyName

#####134: server.Server -> Client "lobby.Lobby creation successful"
- Example: 134 hash lobbyCode

#####136: server.Server -> Client "Update lobby"
- Example: 136 hash Lobby_object_as_json

#####137: Client -> server.Server "lobby.Lobby updated"
- Example: 137 hash

#####138: server.Server -> Client "Game started for everyone" (response to 139)
- Example: 138 hash

#####139: Client -> server.Server "Start game for this lobby"
- Example: 139 hash

#####140: server.Server -> Client "Display next question"
- Example: 140 hash questionId

#####141: Client -> server.Server "Displaying next question"
- Example: 141 hash

#####199: Client -> server.Server "Quitting, please close socket."
- Example: 199 hash 

####2xx codes are for transferring data.

#####201: Client - > server.Server "Request new question"
- Example: 201 hash previousQuestionId

#####202: server.Server -> Client "Send next question"
- Example: 202 hash Question_object_as_json

#####203: Client -> server.Server "Answer for question"
- Example: 203 hash questionId, answerId

#####204: server.Server -> Client "Answer received"
- Example: 204 hash

#####211: Client -> server.Server "Request triviaset list for user"
- Example: 211 hash

#####212: server.Server -> Client "Send user's triviaset"
- Example: 212 hash Triviasets_as_json

####4xx codes are for errors.

#####402 - server.Server -> Client "Unexpected message code"

#####404 - server.Server -> Client "Invalid hash"

#####422 - server.Server -> Client "Invalid login data"

#####424 - server.Server -> Client "Registration failed - username already exists"

#####426 - server.Server -> Client "Registration failed - try again"
 
#####432 - server.Server -> Client "lobby.Lobby does not exist"

#####434 - server.Server -> Client "lobby.Lobby is full"

#####436 - server.Server -> Client "Failed to send next question"
