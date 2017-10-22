package in.barmans.nosoftwarelogin;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.PrintWriter;
import java.io.StringWriter;

import in.barmans.application3.R;

/**
 * Created by mbarman on 10/20/17.
 */
class ErrorHandler implements View.OnClickListener {

    private static Activity mainActivity = null;
    private static ErrorHandler instance = null;
    private Exception exception = null; //TODO:will fail with race condition


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
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{mainActivity.getString(R.string.reportEmail)});
            email.putExtra(Intent.EXTRA_SUBJECT, mainActivity.getString(R.string.emailSubject));

            StringWriter stringWriter = new StringWriter();
            PrintWriter stackWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(stackWriter);
            email.putExtra(Intent.EXTRA_TEXT, stringWriter.toString());
            email.setType("message/rfc822");
            mainActivity.startActivity(Intent.createChooser(email, "Choose an Email client :"));
        } catch (Exception e) {
            ToastDisplayHandler.getInstance().showMessage(e.getMessage());
        }
    }

    public void showErrorOnSnackbar(Exception e) {
        exception = e;
        Snackbar errorReportSnackbar = Snackbar.make(mainActivity.findViewById(R.id.mainLayout),
                R.string.reportErrorMessage, Snackbar.LENGTH_SHORT);
        errorReportSnackbar.setAction(R.string.reportButton, this);
        errorReportSnackbar.setDuration(mainActivity.getResources().getInteger(R.integer.snackbarTimeout));
        errorReportSnackbar.show();
    }

    @Override
    public void onClick(View v) {
        sendEmail();
    }
}
