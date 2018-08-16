package snowbot.Commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

public class Role extends Command{
    public Role() {
        commandName = "Role";
        commandCall = "role";
        commandDescription = "Changes roles";
        commandUsage = "sb!role <role> | sb!role <role> <user>";
        commandArgs = 2;
        commandReturn = "You haven't selected a role!";
    }
    
    @Override
    public void Args(MessageReceivedEvent commandEvent, String[] args, int numArgs) {
        if(args.length > (numArgs + 1)) {
            commandEvent.getChannel().sendMessage("Incorrect usage!").queue();
            return;
        }
        
        GuildController guild = new GuildController(commandEvent.getGuild());
        net.dv8tion.jda.core.entities.Role role = guild.getGuild().getRolesByName(args[1], false).get(0);
        Member member;
        
        if(args.length > 2) {
            String user = args[2];
            user = user.replace("<", "");
            user = user.replace(">", "");
            user = user.replace("@", "");
            
            if(commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                try {
                    member = guild.getGuild().getMemberById(user);
                }
                catch(Exception e) {
                    commandEvent.getChannel().sendMessage("That user doesn't exist!").queue();
                    return;
                }
            }
            else {
                commandEvent.getChannel().sendMessage("You don't have permission to use this command!").queue();
                return;
            }
        }
        else {
            member = commandEvent.getMember();
        }
        
        if(member.getRoles().contains(role)) {
            guild.removeRolesFromMember(member, role).queue();
            commandEvent.getChannel().sendMessage("Role removed! :blush:").queue();
        }
        else {
            guild.addRolesToMember(member, role).queue();
            commandEvent.getChannel().sendMessage("Role added! :blush:").queue();
        }
    }
}
