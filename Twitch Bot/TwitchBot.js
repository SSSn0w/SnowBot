//########################################################################
//################### Setup Bot ##########################################
//########################################################################

//Dependancies
var tmi = require('tmi.js');
var http = require('http');

//Twitch Bot Options
var twitchOptions = {
	options: {
		debug: true
	},
	connection: {
		cluster: 'aws',
		reconnect: true
	},
    identity: {
        username: 'SSSnowBot',
        password: require('./getToken.js').twitchToken()
    },
    channels: { sssnowbot: 'sssnowbot' }
};

//Create Twitch Bot
var twitch = new tmi.client(twitchOptions.options);
twitch.connect();
console.log('Logged into Twitch Channel');

//Chuck Norris Jokes API
var jokeUrl = 'http://api.icndb.com/jokes/random';

//Overwatch API
var owURL = 'http://owapi.net/api/v3/u/';

http.createServer(function(req, res) {});

//########################################################################
//################### Twitch Bot #########################################
//########################################################################

//Check if message starts with "!"
twitch.on('message', function (channel, userstate, message, self) {
    if (self) return;

    if (message.startsWith('!')) {
        messageHandler(message, 'twitch', twitchOptions.channels.sssnowbot);
  	}
});

//If someone joins the stream and is online, welcome them
/*twitch.on('join', function (channel, username, self) {
    twitch.action(options.channels[0], 'Hi @' + username + '! Welcome to the stream! Please enjoy your stay!');
});*/

//########################################################################
//################### Bot Functions ######################################
//########################################################################

//Handles messages from Chat
function messageHandler (mes, type, channel) {
    var message;
    var fmes = mes.split(" ")[0];
    switch(fmes.slice(1)) {
        case 'joke':
            http.get(jokeUrl, function(res){
                var body = '';

                res.on('data', function(chunk){
                    body += chunk;
                });

                res.on('end', function(){
                    var joke = JSON.parse(body);

                    twitch.action(channel, joke.value.joke);
                });
            });
            break
        case 'ping':
            message = 'pong';
            break
        case 'bot':
            message = 'Hi! I\'m the new twitch bot being made by Snow! Nice to meet you! Please look forward to more great features!';
            break
        case 'ow-stats':
            http.get(owURL + mes.split(" ")[1], function(res){
                var body = '';

                res.on('data', function(chunk){
                    body += chunk;
                });

                res.on('end', function(){
                    if(body === 'Not Found') {
                        twitch.action(channel, 'Error retrieving information');
                    }
                    else {
                        var stats = JSON.parse(body);

                        twitch.action(channel, 'Username: ' + mes.split(" ")[1] + '\n' + 'Level: ' + stats.us.stats.competitive.game_stats.overall_stats.level + '\n' + 'Rank: ' + stats.us.stats.competitive.game_stats.overall_stats.comprank);
                    }
                });
            });
            break
    }
    twitch.action(channel, message);
}
