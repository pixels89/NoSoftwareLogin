package in.barmans.nosoftwarelogin;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import in.barmans.application3.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mbarman on 10/20/17.
 */

public class CallAPI extends AsyncTask<String, String, String> {

    private ToastDisplayHandler toastDisplayHandler = null;
    private Activity mainActivity = null;
    private ErrorHandler errorHandler = null;


    public CallAPI(Activity mainActivity) {
        this.mainActivity = mainActivity;
        errorHandler = ErrorHandler.getInstance();
        toastDisplayHandler = ToastDisplayHandler.getInstance();
    }

    @Override
    protected String doInBackground(String... params) {
        authenticate(params[0], params[1]);
        return null;
    }

    private String authenticate(String apiUrl, String password) {
        HttpURLConnection urlConnection = null;
        if (password != null && !password.isEmpty() && apiUrl != null && !apiUrl.isEmpty()) {
            toastDisplayHandler.showMessage(mainActivity.getString(R.string.logingIn));
            String data = mainActivity.getString(R.string.postData) + password;
            try {
                urlConnection = setSslContext(apiUrl);

                //TODO: handle streams properly
                urlConnection.setRequestMethod(mainActivity.getString(R.string.postMethod));
                BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();

//                String result = "";
//                InputStream in = urlConnection.getInputStream();
//                InputStreamReader isw = new InputStreamReader(in);
//                BufferedReader reader = new BufferedReader(isw);
//                String read = "";
//                while ((read = reader.readLine()) != null) {
//                    result += read;
//                }
//                reader.close();

                if (urlConnection.getResponseCode() == 200) {
                    toastDisplayHandler.showMessage(mainActivity.getString(R.string.loggedIn));
                    FileOutputStream os = mainActivity.openFileOutput(mainActivity.getString(R.string.passwordFile), MODE_PRIVATE);
                    OutputStreamWriter sw = new OutputStreamWriter(os);
                    sw.write(password);
                    sw.flush();
                    sw.close();
                } else {
                    toastDisplayHandler.showMessage(mainActivity.getString(R.string.cantLogin));
                }
            } catch (Exception e) {
                errorHandler.showPromptOnSnackbar(new Exception("\r\napiUrl: " + apiUrl + "\r\n data: " + data, e.getCause()));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

            }
        } else {
            toastDisplayHandler.showMessage(mainActivity.getString(R.string.enterPassword));
        }
        return null;
    }

    private HttpURLConnection setSslContext(String urlString) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = mainActivity.getResources().openRawResource(R.raw.nosoftwarecrt);

        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        // Tell the URLConnection to use a SocketFactory from our SSLContext
        URL url = new URL(urlString);
        HttpsURLConnection urlConnection =
                (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());

        return urlConnection;
    }
}
