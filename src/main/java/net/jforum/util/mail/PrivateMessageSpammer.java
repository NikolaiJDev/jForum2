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
 * Created on 20/06/2004 03:30:58
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.mail;

import java.util.ArrayList;
import java.util.List;

import freemarker.template.SimpleHash;
import net.jforum.JForumExecutionContext;
import net.jforum.entities.User;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.view.forum.common.ViewCommon;

/**
 * @author Rafael Steil
 */
public class PrivateMessageSpammer extends Spammer
{
	public PrivateMessageSpammer (final User user)
	{
		super();
		if (user.getEmail() == null || user.getEmail().trim().equals("")) {
			return;
		}

		final String forumLink = new StringBuilder()
			.append(ViewCommon.getForumLink())
			.append("pm/inbox")
			.append(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION))
			.toString();

		final SimpleHash params = JForumExecutionContext.newSimpleHash();
		params.put("path", forumLink);
		params.put("user", user);

		final List<User> recipients = new ArrayList<>();
		recipients.add(user);

		this.setUsers(recipients);
		this.setTemplateParams(params);

		final String subject = SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_SUBJECT).replaceAll("'", "\u2019");

		super.prepareMessage(subject, SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_MESSAGE_FILE));
	}
}
