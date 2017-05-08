package uk.mrshll.matt.accountabilityscrapbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;

import org.json.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

/**
 * Created by marshall on 16/01/17.
 * This class handles conversion to and from the Qualitative Accounting Data standard.
 */

public class QualitativeAccountingHandler
{
    private String accessToken;

    public QualitativeAccountingHandler(String accessToken)
    {
        this.accessToken = accessToken;

    }

    /**
     * Converts the Scraps produced by the system into the Qualitative Accounting JSON Schema
     * @param s
     * @return
     */
    public String scrapToJSON(Scrap s)
    {


        JSONObject payload = new JSONObject();



        try // Fill out the scheme with information from the fields
        {

            payload.put("token", accessToken);

            // Create the JSOn object
            JSONObject jsonScrap = new JSONObject();

            // Dates
            jsonScrap.put("date_created", s.getDateCreated());
            jsonScrap.put("date_given", s.getDateGiven());

            // Tags
            JSONArray tagArray = new JSONArray();
            for(Tag t : s.getAllUniqueTags())
            {
                tagArray.put(t.getTagName());
            }
            jsonScrap.put("tags", tagArray);

            // Quote
            JSONObject jsonQuote = new JSONObject();
            jsonQuote.put("text", s.getQuoteText());
            jsonQuote.put("attribution", s.getQuoteSource());
            jsonScrap.put("quote", jsonQuote);

            // Financial Data
            JSONObject jsonFinance = new JSONObject();
            jsonFinance.put("currency", "GBP");
            jsonFinance.put("value", s.getSpendValue());
            jsonScrap.put("financial_data", jsonFinance);

            // Media
            if (s.getPhotoUri() != null)
            {
                jsonScrap.put("media", getImageFileHash(s.getPhotoUri()));
            } else
            {
                jsonScrap.put("media", "");
            }

            // Location
            if(s.getPlaceLatLng() != null)
            {
                JSONObject jsonLocation = new JSONObject();
                jsonLocation.put("name", s.getPlaceName());
                jsonLocation.put("address", s.getPlaceAddress());
                String placeLatLong = s.getPlaceLatLng();
                String[] latLongSplit = placeLatLong.split(" ");
                jsonLocation.put("latitude", latLongSplit[0]);
                jsonLocation.put("longitude", latLongSplit[1]);
                jsonScrap.put("location", jsonLocation);
            } else
            {
                jsonScrap.put("location", null);
            }


            // Description
            jsonScrap.put("description", s.getName());

            // And finish
            payload.put("data", jsonScrap);


        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return payload.toString();


    }

    /**
     * Returns a SHA-1 hash of the image file if present
     * @return
     */
    private String getImageFileHash(String photoUri)
    {
        if(photoUri != null)
        {
            try
            {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                byte[] buffer = new byte[65536];

                InputStream fis = new FileInputStream(Uri.parse(photoUri).getPath());
                int n = 0;

                while(n != -1)
                {
                    n = fis.read(buffer);
                    if (n > 0)
                    {
                        digest.update(buffer, 0, n);
                    }
                }
                byte[] digestResult = digest.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < digestResult.length; i++)
                {
                    sb.append(Integer.toString((digestResult[i] & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString();


            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;

    }

//    /**
//     * Generates the id field for the QA data standard, from the preferences and the Scrap's date.
//     * Implemented for cleanliness
//     * @param s
//     * @return
//     */
//    private String generateItemIDString(Scrap s)
//    {
//        String dateString = s.getDateCreatedAsTransactionID();
//
//        return String.format(context.getString(R.string.qualitative_accounting_id_format), this.deviceId, dateString);
//    }


}
