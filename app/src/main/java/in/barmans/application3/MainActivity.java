package in.barmans.application3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    private String url = null;
    private ToastDisplay toastDisplay = null;
    private ErrorHandler errorHandler = null;
    private MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            errorHandler = ErrorHandler.initialize(this);
            toastDisplay = ToastDisplay.initialize(this);
            try {
                final EditText editText = (EditText) findViewById(R.id.loginPassword);
                getExistingPassword(editText);

                //url = getString(R.string.badsslUrl);
                url = getString(R.string.noSoftwareLoginUrl);
                new CallAPI(instance).execute(url, editText.getText().toString());
                Button login = (Button) findViewById(R.id.btnLogin);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        new CallAPI(instance).execute(url, editText.getText().toString());
                    }
                });
            } catch (Exception e) {
                errorHandler.showErrorOnSnackbar(e);
            } finally {
                try {
                } catch (Exception e) {
                } finally {
                }
            }
        } catch (Exception e) {
            errorHandler.showErrorOnSnackbar(e);
        }
    }


    private void getExistingPassword(EditText editText) throws IOException {
        BufferedReader reader = null;
        try {
            FileInputStream is = openFileInput("loginDetails");
            reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String existingPass = sb.toString();
            if (existingPass != null) {
                editText.setText(existingPass);
            }
        } catch (FileNotFoundException e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
