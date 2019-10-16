package beproject.com.grapesdiseasedetection;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import beproject.com.Network.NetworkClient;
import beproject.com.constant.Utils;
import retrofit2.Retrofit;

public class PredictActivity extends Activity {

    String hostIP, Port, finalURL;
    Retrofit retrofit;
    Socket socket;
    DataOutputStream dos;
    ReceivePythonData r1;
    byte[] bytes;
    Dialog mDialog;
    TextView heading,information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        heading = findViewById(R.id.heading);
        information = findViewById(R.id.information);

        heading.setVisibility(View.INVISIBLE);
        information.setVisibility(View.INVISIBLE);

        hostIP = Utils.getSharedPreferences(this, "ipdata", "ipaddress");
        Port = Utils.getSharedPreferences(this, "ipdata", "port");

        finalURL = "http://" + hostIP + ":" + Port;
        retrofit = NetworkClient.getRetrofitClient(this, finalURL);

        mDialog = Utils.getDialogBox(this, R.layout.progress_bar);
        mDialog.show();

        sendDatatoPythonServer(Utils.getSharedPreferences(this, "user_data", "file_name"));


    }

    public void sendDatatoPythonServer(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    socket = new Socket(hostIP, (Integer.parseInt(Port) + 1));
                    dos = new DataOutputStream(socket.getOutputStream());
                    bytes = filename.getBytes();
                    dos.write(bytes);
                    dos.flush();
                    r1 = new ReceivePythonData();
                    r1.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public class ReceivePythonData extends Thread {
        @Override
        public void run() {
            super.run();
            BufferedReader in;


            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final String line = in.readLine();
                Log.d("Tag", "Received = " + line);
                /*final String[] final_result = line.split("/");
                final String leftEyeResult = final_result[0];
                String rightEyeResult = final_result[1];

                Log.d("Tag", "Received2 = " + leftEyeResult + " " + rightEyeResult);


                Utils.saveToSharedPreferences(PredictActivity.this, "user_data", "LeftEyeResult", predictionResult[0]);
                Utils.saveToSharedPreferences(PredictActivity.this, "user_data", "RightEyeResult", predictionResult[1]);
*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PredictActivity.this, "" + line, Toast.LENGTH_SHORT).show();
                        if(line.equalsIgnoreCase("0"))
                        {
                            heading.setVisibility(View.VISIBLE);
                            information.setVisibility(View.VISIBLE);
                            heading.setText("Bacterial Spots");
                            information.setText("Treatment:\n\n" +
                                    "1.Chemical Control:\n" +
                                    "Chemical control is possible with fungicides such as triadimefon and propiconazole. Effective chemical control is also possible with fungicides hexaconazole, myclobutanil, and penconazole in reducing the mildew.\n\n" +
                                    "2. Organic Control:\n" +
                                    "Organic fungicides are an effective way to manage powdery mildew disease on plants by offering alternative modes of action. The most effective non-chemical methods of control against powdery mildew are milk, bicarbonates, heavy metals, and oils.\n");
                        }
                        else if (line.equalsIgnoreCase("1")){
                            heading.setVisibility(View.VISIBLE);
                            information.setVisibility(View.VISIBLE);

                            heading.setText("Powdery Mildew");
                            information.setText("Treatment:\n\n" +
                                    "1. Chemical Control:\n" +
                                    "Broad spectrum protectant fungicides such as chlorothalonil, mancozeb, and fixed copper are at least somewhat effective in protecting against downy mildew infection.\n\n" +
                                    "2. Organic Control:\n" +
                                    "One way to control downy mildew is to eliminate moisture and humidity around the impacted plants. Watering from below, such as with a drip system, and improve air circulation through selective pruning. In enclosed environments, like in the house or in a greenhouse, reducing the humidity will help as well.");
                        }

                    }
                });
                mDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
