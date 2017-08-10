//########################################################################
//################### Setup Bot ##########################################
//########################################################################

// Bundle dependancies at the top for easier overview
var Discord = require('discord.io');
var tmi = require('tmi.js');
var http = require('http');

var discord = new Discord.Client({
    token: require('./getToken.js').discordToken(),
    autorun: true
});

var NMID = 'SnowBot';

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
    channels: { sodaJett: 'sodajett'}
};

var discordOptions = {
    channels: { testChannel: '343294539467063298' }
}

var twitch = new tmi.client(twitchOptions.options);
twitch.connect();

// URL for chuck norris jokes API
var jokeUrl = 'http://api.icndb.com/jokes/random';

http.createServer(function(req, res) {});

//########################################################################
//################### Discord Bot ########################################
//########################################################################

discord.on('ready', function() {
    console.log('Logged in as %s - %s\n', discord.username, discord.id);
});

discord.on('message', function(user, userID, channelID, message, event) {
  	if (message.startsWith('!')) {
        messageHandler(message, 'discord', discordOptions.channels.testChannel);
  	}
});

discord.on('presence', function(user, userID, status, game, event) {
	if (status === 'online') {
		discord.sendMessage({
            to: discordOptions.channels.testChannel,
            message: 'Hi <@" + userID + ">! Welcome to the channel! Please enjoy your stay!'
        });
	}
});

//########################################################################
//################### Twitch Bot #########################################
//########################################################################

twitch.on('message', function (channel, userstate, message, self) {
    if (self) return;

    if (message.startsWith('!')) {
        messageHandler(message, 'twitch', twitchOptions.channels.sodaJett);
  	}
});

//twitch.on('join', function (channel, username, self) {
//    twitch.action(options.channels[0], 'Hi @' + username + '! Welcome to the stream! Please enjoy your stay!');
//});


//########################################################################
//################### Message Handlers ###################################
//########################################################################

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
                        message = joke.value.joke;
                    });
                });
            break
        case 'ping':
            message = 'pong';
            break
        case 'bot':
            message = 'Hi! I\'m the new twitch bot being made by Snow and SodaJett! Nice to meet you! Please look forward to more great features!';
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
