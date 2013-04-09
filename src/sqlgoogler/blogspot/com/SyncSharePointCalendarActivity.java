package sqlgoogler.blogspot.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class SyncSharePointCalendarActivity extends Activity {
   
	private EditText txtServiceUrl ,txtRowLimit,txtUserName,txtPassword,txtDomain;
	private Button btnStartService , btnStopService;
	private RadioButton rdb1 , rdb2;
	private TextView lblLastUpdate;
	private SharedPreferences settings;
	private String lastUpdate;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Get settings from Shared Preferences 
        settings = getSharedPreferences(Constants.Pref_Name, 0); 
        
        txtServiceUrl = (EditText)findViewById(R.id.txtServiceUrl);
        txtRowLimit = (EditText)findViewById(R.id.txtRowLimit);
        lblLastUpdate = (TextView)findViewById(R.id.lblLastUpdate);
        txtUserName=(EditText)findViewById(R.id.txtUserName);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        txtDomain=(EditText)findViewById(R.id.txtDomain);
        
        rdb1 = (RadioButton)findViewById(R.id.rdb1);
        rdb2 = (RadioButton)findViewById(R.id.rdb2);
   
        try
    	{
        	txtServiceUrl.setText(settings.getString(Constants.Service_URL_KEY, Constants.Service_URL_VALUE));
        	txtRowLimit.setText(settings.getString(Constants.Row_Limit_KEY, Constants.Row_Limit_VALUE));
        	txtUserName.setText(settings.getString(Constants.UserName_KEY, Constants.UserName_VALUE));
        	txtPassword.setText(settings.getString(Constants.Password_KEY, Constants.Password_VALUE));
        	txtDomain.setText(settings.getString(Constants.Domain_KEY, Constants.Domain_VALUE));
        	lastUpdate = settings.getString(Constants.Last_Update_KEY, Constants.Last_Update_VALUE);
        	
        	if(lastUpdate != "")
        	{
        		lblLastUpdate.setText(String.format("%s %s",getResources().getString(R.string.msgLastUpdate), lastUpdate));
        		lblLastUpdate.setVisibility(View.VISIBLE);
        		rdb2.setChecked(true);
        		rdb1.setChecked(false);
        	}
        	else
        	{
        		lblLastUpdate.setVisibility(View.GONE);
        		rdb2.setChecked(false);
        		rdb1.setChecked(true);
        	}
    	}
    	catch(Exception ex)
    	{    		
    		txtServiceUrl.setText( Constants.Service_URL_VALUE);
        	txtRowLimit.setText(Constants.Row_Limit_VALUE);
        	rdb2.setChecked(true);
        	rdb1.setChecked(false);
    	}
        
        //Start Service Event
        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		//Save settings to Shared Preferences
        		SharedPreferences.Editor editor = settings.edit();
            	editor.putString(Constants.Service_URL_KEY,txtServiceUrl.getText().toString().trim());
            	editor.putString(Constants.Row_Limit_KEY,txtRowLimit.getText().toString().trim());
            	editor.putString(Constants.UserName_KEY,txtUserName.getText().toString().trim());
            	editor.putString(Constants.Password_KEY,txtPassword.getText().toString().trim());
            	editor.putString(Constants.Domain_KEY,txtDomain.getText().toString().trim());
            	editor.commit();
            	
            	//Start the service
        		startService(new Intent(getBaseContext(), SyncService.class));
        		showMessage(getResources().getString(R.string.StartMsg));
        	} 	
        });
        
        //Stop Service Event
        btnStopService = (Button) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		//Stop the service
        		stopService(new Intent(getBaseContext(), SyncService.class));
        		showMessage(getResources().getString(R.string.StopMsg));
        	}
        });
    }
    
    private void showMessage(String msg)
    {
    	Toast.makeText(getBaseContext(),msg, Toast.LENGTH_SHORT).show(); 
    } 
}