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
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
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
 * This file creation date: 03/08/2003 / 05:28:03
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.bbcode;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.jforum.exceptions.ForumException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class BBCodeHandler extends DefaultHandler implements Serializable
{
	private static final long serialVersionUID = 8729382987305842571L;
	private Map<String, BBCode> bbMap = new LinkedHashMap<>();
	private Map<String, BBCode> alwaysProcessMap = new LinkedHashMap<>();
	private String tagName = "";
	private StringBuilder sb;
	private BBCode bb;

	public BBCodeHandler() { }

	public BBCodeHandler parse()
	{
		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final SAXParser parser = factory.newSAXParser();
			BBCodeHandler bbParser = new BBCodeHandler();

			String path = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/bb_config.xml";

			File fileInput = new File(path);

			if (fileInput.exists()) {
				parser.parse(fileInput, bbParser);
			} else {
				InputSource input = new InputSource(path);
				parser.parse(input, bbParser);
			}

			return bbParser;
		} catch (Exception e) {
			throw new ForumException(e);
		}
	}

	public void addBb (BBCode bb)
	{
		if (bb.alwaysProcess()) {
			this.alwaysProcessMap.put(bb.getTagName(), bb);
		} else {
			this.bbMap.put(bb.getTagName(), bb);
		}
	}

	public Collection<BBCode> getBbList()
	{
		return this.bbMap.values();
	}

	public Collection<BBCode> getAlwaysProcessList()
	{
		return this.alwaysProcessMap.values();
	}

	public BBCode findByName(String tagName)
	{
		return this.bbMap.get(tagName);
	}

	@Override public void startElement (String uri, String localName, String tag, Attributes attrs)
	{
		if ("match".equals(tag)) {
			this.sb = new StringBuilder();
			this.bb = new BBCode();

			String tagName = attrs.getValue("name");
			if (tagName != null) {
				this.bb.setTagName(tagName);
			}

			// Shall we remove the infamous quotes?
			String removeQuotes = attrs.getValue("removeQuotes");
			if (removeQuotes != null && "true".equals(removeQuotes)) {
				this.bb.enableRemoveQuotes();
			}

			String alwaysProcess = attrs.getValue("alwaysProcess");
			if (alwaysProcess != null && "true".equals(alwaysProcess)) {
				this.bb.enableAlwaysProcess();
			}

			String lockedTag = attrs.getValue("lockedForSmilies");
			if (lockedTag != null) {
				this.bb.setLockedForSmilies(lockedTag);
			}
		}

		this.tagName = tag;
	}

	@Override public void endElement (String uri, String localName, String tag)
	{
		if (tag.equals("match")) {
			this.addBb(this.bb);
		}
		else if (this.tagName.equals("replace")) {
			this.bb.setReplace(this.sb.toString().trim());
			this.sb.delete(0, this.sb.length());
		}
		else if (this.tagName.equals("rssReplace")) {
			this.bb.setRssReplace(this.sb.toString().trim());
			this.sb.delete(0, this.sb.length());
		}
		else if (this.tagName.equals("regex")) {
			this.bb.setRegex(this.sb.toString().trim());
			this.sb.delete(0, this.sb.length());
		}
		else if (this.tagName.equals("class-name")) {
			this.bb.setClassName(this.sb.toString().trim());
			this.sb.delete(0, this.sb.length());
		}

		this.tagName = "";
	}

	@Override public void characters (char ch[], int start, int length)
	{
		if (this.tagName.equals("replace")
				|| this.tagName.equals("rssReplace")
				|| this.tagName.equals("regex")
				|| this.tagName.equals("class-name")) {
			this.sb.append(ch, start, length);
        }
	}

	@Override public void error (SAXParseException saxpe) throws SAXException
	{
		throw saxpe;
	}
}
