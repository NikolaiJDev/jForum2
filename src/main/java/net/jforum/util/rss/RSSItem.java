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
 * Created on 20/10/2004 22:58:48
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.rss;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single RSS piece of content.
 * 
 * @author Rafael Steil
 */
public class RSSItem 
{
	private String author;
	private String link;
	private String title;
	private String description;
	private String contentType;
	private String publishDate;
	private transient final List<String> categories;
	
	public RSSItem() 
	{
		this.categories = new ArrayList<>();
	}
	
	/**
	 * Gets the item's author
	 * @return the author
	 */
	public String getAuthor() 
	{
		return this.author;
	}
	
	/**
	 * Sets the item's author
	 * @param author 
	 */
	public void setAuthor(final String author) 
	{
		this.author = author;
	}
	
	/**
	 * Gets the document's description content-type
	 * @return The content-type, generally represented by 
	 * <code>text/html</code> or <code>text/plain</code>
	 */
	public String getContentType() 
	{
		return this.contentType;
	}
	
	/**
	 * Sets the document's description content-type
	 * @param contentType <code>text/html</code> or <code>text/plain</code>
	 */
	public void setContentType(final String contentType) 
	{
		this.contentType = contentType;
	}

	/**
	 * Gets the document's description
	 * @return the description
	 */
	public String getDescription() 
	{
		return this.description;
	}
	
	/**
	 * Sets the document description
	 * @param description
	 */
	public void setDescription(final String description) 
	{
		this.description = description;
	}
	
	/**
	 * Gets the document's link 
	 * @return the link
	 */
	public String getLink() 
	{
		return this.link;
	}
	
	/**
	 * Sets the document's link
	 * @param link
	 */
	public void setLink(final String link) 
	{
		this.link = link;
	}
	
	/**
	 * Gets the document's title
	 * @return the title
	 */
	public String getTitle() 
	{
		return this.title;
	}
	
	/**
	 * Sets the document's the title
	 * @param title
	 */
	public void setTitle(final String title) 
	{
		this.title = title;
	}
	
	/**
	 * Sets the content publication date and time
	 * @param date
	 */
	public void setPublishDate(final String date)
	{
		this.publishDate = date;
	}
	
	/**
	 * Gets the document publication date
	 * @return the publishDate
	 */
	public String getPublishDate()
	{
		return this.publishDate;
	}
	
	/**
	 * Associated a new category to this item.
	 * It is possible to associate multiple categories to 
	 * each item 
	 * @param category The category name
	 */
	public void addCategory(final String category)
	{
		this.categories.add(category);
	}
	
	/**
	 * Gets the categories for this item
	 * @return the categories
	 */
	public List<String> getCategories()
	{
		return this.categories;
	}
}
