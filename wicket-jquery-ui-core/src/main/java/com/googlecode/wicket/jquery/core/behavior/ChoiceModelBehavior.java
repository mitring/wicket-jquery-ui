/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.wicket.jquery.core.behavior;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;

import com.googlecode.wicket.jquery.core.data.IChoiceProvider;
import com.googlecode.wicket.jquery.core.renderer.ITextRenderer;
import com.googlecode.wicket.jquery.core.template.IJQueryTemplate;
import com.googlecode.wicket.jquery.core.utils.BuilderUtils;
import com.googlecode.wicket.jquery.core.utils.ListUtils;

/**
 * Provides the choice ajax loading behavior
 *
 * @author Sebastien Briquet - sebfz1
 *
 * @param <T> the model object type
 */
public abstract class ChoiceModelBehavior<T> extends AbstractAjaxBehavior implements IChoiceProvider<T>
{
	private static final long serialVersionUID = 1L;

	protected final ITextRenderer<? super T> renderer;
	protected final IJQueryTemplate template;

	/**
	 * Constructor
	 * 
	 * @param renderer the {@link ITextRenderer}
	 */
	public ChoiceModelBehavior(ITextRenderer<? super T> renderer)
	{
		this(renderer, null);
	}

	// Methods //

	/**
	 * Constructor
	 * 
	 * @param renderer the {@link ITextRenderer}
	 * @param template the {@link IJQueryTemplate}
	 */
	public ChoiceModelBehavior(ITextRenderer<? super T> renderer, IJQueryTemplate template)
	{
		this.renderer = renderer;
		this.template = template;
	}

	/**
	 * Gets the property list that should be appended to the JSON response.<br/>
	 * The value corresponding to the property is retrieved from the {@link ITextRenderer#getText(Object, String)}
	 *
	 * @return the property list
	 */
	protected List<String> getProperties()
	{
		if (this.template != null)
		{
			return ListUtils.exclude(this.template.getTextProperties(), this.renderer.getFields()); // #198
		}

		return Collections.emptyList();
	}

	// Events //

	@Override
	public void onRequest()
	{
		// FIXME: called twice! (see LazyMultiSelectPage)
		// TODO: test in wicket-6
		RequestCycle.get().scheduleRequestHandlerAfterCurrent(this.newRequestHandler());
	}

	// Factories //

	/**
	 * Gets a new {@link IRequestHandler} that will call {@link #getChoices()} and will build be JSON response
	 *
	 * @return a new {@code IRequestHandler}
	 */
	protected IRequestHandler newRequestHandler()
	{
		return new ChoiceModelRequestHandler();
	}

	// Classes //

	/**
	 * Provides the {@link IRequestHandler}
	 */
	protected class ChoiceModelRequestHandler implements IRequestHandler
	{
		@Override
		public void respond(final IRequestCycle requestCycle)
		{
			WebResponse response = (WebResponse) requestCycle.getResponse();

			final String encoding = Application.get().getRequestCycleSettings().getResponseRequestEncoding();
			response.setContentType("application/json; charset=" + encoding);
			response.disableCaching();

			List<T> choices = ChoiceModelBehavior.this.getChoices();

			if (choices != null)
			{
				int index = 0;
				StringBuilder builder = new StringBuilder("[");

				for (T choice : choices)
				{
					if (index++ > 0)
					{
						builder.append(", ");
					}

					builder.append("{ ");

					// ITextRenderer //
					builder.append(renderer.render(choice));

					// Additional properties (like template properties) //
					List<String> properties = ChoiceModelBehavior.this.getProperties();

					for (String property : properties)
					{
						builder.append(", ");
						BuilderUtils.append(builder, property, renderer.getText(choice, property));
					}

					builder.append(" }");
				}

				response.write(builder.append("]"));
			}
		}

		@Override
		public void detach(final IRequestCycle requestCycle)
		{
			// noop
		}
	}
}
