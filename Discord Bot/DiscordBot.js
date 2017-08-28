//########################################################################
//################### Setup Bot ##########################################
//########################################################################

//Dependancies
var Discord = require('discord.io');
var http = require('http');

//Discord Bot Options
var discordOptions = {
    channels: { testChannel: '343294539467063298', teamTams: '350243415591747584' }
}

//Create Discord Bot
var discord = new Discord.Client({
    token: require('./getToken.js').discordToken(),
    autorun: true
});
console.log('Logged into Discord Server');

//Chuck Norris Jokes API
var jokeUrl = 'http://api.icndb.com/jokes/random';

//Overwatch API
var owURL = 'http://owapi.net/api/v3/u/';

http.createServer(function(req, res) {});

//########################################################################
//################### Discord Bot ########################################
//########################################################################

discord.on('message', function(user, userID, channelID, message, event) {
    //Check if message starts with "!"
  	if (message.startsWith('!')) {
        messageHandler(message, 'discord', channelID);
  	}
});

//If someone joins the channel and is online, welcome them
/*discord.on('presence', function(user, userID, status, game, event) {
	if (status === 'online') {
		discord.sendMessage({
            to: channel,
            message: 'Hi <@' + userID + '>! Welcome to the channel! Please enjoy your stay!'
        });
	}
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

                    discord.sendMessage({
                        to: channel,
                        message: joke.value.joke
                    });
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
                        discord.sendMessage({
                            to: channel,
                            message: 'Error retrieving information'
                        });
                    }
                    else {
                        var stats = JSON.parse(body);

                        discord.sendMessage({
                            to: channel,
                            message: 'Username: ' + mes.split(" ")[1] + '\n' + 'Level: ' + stats.us.stats.competitive.game_stats.overall_stats.level + '\n' + 'Rank: ' + stats.us.stats.competitive.game_stats.overall_stats.comprank
                        });
                    }
                });
            });
            break
    }

    discord.sendMessage({
        to: channel,
        message: message
    });
}
