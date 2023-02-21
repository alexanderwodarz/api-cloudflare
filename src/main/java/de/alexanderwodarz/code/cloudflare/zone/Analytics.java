package de.alexanderwodarz.code.cloudflare.zone;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@RequiredArgsConstructor
@Getter
public class Analytics {

    private final JSONArray zones;

    public JSONObject getCountryMap() {
        JSONArray result = new JSONArray();
        JSONArray zones = getZones();
        for (int i = 0; i < zones.length(); i++) {
            JSONObject zone = zones.getJSONObject(i);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                JSONObject tmp = new JSONObject();
                long time = (df.parse(zone.getJSONObject("dimensions").getString("timeslot")).getTime()) / 1000L;
                tmp.put("time", time);
                JSONArray map = new JSONArray();
                for (int k = 0; k < zone.getJSONObject("sum").getJSONArray("countryMap").length(); k++) {
                    JSONObject country = zone.getJSONObject("sum").getJSONArray("countryMap").getJSONObject(k);
                    country.remove("__typename");
                    map.put(country);
                }
                tmp.put("list", map);
                result.put(tmp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        JSONObject res = new JSONObject();
        for (int i = 0; i < result.length(); i++) {
            for (int j = 0; j < result.getJSONObject(i).getJSONArray("list").length(); j++) {
                JSONObject tmp = result.getJSONObject(i).getJSONArray("list").getJSONObject(j);
                int set = tmp.getInt("requests");
                if (res.has(tmp.getString("key")))
                    set = set + res.getInt(tmp.getString("key"));
                res.put(tmp.getString("key"), set);
            }
        }
        res.put("detail", result);
        return res;
    }
    public JSONObject getContentTypeMap() {
        JSONArray result = new JSONArray();
        JSONArray zones = getZones();
        for (int i = 0; i < zones.length(); i++) {
            JSONObject zone = zones.getJSONObject(i);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                JSONObject tmp = new JSONObject();
                long time = (df.parse(zone.getJSONObject("dimensions").getString("timeslot")).getTime()) / 1000L;
                tmp.put("time", time);
                JSONArray map = new JSONArray();
                for (int k = 0; k < zone.getJSONObject("sum").getJSONArray("contentTypeMap").length(); k++) {
                    JSONObject country = zone.getJSONObject("sum").getJSONArray("contentTypeMap").getJSONObject(k);
                    country.remove("__typename");
                    map.put(country);
                }
                tmp.put("list", map);
                result.put(tmp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        JSONObject res = new JSONObject();
        res.put("detail", result);
        return res;
    }

    public JSONObject getRequests() {
        JSONArray zones = getZones();
        JSONObject response = new JSONObject();
        JSONArray result = new JSONArray();
        for (int i = 0; i < zones.length(); i++) {
            JSONObject zone = zones.getJSONObject(i);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                JSONObject tmp = new JSONObject();
                long time = (df.parse(zone.getJSONObject("dimensions").getString("timeslot")).getTime()) / 1000L;
                tmp.put("time", time);
                tmp.put("requests", zone.getJSONObject("sum").getInt("requests"));
                tmp.put("cached", zone.getJSONObject("sum").getInt("cachedRequests"));
                result.put(tmp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < result.length(); i++) {
            int requests = result.getJSONObject(i).getInt("requests");
            int cached = result.getJSONObject(i).getInt("cached");
            if (response.has("requests"))
                requests = requests + response.getInt("requests");
            if (response.has("cached"))
                cached = cached + response.getInt("cached");
            response.put("requests", requests);
            response.put("cached", cached);
        }
        response.put("detail", result);
        return response;
    }

    private JSONArray getZones() {
        JSONArray zones = new JSONArray();
        for (int i = 0; i < this.zones.length(); i++)
            for (int j = 0; j < this.zones.getJSONObject(i).getJSONArray("zones").length(); j++)
                zones.put(this.zones.getJSONObject(i).getJSONArray("zones").getJSONObject(j));
        return zones;
    }

}
