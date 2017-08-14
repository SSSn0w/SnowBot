//########################################################################
//################### Setup Bot ##########################################
//########################################################################

//Dependancies
var Discord = require('discord.io');
var tmi = require('tmi.js');
var http = require('http');

//Discord Bot Options
var discordOptions = {
    channels: { testChannel: '343294539467063298' }
}

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

//Create Discord Bot
var discord = new Discord.Client({
    token: require('./getToken.js').discordToken(),
    autorun: true
});

//Create Twitch Bot
var twitch = new tmi.client(twitchOptions.options);
twitch.connect();

//Chuck Norris Jokes API
var jokeUrl = 'http://api.icndb.com/jokes/random';
http.createServer(function(req, res) {});

//########################################################################
//################### Discord Bot ########################################
//########################################################################

//Check if message starts with "!"
discord.on('message', function(user, userID, channelID, message, event) {
  	if (message.startsWith('!')) {
        require('./messageHandler.js').messageHandler(message, 'discord', discordOptions.channels.testChannel);
  	}
});

//If someone joins the channel and is online, welcome them
discord.on('presence', function(user, userID, status, game, event) {
	if (status === 'online') {
		discord.sendMessage({
            to: discordOptions.channels.testChannel,
            message: 'Hi <@' + userID + '>! Welcome to the channel! Please enjoy your stay!'
        });
	}
});

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
