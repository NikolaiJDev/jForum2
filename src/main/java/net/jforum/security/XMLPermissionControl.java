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
 * Created on 21/09/2003 / 16:36:44
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.security;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.jforum.JForumExecutionContext;
import net.jforum.exceptions.DatabaseException;
import net.jforum.exceptions.ForumException;
import net.jforum.util.DbUtils;
import net.jforum.util.FormSelectedData;
import net.jforum.util.I18n;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Manipulates XML permission control file definition 
 * 
 * @author Rafael Steil
 */
public class XMLPermissionControl extends DefaultHandler 
{
    private transient PermissionSection section;
	private transient final PermissionControl permissionControl;
	private transient final List<PermissionSection> listSections;
	private transient List<FormSelectedData> permissionData;
	private transient final Map<String, List<SelectData>> queries;
	private String permissionName;
	private String permissionId;
	private String permissionType;
	
	private boolean alreadySelected;
	
	private static class SelectData
	{
		final private int id;
		final private String name;
		
		public SelectData(final int id, final String name)
		{
			this.id = id;
			this.name = name;
		}
		
		public int getId()
		{
			return this.id;
		}
		
		public String getName()
		{
			return this.name;
		}
	}
	
	public XMLPermissionControl(final PermissionControl permissionControl)
	{
		this.listSections = new ArrayList<>();
		this.permissionData = new ArrayList<>();
		this.queries = new ConcurrentHashMap<>();
		this.permissionControl = permissionControl;
	}

	/**
	 * @return <code>List</code> object containing <code>Section</code> objects. Each
	 * <code>Section</code>  contains many <code>PermissionItem</code> objects, 
	 * which represent the permission elements of some section. For its turn, the
	 * <code>PermissionItem</code> objects have many <code>FormSelectedData</code>
	 * objects, which are the ones responsible to store field values, and which values
	 * are checked and which not.
     * @param xmlFile String
	 */
	public List<PermissionSection> loadConfigurations(final String xmlFile)
	{
        try
        {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final SAXParser parser = factory.newSAXParser();
            final File fileInput = new File(xmlFile);

            if (fileInput.exists()) {
                parser.parse(fileInput, this);
            }
            else {
                final InputSource inputSource = new InputSource(xmlFile);
                parser.parse(inputSource, this);
            }

            return this.listSections;
        }
        catch (Exception e)
        {
            throw new ForumException(e);
        }
    }

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	@Override public void endElement(final String namespaceURI, final String localName, final String tag)
		throws SAXException 
	{
		if ("section".equals(tag)) {
			this.listSections.add(this.section);
		}
		else if ("permission".equals(tag)) {
			this.section.addPermission(new PermissionItem(this.permissionName, this.permissionId, this.permissionType, this.permissionData));

			this.permissionData = new ArrayList<>();
		}
	}

	/**
	 * @see org.xml.sax.ErrorHandler#error(SAXParseException)
	 */
	@Override public void error(final SAXParseException exception) throws SAXException 
	{
		throw exception;
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	@Override public void startElement(
		final String namespaceURI,
		final String localName,
		final String tag,
		final Attributes atts)
		throws SAXException 
	{
		if ("section".equals(tag)) {
			final String title = I18n.getMessage(atts.getValue("title"));
			this.section = new PermissionSection(title, atts.getValue("id"));
		}
		else if ("permission".equals(tag)) {
			final String title = I18n.getMessage(atts.getValue("title"));
			
			this.permissionName = title;
			this.permissionId = atts.getValue("id");
			this.permissionType = atts.getValue("type");
			this.alreadySelected = false;
		}
		else if ("sql".equals(tag)) {
			final String refName = atts.getValue("refName");
			
			// If refName is present, then we have a template query
			if (refName != null) {
                ResultSet resultSet = null;
                PreparedStatement pstmt = null;
                
				try {
					pstmt = JForumExecutionContext.getConnection().prepareStatement(
						SystemGlobals.getSql(atts.getValue("queryName")));
					resultSet = pstmt.executeQuery();
					
					final String valueField = atts.getValue("valueField");
					final String captionField = atts.getValue("captionField");
					
					final List<SelectData> list = new ArrayList<>();
					
					while (resultSet.next()) {
						list.add(new SelectData(resultSet.getInt(valueField), resultSet.getString(captionField)));
					}
					
					this.queries.put(refName, list);
				}
				catch (Exception e) {
                    throw new DatabaseException(e);
				}
				finally {
                    DbUtils.close(resultSet, pstmt);
				}
			}
			else {
				// If it gets here, then it should be a <sql ref="xxxx"> section
				RoleValueCollection roleValues = new RoleValueCollection();
				Role role = this.permissionControl.getRole(this.permissionId);
				
				if (role != null) {
					roleValues = role.getValues();
				}
				
				List<SelectData> list = this.queries.get(atts.getValue("ref"));
				
				for (SelectData data : list) {
					String id = Integer.toString(data.getId());
					RoleValue roleValue = roleValues.get(id);

					this.permissionData.add(new FormSelectedData(data.getName(), id, roleValue == null));
				}
			}
		}
		else if ("option".equals(tag)) {
			boolean selected = false;
			
			if (this.permissionType.equals("single")) {
				if (this.permissionControl.canAccess(this.permissionId) && atts.getValue("value").equals("allow") && !this.alreadySelected) {
					selected = true;
					this.alreadySelected = true;
				}
			}
			else {
				throw new UnsupportedOperationException("'option' tag with 'multiple' attribute support not yet implemented");
			}
			
			this.permissionData.add(new FormSelectedData(
				I18n.getMessage(atts.getValue("description")), atts.getValue("value"), selected));
		}
	}
}
