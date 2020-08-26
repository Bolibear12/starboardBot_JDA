package starbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import starbot.serverEvent.Starboard;

public class Main {
	public static void main(String[] args) throws Exception{
		JDABuilder.createDefault("[botToken")
			//Caching policies of the users in the server//cache catching :shrug:
			.enableIntents(GatewayIntent.GUILD_MEMBERS)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.NONE)
			
			//Small Detail
			.setActivity(Activity.watching("Hall of Fame"))
			
			//Builds
			.addEventListeners(new Starboard())
			.build();
		
	}
}
