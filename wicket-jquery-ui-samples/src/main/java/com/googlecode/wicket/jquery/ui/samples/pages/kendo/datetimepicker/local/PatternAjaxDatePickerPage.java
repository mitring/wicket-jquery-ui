package com.googlecode.wicket.jquery.ui.samples.pages.kendo.datetimepicker.local;

import java.util.Locale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.threeten.bp.LocalDate;

import com.googlecode.wicket.jquery.core.utils.LocaleUtils;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.button.Button;
import com.googlecode.wicket.kendo.ui.form.datetime.local.AjaxDatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DatePicker;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class PatternAjaxDatePickerPage extends AbstractTimePickerPage
{
	private static final long serialVersionUID = 1L;

	public PatternAjaxDatePickerPage()
	{
		final Form<?> form = new Form<Void>("form");
		this.add(form);

		// FeedbackPanel //
		final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");
		form.add(feedback);

		// DatePicker //
		final String pattern = LocaleUtils.getLocaleDatePattern(Locale.FRENCH); // gives the french date pattern, ie: dd/MM/yyyy  

		final DatePicker datepicker = new AjaxDatePicker("datepicker", Model.of(LocalDate.now()), pattern) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onValueChanged(IPartialPageRequestHandler handler)
			{
				this.info("Value Changed: " + this.getModelObject());

				handler.add(feedback);
			}
		};

		form.add(datepicker);

		// Buttons //
		form.add(new Button("submit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit()
			{
				this.info("Date: " + datepicker.getModelObject());
			}
		});

		form.add(new AjaxButton("button") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				this.info("Date: " + datepicker.getModelObject());
				target.add(form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form)
			{
				target.add(feedback);
			}
		});
	}
}
