package in.barmans.application3;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by mbarman on 10/20/17.
 */
class ErrorHandler implements View.OnClickListener, Initiable {

    private static MainActivity mainActivity;
    private static ErrorHandler instance = null;
    private Exception exception;


    public static ErrorHandler initialize(MainActivity activity) {
        mainActivity = activity;
        instance = new ErrorHandler();
        return getInstance();
    }

    public static ErrorHandler getInstance() {
        if (instance == null)
            throw new RuntimeException("ErrorHandler should be initialized from MainActivity");
        return instance;
    }

    private void sendEmail() {
        try {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"mbarman@salesforce.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Error details");

            StringWriter stringWriter = new StringWriter();
            PrintWriter stackWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(stackWriter);
            email.putExtra(Intent.EXTRA_TEXT, stringWriter.toString());
            email.setType("message/rfc822");
            mainActivity.startActivity(Intent.createChooser(email, "Choose an Email client :"));
        } catch (Exception e) {
            ToastDisplay.getInstance().showMessage(e.getMessage());
        }
    }

    public void showErrorOnSnackbar(Exception e) {
        exception = e;
        Snackbar errorReportSnackbar = Snackbar.make(mainActivity.findViewById(R.id.mainLayout),
                "Report error to mbarman@salesforce.com", Snackbar.LENGTH_SHORT);
        errorReportSnackbar.setAction("Report", this);
        errorReportSnackbar.setDuration(10000);
        errorReportSnackbar.show();
    }

    @Override
    public void onClick(View v) {
        sendEmail();
    }
}
