package com.grsoft.mkconf;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Mkconf {
	
	private static String pkgName = "";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("mkconf starting at " + currentTime());
		boolean updManResult = updateManifest();
		System.out.println("mkconf finished: " + updManResult);
	}
	
	public static boolean updateManifest(){
		String baseManifestPath = System.getenv("BASE_MANIFEST_PATH");
		
		if (baseManifestPath == null){
			System.out.println("BASE_MANIFEST isn't configured");
			return false;
		}
			
		File bmFile = new File(baseManifestPath);
		
		if (!bmFile.exists()){
			System.out.println("Base manifest not found");
			return false;
		}
		
		File currentDir = new File(".");
		/*Строка для отладки! Удалить, и расскоментировать строчку выше*/
		//File currentDir = 
		//	new File("D:\\GRSoft\\Napoleon.ce\\Core\\Napoleon.A\\dev");
		
		File targetFile = new  File(currentDir, "AndroidManifest.xml");
		
		if (!targetFile.exists()){
			System.out.println("The project doesn't contain AndroidManifest.xml");
			return false;
		}
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			Document baseManDoc = domBuilder.parse(bmFile);
			Element root = baseManDoc.getDocumentElement();
			Document targetDoc = domBuilder.parse(targetFile);
			
			pkgName = getPackageName(baseManDoc);
			Visitor visit = new Visitor();
			updateTarget(root, targetDoc, visit);
			
			if (visit.isChanged()){
				saveTargetDoc(targetFile, targetDoc);
				System.out.println("The manifest has been changed you have to refresh the project before run.");
			}

			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static void updateAttrs(Element source, Element target, Visitor visitor) {
		if(source.hasAttributes()){
			NamedNodeMap attrMap = ((Node)source).getAttributes();
			
			for(int i = 0; i < attrMap.getLength(); i++){
				Node node = attrMap.item(i);

				if(node.getNodeName().equals("android:name"))
					continue;
				
				Node updNode = target.getAttributeNode(node.getNodeName());
				
				if (updNode == null || 
						(!node.getNodeValue().equals(updNode.getNodeValue())))
				{
					target.setAttribute(node.getNodeName(), node.getNodeValue());
					visitor.setChanged();
				}
			}
		}
	}
	
	private static void saveTargetDoc(File outputFile, Document outputDoc)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("indent","yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		DOMSource source = new DOMSource(outputDoc);
		StreamResult streamResult =  new StreamResult(outputFile);
		transformer.transform(source, streamResult);
	}
	
	private static String currentTime(){
		final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(calendar.getTime());
	}
	
	public static void updateTarget(Element el, Node target, Visitor visitor){
		NodeList nodeList = null;
		
		if (target instanceof Document)
			nodeList = ((Document)target)
				.getElementsByTagName(el.getNodeName());
		else
			if(target instanceof Element)
				nodeList = ((Element)target)
					.getElementsByTagName(el.getNodeName());

		if (nodeList == null)
			return;
		
		boolean nodeFounded = false;
		
		for(int i = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			
			if(node.getNodeType() == Node.ELEMENT_NODE){
				if (cmpAndroidNames(el, node)){
					updateAttrs(el, (Element) node, visitor);
					
					if (el.getNodeValue() != null && node.getNodeValue() != null &&
							!el.getNodeValue().equals(node.getNodeValue()))
					{
						node.setNodeValue(el.getNodeValue());
						visitor.setChanged();
					}
					
					NodeList childNodes = el.getChildNodes();
					for(int ci = 0; ci < childNodes.getLength(); ci++){
						Node childNode = childNodes.item(ci);
						
						if (childNode.getNodeType() == Node.ELEMENT_NODE)
							updateTarget((Element)childNode, node, visitor);
					}
					
					return;
				}
			}
		}
		
		if (!nodeFounded){
			Node newNode = ((Element)target).getOwnerDocument().importNode(el, true);
			target.appendChild(newNode);
			visitor.setChanged();
		}
	}

	private static boolean cmpAndroidNames(Element el, Node node) {
		String name1 = ((Element)node).getAttribute("android:name");
		String name2 = el.getAttribute("android:name");
	
		if(name1.startsWith("."))
				name1 = name1.substring(1);
		
		if(name2.startsWith("."))
			name2 = name2.substring(1);
		
		return name1.equals(name2) || (pkgName + name1).equals(name2)
			|| (name1).equals(pkgName +  name2) || (pkgName + name1).equals(pkgName + name2);
				
	}
	
	public static String getPackageName(Document doc){
		NodeList nodeList = doc.getElementsByTagName("manifest");
		
		if (nodeList.getLength() == 1)
			return nodeList.item(0).getAttributes().getNamedItem("package").getNodeValue();
		
		return "";
	}
}

class Visitor{
	private boolean changed = false;
	
	public boolean isChanged(){
		return changed;
	}
	
	public void setChanged(){
		if (!changed)
			changed = true;
	}
}