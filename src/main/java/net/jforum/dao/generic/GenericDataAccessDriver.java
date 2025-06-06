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
 * Created on Mar 3, 2003 / 2:19:47 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import net.jforum.dao.ApiDAO;
import net.jforum.dao.AttachmentDAO;
import net.jforum.dao.BanlistDAO;
import net.jforum.dao.BannerDAO;
import net.jforum.dao.BookmarkDAO;
import net.jforum.dao.CategoryDAO;
import net.jforum.dao.ConfigDAO;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.ForumDAO;
import net.jforum.dao.GroupDAO;
import net.jforum.dao.GroupSecurityDAO;
import net.jforum.dao.KarmaDAO;
import net.jforum.dao.LuceneDAO;
import net.jforum.dao.MailIntegrationDAO;
import net.jforum.dao.ModerationDAO;
import net.jforum.dao.ModerationLogDAO;
import net.jforum.dao.PollDAO;
import net.jforum.dao.PostDAO;
import net.jforum.dao.PrivateMessageDAO;
import net.jforum.dao.RankingDAO;
import net.jforum.dao.RegEmailDAO;
import net.jforum.dao.SmilieDAO;
import net.jforum.dao.SpamDAO;
import net.jforum.dao.SummaryDAO;
import net.jforum.dao.TopicDAO;
import net.jforum.dao.TreeGroupDAO;
import net.jforum.dao.UserDAO;
import net.jforum.dao.UserSessionDAO;
import net.jforum.dao.generic.security.GenericGroupSecurityDAO;

/**
 * @author Rafael Steil
 */
public class GenericDataAccessDriver extends DataAccessDriver 
{
	private static GroupDAO groupDao = new GenericGroupDAO();
	private static PostDAO postDao = new GenericPostDAO();
	private static PollDAO pollDao = new GenericPollDAO();
	private static RankingDAO rankingDao = new GenericRankingDAO();
	private static TopicDAO topicDao = new GenericTopicDAO();
	private static UserDAO userDao = new GenericUserDAO();
	private static TreeGroupDAO treeGroupDao = new GenericTreeGroupDAO();
	private static SmilieDAO smilieDao = new GenericSmilieDAO();
	private static GroupSecurityDAO groupSecurityDao = new GenericGroupSecurityDAO();
	private static PrivateMessageDAO privateMessageDao = new GenericPrivateMessageDAO();
	private static UserSessionDAO userSessionDao = new GenericUserSessionDAO();
	private static KarmaDAO karmaDao = new GenericKarmaDAO();
	private static BookmarkDAO bookmarkDao = new GenericBookmarkDAO();
	private static AttachmentDAO attachmentDao = new GenericAttachmentDAO();
	private static ModerationDAO moderationDao = new GenericModerationDAO();
	private static ForumDAO forumDao = new GenericForumDAO();
	private static CategoryDAO categoryDao = new GenericCategoryDAO();
	private static ConfigDAO configDao = new GenericConfigDAO();
	private static BannerDAO bannerDao = new GenericBannerDAO();
    private static SummaryDAO summaryDao = new GenericSummaryDAO();
    private static MailIntegrationDAO mailIntegrationDao = new GenericMailIntegrationDAO();
    private static ApiDAO apiDAO = new GenericApiDAO();
    private static BanlistDAO banlistDao = new GenericBanlistDAO();
    private static ModerationLogDAO moderationLogDao = new GenericModerationLogDAO();
    private static LuceneDAO luceneDao = new GenericLuceneDAO();
    private static SpamDAO spamDao = new GenericSpamDAO();
    private static RegEmailDAO regEmailDao = new GenericRegEmailDAO();

	/**
	 * @see net.jforum.dao.DataAccessDriver#newForumDAO()
	 */
	@Override public ForumDAO newForumDAO() 
	{
		return forumDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newGroupDAO()
	 */
	@Override public GroupDAO newGroupDAO() 
	{
		return groupDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newPostDAO()
	 */
	@Override public PostDAO newPostDAO() 
	{
		return postDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newPollDAO()
	 */
	@Override public PollDAO newPollDAO() 
	{
		return pollDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newRankingDAO()
	 */
	@Override public RankingDAO newRankingDAO() 
	{
		return rankingDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newTopicDAO()
	 */
	@Override public TopicDAO newTopicDAO() 
	{
		return topicDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newUserDAO()
	 */
	@Override public UserDAO newUserDAO() 
	{
		return userDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newCategoryDAO()
	 */
	@Override public CategoryDAO newCategoryDAO() 
	{
		return categoryDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newTreeGroupDAO()
	 */
	@Override public TreeGroupDAO newTreeGroupDAO() 
	{
		return treeGroupDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newSmilieDAO()
	 */
	@Override public SmilieDAO newSmilieDAO() 
	{
		return smilieDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newGroupSecurityDAO()
	 */
	@Override public GroupSecurityDAO newGroupSecurityDAO() 
	{
		return groupSecurityDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newPrivateMessageDAO()
	 */
	@Override public PrivateMessageDAO newPrivateMessageDAO() 
	{
		return privateMessageDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newUserSessionDAO()
	 */
	@Override public UserSessionDAO newUserSessionDAO()
	{
		return userSessionDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newConfigDAO()
	 */
	@Override public ConfigDAO newConfigDAO()
	{
		return configDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newKarmaDAO()
	 */
	@Override public KarmaDAO newKarmaDAO()
	{
		return karmaDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newBookmarkDAO()
	 */
	@Override public BookmarkDAO newBookmarkDAO()
	{
		return bookmarkDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newAttachmentDAO()
	 */
	@Override public AttachmentDAO newAttachmentDAO()
	{
		return attachmentDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newModerationDAO()
	 */
	@Override public ModerationDAO newModerationDAO()
	{
		return moderationDao;
	}

    /**
     * @see net.jforum.dao.DataAccessDriver#newBannerDAO()
     */
	@Override public BannerDAO newBannerDAO()
	{
		return bannerDao;
	}
    
    /**
     * @see net.jforum.dao.DataAccessDriver#newSummaryDAO()
     */
    @Override public SummaryDAO newSummaryDAO()
    {
        return summaryDao;
    }
    
    /**
     * @see net.jforum.dao.DataAccessDriver#newMailIntegrationDAO()
     */
    @Override public MailIntegrationDAO newMailIntegrationDAO()
    {
    	return mailIntegrationDao;
    }

    /**
     * @see net.jforum.dao.DataAccessDriver#newApiDAO()
     */
    @Override public ApiDAO newApiDAO()
    {
    	return apiDAO;
    }

    /**
     * @see net.jforum.dao.DataAccessDriver#newBanlistDAO()
     */
    @Override public BanlistDAO newBanlistDAO()
    {
    	return banlistDao;
    }

    /**
     * @see net.jforum.dao.DataAccessDriver#newModerationLogDAO()
     */
    @Override public ModerationLogDAO newModerationLogDAO()
    {
    	return moderationLogDao;
    }

    /**
     * @see net.jforum.dao.DataAccessDriver#newLuceneDAO()
     */
    @Override public LuceneDAO newLuceneDAO()
    {
    	return luceneDao;
    }

    /**
     * @see net.jforum.dao.DataAccessDriver#newSpamDAO()
     */
    @Override public SpamDAO newSpamDAO()
    {
    	return spamDao;
    }

    /**
     * @see net.jforum.dao.DataAccessDriver#newRegEmailDAO()
     */
    @Override public RegEmailDAO newRegEmailDAO()
    {
    	return regEmailDao;
    }

}
