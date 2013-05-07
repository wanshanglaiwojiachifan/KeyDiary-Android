package com.xeodou.keydiary;

import java.util.Date;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HockeySender implements ReportSender {

    private static String BASE_URL = "https://rink.hockeyapp.net/api/2/apps/";
    private static String CRASHES_PATH = "/crashes";

    @Override
    public void send(CrashReportData report) throws ReportSenderException {
        String log = createCrashLog(report);
        String url = BASE_URL + ACRA.getConfig().formKey() + CRASHES_PATH;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("raw", log);
        params.put("userID", report.get(ReportField.INSTALLATION_ID));
        params.put("contact", report.get(ReportField.USER_EMAIL));
        params.put("description", report.get(ReportField.USER_COMMENT));
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler());
    }

    private String createCrashLog(CrashReportData report) {
        Date now = new Date();
        StringBuilder log = new StringBuilder();

        log.append("Package: " + report.get(ReportField.PACKAGE_NAME) + "\n");
        log.append("Version: " + report.get(ReportField.APP_VERSION_CODE)
                + "\n");
        log.append("Android: " + report.get(ReportField.ANDROID_VERSION) + "\n");
        log.append("Manufacturer: " + android.os.Build.MANUFACTURER + "\n");
        log.append("Model: " + report.get(ReportField.PHONE_MODEL) + "\n");
        log.append("Date: " + now + "\n");
        log.append("\n");
        log.append(report.get(ReportField.STACK_TRACE));

        return log.toString();
    }

}
