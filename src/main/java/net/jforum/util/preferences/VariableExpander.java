/*
 * Copyright (c) JForum Team
 * All rights reserved.
 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 
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
 * Created on May 31, 2004 by pieter
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pieter Olivier
 * @author Rafael Steil
 */
public class VariableExpander
{
	private transient final VariableStore variables;

	private transient final String pre;
	private transient final String post;

	private transient final Map<String, String> cache;

	public VariableExpander(final VariableStore variables, final String pre, final String post)
	{
		this.variables = variables;
		this.pre = pre;
		this.post = post;
		cache = new HashMap<>();
	}

	public void clearCache()
	{
		cache.clear();
	}

	public String expandVariables(final String source)
	{
		String result = this.cache.get(source);

		if (source == null || result != null) {
			return result;
		}

		int fIndex = source.indexOf(this.pre);

		if (fIndex == -1) {
			return source;
		}

		final StringBuilder stringBuffer = new StringBuilder(source);

		while (fIndex > -1) {
			final int lIndex = stringBuffer.indexOf(this.post);

			final int start = fIndex + this.pre.length();

			if (fIndex == 0) {
				final String varName = stringBuffer.substring(start, start + lIndex - this.pre.length());
				stringBuffer.replace(fIndex, fIndex + lIndex + 1, this.variables.getVariableValue(varName));
			}
			else {
				final String varName = stringBuffer.substring(start, lIndex);
				stringBuffer.replace(fIndex, lIndex + 1, this.variables.getVariableValue(varName));
			}

			fIndex = stringBuffer.indexOf(this.pre);
		}

		result = stringBuffer.toString();

		this.cache.put(source, result);

		return result;
	}
}
