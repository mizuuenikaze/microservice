/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.muk.ext.core.json.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.json.RestReply;

@JsonInclude(Include.NON_NULL)
public class BlogSliceSummary extends RestReply {
	private List<BlogPage> prevpages;
	private List<BlogPage> nextpages;
	private List<BlogEntrySummary> rows;
	private Long offset;

	public List<BlogPage> getPrevpages() {
		return prevpages;
	}

	public void setPrevpages(List<BlogPage> prevpages) {
		this.prevpages = prevpages;
	}

	public List<BlogPage> getNextpages() {
		return nextpages;
	}

	public void setNextpages(List<BlogPage> nextpages) {
		this.nextpages = nextpages;
	}

	public List<BlogEntrySummary> getRows() {
		return rows;
	}

	public void setRows(List<BlogEntrySummary> rows) {
		this.rows = rows;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}
}
