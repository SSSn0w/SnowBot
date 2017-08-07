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
        username: "SSSSn0w",
        password: "oauth:tg6dtj9mb0tvypgocznxglyd5x25zt"
    },
    channels: ["ssssn0w"]
};

var client = new tmi.client(options);
client.connect();

client.on("message", function (channel, userstate, message, self) {
    if (self) return;

    switch(userstate["message-type"]) {
        case "chat":
            if(message.indexOf("!bot") !== -1) {
				client.action(options.channels[0], "Hi! I'm the new twitch bot being made by Snow! Nice to meet you! Please look forward to more great features!");
			}
            break;
    }
});

client.on("join", function (channel, username, self) {
    client.action(options.channels[0], "Hi @" + username + "! Welcome to the stream! Please enjoy your stay!");
});