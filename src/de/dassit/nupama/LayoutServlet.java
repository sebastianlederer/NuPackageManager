package de.dassit.nupama;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityLayoutServlet;

// We need our own LayoutServlet class because VelocityLayoutServlet does not
// work correctly. VelocityLayoutServlet overrides the deprecated version
// of the mergeTemplate method (without the HttpServletRequest parameter),
// which never gets called.

public class LayoutServlet extends VelocityLayoutServlet {
	private static final long serialVersionUID = -8743195198376764930L;

	/**
	 * Overrides VelocityViewServlet.mergeTemplate to do a two-pass render for
	 * handling layouts
	 * 
	 * @param template {@link Template} object
	 * @param context  Velocity context
	 * @param response servlet response
	 */
	@Override
	protected void mergeTemplate(Template template, Context context, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//
		// this section is based on Tim Colson's "two pass render"
		//
		// Render the screen content
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		// Add the resulting content to the context
		context.put(KEY_SCREEN_CONTENT, sw.toString());

		// Check for an alternate layout
		//
		// we check after merging the screen template so the screen
		// can overrule any layout set in the request parameters
		// by doing #set( $layout = "MyLayout.vm" )
		Object obj = context.get(KEY_LAYOUT);
		String layout = (obj == null) ? null : obj.toString();
		if (layout == null) {
			// no alternate, use default
			layout = defaultLayout;
		} else {
			// make it a full(er) path
			layout = layoutDir + layout;
		}

		try {
			// load the layout template
			template = getTemplate(layout);
		} catch (Exception e) {
			getLog().error("Can't load layout \"{}\"", layout, e);

			// if it was an alternate layout we couldn't get...
			if (!layout.equals(defaultLayout)) {
				// try to get the default layout
				// if this also fails, let the exception go
				template = getTemplate(defaultLayout);
			}
		}

		// Render the layout template into the response
		super.mergeTemplate(template, context, request, response);
	}
}
