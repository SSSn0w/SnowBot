//########################################################################
//################### Setup Bot ##########################################
//########################################################################

//Dependancies
var Discord = require('discord.io');
var tmi = require('tmi.js');
var http = require('http');

//Discord Bot Options
var discordOptions = {
    channels: { testChannel: '343294539467063298', teamTams: '350243415591747584' }
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
console.log('Logged into Discord Server');

//Create Twitch Bot
var twitch = new tmi.client(twitchOptions.options);
twitch.connect();
console.log('Logged into Twitch Channel');

//Chuck Norris Jokes API
var jokeUrl = 'http://api.icndb.com/jokes/random';

//Overwatch API
var owURL = 'http://ow-api.herokuapp.com/profile/pc/us/';

http.createServer(function(req, res) {});

//########################################################################
//################### Discord Bot ########################################
//########################################################################

//Check if message starts with "!"
discord.on('message', function(user, userID, channelID, message, event) {
  	if (message.startsWith('!')) {
        messageHandler(message, 'discord', discordOptions.channels.teamTams);
  	}
});

//If someone joins the channel and is online, welcome them
discord.on('presence', function(user, userID, status, game, event) {
	if (status === 'online') {
		discord.sendMessage({
            to: discordOptions.channels.teamTams,
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
        messageHandler(message, 'twitch', twitchOptions.channels.sssnowbot);
  	}
});

//If someone joins the stream and is online, welcome them
twitch.on('join', function (channel, username, self) {
    twitch.action(options.channels[0], 'Hi @' + username + '! Welcome to the stream! Please enjoy your stay!');
});

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
                        if(type === 'discord') {
                            discord.sendMessage({
                                to: channel,
                                message: 'Error retrieving information'
                            });
                        }
                        else if(type === 'twitch') {
                            twitch.action(channel, 'Error retrieving information');
                        }
                    }
                    else {
                        var stats = JSON.parse(body);

                        if(type === 'discord') {
                            discord.sendMessage({
                                to: channel,
                                message: 'Username: ' + stats.username + '\n' + 'Level: ' + stats.level + '\n' + 'Rank: ' + stats.competitive.rank
                            });
                        }
                        else if(type === 'twitch') {
                            twitch.action(channel, 'Username: ' + stats.username + '\n' + 'Level: ' + stats.level + '\n' + 'Rank: ' + stats.competitive.rank);
                        }
                    }
                });
            });
            break
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
