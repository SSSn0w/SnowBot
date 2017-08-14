//########################################################################
//################### Bot Functions ######################################
//########################################################################

//Handles messages from Chat
function messageHandler (mes, type, channel) {
    var message;

    switch(mes.slice(1)) {
        case 'joke':
            http.get(jokeUrl, function(res){
                var body = '';

                res.on('data', function(chunk){
                    body += chunk;
                });

                res.on('end', function(){
                    var joke = JSON.parse(body);

                    if(type === 'discord') {
                        discord.sendMessage({
                            to: channel,
                            message: joke.value.joke
                        });
                    }
                    else if(type === 'twitch') {
                        twitch.action(channel, joke.value.joke);
                    }
                });
            });
            break;
        case 'ping':
            message = 'pong';
            break
        case 'bot':
            message = 'Hi! I\'m the new twitch bot being made by Snow and SodaJett! Nice to meet you! Please look forward to more great features!';
            break;
    }

    if(type === 'discord') {
        discord.sendMessage({
            to: channel,
            message: message
        });
    }
    else if(type === 'twitch') {
        twitch.action(channel, message);
    }
}

module.exports.messageHandler = messageHandler;
