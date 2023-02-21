package de.alexanderwodarz.code.cloudflare;

import de.alexanderwodarz.code.cloudflare.zone.Zone;
import de.alexanderwodarz.code.rest.ClientThread;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CloudFlare {

    private String bearer;

    public CloudFlare(String bearer) {
        this(bearer, false);
    }

    public CloudFlare(String bearer, boolean ipv6) {
        this.bearer = bearer;
    }

    public Request request(String path, ClientThread.RequestMethod method, String body) {
        return new Request(this, path, method, body);
    }

    public Zone getZoneByName(String name) {
        Request request = request("/zones?name=" + name, ClientThread.RequestMethod.GET, null);
        if (request.getResponseObject().getJSONObject("result_info").getInt("count") == 0)
            return null;
        return new Zone(this, request.getResponseObject().getJSONArray("result").getJSONObject(0));
    }

    public List<Zone> listZones() {
        Request request = request("/zones", ClientThread.RequestMethod.GET, null);
        JSONObject result = request.getResponseObject();
        JSONArray retur = result.getJSONArray("result");
        if (result.getJSONObject("result_info").getInt("total_pages") > 1) {
            for (int i = 1; i < result.getJSONObject("result_info").getInt("total_pages"); i++) {
                Request page = request("/zones?per_page=20&page=" + i + 1, ClientThread.RequestMethod.GET, null);
                for (int j = 0; j < page.getResponseObject().getJSONArray("result").length(); j++) {
                    retur.put(page.getResponseObject().getJSONArray("result").getJSONObject(j));
                }
            }
        }
        List<Zone> zones = new ArrayList<>();
        for (int i = 0; i < retur.length(); i++) {
            zones.add(new Zone(this, retur.getJSONObject(i)));
        }
        return zones;
    }

    public Zone createZone(String accountID, String name) {
        JSONObject request = new JSONObject();
        request.put("name", name);
        request.put("account", new JSONObject().put("id", accountID));
        request.put("jump_start", true);
        Request r = request("/zones", ClientThread.RequestMethod.POST, request.toString());
        System.out.println(r.getResponse());
        return new Zone(this, r.getResponseObject().getJSONObject("result"));
    }

}
