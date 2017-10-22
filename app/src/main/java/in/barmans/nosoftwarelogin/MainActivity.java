package in.barmans.nosoftwarelogin;

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

import in.barmans.application3.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ErrorHandler errorHandler = ErrorHandler.initialize(this);

        try {
            final MainActivity thisInstance = this;
            final String url = getString(R.string.badsslUrl);
            //        final String url = getString(R.string.noSoftwareLoginUrl);
            final Button loginButton = (Button) findViewById(R.id.btnLogin);
            final EditText passwordBox = (EditText) findViewById(R.id.loginPassword);
            final InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            ToastDisplay toastDisplay = ToastDisplay.initialize(this);

            passwordBox.requestFocus();
            inputManager.showSoftInput(passwordBox,
                    InputMethodManager.SHOW_IMPLICIT);
            setPasswordBoxToExistingPassword(passwordBox);

            new CallAPI(thisInstance).execute(url, passwordBox.getText().toString());

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    new CallAPI(thisInstance).execute(url, passwordBox.getText().toString());
                }
            });
        } catch (Exception e) {
            errorHandler.showErrorOnSnackbar(e);
        }
    }

    private void setPasswordBoxToExistingPassword(EditText editText) {
        BufferedReader reader = null;
        try {
            FileInputStream is = openFileInput(getString(R.string.passwordFile));
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
            //its okay
        } catch (IOException e) {
            throw new RuntimeException(e);

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
