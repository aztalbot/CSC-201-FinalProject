package final_project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

// class that holds the website domain and methods for querying JSON data
public class CaseData {

    private final String apiDomain = "http://scc.virginia.gov/DocketSearchAPI/breeze/";

    // takes a data set (URL path) and a query string, builds a query URL, calls it, and parses its JSON data into
    // an ArrayList of type Filing
    public ArrayList<Filing> getFilings(String dataSet, String query) {
        ArrayList<Filing> filings = new ArrayList<>();
        String apiQuery = apiDomain + dataSet + "?$filter=" + query;

        // try and catch call to query URL
        try {
            URL url = new URL(apiQuery);
            // System.out.println(apiQuery);
            Scanner input = new Scanner(url.openStream());

            // get all JSON data from URL as a JSONArray,
            // loop through array and instantiate Filings, store in ArrayList
            JSONArray jsonArray = new JSONArray(input.nextLine());
            for (int i = 0; i < jsonArray.length(); i++) {
                Filing filing = new Filing(jsonArray.getJSONObject(i));
                filings.add(filing);
            }
        }
        catch(MalformedURLException ex1) {
            System.out.println("There is a problem with the URL");
        }
        catch (java.io.IOException ex2) {
            System.out.println("I/O Errors: no such URL");
        }
        catch(JSONException ex3) {
            System.out.println("JSON not parsed successfully");
        }

        return filings;
    }

    // takes a case number and passes it into a query string,
    // the query URL is called and data surrounding the case is returned
    public JSONObject getCaseInfo(String caseNumber) {

        final String dataSet = "CASES_ESTABDATE/GetCasesEstDate";
        final String query = "substringof('" + caseNumber + "'%2CCase_Number)%20eq%20true&$orderby=Case_Established_Date" +
                "%20desc&$select=MATTER_NO%2CCase_Number%2CCase_Name%2CCase_Caption%2CCase_Established_Date%2CSTATUS";
        String apiQuery = apiDomain + dataSet + "?$filter=" + query;

        // try and catch call to the URL and input from the JSON data
        try {
            URL url = new URL(apiQuery);
            // System.out.println(apiQuery);
            Scanner input = new Scanner(url.openStream());

            // parse json data and return the JSON object, it should only be one case (one object)
            JSONArray jsonArray = new JSONArray(input.nextLine());
            return jsonArray.getJSONObject(0); // stored as an array of length 1
        }
        catch(MalformedURLException ex1) {
            System.out.println("There is a problem with the URL");
        }
        catch (java.io.IOException ex2) {
            System.out.println("I/O Errors: no such URL");
        }
        catch(JSONException ex3) {
            System.out.println("JSON not parsed successfully");
        }

        // if it fails return some generally blank data
        return new JSONObject("{\"Case_Caption\": \"Not found\",\"MATTER_NO\":\"Not found\"}");
    }
}

// class representing a filing and its characteristics, used in cell factory to display all filings
// fields are populated from JSON data
class Filing
        extends JSONObject {

    private String caseNumber, caseTitle, documentName, fileName, caseCaption;
    private Integer matterNumber;
    private GregorianCalendar dateFiled;
    private JSONObject caseInfo;

    public Filing() {

        super();
    }

    // constructor for filing object, takes JSON data and gets specific fields to populate instance data
    // try and catch is used because different data sets use different key names, so if an exception is thrown,
    // the constructor attempts a second time using alternative key names
    // could also be done by passing a conditional parameter into the constructor and using if-else in method
    public Filing(JSONObject json) {

        try {
            caseNumber = json.getString("CaseNumber");
            documentName = json.getString("DocName");
            dateFiled = new GregorianCalendar(json.getInt("Year"),
                    json.getInt("Month"), json.getInt("Day"));
        }
        catch(JSONException ex) {
            caseNumber = json.getString("MATTER_ID");
            documentName = json.getString("Document_Name");

            // in alternative data set the data is kept as a signle string, so it must be parsed
            String dateFiledString = json.getString("Date_Filed");
            dateFiled = new GregorianCalendar(Integer.parseInt(dateFiledString.substring(0, 4)),
                    Integer.parseInt(dateFiledString.substring(5, 7)), Integer.parseInt(dateFiledString.substring(8, 10)));
        }

        fileName = json.getString("FileName");

        // get other data on the surrounding the filing
        caseInfo = getCaseInfo();
        caseCaption = caseInfo.getString("Case_Caption");
        matterNumber = caseInfo.getInt("MATTER_NO");

    }

    // GETTERS AND SETTERS
    //====================

    // return the URL for a document, which requires the file name to be encoded
    // file names at end of URL need to be encoded same as JavaScripts encodeURIComponent
    public String getDocumentURL() {
        try {
            return "http://www.scc.virginia.gov/docketsearch/DOCS/" +
                    URLEncoder.encode(fileName, "UTF-8")
                            .replaceAll("\\+", "%20")
                            .replaceAll("\\%21", "!")
                            .replaceAll("\\%27", "'")
                            .replaceAll("\\%28", "(")
                            .replaceAll("\\%29", ")")
                            .replaceAll("\\%7E", "~");
        }
        catch(UnsupportedEncodingException ex) {

            System.out.println(ex.getMessage());
        }

        // if it fails a general link is returned
        return "http://www.scc.virginia.gov/docketsearch/";
    }

    // return the URL to the case on the court website
    public String getCaseURL() {
        return "http://www.scc.virginia.gov/docketsearch#/caseDetails/" + matterNumber;
    }

    // return JSON data with information on a document's case
    private JSONObject getCaseInfo() {
        CaseData caseInfo = new CaseData();
        return caseInfo.getCaseInfo(caseNumber);
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    // return the data of filing as a string formatted as mm/dd/yy
    public String getDateAsString() {
        return String.valueOf(dateFiled.get(Calendar.MONTH)) + "/" +
                String.valueOf(dateFiled.get(Calendar.DAY_OF_MONTH)) + "/" +
                String.valueOf(dateFiled.get(Calendar.YEAR)).substring(2, 4);
    }

    // return everything before the first dash character
    public String getFiler() {
        return documentName.split(" - ")[0];
    }

    // split document name by the first dash character and return the second part
    public String getDocumentDescription() {
        String description = documentName.split(" - ", 2)[1];

        // some documents have typos causing extra spacing at beginning of description
        if(description.startsWith(" ")) return description.substring(1, description.length());

        return description;
    }

    // unused getters and setters
    //===========================

    public Integer getMatterNumber() {
        return matterNumber;
    }

    public void setMatterNumber(Integer matterNumber) {
        this.matterNumber = matterNumber;
    }

    public void setCaseInfo(JSONObject caseInfo) {
        this.caseInfo = caseInfo;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseCaption() {
        return caseCaption;
    }

    public void setCaseCaption(String caseCaption) {
        this.caseCaption = caseCaption;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GregorianCalendar getDateFiled() {
        return dateFiled;
    }

    public void setDateFiled(GregorianCalendar dateFiled) {
        this.dateFiled = dateFiled;
    }
}
