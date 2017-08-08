//########################################################################
//################### Setup Bot ##########################################
//########################################################################

var Discord = require('discord.io');

var discord = new Discord.Client({
    token: require('./getToken.js').discordToken(),
    autorun: true
});

var NMID = 'SnowBot';

var tmi = require('tmi.js');

var options = {
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
    channels: ['ssssn0w']
};

var twitch = new tmi.client(options);
twitch.connect();

var jokeUrl = 'http://api.icndb.com/jokes/random';

var http = require('http');
http.createServer(function(req, res) {});

//########################################################################
//################### Discord Bot ########################################
//########################################################################

discord.on('ready', function() {
    console.log('Logged in as %s - %s\n', discord.username, discord.id);
});

discord.on('message', function(user, userID, channelID, message, event) {
    if (message === 'ping' && user != NMID) {
        discord.sendMessage({
            to: channelID,
            message: 'pong'
        });
    }
  	if (message.indexOf('!joke') !== -1) {
        http.get(jokeUrl, function(res){
            var body = '';

            res.on('data', function(chunk){
                body += chunk;
            });

            res.on('end', function(){
                var joke = JSON.parse(body);
                discord.sendMessage({
                    to: channelID,
                    message: joke.value.joke
                });
            });
        }).on('error', function(e){
            console.log("Got an error: ", e);
        });
  	}
});

discord.on('presence', function(user, userID, status, game, event) {
	if (status === 'online') {
		discord.sendMessage({
            to: '343294539467063298',
            message: 'Hi <@" + userID + ">! Welcome to the channel! Please enjoy your stay!'
        });
	}
});

//########################################################################
//################### Twitch Bot #########################################
//########################################################################

twitch.on('message', function (channel, userstate, message, self) {
    if (self) return;

    switch(userstate['message-type']) {
        case 'chat':
            if(message.indexOf('!bot') !== -1) {
				twitch.action(options.channels[0], 'Hi! I\'m the new twitch bot being made by Snow! Nice to meet you! Please look forward to more great features!');
			}
            else if(message.indexOf('!joke') !== -1) {
                http.get(jokeUrl, function(res){
                    var body = '';

                    res.on('data', function(chunk){
                        body += chunk;
                    });

                    res.on('end', function(){
                        var joke = JSON.parse(body);
                        twitch.action(options.channels[0], joke.value.joke);
                    });
                }).on('error', function(e){
                    console.log("Got an error: ", e);
                });
            }
            break;
    }
});

twitch.on('join', function (channel, username, self) {
    twitch.action(options.channels[0], 'Hi @' + username + '! Welcome to the stream! Please enjoy your stay!');
});
