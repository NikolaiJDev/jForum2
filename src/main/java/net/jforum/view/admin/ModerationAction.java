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
 * Created on Jan 30, 2005 2:49:29 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.admin;

import freemarker.template.SimpleHash;

import net.jforum.SessionFacade;
import net.jforum.context.RequestContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.ForumDAO;
import net.jforum.dao.ModerationDAO;
import net.jforum.dao.ModerationLogDAO;
import net.jforum.dao.PostDAO;
import net.jforum.dao.TopicDAO;
import net.jforum.dao.UserDAO;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.view.forum.common.AttachmentCommon;
import net.jforum.view.forum.common.PostCommon;
import net.jforum.view.forum.common.TopicsCommon;

/**
 * @author Rafael Steil
 */
public class ModerationAction extends AdminCommand
{
	/**
	 * Empty Constructor
	 */
	public ModerationAction() {}

	public ModerationAction(final SimpleHash context, final RequestContext request)
	{
		this.context = context;
		this.request = request;
	}

	/**
	 * @see net.jforum.Command#list()
	 */
	@Override public void list()
	{
		this.setTemplateName(TemplateKeys.MODERATION_ADMIN_LIST);
		this.context.put("infoList", DataAccessDriver.getInstance().newModerationDAO().categoryPendingModeration());
	}

	public void view()
	{
		final int forumId = this.request.getIntParameter("forum_id");

		this.setTemplateName(TemplateKeys.MODERATION_ADMIN_VIEW);
		this.context.put("forum", ForumRepository.getForum(forumId));
		this.context.put("topics", DataAccessDriver.getInstance().newModerationDAO().topicsByForum(forumId));
	}

	public void doSave()
	{
		final String[] posts = this.request.getParameterValues("post_id");

		if (posts != null) {
			final TopicDAO topicDao = DataAccessDriver.getInstance().newTopicDAO();
			final PostDAO postDao = DataAccessDriver.getInstance().newPostDAO();
			final UserDAO userDao = DataAccessDriver.getInstance().newUserDAO();
			final ModerationDAO moderationDao = DataAccessDriver.getInstance().newModerationDAO();
			final ModerationLogDAO dao = DataAccessDriver.getInstance().newModerationLogDAO();
			final ForumDAO forumDao = DataAccessDriver.getInstance().newForumDAO();

			for (int i = 0; i < posts.length; i++) {
				final int postId = Integer.parseInt(posts[i]);

				final String status = this.request.getParameter("status_" + postId);

				if ("defer".startsWith(status)) {
					continue;
				}

				if ("approve".startsWith(status)) {
					Post post = postDao.selectById(postId);

					// Check is the post is in fact waiting for moderation
					if (!post.isModerationNeeded()) {
						continue;
					}

					User user = userDao.selectById(post.getUserId());

					boolean first = false;
					Topic topic = TopicRepository.getTopic(new Topic(post.getTopicId()));

					if (topic == null) {
						topic = topicDao.selectById(post.getTopicId());

						if (topic.getId() == 0) {
							first = true;
							topic = topicDao.selectRaw(post.getTopicId());
						}
					}

					moderationDao.approvePost(postId);

					boolean firstPost = (topic.getFirstPostId() == postId);

					if (!firstPost) {
						topic.setTotalReplies(topic.getTotalReplies() + 1);
					}

					topic.setLastPostId(postId);
					topic.setLastPostBy(user);
					topic.setLastPostDate(post.getTime());
					topic.setLastPostTime(post.getTime());

					topicDao.update(topic);

					if (first) {
						topic = topicDao.selectById(topic.getId());
					}

					TopicsCommon.updateBoardStatus(topic, postId, firstPost, topicDao, forumDao);

					ForumRepository.updateForumStats(topic, user, post);
					TopicsCommon.notifyUsers(topic, post);

					userDao.incrementPosts(post.getUserId());

					if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED)) {
						PostRepository.append(post.getTopicId(), PostCommon.preparePostForDisplay(post));
					}

					post.setModerate(false);
					postDao.index(post);
				} // must be "reject"
				else {
					Post post = postDao.selectById(postId);

					if (post == null || !post.isModerationNeeded()) {
						continue;
					}

					postDao.delete(post);

					new AttachmentCommon(this.request, post.getForumId()).deleteAttachments(postId, post.getForumId());

					int totalPosts = topicDao.getTotalPosts(post.getTopicId());
					if (totalPosts == 0) {
						TopicsCommon.deleteTopic(post.getTopicId(), post.getForumId(), true);
					}

					// moderation log entry that a post was rejected
					ModerationLog log = new ModerationLog();
					User user = new User();
					user.setId(SessionFacade.getUserSession().getUserId());
					log.setUser(user);
					User posterUser = new User();
					posterUser.setId(post.getUserId());
					log.setPosterUser(posterUser);
					log.setType(5); // post rejected
					if (totalPosts > 0) {
						log.setTopicId(post.getTopicId());
					}
					log.setDescription(I18n.getMessage("ForumIndex.topics")
									+" \""+post.getSubject()+"\": "
									+this.request.getParameter("comment_" + postId));
					log.setOriginalMessage(post.getText());

					dao.add(log);

				}
			}
		}
	}

	public void save()
	{
		this.doSave();
		this.view();
	}
}
