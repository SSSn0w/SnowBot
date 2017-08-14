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

//Chuck Norris Jokes API
var jokeUrl = 'http://api.icndb.com/jokes/random';
http.createServer(function(req, res) {});

//########################################################################
//################### Twitch Bot #########################################
//########################################################################

//Check if message starts with "!"
twitch.on('message', function (channel, userstate, message, self) {
    if (self) return;

    if (message.startsWith('!')) {
        require('./messageHandler.js').messageHandler(message, 'twitch', twitchOptions.channels.sodaJett);
  	}
});

//If someone joins the stream and is online, welcome them
twitch.on('join', function (channel, username, self) {
    twitch.action(options.channels[0], 'Hi @' + username + '! Welcome to the stream! Please enjoy your stay!');
});
