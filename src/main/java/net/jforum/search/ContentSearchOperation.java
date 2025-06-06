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
 * Created on 25/07/2007 19:36:18
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.search;

import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.Post;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.view.forum.common.PostCommon;

/**
 * @author Rafael Steil
 */
public class ContentSearchOperation extends SearchOperation
{
    private transient List<Post> results = new ArrayList<>();
	
	@Override public SearchResult<Post> performSearch(final SearchArgs args)
	{
        final SearchResult<Post> searchResult =
				(args.getKeywords().length > 0 || args.getUserIds().length > 0)
				? SearchFacade.search(args)
				: new SearchResult<Post>(new ArrayList<>(), 0);

		this.results = searchResult.getRecords();

		return searchResult;
	}
	
	@Override public void prepareForDisplay()
	{
		for (Post post : this.results) {
			PostCommon.preparePostForDisplay(post);
			// enable highlighting in CODE blocks
			// must match what is used in LuceneContentCollector.retrieveRealPosts
			// Transforms
			//     &lt;span class='sr'&gt;foo&lt;/span&gt;
			// back to
			//     <span class='sr'>foo</span>
			post.setSubject(post.getSubject().replaceAll("&lt;span class='sr'&gt;(.*?)&lt;/span&gt;", "<span class='sr'>$1</span>"));
			post.setText(post.getText().replaceAll("&lt;span class='sr'&gt;(.*?)&lt;/span&gt;", "<span class='sr'>$1</span>"));
		}
	}

	@Override public List<Post> getResults()
	{
		return this.results;
	}

	@Override public int totalRecords()
	{
		return this.results.size();
	}

	@Override public String viewTemplate()
	{
		return TemplateKeys.SEARCH_SEARCH;
	}
	
	@Override public int extractForumId(final Object value)
	{
		return ((Post)value).getForumId();
	}
}
