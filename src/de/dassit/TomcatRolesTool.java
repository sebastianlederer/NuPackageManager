package de.dassit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

// read tomcat user.xml to get all available roles

public class TomcatRolesTool {

	protected String path;

	public List<String> getAll() throws JDOMException, IOException {
		ArrayList<String> result = new ArrayList<String>();

		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(path));
		Element root = doc.getRootElement();
		@SuppressWarnings("rawtypes")
		List roles = root.getChildren("role");

		for (Object o : roles) {
			Element e = (Element) o;
			String name = e.getAttributeValue("rolename");
			result.add(name);
		}
		return result;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public static void main(String[] args) throws JDOMException, IOException {
		TomcatRolesTool trt = new TomcatRolesTool();
		trt.setPath("/etc/tomcat9/tomcat-users.xml");
		for (String s : trt.getAll()) {
			System.out.println(s);
		}
	}
}
