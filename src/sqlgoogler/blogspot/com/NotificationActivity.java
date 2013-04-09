package sqlgoogler.blogspot.com;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification);
		
TextView lblMsg = (TextView)findViewById(R.id.lblMsg);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	lblMsg.setText(extras.getString("msg").trim());
        }	
				
		NotificationManager nm = (NotificationManager)
		getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(getIntent().getExtras().getInt("notificationID"));
		
		
	}

}
