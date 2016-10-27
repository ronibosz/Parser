package pl.parser.nbp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class Rates {
	
	private List<Double> bidRatesList = new ArrayList<>();
	private List<Double> askRatesList = new ArrayList<>();
	
	public Rates(String currency, String dateFrom, String dateTo){
		
		getRates(currency, dateFrom, dateTo);
	}
	
	public void getRates(String aCurrency, String aDateFrom, String aDateTo){
		
		double bidRate;
		double askRate;
		String txtFilename;
		String xmlFilename;
		String currentDate = aDateFrom;
		
		do {
			txtFilename = getTxtFilename(currentDate);
			xmlFilename = getXMLFilename(txtFilename, currentDate);
			
			if (xmlFilename != null){
				
				bidRate = getRatesFromXML(xmlFilename, aCurrency)[0];
				bidRatesList.add(bidRate);
				
				askRate = getRatesFromXML(xmlFilename, aCurrency)[1];
				askRatesList.add(askRate);
			}
			
			currentDate = incrementDateByOneDay(currentDate);
		} while (! currentDate.equals(incrementDateByOneDay(aDateTo)));
	}
	
	private String getTxtFilename(String date){
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int dateYear = Integer.parseInt(date.substring(0, 4));
		
		if (currentYear == dateYear)
			return "dir.txt";
		else
			return "dir"+dateYear+".txt";
	}
	
	private String getXMLFilename(String txtFilename, String date){
		
		String currentLine;
		String currentLineSubstring;
		String dateToCompare = formatDateToYYMMDD(date);
		URL txtFileURL;
		
		try {
			
			txtFileURL = new URL("http://nbp.pl/kursy/xml/"+txtFilename);
			URLConnection urlConnection = txtFileURL.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				
			while((currentLine = bufferedReader.readLine()) != null){
					
				currentLineSubstring = currentLine.substring(0, 1) + currentLine.substring(5, 11);
					
				if(currentLineSubstring.equals("c" + dateToCompare))
					return currentLine+".xml";	
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return null; 
	}
	
	private String formatDateToYYMMDD(String date){
		 return date.replaceAll("-", "").substring(2, 8);
	 }
	
	private Double[] getRatesFromXML(String xmlFilename, String currencySymbol){
		 
		 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse("http://www.nbp.pl/kursy/xml/"+xmlFilename);
			XPath xPath = XPathFactory.newInstance().newXPath();
			String currencyCode = (String) xPath.evaluate("/tabela_kursow/pozycja[1]/kod_waluty/text()", document, XPathConstants.STRING);
			int i = 1;
				
			while (!currencyCode.equals(currencySymbol) && i<15){
				i++;
				currencyCode = (String) xPath.evaluate("/tabela_kursow/pozycja["+ i +"]/kod_waluty/text()", document, XPathConstants.STRING);
			}
				
			String bidRate = (String) xPath.evaluate("/tabela_kursow/pozycja["+ i +"]/kurs_kupna/text()", document, XPathConstants.STRING);
			String askRate = (String) xPath.evaluate("/tabela_kursow/pozycja["+ i +"]/kurs_sprzedazy/text()", document, XPathConstants.STRING);
				
			
			return new Double[] {Double.parseDouble(bidRate.replace(',', '.')), Double.parseDouble(askRate.replace(',', '.'))};
				
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
			
		return new Double[] {0d, 0d};
	}
	
	private String incrementDateByOneDay(String date){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(date));
			calendar.add(Calendar.DATE, 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return dateFormat.format(calendar.getTime());
	}
	
	public List<Double> getBidRates(){
		return bidRatesList;
	}
	
	public List<Double> getAskRates(){
		return askRatesList;
	}
}
