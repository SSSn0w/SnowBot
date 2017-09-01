//########################################################################
//################### Setup Bot ##########################################
//########################################################################

//Dependancies
var tmi = require('tmi.js');
var http = require('http');
var request = require('request');

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
var owURL = 'https://owapi.net/api/v3/u/';

var request_options = {
    url: owURL,
    headers: {'User-Agent': 'Mozilla/5.0'},
};

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
            request.get(jokeUrl, function (error, response, body) {
                var joke = JSON.parse(body);

                twitch.action(channel, joke.value.joke);
            });
            break
        case 'ping':
            message = 'pong';
            break
        case 'bot':
            message = 'Hi! I\'m the new twitch bot being made by Snow! Nice to meet you! Please look forward to more great features!';
            break
        case 'ow-stats':
            if(mes.split(" ")[2] === "hero") {
                request_options.url = owURL + mes.split(" ")[1] + '/heroes';
            }
            else {
                request_options.url = owURL + mes.split(" ")[1] + '/stats';
            }
            request.get(request_options, function (error, response, body) {
                if(error !== null) {
                    twitch.action(channel, 'Error retrieving information');
                }
                else if(mes.split(" ")[2] === "hero") {
                    var stats = JSON.parse(body);

                    if(mes.split(" ")[3] === "qp") {
                        try {
                            twitch.action(channel, 'Username: ' + mes.split(" ")[1] + '\n' + '\n' + 'Hero: ' + mes.split(" ")[4] + '\n' + 'Gamemode: Quickplay' + '\n' + '\n' + 'Games Won: ' + stats.us.heroes.stats.quickplay[mes.split(" ")[4]].general_stats.games_won + '\n' + 'K/D: ' + stats.us.heroes.stats.quickplay[mes.split(" ")[4]].general_stats.eliminations_per_life + '\n' + 'Weapon Accuracy: ' + (stats.us.heroes.stats.quickplay[mes.split(" ")[4]].general_stats.weapon_accuracy * 100) + '%');
                        }
                        catch(e) {
                            twitch.action(channel, 'Error retrieving information');
                        }
                    }
                    else if(mes.split(" ")[3] === "comp") {
                        try {
                            twitch.action(channel, 'Username: ' + mes.split(" ")[1] + '\n' + '\n' + 'Hero: ' + mes.split(" ")[4] + '\n' + 'Gamemode: Competitive' + '\n' + '\n' + 'Games Won: ' + stats.us.heroes.stats.competitive[mes.split(" ")[4]].general_stats.games_won + '\n' + 'Win Percentage: ' + (stats.us.heroes.stats.competitive[mes.split(" ")[4]].general_stats.win_percentage * 100) + '%' + '\n' + 'K/D: ' + stats.us.heroes.stats.competitive[mes.split(" ")[4]].general_stats.eliminations_per_life + '\n' + 'Weapon Accuracy: ' + (stats.us.heroes.stats.competitive[mes.split(" ")[4]].general_stats.weapon_accuracy * 100) + '%');
                        }
                        catch(e) {
                            twitch.action(channel, 'Error retrieving information');
                        }
                    }
                }
                else {
                    var stats = JSON.parse(body);

                    twitch.action(channel, 'Username: ' + mes.split(" ")[1] + '\n' + '\n' + 'Level: ' + (stats.us.stats.quickplay.overall_stats.prestige * 100 + stats.us.stats.quickplay.overall_stats.level) + '\n' + 'Rank: ' + stats.us.stats.quickplay.overall_stats.comprank);
                }
            });
            break
    }
    twitch.action(channel, message);
}
