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
 * Created on Apr 6, 2003 / 2:38:28 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jforum.JForumExecutionContext;
import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.ForumDAO;
import net.jforum.dao.PollDAO;
import net.jforum.dao.PostDAO;
import net.jforum.dao.TopicDAO;
import net.jforum.entities.KarmaStatus;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.exceptions.DatabaseException;
import net.jforum.repository.ForumRepository;
import net.jforum.search.SearchArgs;
import net.jforum.search.SearchResult;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class GenericTopicDAO extends AutoKeys implements TopicDAO
{
	private static final String USER_ID = "user_id";
	/**
	 * @see net.jforum.dao.TopicDAO#findTopicsByDateRange(net.jforum.search.SearchArgs)
	 */
	@Override public SearchResult<Topic> findTopicsByDateRange(final SearchArgs args) 
	{
		SearchResult<Topic> result = null;

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.findTopicsByDateRange"));

			pstmt.setTimestamp(1, new Timestamp(args.getFromDate().getTime()));
			pstmt.setTimestamp(2, new Timestamp(args.getToDate().getTime()));

			resultSet = pstmt.executeQuery();
			final List<Integer> list = new ArrayList<>();

			int counter = 0;

			while (resultSet.next()) {
				if (counter >= args.startFrom() && counter < args.startFrom() + args.fetchCount()) {
					list.add(Integer.valueOf(resultSet.getInt(1)));
				}

				counter++;
			}

			result = new SearchResult<>(this.newMessages(list), list.size());
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}

		return result;
	}

	/**
	 * @see net.jforum.dao.TopicDAO#fixFirstLastPostId(int)
	 */
	@Override public void fixFirstLastPostId(final int topicId)
	{
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.getFirstLastPostId"));
			pstmt.setInt(1, topicId);

			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				final int first = resultSet.getInt("first_post_id");
				final int last = resultSet.getInt("last_post_id");

				resultSet.close();
				pstmt.close();

				pstmt = JForumExecutionContext.getConnection().prepareStatement(
						SystemGlobals.getSql("TopicModel.fixFirstLastPostId"));
				pstmt.setInt(1, first);
				pstmt.setInt(2, last);
				pstmt.setInt(3, topicId);
				pstmt.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectById(int)
	 */
	@Override public Topic selectById(final int topicId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectById"));
			pstmt.setInt(1, topicId);

			Topic topic = new Topic();
			final List<Topic> list = this.fillTopicsData(pstmt);

			if (!list.isEmpty()) {
				topic = list.get(0);
			}

			return topic;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectRaw(int)
	 */
	@Override public Topic selectRaw(final int topicId)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectRaw"));
			pstmt.setInt(1, topicId);

			Topic topic = new Topic();
			rs = pstmt.executeQuery();
			if (rs.next()) {
				topic = this.getBaseTopicData(rs);
			}

			return topic;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#delete(net.jforum.entities.Topic, boolean)
	 */
	@Override public void delete(final Topic topic, final boolean fromModeration)
	{
		final List<Topic> list = new ArrayList<>();
		list.add(topic);
		this.deleteTopics(list, fromModeration);
	}

	@Override public void deleteTopics(List<Topic> topics, boolean fromModeration)
	{
		// Topic
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.delete"));

			ForumDAO forumDao = DataAccessDriver.getInstance().newForumDAO();
			PostDAO postDao = DataAccessDriver.getInstance().newPostDAO();
			PollDAO pollDao = DataAccessDriver.getInstance().newPollDAO();

			for (Topic topic : topics) {
				// Remove watches
				this.removeSubscriptionByTopic(topic.getId());

				// Remove the messages
				postDao.deleteByTopic(topic.getId());

				// Remove the poll
				pollDao.deleteByTopicId(topic.getId());

				// Delete the topic itself
				pstmt.setInt(1, topic.getId());
				pstmt.executeUpdate();

				if (!fromModeration) {
					forumDao.decrementTotalTopics(topic.getForumId(), 1);
				}
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}

	}

	/**
	 * @see net.jforum.dao.TopicDAO#deleteByForum(int)
	 */
	@Override public void deleteByForum(int forumId)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.deleteByForum"));
			pstmt.setInt(1, forumId);

			rs = pstmt.executeQuery();
			List<Topic> topics = new ArrayList<>();

			while (rs.next()) {
				Topic topic = new Topic();
				topic.setId(rs.getInt("topic_id"));
				topic.setForumId(forumId);

				topics.add(topic);
			}

			this.deleteTopics(topics, true);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#update(net.jforum.entities.Topic)
	 */
	@Override public void update(Topic topic)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.update"));

			pstmt.setString(1, topic.getTitle());
			pstmt.setInt(2, topic.getLastPostId());
			pstmt.setInt(3, topic.getFirstPostId());
			pstmt.setInt(4, topic.getType());
			pstmt.setInt(5, topic.isModerated() ? 1 : 0);
			pstmt.setInt(6, topic.getVoteId());
			pstmt.setInt(7, topic.getId());
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}

	}

	/**
	 * @see net.jforum.dao.TopicDAO#addNew(net.jforum.entities.Topic)
	 */
	@Override public int addNew(Topic topic)
	{
		PreparedStatement pstmt = null;

		try {
			pstmt = this.getStatementForAutoKeys("TopicModel.addNew");

			pstmt.setInt(1, topic.getForumId());
			pstmt.setString(2, topic.getTitle());
			pstmt.setInt(3, topic.getPostedBy().getId());
			pstmt.setTimestamp(4, new Timestamp(topic.getTime().getTime()));
			pstmt.setInt(5, topic.getFirstPostId());
			pstmt.setInt(6, topic.getLastPostId());
			pstmt.setInt(7, topic.getType());
			pstmt.setInt(8, topic.isModerated() ? 1 : 0);

			this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("TopicModel.lastGeneratedTopicId"));

			int topicId = this.executeAutoKeysQuery(pstmt);

			topic.setId(topicId);

			return topicId;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#incrementTotalViews(int)
	 */
	@Override public void incrementTotalViews(int topicId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.incrementTotalViews"));
			pstmt.setInt(1, topicId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#incrementTotalReplies(int)
	 */
	@Override public void incrementTotalReplies(int topicId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.incrementTotalReplies"));
			pstmt.setInt(1, topicId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#decrementTotalReplies(int)
	 */
	@Override public void decrementTotalReplies(int topicId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.decrementTotalReplies"));
			pstmt.setInt(1, topicId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#setLastPostId(int, int)
	 */
	@Override public void setLastPostId(int topicId, int postId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.setLastPostId"));
			pstmt.setInt(1, postId);
			pstmt.setInt(2, topicId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectAllByForum(int)
	 */
	@Override public List<Topic> selectAllByForum(int forumId)
	{
		return this.selectAllByForumByLimit(forumId, 0, -1);
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectAllByForumByLimit(int, int, int)
	 *
	 * A count smaller than zero means no limit - simplified SQL is used
	 * in that case in order to avoid a limit of Integer.MAX_VALUE
	 */
	@Override public List<Topic> selectAllByForumByLimit(int forumId, int startFrom, int count)
	{
		String sql = (count < 0)
					? SystemGlobals.getSql("TopicModel.selectAllByForum")
					: SystemGlobals.getSql("TopicModel.selectAllByForumByLimit");

		PreparedStatement pstmt = null;

		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);
			pstmt.setInt(1, forumId);
			pstmt.setInt(2, forumId);
			if (count >= 0) {
				pstmt.setInt(3, startFrom);
				pstmt.setInt(4, count);
			}

			return this.fillTopicsData(pstmt);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectByUserByLimit(int, int, int)
	 */
	@Override public List<Topic> selectByUserByLimit(int userId, int startFrom, int count)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.selectByUserByLimit").replaceAll(":fids:",
							ForumRepository.getListAllowedForums()));

			pstmt.setInt(1, userId);
			pstmt.setInt(2, startFrom);
			pstmt.setInt(3, count);

			return this.fillTopicsData(pstmt);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#countUserTopics(int)
	 */
	@Override public int countUserTopics(int userId)
	{
		int total = 0;

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.countUserTopics").replaceAll(":fids:",
							ForumRepository.getListAllowedForums()));
			pstmt.setInt(1, userId);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				total = resultSet.getInt(1);
			}

			return total;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#countAllTopics()
	 */
	@Override public int countAllTopics()
	{
		int total = 0;

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			// performance optimization: beyond 100000 posts, return a possibly approximate topic count 
			// instead of the correct one. Mostly affects the paging of the Recent Topics pages.
			int totalPosts = ForumRepository.getTotalMessages();
			String sql = SystemGlobals.getSql(totalPosts < 100000
						? "TopicModel.countAllTopics"
						: "TopicModel.countAllTopicsApprox");

			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					sql.replaceAll(":fids:", ForumRepository.getListAllowedForums()));

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				total = resultSet.getInt(1);
			}

			return total;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(resultSet, pstmt);
		}
	}

	protected Topic getBaseTopicData(ResultSet rs) throws SQLException
	{
		Topic topic = new Topic();

		topic.setTitle(rs.getString("topic_title"));
		topic.setId(rs.getInt("topic_id"));
		topic.setTime(new Date(rs.getTimestamp("topic_time").getTime()));
		topic.setStatus(rs.getInt("topic_status"));
		topic.setTotalViews(rs.getInt("topic_views"));
		topic.setTotalReplies(rs.getInt("topic_replies"));
		topic.setFirstPostId(rs.getInt("topic_first_post_id"));
		topic.setLastPostId(rs.getInt("topic_last_post_id"));
		topic.setType(rs.getInt("topic_type"));
		if (topic.getType() == Topic.TYPE_WIKI) try {
			Timestamp postEditTime = rs.getTimestamp("post_edit_time");
			topic.setLastEditTime(postEditTime);
		} catch (SQLException e) { /* ignore - not all resultsets have this field */ }
		topic.setForumId(rs.getInt("forum_id"));
		topic.setModerated(rs.getInt("moderated") == 1);
		topic.setVoteId(rs.getInt("topic_vote_id"));
		topic.setMovedId(rs.getInt("topic_moved_id"));

		User user = new User();
		user.setId(rs.getInt(USER_ID));

		topic.setPostedBy(user);

		return topic;
	}

	/**
	 * @see net.jforum.dao.TopicDAO#getMaxPostId(int)
	 */
	@Override public int getMaxPostId(int topicId)
	{
		int id = -1;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("TopicModel.getMaxPostId"));
			pstmt.setInt(1, topicId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("post_id");
			}

			return id;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#getTotalPosts(int)
	 */
	@Override public int getTotalPosts(int topicId)
	{
		int total = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.getTotalPosts"));
			pstmt.setInt(1, topicId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				total = rs.getInt("total");
			}

			return total;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#notifyUsers(net.jforum.entities.Topic)
	 */
	@Override public List<User> notifyUsers(Topic topic)
	{
		int posterId = SessionFacade.getUserSession().getUserId();
		int anonUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		boolean isTopicInTrash = topic.getForumId() == SystemGlobals.getIntValue(ConfigKeys.FORUM_TRASHCAN);

		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.notifyUsers"));

			pstmt.setInt(1, topic.getId());
			pstmt.setInt(2, posterId); // don't notify the poster
			pstmt.setInt(3, anonUser); // don't notify the anonymous user

			rs = pstmt.executeQuery();

			List<User> users = new ArrayList<>();

			while (rs.next()) {
				User user = new User(rs.getInt(USER_ID));

				// if topic is in trash, only notify admins and moderators
				if (isTopicInTrash && !user.isAdmin() && !user.isModerator()) {
					//LOGGER.info("skipping notification for user="+user.getId());
					continue;
				}

				user.setEmail(rs.getString("user_email"));
				user.setUsername(rs.getString("username"));
				user.setLang(rs.getString("user_lang"));
				user.setNotifyText(rs.getInt("user_notify_text") == 1);

				users.add(user);
			}

			rs.close();
			pstmt.close();

			// Set read status to false
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.markAllAsUnread"));
			pstmt.setInt(1, topic.getId());
			pstmt.setInt(2, posterId); // don't notify the poster
			pstmt.setInt(3, anonUser); // don't notify the anonymous user

			pstmt.executeUpdate();

			return users;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#subscribeUsers(int, java.util.List)
	 */
	@Override public void subscribeUsers(int topicId, List<User> users)
	{
		PreparedStatement pstmt = null;

		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.subscribeUser"));

			pstmt.setInt(1, topicId);

			for (User user : users) {
				int userId = user.getId();

				pstmt.setInt(2, userId);
				pstmt.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#subscribeUser(int, int)
	 */
	@Override public void subscribeUser(int topicId, int userId)
	{
		User user = new User();
		user.setId(userId);

		List<User> list = new ArrayList<>();
		list.add(user);

		this.subscribeUsers(topicId, list);
	}

	/**
	 * @see net.jforum.dao.TopicDAO#isUserSubscribed(int, int)
	 */
	@Override public boolean isUserSubscribed(int topicId, int userId)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.isUserSubscribed"));

			pstmt.setInt(1, topicId);
			pstmt.setInt(2, userId);

			rs = pstmt.executeQuery();

			return rs.next() && rs.getInt(1) > 0;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#removeSubscription(int, int)
	 */
	@Override public void removeSubscription(int topicId, int userId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.removeSubscription"));
			pstmt.setInt(1, topicId);
			pstmt.setInt(2, userId);

			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#removeSubscriptionByTopic(int)
	 */
	@Override public void removeSubscriptionByTopic(int topicId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.removeSubscriptionByTopic"));
			pstmt.setInt(1, topicId);

			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#updateReadStatus(int, int, boolean)
	 */
	@Override public void updateReadStatus(int topicId, int userId, boolean read)
	{
		if (this.isUserSubscribed(topicId, userId)) {
			PreparedStatement pstmt = null;
			try {
				pstmt = JForumExecutionContext.getConnection().prepareStatement(
						SystemGlobals.getSql("TopicModel.updateReadStatus"));
				pstmt.setInt(1, read ? 1 : 0);
				pstmt.setInt(2, topicId);
				pstmt.setInt(3, userId);

				pstmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DatabaseException(e);
			}
			finally {
				DbUtils.close(pstmt);
			}
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#lockUnlock(int[], int)
	 */
	@Override public void lockUnlock(int[] topicId, int status)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.lockUnlock"));
			pstmt.setInt(1, status);

			for (int i = 0; i < topicId.length; i++) {
				pstmt.setInt(2, topicId[i]);
				pstmt.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	private List<Topic> newMessages(List<Integer> topicIds)
	{
		if (topicIds.isEmpty()) {
			return new ArrayList<>();
		}

		PreparedStatement pstmt = null;

		try {
			String sql = SystemGlobals.getSql("TopicModel.selectForNewMessages");

			StringBuilder sb = new StringBuilder();

			for (Integer intId : topicIds) {
				sb.append(intId).append(',');
			}

			sb.append("-1");

			sql = sql.replaceAll(":topicIds:", sb.toString());

			pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);

			return this.fillTopicsData(pstmt);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * Fills all topic data. The method will try to get all fields from the topics table, as well
	 * information about the user who made the first and the last post in the topic. <br>
	 * <b>The method <i>will</i> close the <i>PreparedStatement</i></b>
	 * 
	 * @param pstmt the PreparedStatement to execute
	 * @return A list with all topics found
	 * @throws SQLException
	 */
	public List<Topic> fillTopicsData(PreparedStatement pstmt)
	{
		List<Topic> list = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;

		try {
			rs = pstmt.executeQuery();

			StringBuilder sbFirst = new StringBuilder(128);
			StringBuilder sbLast = new StringBuilder(128);

			while (rs.next()) {
				Topic topic = this.getBaseTopicData(rs);

				// Posted by
				User user = new User();
				user.setId(rs.getInt(USER_ID));
				topic.setPostedBy(user);

				// Last post by
				user = new User();
				user.setId(rs.getInt("last_user_id"));
				topic.setLastPostBy(user);

				topic.setHasAttach(rs.getInt("attach") > 0);
                topic.setFirstPostTime(rs.getTimestamp("topic_time"));
                Timestamp postTime = rs.getTimestamp("post_time");
                topic.setLastPostTime(postTime);
                topic.setLastPostDate(new Date(postTime.getTime()));

				list.add(topic);

				sbFirst.append(rs.getInt(USER_ID)).append(',');
				sbLast.append(rs.getInt("last_user_id")).append(',');
			}

			rs.close();

			// Users
			if (sbFirst.length() > 0) {
				sbLast.delete(sbLast.length() - 1, sbLast.length());

				String sql = SystemGlobals.getSql("TopicModel.getUserInformation");
				sql = sql.replaceAll("#ID#", sbFirst.toString() + sbLast.toString());

				Map<Integer, String> users = new ConcurrentHashMap<>();

				pstmt2 = JForumExecutionContext.getConnection().prepareStatement(sql);
				rs = pstmt2.executeQuery();

				while (rs.next()) {
					users.put(Integer.valueOf(rs.getInt(USER_ID)), rs.getString("username"));
				}

				for (Topic topic : list) {
					topic.getPostedBy().setUsername(users.get(Integer.valueOf(topic.getPostedBy().getId())));
					topic.getLastPostBy().setUsername(users.get(Integer.valueOf(topic.getLastPostBy().getId())));
				}
			}

			return list;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs);
			DbUtils.close(pstmt2);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectRecentTopics(int)
	 */
	@Override public List<Topic> selectRecentTopics (int limit)
	{
		return selectRecentTopics(0, limit);
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectRecentTopics(int, int)
	 */
	@Override public List<Topic> selectRecentTopics (int start, int limit)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.selectRecentTopicsByLimit"));
			pstmt.setInt(1, start);
			pstmt.setInt(2, limit);

			return this.fillTopicsData(pstmt);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectHottestTopics(int)
	 */
	@Override public List<Topic> selectHottestTopics (int limit)
	{
		boolean sortByViews = SystemGlobals.getBoolValue(ConfigKeys.HOTTEST_TOPICS_SORT);
	    PreparedStatement pstmt = null;
	    try {
	        String sql = SystemGlobals.getSql("TopicModel.selectHottestTopicsByLimit");
			sql = sql.replaceAll(":WHAT:", sortByViews ? "t.topic_views" : "t.topic_replies");
	        pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);
	        pstmt.setInt(1, limit);

	        return this.fillTopicsData(pstmt);
	    }
	    catch (SQLException e) {
	        throw new DatabaseException(e);
	    }
	    finally {
	        DbUtils.close(pstmt);
	    }
	}

	/**
	 * @see net.jforum.dao.TopicDAO#setFirstPostId(int, int)
	 */
	@Override public void setFirstPostId(int topicId, int postId)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.setFirstPostId"));
			pstmt.setInt(1, postId);
			pstmt.setInt(2, topicId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#getMinPostId(int)
	 */
	@Override public int getMinPostId(int topicId)
	{
		int id = -1;

		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("TopicModel.getMinPostId"));
			pstmt.setInt(1, topicId);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("post_id");
			}

			return id;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#setModerationStatus(int, boolean)
	 */
	@Override public void setModerationStatus(int forumId, boolean status)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.setModerationStatus"));
			pstmt.setInt(1, status ? 1 : 0);
			pstmt.setInt(2, forumId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#setModerationStatusByTopic(int, boolean)
	 */
	@Override public void setModerationStatusByTopic(int topicId, boolean status)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("TopicModel.setModerationStatusByTopic"));
			pstmt.setInt(1, status ? 1 : 0);
			pstmt.setInt(2, topicId);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#selectTopicTitlesByIds(java.util.Collection)
	 */
	@Override public List<Map<String, Object>> selectTopicTitlesByIds(Collection<?> idList)
	{
		List<Map<String, Object>> list = new ArrayList<>();
		String sql = SystemGlobals.getSql("TopicModel.selectTopicTitlesByIds");

		StringBuilder sb = new StringBuilder(idList.size() * 2);
		for (Object obj : idList) {
			sb.append(obj).append(',');
		}

		int len = sb.length();
		sql = sql.replaceAll(":ids:", len > 0 ? sb.toString().substring(0, len - 1) : "0");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> m = new ConcurrentHashMap<>();
				m.put("id", Integer.valueOf(rs.getInt("topic_id")));
				m.put("title", rs.getString("topic_title"));

				list.add(m);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.TopicDAO#topicPosters(int)
	 */
	@Override public Map<Integer, User> topicPosters(int topicId)
	{
		Map<Integer, User> m = new ConcurrentHashMap<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			StringBuilder sql = new StringBuilder(SystemGlobals.getSql("TopicModel.topicPosters"));

			pstmt = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.distinctPosters"));
			pstmt.setInt(1, topicId);

			rs = pstmt.executeQuery();

			StringBuilder sb = new StringBuilder();

			while (rs.next()) {
				sb.append(rs.getInt(USER_ID)).append(',');
			}

			rs.close();
			pstmt.close();

			int index = sql.indexOf(":ids:");
			if (index > -1) {
				sql.replace(index, index + 5, sb.substring(0, sb.length() - 1));
			}

			pstmt = JForumExecutionContext.getConnection().prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				User user = new User();

				user.setId(rs.getInt(USER_ID));
				user.setUsername(rs.getString("username"));
				user.setKarma(new KarmaStatus(user.getId(), rs.getDouble("user_karma")));
				user.setAvatar(rs.getString("user_avatar"));
				user.setAvatarEnabled(rs.getInt("user_allowavatar") == 1);
				user.setRegistrationDate(new Date(rs.getTimestamp("user_regdate").getTime()));
				user.setTotalPosts(rs.getInt("user_posts"));
				user.setFrom(rs.getString("user_from"));
				user.setEmail(rs.getString("user_email"));
				user.setRankId(rs.getInt("rank_id"));
				user.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);
				user.setTwitter(rs.getString("user_twitter"));
				user.setAttachSignatureEnabled(rs.getInt("user_attachsig") == 1);
				user.setWebSite(rs.getString("user_website"));
				user.setSignature(rs.getString("user_sig"));

				m.put(Integer.valueOf(user.getId()), user);
			}

			return m;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

    /**
    * Returns all topics that are watched by a given user.
    * @param userId The user id
    */
    @Override public List<Map<String, Object>> selectWatchesByUser(int userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = JForumExecutionContext.getConnection().prepareStatement(
                    SystemGlobals.getSql("TopicModel.selectWatchesByUser"));
            p.setInt(1, userId);
            rs = p.executeQuery();
            while (rs.next()) {
            	Map<String, Object> m = new ConcurrentHashMap<>();
                m.put("id", Integer.valueOf(rs.getInt("topic_id")));
                m.put("title", rs.getString("topic_title"));
                m.put("forumName", rs.getString("forum_name"));
                list.add(m);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            DbUtils.close(p);
        }
    }

}
