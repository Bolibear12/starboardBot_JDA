package starbot.withoutComments;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Starbot extends ListenerAdapter {	
	public HashMap<String, String> starboardId = new HashMap<>();
	
	public int qualify = 1;
	
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
    	TextChannel starboard = event.getGuild().getTextChannelById("channelID");
    	MessageReaction react = event.getReaction();
    	EmbedBuilder embed = new EmbedBuilder();
    	String msgID = event.getMessageId();
    	
        event.getChannel().retrieveMessageById(msgID).queue(message->{
        	int count = message.getReactions().get(message.getReactions().indexOf(react)).getCount();
           	List<Attachment> attachment = message.getAttachments();
           	List<MessageEmbed> embeds = message.getEmbeds();
    		String contentId = message.getId();
           	String url = message.getJumpUrl();
           	String sauce = message.getChannel().getName();
           	LocalDate date = LocalDate.now();
           	String dateFormat = date.toString();
           	int memberColor = message.getMember().getColorRaw();
           	
           	if(!message.getAuthor().isBot()) {
           	//For the first time the original content to be posted into the starboard channel or when the starred content that failed to reach the qualification is qualified again
           		if(event.getReactionEmote().getEmoji().equals("⭐") && count == qualify) {
	        		if(attachment.size() == 0 && embeds.size() == 0) {
		            		starboard.sendMessage("⭐"+"**"+count+"**").queue(id ->{
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.setFooter("Starred Date: "+dateFormat);
		            			id.editMessage(embed.build()).queue();
		            			starboardId.put(contentId, id.getId());
		                		embed.clear();
		            		});
		        		}
		        		
	        		if(attachment.size() == 0 && embeds.size() > 0) { //Checks if there's only an embedded message only
	                   	String embedURL = embeds.get(0).getUrl();
	                   	if(embedURL.contains("https://twitter.com/")) { //Checks if the url is from Twitter
		                   	String TwitterIMG = embeds.get(0).getImage().getProxyUrl();
		                   	String TwitterContent = embeds.get(0).getDescription();
		                   	starboard.sendMessage("⭐"+" **"+count+"**").queue(id ->{
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.addField("Twitter:", TwitterContent, false);
		                		embed.setImage(TwitterIMG);
		                		embed.setFooter("Starred Date: "+dateFormat);
		            			id.editMessage(embed.build()).queue();
		            			starboardId.put(contentId, id.getId());
		                		embed.clear();
		            		});
	                   	} else if (embedURL.contains("https://www.youtube.com/")) { //Checks if the url is from YT
	                   		String YTTitle = embeds.get(0).getTitle();
	                   		String YTDesc = embeds.get(0).getDescription();
	                   		String YTVideo = embeds.get(0).getThumbnail().getProxyUrl();
	                   		starboard.sendMessage("⭐"+" **"+count+"**").queue(id ->{
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.addField("Youtube: "+YTTitle, YTDesc, false);
		                		embed.setImage(YTVideo);
		                		embed.setFooter("Starred Date: "+dateFormat);
		            			id.editMessage(embed.build()).queue();
		            			starboardId.put(contentId, id.getId());
		                		embed.clear();
		            		});
	        			}else {
		            		starboard.sendMessage("⭐"+" **"+count+"**").queue(id ->{ //Default
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.setImage(embedURL);
		                		embed.setFooter("Starred Date: "+dateFormat);
		            			id.editMessage(embed.build()).queue();
		            			starboardId.put(contentId, id.getId());
		                		embed.clear();
		            		});
	                   	}
	        		}
		        		
		        		if(attachment.size() > 0 && embeds.size() == 0) { //Checks if there's an attachment in the message
			        		starboard.sendMessage("⭐"+" **"+count+"**").queue(id ->{
			                  	String imgURL = attachment.get(0).getProxyUrl();
				        		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
				        		embed.setColor(memberColor);
				        		embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", false);
				        		embed.addField("Content: ", message.getContentRaw() , false);
				        		embed.setImage(imgURL);
				        		embed.setFooter("Starred Date: "+dateFormat);
		            			id.editMessage(embed.build()).queue();
		            			starboardId.put(contentId, id.getId());
				        		embed.clear();	
			        		});
		        		}
		        	}
		        	
		        	//When a new reaction number is added
		        	if(event.getReactionEmote().getEmoji().equals("⭐") && count >= qualify+1 ) {
		        		if(attachment.size() == 0) {
		            		starboard.editMessageById(starboardId.get(contentId), "⭐ "+" **"+count+"**").queue(builder ->{
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.setFooter("Starred Date: "+dateFormat);
		                		builder.editMessage(embed.build()).queue();
		                		embed.clear();
		            		});
		        		}
		        		
		        		if(attachment.size() == 0 && embeds.size() == 0) {
		            		starboard.editMessageById(starboardId.get(contentId), "⭐ "+" **"+count+"**").queue(builder ->{
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.setFooter("Starred Date: "+dateFormat);
		                		builder.editMessage(embed.build()).queue();
		                		embed.clear();
		            		});
		        		}
		        		
		        		if(attachment.size() == 0 && embeds.size() > 0) {
		        			String embedURL = embeds.get(0).getUrl();
		        			if(embedURL.contains("https://twitter.com/")) { //Checks if the url is from Twitter
			                   	String TwitterIMG = embeds.get(0).getImage().getProxyUrl();
			                   	String TwitterContent = embeds.get(0).getDescription();
			            		starboard.editMessageById(starboardId.get(contentId), "⭐ "+" **"+count+"**").queue(builder ->{
			                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
			                		embed.setColor(memberColor);
			    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
			                		embed.addField("Content: ", message.getContentRaw() , false);
			                		embed.addField("Twitter:", TwitterContent, false);
			                		embed.setImage(TwitterIMG);
			                		embed.setFooter("Starred Date: "+dateFormat);
			            			builder.editMessage(embed.build()).queue();
			                		embed.clear();
			            		});
		                   	} else if (embedURL.contains("https://www.youtube.com/")) { //Checks if the url is from YT
		                   		String YTTitle = embeds.get(0).getTitle();
		                   		String YTDesc = embeds.get(0).getDescription();
		                   		String YTVideo = embeds.get(0).getThumbnail().getProxyUrl();
			            		starboard.editMessageById(starboardId.get(contentId), "⭐ "+" **"+count+"**").queue(builder ->{
			                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
			                		embed.setColor(memberColor);
			    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
			                		embed.addField("Content: ", message.getContentRaw() , false);
			                		embed.addField("Youtube: "+YTTitle, YTDesc, false);
			                		embed.setImage(YTVideo);
			                		embed.setFooter("Starred Date: "+dateFormat);
			            			builder.editMessage(embed.build()).queue();
			                		embed.clear();
			            		});
		        			}else {
			            		starboard.editMessageById(starboardId.get(contentId), "⭐ "+" **"+count+"**").queue(builder ->{ //Default
			                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
			                		embed.setColor(memberColor);
			    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
			                		embed.addField("Content: ", message.getContentRaw() , false);
			                		embed.setImage(embedURL);
			                		embed.setFooter("Starred Date: "+dateFormat);
			            			builder.editMessage(embed.build()).queue();
			                		embed.clear();
			            		});
		                   	}
		        		}
		        		
		        		if(attachment.size() > 0 && embeds.size() == 0) {
		        			String embedURL = embeds.get(0).getUrl();
		            		starboard.editMessageById(starboardId.get(contentId), "⭐ "+" **"+count+"**").queue(builder ->{
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor);
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true);
		                		embed.addField("Content: ", message.getContentRaw() , false);
		                		embed.setImage(embedURL);
		                		embed.setFooter("Starred Date: "+dateFormat);
		                		builder.editMessage(embed.build()).queue();
		                		embed.clear();
		            		});
		        		}
	        		}  	
           	}	
        });
    }
    
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
    	TextChannel starboard = event.getGuild().getTextChannelById("channelID");
    	MessageReaction react = event.getReaction();
    	String msgID = event.getMessageId();
    	
    	//This will delete the starred content posted in the starboard channel if its reaction number no longer satisfy the int qualify
        event.getChannel().retrieveMessageById(msgID).queue(message->{
        	if(!message.getAuthor().isBot()) {
	        	int count = message.getReactions().stream().filter(react::equals).findFirst().map(MessageReaction::getCount).orElse(0);
	        	String contentId = message.getId();
	        	
	        	if(count == qualify-1) {
	        		starboard.deleteMessageById(starboardId.get(contentId)).queue();
	        	}
        	}
        });
    }
}