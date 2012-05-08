package com.googlecode.wicket.jquery.ui.samples.pages.kendo.splitter;

import com.googlecode.wicket.jquery.ui.kendo.splitter.SplitterBehavior;

public class DefaultSplitterPage extends AbstractSplitterPage
{
	private static final long serialVersionUID = 1L;
	
	public DefaultSplitterPage()
	{
		this.add(new SplitterBehavior("#splitter"));
	}
}
