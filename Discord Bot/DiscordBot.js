//########################################################################
//################### Setup Bot ##########################################
//########################################################################

//Dependancies
var Discord = require('discord.io');
var http = require('http');

//Discord Bot Options
var discordOptions = {
    channels: { testChannel: '343294539467063298' }
}

//Create Discord Bot
var discord = new Discord.Client({
    token: require('./getToken.js').discordToken(),
    autorun: true
});

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
