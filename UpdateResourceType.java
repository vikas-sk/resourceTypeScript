package com.quantumhub.core.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.query.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

public class UpdateResourceType extends SlingAllMethodsServlet {

	@Reference
	ResourceResolverFactory resolverFactory = null;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

//		 final Map <String, Object > param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, (Object) "getResourceResolver"); 
//		 ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param); 
		ResourceResolver resolver = request.getResourceResolver();

		out.println("starting...");

		try {
			;

			if (resolver != null) {
				String sourceComponent = request.getParameter("sourceComponent").replace("__", "/");
				String targetComponent = request.getParameter("targetComponent").replace("__", "/");
				Iterator<Resource> resources = resolver.findResources(
						"/jcr:root/content/mysite//*[(jcr:like(@sling:resourceType='" + sourceComponent + "'))]",
						Query.XPATH);

				while (resources.hasNext()) {
					Resource resource = resources.next();
					ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
					properties.put("sling:resourceType", targetComponent);
					resolver.commit();
					out.println(resource.getPath());
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(out);
		} finally {
			if (resolver != null && resolver.isLive()) {
				resolver.close();
				resolver = null;
			}
		}

		out.println("...finished");
	}
}