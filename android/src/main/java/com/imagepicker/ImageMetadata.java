package com.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class ImageMetadata extends Metadata {
  protected double latitude;
  protected double longitude;
  protected double altitude;
  private String md5;

  public ImageMetadata(Uri uri, Context context) {
    try {
      InputStream inputStream = context.getContentResolver().openInputStream(uri);
      ExifInterface exif = new ExifInterface(inputStream);
      String datetimeTag = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
      double[] latLong = exif.getLatLong();
      double altitude = exif.getAltitude(0);

      String md5 = fileToMD5(inputStream);

      // Extract anymore metadata here...
      if(datetimeTag != null) this.datetime = getDateTimeInUTC(datetimeTag, "yyyy:MM:dd HH:mm:ss");
      if(latLong != null) this.latitude = latLong[0];
      if(latLong != null) this.longitude = latLong[1];
      if(altitude != 0) this.altitude = altitude;
      if(md5 != null) this.md5 = md5;

    } catch (Exception e) {
      // This error does not bubble up to RN as we don't want failed datetime retrieval to prevent selection
      Log.e("RNIP", "Could not load image metadata: " + e.getMessage());
    }
  }

  public static String fileToMD5(InputStream inputStream) {
    try {
      byte[] buffer = new byte[1024];
      MessageDigest digest = MessageDigest.getInstance("MD5");
      int numRead = 0;
      while (numRead != -1) {
        numRead = inputStream.read(buffer);
        if (numRead > 0)
          digest.update(buffer, 0, numRead);
      }
      byte [] md5Bytes = digest.digest();
      return convertHashToString(md5Bytes);
    } catch (Exception e) {
      return null;
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception e) { }
      }
    }
  }

  private static String convertHashToString(byte[] md5Bytes) {
    String returnVal = "";
    for (int i = 0; i < md5Bytes.length; i++) {
      returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
    }
    return returnVal.toUpperCase();
  }

  @Override
  public String getDateTime() { return datetime; }
  public double getLatitude() { return latitude; }
  public double getLongitude() { return longitude; }
  public double getAltitude() { return altitude; }
  public String getMd5() { return md5; }
  // At the moment we are not using the ImageMetadata class to get width/height
  // TODO: to use this class for extracting image width and height in the future
  @Override
  public int getWidth() { return 0; }
  @Override
  public int getHeight() { return 0; }
}
