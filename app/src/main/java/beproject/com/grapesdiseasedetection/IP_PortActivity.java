package beproject.com.grapesdiseasedetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import beproject.com.constant.Utils;


public class IP_PortActivity extends Activity {

    Button Submit;
    EditText IP,Port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip__port);

        init();

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IP_PortActivity.this,SelectImageActivity.class);
                i.putExtra("IP",IP.getText().toString());
                i.putExtra("Port",Port.getText().toString());
                i.putExtra("Address",Port.getText().toString());

                Utils.saveToSharedPreferences(IP_PortActivity.this,"ipdata","ipaddress",IP.getText().toString());
                Utils.saveToSharedPreferences(IP_PortActivity.this,"ipdata","port",Port.getText().toString());

                startActivity(i);
                finish();
            }
        });

    }


    public void init()
    {

        Submit = findViewById(R.id.submitData);
        IP = findViewById(R.id.ipAddress);
        Port = findViewById(R.id.portNumber);

        IP.setText(Utils.getSharedPreferences(IP_PortActivity.this,"ipdata","ipaddress"));
        Port.setText(Utils.getSharedPreferences(IP_PortActivity.this,"ipdata","port"));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
