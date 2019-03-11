package algRead;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.solartis.exception.DatabaseException;
import com.solartis.util.common.DatabaseOperation;

public class XMLParse {
	public static Map<String, String> filelistwithpath = new LinkedHashMap<String, String>();
	public static DatabaseOperation db = new DatabaseOperation();
	public static Map<String, String> condition = new LinkedHashMap<String, String>();

	@SuppressWarnings("resource")
	public String XmlFiletoString(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		String line;
		StringBuilder sb = new StringBuilder();

		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
		}
		// System.out.println(sb);
		return sb.toString();
	}

	public void FilterXML(String xml, String xpathExpression, String FormName, String ALGFileName, String VehicleName)
			throws SQLException {
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			// parse = b.parse(new InputSource(new StringReader(xmlString)));
			Document original = b.parse(new InputSource(new StringReader(xml)));
			original.getDocumentElement().normalize();

			Document filtered = b.newDocument();
			Node root = filtered.createElement("LOB");
			filtered.appendChild(root);

			String expression = xpathExpression;
			XPath xPath = XPathFactory.newInstance().newXPath();
			Object result = xPath.compile(expression).evaluate(original, XPathConstants.NODESET);
			NodeList ParentNodeList =  (NodeList) xPath.compile(expression+"/parent::node()").evaluate(original, XPathConstants.NODESET);

			NodeList nodes = (NodeList) result;
			StringBuffer temp1 = new StringBuffer();
			for (int i = 0; i < nodes.getLength(); i++) {

				Node node = nodes.item(i);
				Node ParentNode = ParentNodeList.item(i);
				
				//System.out.println(ParentNode.getAttributes().getNamedItem("op"));
				//temp1 = temp1.append(" If ( ").append(node.getNodeValue()).append(" )");
				//System.out.println(ALGFileName + ": " + VehicleName + ": " + FormName + ": " + node.getNodeValue());
				//System.out.println(ParentNode.getParentNode().getParentNode().getParentNode().getNodeName());
				//System.out.println(ParentNode.getParentNode().getParentNode().getNodeName());
				//System.out.println(ParentNode.getParentNode().getNodeName());
				if(ParentNode.getParentNode().getParentNode().getParentNode().getNodeName().equals("then") && ParentNode.getParentNode().getParentNode().getNodeName().equals("if")) {
					temp1 = temp1.append(" then If ( ").append(node.getNodeValue()).append(" )");
				} else if(ParentNode.getParentNode().getParentNode().getParentNode().getNodeName().equals("else")) {
					temp1 = temp1.append(" else If ( ").append(node.getNodeValue()).append(" )");
				} else if(ParentNode.getParentNode().getNodeName().equals("assign") && ParentNode.getParentNode().getParentNode().getNodeName().equals("then")) {
					temp1 = temp1.append(" then assign ( ").append(ParentNode.getParentNode().getAttributes().getNamedItem("lValue")).append(" = ").append(node.getNodeValue()).append(" )");
				} else if(ParentNode.getParentNode().getParentNode().getParentNode().getNodeName().equals("PCH") && ParentNode.getParentNode().getParentNode().getNodeName().equals("if")){
					temp1 = temp1.append(" If ( ").append(node.getNodeValue()).append(" )");
				}

				// filtered.adoptNode(node);
				// root.appendChild(node);
			}
			if(temp1.length() == 0) {
				
			}else if(condition.isEmpty()) {
				condition.put(temp1.toString(), ALGFileName);
			}else if(condition.containsKey(temp1.toString())) {
				condition.put(temp1.toString(), condition.get(temp1.toString())+", "+ALGFileName);
			}else if(!condition.containsKey(temp1.toString())) {
				condition.put(temp1.toString(), ALGFileName);
			}
			
			
			/*
			 * try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			 * 
			 * Transformer tf = TransformerFactory.newInstance().newTransformer();
			 * tf.setOutputProperty(OutputKeys.INDENT, "yes");
			 * tf.setOutputProperty(OutputKeys.METHOD, "xml");
			 * tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			 * 
			 * DOMSource domSource = new DOMSource(filtered); StreamResult sr = new
			 * StreamResult(os); tf.transform(domSource, sr);
			 * 
			 * String text = new String(os.toByteArray()); System.out.println(text);
			 * 
			 * } catch (TransformerException ex) { ex.printStackTrace(); }
			 */

		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException
				| DOMException exp) {
			exp.printStackTrace();
		}
	}

	public void getALGFilePath(String AllALGFilePath) {
		// File object
		File maindir = new File(AllALGFilePath);
		if (maindir.exists() && maindir.isDirectory()) {
			// array for files and sub-directories
			// of directory pointed by maindir
			File arr[] = maindir.listFiles();
			// Calling recursive method
			RecursivePrint(arr, 0, 0);
		}
	}

	private void RecursivePrint(File[] arr, int index, int level) {

		// terminate condition
		if (index == arr.length)
			return;

		// for files
		if (arr[index].isFile()) {
			if (arr[index].getName().endsWith(".xml") && arr[index].getName().startsWith("ALG-")) {
				filelistwithpath.put(arr[index].getName().substring(7, 9), arr[index].getAbsolutePath());
				// System.out.println(arr[index].getName()
				// +"-----"+arr[index].getAbsolutePath());
			}
		}
		// for sub-directories
		else if (arr[index].isDirectory()) {
			// System.out.println("[" + arr[index].getName() + "]");

			// recursion for sub-directories
			RecursivePrint(arr[index].listFiles(), 0, level + 1);
		}

		// recursion for main directory
		RecursivePrint(arr, ++index, level);
	}

	public static void main(String args[]) throws IOException, DatabaseException, SQLException {
		String maindirpath = "E:\\ERCPackagesCA\\MA\\RC-CA-CW-10012014-V02\\";
		String[] vehicleArr = { "Form Logic", "FormLogic-PrivatePassenger", "FormLogic-PublicTransportati",
				"FormLogic-SpecialTypes", "FormLogic-Truck", "FormLogic-ZoneRated", "FormLogic-Garage",
				"FormLogic-GarageServices" };

		DatabaseOperation.ConnectionSetup("com.mysql.jdbc.Driver",
				"jdbc:mysql://192.168.84.225:3700/CommercialAuto_Development_ADMIN?useSSL=false", "root", "redhat");

		XMLParse xmlparse = new XMLParse();
		
		xmlparse.getALGFilePath(maindirpath);
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> input =db.GetDataObjects("Select * from UniqueFormTableName");
		for (Entry<Integer, LinkedHashMap<String, String>> entry1 : input.entrySet())	
		{
			LinkedHashMap<String, String> inputrow = entry1.getValue();
			if(inputrow.get("Flag").equals("Y"))
			{
				for (int i = 0; i < vehicleArr.length; i++) 
				{
					String exp1 = "//PCH[@excelSheet='" + vehicleArr[i]
							+ "' and @name='"+inputrow.get("FormTableName")+"']//expression/@asText"; ///@asText
		
					for (Map.Entry<String, String> entry : filelistwithpath.entrySet()) 
					{
						String FileString = xmlparse.XmlFiletoString(entry.getValue());
						xmlparse.FilterXML(FileString, exp1, inputrow.get("FormTableName"), entry.getKey(), vehicleArr[i]);
					}
					
					for (Entry<String, String> entry : condition.entrySet()) 
					{				
						String insterQuery = "INSERT INTO Output_FormSelection_Conditions VALUES(temp2)";
						StringBuffer temp2 = new StringBuffer();
						String ALGFormula = entry.getKey();
						ALGFormula = ALGFormula.replaceAll("\'", "\\\\'");
						System.out.println(ALGFormula);
						temp2 = temp2.append("'").append(entry.getValue()).append("'").append(",");
						temp2 = temp2.append("'").append(vehicleArr[i]).append("'").append(",");
						temp2 = temp2.append("'").append(inputrow.get("FormTableName")).append("'").append(",");
						temp2 = temp2.append("'").append(ALGFormula).append("'").append(",");
			
						insterQuery = insterQuery.replace("temp2", temp2.substring(0, temp2.length() - 1));
						temp2 = temp2.delete(0, temp2.length());
						System.out.println(insterQuery);
						db.insertRow(insterQuery);
						
					}
					condition.clear();
				}
			}
		}
		DatabaseOperation.CloseConn();
	}
}
