# SnowBot
Bot for twitch.tv and Discord

Twitch Bot Requirements: tmi.js, bufferutil@1

Discord Bot Requirements: discord.io


To run, make sure to create a file called "getToken.js" and incude the following code:

    function discordToken() {

        return "Discord Token test";

    }


    function twitchToken() {

        return "Twitch Token";

    }


    module.exports.discordToken = discordToken;

    module.exports.twitchToken = twitchToken;
