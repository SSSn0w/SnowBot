var Discord = require('discord.io');

var bot = new Discord.Client({
    token: "",
    autorun: true
});

var NMID = "Night Maid";

bot.on('ready', function() {
    console.log('Logged in as %s - %s\n', bot.username, bot.id);
});

bot.on('message', function(user, userID, channelID, message, event) {
    if (message === "ping") {
        bot.sendMessage({
            to: channelID,
            message: "pong"
        });
    }
	else if (message === "noot" && user != NMID) {
        bot.sendMessage({
            to: channelID,
            message: "noot"
        });
    }
	else if (message.indexOf("!joke") !== -1) {
		bot.sendMessage({
            to: channelID,
            message: "Can a kangaroo jump higher than a house? \nOf course, a house doesn’t jump at all."
        });
	}
});

bot.on('presence', function(user, userID, status, game, event) {
	if (status === "online") {
		bot.sendMessage({
            to: "343294539467063298",
            message: "Hi " + user + "! Welcome to the channel! Please enjoy your stay!"
        });
	}
});
