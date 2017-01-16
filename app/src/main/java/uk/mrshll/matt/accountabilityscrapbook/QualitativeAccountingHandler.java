package uk.mrshll.matt.accountabilityscrapbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;

import org.json.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import uk.mrshll.matt.accountabilityscrapbook.model.Scrap;
import uk.mrshll.matt.accountabilityscrapbook.model.Tag;

/**
 * Created by marshall on 16/01/17.
 * This class handles conversion to and from the Qualitative Accounting Data standard.
 */

public class QualitativeAccountingHandler
{
    Context context;
    SharedPreferences preferences;
    public QualitativeAccountingHandler(Context c)
    {
        this.context = c;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    /**
     * Converts the Scraps produced by the system into the Qualitative Accounting JSON Schema
     * @param s
     * @return
     */
    public String scrapToJSON(Scrap s)
    {
        // Create the JSOn object
        JSONObject jsonScrap = new JSONObject();


        try // Fill out the scheme with information from the fields
        {
            //Id
            String id = generateItemIDString(s);
            jsonScrap.put("id", id);

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
                JSONObject jsonMedia = new JSONObject();
                jsonMedia.put("raw_data", getByteStringFromImage(s.getPhotoUri()));
                jsonMedia.put("raw_data_format", "jpeg");
                jsonMedia.put("uri", null);

                jsonScrap.put("media", jsonMedia);
            } else
            {
                jsonScrap.put("media", null);
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

        } catch (JSONException e)
        {
            e.printStackTrace();
        }


        return jsonScrap.toString();
    }

    /**
     * Take a Photo URI and converts it to a Base64 encoded string for JSON transmission
     * @param photoURI
     * @return
     */
    private String getByteStringFromImage(String photoURI)
    {
        String byteString = "";

        try
        {
            Bitmap bm = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(photoURI)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArray = baos.toByteArray();

            byteString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return byteString;

    }

    /**
     * Generates the id field for the QA data standard, from the preferences and the Scrap's date.
     * Implemented for cleanliness
     * @param s
     * @return
     */
    private String generateItemIDString(Scrap s)
    {
        String dateString = s.getDateCreatedAsTransactionID();

        return String.format(context.getString(R.string.qualitative_accounting_id_format), preferences.getString("device-id", "n/a"), dateString);
    }


}
