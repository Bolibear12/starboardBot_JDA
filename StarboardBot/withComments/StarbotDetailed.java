package starbot.withComments;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StarbotDetailed extends ListenerAdapter {	
	public HashMap<String, String> starboardId = new HashMap<>(); //To link the original starred message with the posted content in the starboard channel
	
	public int qualify = 1; //The threshold for the content being starred to be posted into the starboard channel
	
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
    	TextChannel starboard = event.getGuild().getTextChannelById("channelID"); //The starboard channel id
    	MessageReaction react = event.getReaction(); //This is to get the reactions received by a message
    	EmbedBuilder embed = new EmbedBuilder(); //To build an embed, putting it before the if statements for efficiency 
    	String msgID = event.getMessageId(); //To get the message id for later use
    	
        event.getChannel().retrieveMessageById(msgID).queue(message->{ //Tells the bot to retrieve the original message id as the event being executed then use lambda to furtherly executed stuff
        	int count = message.getReactions().get(message.getReactions().indexOf(react)).getCount(); //This is to get the number of any reactions from that retrieved message
           	List<Attachment> attachment = message.getAttachments(); //This is to list out the attachments sent by the user in the message, at most this will retrieve as 1
           	List<MessageEmbed> embeds = message.getEmbeds(); //This is to list out the message embeds (i.e https://twiterURL etc.), at most this will retrieve as 1
    		String contentId = message.getId(); //This will store the message id that will be posted by the bot in the starboard channel
           	String url = message.getJumpUrl(); //This will bring the user to the original starred message's link if they click the url
           	String sauce = message.getChannel().getName(); //This is to obtain the channel's name where the original message that had been posted into the starboard channel
           	LocalDate date = LocalDate.now(); //This is to get the date of the where the message was posted into the starboard channel
           	String dateFormat = date.toString(); //Format the date variable into a string variable
           	int memberColor = message.getMember().getColorRaw(); //This is to get the role colour of the user
           	
           	if(!message.getAuthor().isBot()) { //Checks if the message posted is by a bot, thus prevents its content being starred into the starboard channel
           	//For the first time the original content to be posted into the starboard channel or when the starred content that failed to reach the qualification is qualified again
           		if(event.getReactionEmote().getEmoji().equals("⭐") && count == qualify) { //First boolean statement checks if the reaction is the star emoji, you can change this if you want
	        		if(attachment.size() == 0 && embeds.size() == 0) { //Checks the size of attachment and embedded URL; If there's no attachment or embedded messages it will result in the size to be 0, otherwise it'll result the size to be counted as 1 (Do note there can only 1 to be shown in the embed, mainly the attachment is prioritised;
		            		starboard.sendMessage("⭐"+"**"+count+"**").queue(id ->{ //Sends the message first with the emoji count, then the embed since you can't edit an embed (iirc)
		                		embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getAvatarUrl());
		                		embed.setColor(memberColor); //You can change this to a specific colour that you want
		    					embed.addField("Posted in: ", "[#"+sauce+"]("+url+")", true); //This is a markdown in making the text to be a url
		                		embed.addField("Content: ", message.getContentRaw() , false); //This displays the original starred content
		                		embed.setFooter("Starred Date: "+dateFormat); //This shows the date the starred content is posted into the starboard channel
		            			id.editMessage(embed.build()).queue(); //This is to execute the edit with the embed in the same message
		            			starboardId.put(contentId, id.getId()); //This is where we get the id of the newly posted message in the starboard channel and let the HashMap stores it for the other fucntions to use it
		                		embed.clear(); //This is basically to clear the cache of building the embed
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
		        	
		        	//When  new reaction number is added
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