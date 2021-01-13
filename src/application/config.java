package application;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class config {

	private Logger log = LogManager.getLogger(config.class.getName());
	public String file_cfg = null;
	String db_url;
	String db_user;
	String db_password;
	String panel;
	int machineCount= 0;
	ArrayList<Integer> inverters= new ArrayList<Integer>();
	Map<Integer, machine> machine = new TreeMap<Integer, machine>();
	Map<Integer, machine> analog = new TreeMap<Integer, machine>();
	int version;
	config(String cfg) {
		log.entry(cfg);
		file_cfg = cfg;
		read(cfg);
		log.exit();
	}
	synchronized void read(String file) {
		log.entry(file);
		File inputFile = new File(file);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			JOptionPane.showMessageDialog(null, "Errore durante la lettura della configurazione: " + e,
					"Errore nella configurazione", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(inputFile);
		} catch (SAXException | IOException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			JOptionPane.showMessageDialog(null, "Errore durante la lettura della configurazione: " + e,
					"Errore nella configurazione", JOptionPane.ERROR_MESSAGE);
			return;
		}
		doc.getDocumentElement().normalize();
		log.debug("Root element : " + doc.getDocumentElement().getNodeName());

		NodeList nList = null;
		nList = doc.getElementsByTagName("db_conn");
		if (nList.getLength() > 1)
			log.error("Numero di elementi \"db_conn\" maggiore di uno");
		else if (nList.getLength() == 0)
			log.info("No db selected");
		else {
			Element eElement = (Element) nList.item(0);

			db_user = eElement.getAttribute("user").trim();
			db_password = eElement.getAttribute("password").trim();

			db_url = "jdbc:mysql://localhost:3306/desa-logger";
			log.debug("db_conn: " + db_url + ": db_user=" + db_user + ": db_pass=" + db_password);
		}
		
		try {
			panel = parseStringField("panel", doc.getElementsByTagName("panel"));
		} catch (configException e5) {
			e5.printStackTrace();
		}
		
		try {
			machineCount = parseIntField("machineCount", doc.getElementsByTagName("machineCount"));
		} catch (configException e5) {
			e5.printStackTrace();
		}
		try {
			version = parseIntField("version", doc.getElementsByTagName("version"));
		} catch (configException e5) {
			e5.printStackTrace();
		}
		parseMachines(doc.getElementsByTagName("machines"));
		parseAnalogs(doc.getElementsByTagName("analogs"));
	}
	
	String parseStringField(String name, NodeList nList) throws configException {
		log.entry();
		String ret;

		if (nList.getLength() > 1)
			log.error("Numero di elementi \"" + name + "\" maggiore di uno");

		Element eElement = (Element) nList.item(0);
		ret = eElement.getTextContent();
		if (ret == null)
			throw new configException("Elemento " + name + " == null");
		log.debug(name + ": " + ret);
		return log.exit(ret);
	}
	
	int parseIntField(String name, NodeList nList) throws configException {
		log.entry();
		int ret;

		if (nList.getLength() > 1)
			log.error("Numero di elementi \"" + name + "\" maggiore di uno");

		Element eElement = (Element) nList.item(0);
		try {
			ret = Integer.parseInt(eElement.getTextContent());
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new configException(e);
		}

		log.debug(name + ": " + ret);
		return log.exit(ret);
	}
	void parseMachines(NodeList nodeList) {
		NodeList nl = nodeList.item(0).getChildNodes();
		log.debug("----------------------------");
		for (int temp = 0; temp < nl.getLength(); temp++) {
			Node nNode = nl.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getNodeName().equals("machine")) {
				Element eElement = (Element) nNode;
				log.debug("Current Element :" + nNode.getNodeName());

				int id = Integer.parseInt(eElement.getAttribute("id"));
				machine.put(id, new machine(id,
						eElement.getAttribute("name").trim() ,
						Integer.parseInt(eElement.getAttribute("vol")),Integer.parseInt(eElement.getAttribute("inv"))));
			}
		}

	}
	void parseInverters(NodeList nodeList) {
		NodeList nl = nodeList.item(0).getChildNodes();
		log.debug("----------------------------");
		for (int temp = 0; temp < nl.getLength(); temp++) {
			Node nNode = nl.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getNodeName().equals("inverter")) {
				Element eElement = (Element) nNode;
				log.debug("Current Element :" + nNode.getNodeName());
				inverters.add(Integer.parseInt(eElement.getAttribute("id")));
				
				}
		}

	}
	void parseAnalogs(NodeList nodeList) {
		NodeList nl = nodeList.item(0).getChildNodes();
		log.debug("----------------------------");
		for (int temp = 0; temp < nl.getLength(); temp++) {
			Node nNode = nl.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.getNodeName().equals("analog")) {
				Element eElement = (Element) nNode;
				log.debug("Current Element :" + nNode.getNodeName());

				int id = Integer.parseInt(eElement.getAttribute("id"));
				analog.put(id, new machine(id,
						eElement.getAttribute("name").trim() ,
						Integer.parseInt(eElement.getAttribute("vol")),Integer.parseInt(eElement.getAttribute("inv"))));
			}
		}

	}
	public class configException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 157466757016238771L;

		configException(String ex) {
			super(ex);
		}

		public configException(Exception e1) {
			super(e1);
		}
	};
}
