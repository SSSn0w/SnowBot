//########################################################################
//################### Setup Bot ##########################################
//########################################################################

var Discord = require('discord.io');

var discord = new Discord.Client({
    token: require("./getToken.js").discordToken(),
    autorun: true
});

var NMID = "SnowBot";

var tmi = require("tmi.js");

var options = {
	options: {
		debug: true
	},
	connection: {
		cluster: "aws",
		reconnect: true
	},
    identity: {
        username: "SSSnowdiscord",
        password: require("./getToken.js").twitchToken()
    },
    channels: ["ssssn0w"]
};

var twitch = new tmi.client(options);
twitch.connect();

//########################################################################
//################### Discord Bot ########################################
//########################################################################

discord.on('ready', function() {
    console.log('Logged in as %s - %s\n', discord.username, discord.id);
});

discord.on('message', function(user, userID, channelID, message, event) {
    if (message === "ping" && user != NMID) {
        discord.sendMessage({
            to: channelID,
            message: "pong"
        });
    }
  	else if (message.indexOf("!joke") !== -1) {
        discord.sendMessage({
            to: channelID,
            message: "Can a kangaroo jump higher than a house? \nOf course, a house doesn’t jump at all."
        });
  	}
});

discord.on('presence', function(user, userID, status, game, event) {
	if (status === "online") {
		discord.sendMessage({
            to: "343294539467063298",
            message: "Hi <@" + userID + ">! Welcome to the channel! Please enjoy your stay!"
        });
	}
});

//########################################################################
//################### Twitch Bot #########################################
//########################################################################

twitch.on("message", function (channel, userstate, message, self) {
    if (self) return;

    switch(userstate["message-type"]) {
        case "chat":
            if(message.indexOf("!discord") !== -1) {
				twitch.action(options.channels[0], "Hi! I'm the new twitch discord being made by Snow! Nice to meet you! Please look forward to more great features!");
			}
            break;
    }
});

twitch.on("join", function (channel, username, self) {
    twitch.action(options.channels[0], "Hi @" + username + "! Welcome to the stream! Please enjoy your stay!");
});
