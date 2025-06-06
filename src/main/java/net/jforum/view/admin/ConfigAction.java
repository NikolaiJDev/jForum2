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
 * Created on 15/08/2003 / 20:56:33
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.jforum.JForumExecutionContext;
import net.jforum.context.RequestContext;
import net.jforum.context.ResponseContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.GroupDAO;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.exceptions.ForumException;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.search.SearchFacade;
import net.jforum.view.forum.common.ForumCommon;
import net.jforum.util.I18n;
import net.jforum.util.SafeHtml;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 */
public class ConfigAction extends AdminCommand 
{
	public ConfigAction() {}

	public ConfigAction (RequestContext request, ResponseContext response, SimpleHash context)
	{
		this.request = request;
		this.response = response;
		this.context = context;
	}

	@Override public void list() {
		Properties p = new Properties();
		Iterator<Object> iter = SystemGlobals.fetchConfigKeyIterator();

		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = SystemGlobals.getValue(key);
			p.put(key, value);
		}

		Properties locales = new Properties();

		try (FileInputStream fis = new FileInputStream(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/languages/locales.properties"))
		{
			locales.load(fis);
		}
		catch (IOException e) {
			throw new ForumException(e);
		}

		List<Object> localesList = new ArrayList<>();

		for (Enumeration<Object> e = locales.keys(); e.hasMoreElements();) {
			localesList.add(e.nextElement());
		}

		this.context.put("config", p);
		this.context.put("locales", localesList);
		GroupDAO groupDao = DataAccessDriver.getInstance().newGroupDAO();
        this.context.put("groups", groupDao.selectAll());
		this.context.put("forumTrashcan", Integer.valueOf(SystemGlobals.getIntValue(ConfigKeys.FORUM_TRASHCAN)));
		this.context.put("defaultUserGroup", Integer.valueOf(SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP)));
		this.context.put("allCategories", ForumCommon.getAllCategoriesAndForums(true));
		this.setTemplateName(TemplateKeys.CONFIG_LIST);
	}

	public void editSave()
	{
		this.updateData(this.getConfig());
		this.list();
	}

	protected Properties getConfig()
	{
		Properties p = new Properties();

		Enumeration<String> e = this.request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();

			if (name.startsWith("p_")) {
				p.setProperty(name.substring(name.indexOf('_') + 1), this.request.getParameter(name));
			}
		}

		return p;
	}

	protected void updateData(Properties p)
	{
		for (Iterator<Map.Entry<Object, Object>>  iter = p.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry<Object, Object> entry = iter.next();

			SystemGlobals.setValue((String)entry.getKey(), (String)entry.getValue());
		}

		SystemGlobals.saveInstallation(true);
		I18n.changeBoardDefault(SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT));

		// list of welcome HTML tags and attributes
		SafeHtml.updateConfiguration();

		// languages for stop words might have changed
		SearchFacade.manager().init();

		// If topicsPerPage has changed, force a reload in all forums
		int oldTopicsPerPage = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
		if (oldTopicsPerPage != SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE)) {
			List<Category> categories = ForumRepository.getAllCategories();

			for (Category category : categories) {
				for (Forum forum : category.getForums()) {
					TopicRepository.clearCache(forum.getId());
				}
			}
		}

		// needs to be done especially, as it's only loaded at servlet initialization
		Configuration templateCfg = JForumExecutionContext.getTemplateConfig();
		if (SystemGlobals.getBoolValue(ConfigKeys.DEVELOPMENT)) {
			templateCfg.setTemplateUpdateDelayMilliseconds(2000);
		} else {
			templateCfg.setTemplateUpdateDelayMilliseconds(3600000);
		}
	}
}
