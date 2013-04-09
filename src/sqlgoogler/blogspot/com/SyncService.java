package sqlgoogler.blogspot.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//Thai

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient; 
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineException;
import org.apache.http.auth.UsernamePasswordCredentials;
//end


import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.EventsEntity;

public class SyncService extends Service  {
    private Timer timer = new Timer(); 
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
    private long updateInterval;
    private SharedPreferences settings;
    
    private String serviceUrl;
    private String rowLimit;
    private String lastUpdate;
    private String userName;
    private String passWord;
    private String Domain;
    private String lastSync="";
    
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override 
    public void onCreate() 
    {
      super.onCreate();
    
      //Read settings from SharedPreferences
      settings = getSharedPreferences(Constants.Pref_Name, 0);
      updateInterval = 1000 * 60 * 60 * 24; // By Day
      updateInterval= 5000 * updateInterval;
      
      
      serviceUrl = settings.getString(Constants.Service_URL_KEY, Constants.Service_URL_VALUE);
      rowLimit = settings.getString(Constants.Row_Limit_KEY, Constants.Row_Limit_VALUE);
      lastUpdate = settings.getString(Constants.Last_Update_KEY, Constants.Last_Update_VALUE);
      userName = settings.getString(Constants.UserName_KEY, Constants.UserName_VALUE);
      passWord = settings.getString(Constants.Password_KEY, Constants.Password_VALUE);
      Domain = settings.getString(Constants.Domain_KEY, Constants.Domain_VALUE);
      _startService();
    }

    @Override 
    public void onDestroy() 
    {
      super.onDestroy();
      _shutdownService();
    }

    private void _startService()
    {      
      timer.scheduleAtFixedRate(    
          
              new TimerTask() {

                    public void run() {

                        try{

                        doServiceWork();

                        Thread.sleep(updateInterval);

                        }catch(InterruptedException ie){
                        }      
                    }
                  },
                  Constants.Delay_Interval,
                  updateInterval);
    }
    
    private void _shutdownService()
    {
      if (timer != null) timer.cancel();
      Log.i(getClass().getSimpleName(), "Timer stopped...");
    }
    //======================================\\
    private String mServer="admin.mixrc.com";
    private String mServerUrl="http://admin.mixrc.com";
    private int mPort = 80;
    private String mUser="mixrc\\sanghv";
    private String mPassword="Thu234567";

   private void makeRequest() {
            HttpClient httpclient;
            HttpParams httpParameters;
            
             
            int timeoutConnection = 10000;
            int timeoutSocket = 10000;

            httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            
            httpclient = new DefaultHttpClient(httpParameters);  
		    Credentials creds = new UsernamePasswordCredentials(mUser, mPassword);        
		    ((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
		                    new AuthScope(mServer, mPort), creds);        
		    HttpPost httppost = new HttpPost(mServerUrl);
		    
		    HttpResponse res;
			try {
				res = httpclient.execute(httppost);
				int status = res.getStatusLine().getStatusCode();
				if(status==200)
				{
					Log.v("ok", "ok");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
                          
            
     }

    //\\===================================//\\
    private void doServiceWork()
    {  
    	try 
        {
    		/*HttpClient httpclient = new DefaultHttpClient();        
            ((AbstractHttpClient) httpclient).getAuthSchemes().register(AuthPolicy.DIGEST,new NTLMSchemeFactory());

            NTCredentials creds = new NTCredentials(userName+":"+ passWord);

            ((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000); 
            */
    		
    		
    		CredentialsProvider credProvider = new BasicCredentialsProvider();
    	    credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
    	        new UsernamePasswordCredentials("cn=root,dc=vpb,dc=com,dc=vn", "cuchuoi"));
    	    
    	    DefaultHttpClient httpclient = new DefaultHttpClient();
    	    //httpclient.setCredentialsProvider(credProvider);
    		
    		
            HttpPost httppost = new HttpPost(serviceUrl);
           
	        
	        StringEntity se;
	      
            /*if(lastUpdate != "")
            {	
            	//CAML query for Last Events            
            	se = new StringEntity( String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>eventsCal</listName>%s<rowLimit></rowLimit><viewName></viewName><query><Query><Where><Gt><FieldRef Name='Created' /><Value  IncludeTimeValue='True' Type='DateTime'>%sZ</Value></Gt></Where><OrderBy><FieldRef Name='Created' /></OrderBy></Query></query></GetListItems></soap:Body></soap:Envelope>", rowLimit, lastUpdate, HTTP.UTF_8));
            }
            else*/
            	//CAML query for All Events
                se = new StringEntity( String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>eventsCal</listName><rowLimit>%s</rowLimit><viewName></viewName><query><Query><OrderBy><FieldRef Name='Created' /></OrderBy></Query></query></GetListItems></soap:Body></soap:Envelope>", rowLimit), HTTP.UTF_8);	
            se.setContentType("text/xml");
            httppost.setEntity(se);
                  

            HttpResponse httpresponse = httpclient.execute(httppost);
            InputStream in = httpresponse.getEntity().getContent();
            String str = inputStreamToString(in).toString();
            
            int Status = httpresponse.getStatusLine().getStatusCode();
            
            readSoap(str);
            
            //Show Notification 
            displayNotification(String.format("%s",getResources().getString(R.string.msgImported)));
            			
            //Set Last Update setting 
          
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date update=lastSync!=""?	sdf.parse(lastSync):df.parse(lastUpdate);
    		SharedPreferences.Editor editor = settings.edit();
        	//editor.putString(Constants.Last_Update_KEY, df.format(new java.util.Date()).toString());
    		editor.putString(Constants.Last_Update_KEY, df.format(update).toString());
        		
        	editor.commit();
            
        } catch (ClientProtocolException e) {
        	displayNotification(String.format("%s \n Error Details : %s",getResources().getString(R.string.msgImportedFailed),e.toString()));
        } catch (IOException e) {
            displayNotification(String.format("%s \n Error Details : %s",getResources().getString(R.string.msgImportedFailed),e.toString()));
        } catch (ParseException e) {
        	displayNotification(String.format("%s \n Error Details : %s",getResources().getString(R.string.msgImportedFailed),e.toString()));
		}
    	
    }
    
    private void readSoap(String in)
    {    
    	Document doc = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try 
        {
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		InputSource is = new InputSource();
    		is.setCharacterStream(new StringReader(in));
    	    doc = db.parse(is); 

    		} catch (ParserConfigurationException e) {
    		} catch (SAXException e) {
    			e.printStackTrace();
    		} catch (IOException e) {e.printStackTrace();
    		}
            
            //---retrieve all the <z:row> nodes---
    		NodeList elements = doc.getElementsByTagName("z:row"); 
    		
    		for (int i = 0; i < elements.getLength(); i++) { 
    		    Node itemNode = elements.item(i); 
    		   
    		    if (itemNode.getNodeType() == Node.ELEMENT_NODE) 
    		    {            
    		        //---convert the Node into an Element---
    		        Element element = (Element) itemNode;
    		        addEvent(element.getAttribute("ows_ID"),element.getAttribute("ows_Title"), element.getAttribute("ows_EventDate"), element.getAttribute("ows_EndDate"), element.getAttribute("ows_Description"), element.getAttribute("ows_Location"));
    		       
    		        lastSync=  element.getAttribute("ows_Created");
    		        
    		    } 
    		} 
    }
    private boolean CalExists(String id, String path) {
        Uri calendars = Uri.parse("content://calendar/" + path);
        
        String selection =  "original_id=" + id;
        Cursor cCursor = null;
        try 
        {
            cCursor = getContentResolver().query(calendars, null, selection, null, null);
        } 
        catch (IllegalArgumentException e) {}
        if (cCursor == null) 
        {
            calendars = Uri.parse("content://com.android.calendar/" + path);
            try 
            {
               //cCursor = getContentResolver().query(calendars, projection, selection, null, null);
            	cCursor = getContentResolver().query(calendars, null, selection, null, null);
            } 
            catch (IllegalArgumentException e) 
            {
            	e.printStackTrace();
            }
        }
        try 
        {
        	if(cCursor!=null&& cCursor.getCount()>0)
            	return true;
		}
        catch (IllegalArgumentException e) {
        	appendLog("CalExists: IllegalArgumentException - "+e.getMessage());
        	e.printStackTrace();
		}
        catch (Exception e) {
        	appendLog("CalExists: Exception - "+e.getMessage());
			e.printStackTrace();
		}
        
        return false;
    }
    public void appendLog(String text)
    {       
       File logFile = new File("sdcard/log.txt");
       if (!logFile.exists())
       {
          try
          {
             logFile.createNewFile();
          } 
          catch (IOException e)
          {
             // TODO Auto-generated catch block
             e.printStackTrace();
          }
       }
       try
       {
          //BufferedWriter for performance, true to set append to file flag
          BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
          buf.append(text);
          buf.newLine();
          buf.close();
       }
       catch (IOException e)
       {
          // TODO Auto-generated catch block
          e.printStackTrace();
       }
    }
    private void addEvent(String id,String title , String startEvent , String endEvent, String desc , String location) 
    {
    	try{
    		// date
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Date date1 = new Date();
        	Date date2 = new Date();
        	
        	try {
				 date1 = format.parse(startEvent);
				 date2 = format.parse(endEvent);			
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	           
        	Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            long startTime = cal.getTimeInMillis() + 1000;
            cal.setTime(date2);
            long endTime   = cal.getTimeInMillis() + 1000;
            
            ContentValues event = new ContentValues();
            //event.put("calendar_id", getCalendar_ID());
            event.put("calendar_id", 1);
            event.put("title", title);
            event.put("description", desc);
            event.put("eventLocation", location);           
            event.put("dtstart", startTime);
            event.put("dtend", endTime);
            event.put("allDay", 0);
            event.put(Events.SYNC_DATA1, id);
            //event.put(Events.sy,id);
            
            event.put("eventTimezone", TimeZone.getDefault().getID());
            String CalUri="content://com.android.calendar/events";
            Uri uri=Uri.parse(CalUri);
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;

            calUri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .build();

            getContentResolver().insert(calUri, event);
           /* boolean exists=CalExists("10", "calendars");
            if(exists)
            {
            	Uri uri = ContentUris.withAppendedId(Uri.parse(CalUri), Integer.parseInt(id));
            	getContentResolver().update(uri, event, null, null);
            	appendLog("updated id="+id+" Title ="+title);            	
            }
            else
            {
            	getContentResolver().insert(Uri.parse(CalUri), event);
            	appendLog("Added id="+id+" Title ="+title);            	
            }  */                    
        }catch(Exception ee){
        	appendLog("addEvent: "+ee.getMessage());
        	ee.printStackTrace();
        }
    }
    
    private int getCalendar_ID() {
        int calendar_id         = 0;
        String[] projection     = new String[] { "_id", "name" };
        String selection        = "selected=1";
        String path             = "calendars";
        Cursor calendarCursor   = getCalendarCursor(projection, selection, path);

        if (calendarCursor != null && calendarCursor.moveToFirst()) {
        
            int nameColumn = calendarCursor.getColumnIndex("name");
            int idColumn   = calendarCursor.getColumnIndex("_id");
            do {
            	String calId    = calendarCursor.getString(idColumn);
            	String calName  = calendarCursor.getString(nameColumn);
                if (calName != null) {
                    calendar_id = Integer.parseInt(calId);
                }
            } while (calendarCursor.moveToNext());
        }
        return calendar_id;
    }
    
    private Cursor getCalendarCursor(String[] projection, String selection, String path) {
        Uri calendars = Uri.parse("content://calendar/" + path);
        Cursor cCursor = null;
        try 
        {
            cCursor = getContentResolver().query(calendars, projection, selection, null, null);
        } 
        catch (IllegalArgumentException e) {}
        if (cCursor == null) 
        {
            calendars = Uri.parse("content://com.android.calendar/" + path);
            try 
            {
               //cCursor = getContentResolver().query(calendars, projection, selection, null, null);
            	cCursor = getContentResolver().query(calendars, projection, null, null, null);
            } 
            catch (IllegalArgumentException e) 
            {
            	e.printStackTrace();
            }
        }
        return cCursor;
    }
          
    private StringBuilder inputStreamToString(InputStream is) 
    {
        String line = "";
        StringBuilder total = new StringBuilder();
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        // Read response until the end
        try 
        {
         while ((line = rd.readLine()) != null) 
         { 
           total.append(line); 
         }
        } catch (IOException e) {
         e.printStackTrace();
        }
        
        // Return full string
        return total;
     }

    protected void displayNotification(String msg)
    {
    	Intent i = new Intent(this, NotificationActivity.class);
    	i.putExtra("notificationID", 1);
    	i.putExtra("msg", msg);
    	PendingIntent pendingIntent =
    		PendingIntent.getActivity(this, 0, i, 0);
    	NotificationManager nm = (NotificationManager)
    	getSystemService(NOTIFICATION_SERVICE);
    	Notification notif = new Notification(
    	R.drawable.ic_launcher,getResources().getString(R.string.notificationdesc), System.currentTimeMillis());
    	CharSequence from = getResources().getString(R.string.notificationtitle);
    	CharSequence message = getResources().getString(R.string.notificationdesc);
    	notif.setLatestEventInfo(this, from, message, pendingIntent);
    	//---100ms delay, vibrate for 250ms, pause for 100 ms and
    	// then vibrate for 500ms---
    	notif.vibrate = new long[] { 100, 250, 100, 500};
    	nm.notify(1, notif);
    }
    
 // JCIFSEngine 
    public class JCIFSEngine implements NTLMEngine {

        public String generateType1Msg(
                String domain, 
                String workstation) throws NTLMEngineException {

            Type1Message t1m = new Type1Message(
                    Type1Message.getDefaultFlags(),
                    domain,
                    workstation);
            return Base64.encode(t1m.toByteArray());
        }

        public String generateType3Msg(
                String username, 
                String password, 
                String domain,
                String workstation, 
                String challenge) throws NTLMEngineException {
            Type2Message t2m;
            try {
                t2m = new Type2Message(Base64.decode(challenge));
            } catch (IOException ex) {
                throw new NTLMEngineException("Invalid Type2 message", ex);
            }
            Type3Message t3m = new Type3Message(
                    t2m, 
                    password, 
                    domain, 
                    username, 
                    workstation);
            return Base64.encode(t3m.toByteArray());
        }

    }

    //NTLM Scheme factory 
    public class NTLMSchemeFactory implements AuthSchemeFactory {

        public AuthScheme newInstance(final HttpParams params) {
            return new NTLMScheme(new JCIFSEngine());
        }
        
    }
}
