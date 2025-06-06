/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on 12/11/2004 18:04:12
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.ForumDAO;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.LastPostInfo;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;
import net.jforum.util.concurrent.Executor;
import net.jforum.util.mail.EmailSenderTask;
import net.jforum.util.mail.ForumNewTopicSpammer;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public final class ForumCommon 
{
	private static final Logger LOGGER = Logger.getLogger(ForumCommon.class);
	/**
	 * Check if some forum has unread messages.
	 * @param forum The forum to search for unread messages 
	 * @param tracking Tracking of the topics read by the user
	 * @param lastVisit The last visit time of the current user
	 */
	public static void checkUnreadPosts(final Forum forum, final Map<Integer, Long> tracking, final long lastVisit) 
	{
		final LastPostInfo lpi = forum.getLastPostInfo();
		
		if (lpi == null) {
			return;
		}

		final Integer topicId = Integer.valueOf(lpi.getTopicId());
		
		if (tracking != null && tracking.containsKey(topicId)) {
			final long readTime = tracking.get(topicId).longValue();
		
			forum.setUnread(readTime > 0 && lpi.getPostTimeMillis() > readTime);
		}
		else {
			forum.setUnread(lpi.getPostTimeMillis() > lastVisit);
		}
	}
	
	/**
	 * Gets all forums available to the user.
	 * 
	 * @param userSession A <code>UserSession</code> instance with user information
	 * @param anonymousUserId The id which represents the anonymous user
	 * @param tracking <code>Map</code> instance with information 
	 * about the topics read by the user
	 * @param origCheckUnreadPosts <code>true</code> if is to search for unread topics inside the forums, 
	 * or <code>false</code> if this action is not needed. 
	 * @return A <code>List</code> instance where each record is an instance of a <code>Category</code> object
	 */
	public static List<Category> getAllCategoriesAndForums(final UserSession userSession, final int anonymousUserId, 
			final Map<Integer, Long> tracking, boolean origCheckUnreadPosts)
	{
		boolean checkUnreadPosts = origCheckUnreadPosts;
		long lastVisit = 0;
		int userId = anonymousUserId;
		
		if (userSession != null) {
			lastVisit = userSession.getLastVisit().getTime();
			userId = userSession.getUserId();
		}

        // Do not check for unread posts if the user is not logged in
		checkUnreadPosts = checkUnreadPosts && (userId != anonymousUserId);

		final List<Category> categories = ForumRepository.getAllCategories(userId);
		
		if (!checkUnreadPosts) {
			return categories;
		}

		final List<Category> returnCategories = new ArrayList<>();
		for (Category cat : categories) {
			Category category = new Category(cat);

			for (Forum forum : category.getForums()) {
				ForumCommon.checkUnreadPosts(forum, tracking, lastVisit);
			}

			returnCategories.add(category);
		}

		return returnCategories;
	}
	
	/**
	 * @see #getAllCategoriesAndForums(UserSession, int, Map, boolean)
     * @return List
     * @param checkUnreadPosts boolean
	 */
	public static List<Category> getAllCategoriesAndForums(boolean checkUnreadPosts)
	{
		return getAllCategoriesAndForums(SessionFacade.getUserSession(), 
				SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID), 
				SessionFacade.getTopicsReadTime(), 
				checkUnreadPosts);
	}
	
	/**
	 * @see #getAllCategoriesAndForums(boolean)
     * @return List
	 */
	public static List<Category> getAllCategoriesAndForums()
	{
		UserSession userSession = SessionFacade.getUserSession();
		boolean checkUnread = (userSession != null && userSession.getUserId() 
			!= SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));
		return getAllCategoriesAndForums(checkUnread);
	}
	
	/**
	 * Sends a "new topic" notification message to all users watching the forum.
	 * 
	 * @param forum The Forum changed
	 * @param topic The new topic
	 * @param post the newly created message
	 */
	public static void notifyUsers(Forum forum, Topic topic, Post post)
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_NOTIFY_ANSWERS)) {
			try {
				ForumDAO dao = DataAccessDriver.getInstance().newForumDAO();
				List<User> usersToNotify = dao.notifyUsers(forum);

				// we only have to send an email if there are users
				// subscribed to the topic
				if (usersToNotify != null && !usersToNotify.isEmpty()) {
					Executor.execute(
						new EmailSenderTask(
							new ForumNewTopicSpammer(forum, topic, post, usersToNotify)));
				}
			}
			catch (Exception e) {
				LOGGER.warn("Error while sending notification emails: " + e);
			}
		}
	}
	
	private ForumCommon() {}
}
