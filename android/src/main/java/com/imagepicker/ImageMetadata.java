package com.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import java.io.InputStream;

public class ImageMetadata extends Metadata {
  protected double latitude;
  protected double longitude;
  protected double altitude;

  public ImageMetadata(Uri uri, Context context) {
    try {
      InputStream inputStream = context.getContentResolver().openInputStream(uri);
      ExifInterface exif = new ExifInterface(inputStream);
      String datetimeTag = exif.getAttribute(ExifInterface.TAG_DATETIME);
      double[] latLong = exif.getLatLong();
      double altitude = exif.getAltitude(0);

      // Extract anymore metadata here...
      if(datetimeTag != null) this.datetime = getDateTimeInUTC(datetimeTag, "yyyy:MM:dd HH:mm:ss");
      if(latLong != null) this.latitude = latLong[0];
      if(latLong != null) this.longitude = latLong[1];
      if(altitude != 0) this.altitude = altitude;

    } catch (Exception e) {
      // This error does not bubble up to RN as we don't want failed datetime retrieval to prevent selection
      Log.e("RNIP", "Could not load image metadata: " + e.getMessage());
    }
  }

  @Override
  public String getDateTime() { return datetime; }
  public double getLatitude() { return latitude; }
  public double getLongitude() { return longitude; }
  public double getAltitude() { return altitude; }
  // At the moment we are not using the ImageMetadata class to get width/height
  // TODO: to use this class for extracting image width and height in the future
  @Override
  public int getWidth() { return 0; }
  @Override
  public int getHeight() { return 0; }
}
