# trivia



## Client types
1 - PresenterClient

2 - PlayerClient


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
- Example: 123 hash username password nickname

#####124: Server -> Client "Registration successful"
- Example: 124 hash

#####131: Client -> Server "Connecting to lobby"
- Example: 131 hash lobbyCode

#####132: Server -> Client "Connection to lobby successful"
- Example: 132 hash Lobby_object_as_json

#####133: Client -> Server "Create lobby"
- Example: 133 hash lobbyName

#####134: Server -> Client "Lobby creation successful"
- Example: 134 hash lobbyCode

#####136: Server -> Client "Update lobby"
- Example: 136 hash Lobby_object_as_json

#####137: Client -> Server "Lobby updated"
- Example: 137 hash

#####138: Server -> Client "Game started for everyone" (response to 139)
- Example: 138 hash

#####139: Client -> Server "Start game for this lobby"
- Example: 139 hash

#####140: Server -> Client "Display next question"
- Example: 140 hash questionId

#####141: Client -> Server "Displaying next question"
- Example: 141 hash

#####199: Client -> Server "Quitting, please close socket."
- Example: 199 hash 

####2xx codes are for transferring data.

#####201: Client - > Server "Request new question"
- Example: 201 hash previousQuestionId

#####202: Server -> Client "Send next question"
- Example: 202 hash Question_object_as_json

#####203: Client -> Server "Answer for question"
- Example: 203 hash questionId, answerId

#####204: Server -> Client "Answer received"
- Example: 204 hash

#####211: Client -> Server "Request triviasets list for user"
- Example: 211 hash

#####212: Server -> Client "Send user's triviasets"
- Example: 212 hash Triviasets_as_json

####4xx codes are for errors.

#####402 - Server -> Client "Unexpected message code"

#####404 - Server -> Client "Invalid hash"

#####422 - Server -> Client "Invalid login data"

#####424 - Server -> Client "Registration failed - username already exists"

#####426 - Server -> Client "Registration failed - try again"
 
#####432 - Server -> Client "Lobby does not exist"

#####434 - Server -> Client "Lobby is full"

#####436 - Server -> Client "Failed to send next question"
