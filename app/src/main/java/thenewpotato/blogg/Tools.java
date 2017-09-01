package thenewpotato.blogg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by thenewpotato on 3/30/17.
 * This class stores universal helper methods
 * and universal constants (e.g. Result/Request Codes)
 */

public class Tools {

    public static final int FN_LIVE = 100;
    public static final int FN_DRAFT = 101;
    public static final int FN_ACTIVITIES = 102;
    public static final int FN_APP_SETTINGS = 104;
    public static final int FN_ABOUT = 105;
    public static final String KEY_STARTUP_FRAGMENT = "fragment_pref";
    public static final String KEY_STARTUP_BLOG = "blog_pref";
    public static final String KEY_SORT_OPTION = "sort_opt";
    public static final int RC_AUTHORIZE = 1000;
    public static final int RC_NEW = 1001;
    public static final int RC_UPDATE = 1003;
    public static final String PREFERENCE_NAME = "thenewpotato.blogg";
    public static final int VAL_NULL = 9999;
    public static final String KEY_PRIMITIVE_COMMENTS = "p_comments";
    public static final String KEY_ROOT_COMMENT = "comment";
    public static final int OPTION_A_TO_Z = 200;
    public static final int OPTION_Z_TO_A = 201;
    public static final int OPTION_NEWEST = 202;
    public static final int OPTION_OLDEST = 203;

    private static DateFormat inputFormat = null;
    private static DateFormat outputFormat = null;
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public static String parseDateTime(DateTime dateTime, Context appContext){
        if(inputFormat == null || outputFormat == null){
            inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            outputFormat = android.text.format.DateFormat.getDateFormat(appContext);
        }
        try {
            return outputFormat.format(inputFormat.parse(dateTime.toString()));
        }catch (ParseException e){
            return null;
        }
    }

    public static String parseDateTime(String dateTime, Context appContext){
        if(inputFormat == null || outputFormat == null){
            inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            outputFormat = android.text.format.DateFormat.getDateFormat(appContext);
        }
        try {
            return outputFormat.format(inputFormat.parse(dateTime));
        }catch (ParseException e){
            return null;
        }
    }

    public final static boolean DEBUG = false;
    public static void log(String message) {
        if (DEBUG) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

            Log.d(className + "." + methodName + "():" + lineNumber, message);
        }
    }

    public static void loge(String message) {
        if (DEBUG) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

            Log.e(className + "." + methodName + "():" + lineNumber, message);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight){
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth = ((float) newWidth / width);
            float scaleHieght = ((float) newHeight / height);
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHieght);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            bitmap.recycle();
            return resizedBitmap;
        } catch (NullPointerException e){
            log("Cannot load bitmap, " + e.getMessage());
            return null;
        }
    }

}
