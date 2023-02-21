package de.alexanderwodarz.code.cloudflare.zone;

import de.alexanderwodarz.code.cloudflare.CloudFlare;
import de.alexanderwodarz.code.cloudflare.Request;
import de.alexanderwodarz.code.cloudflare.zone.dns.DnsRecord;
import de.alexanderwodarz.code.rest.ClientThread;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class Zone {

    private CloudFlare cf;
    private JSONObject obj;

    public String getId() {
        return getObj().getString("id");
    }

    public String getName() {
        return getObj().getString("name");
    }

    public String getStatus() {
        return getObj().getString("status");
    }

    public boolean deleteRecord(String recordID) {
        return cf.request("/zones/" + getId() + "/dns_records/" + recordID, ClientThread.RequestMethod.DELETE, null).getResponseObject().getBoolean("success");
    }

    public JSONArray getNameServers() {
        return getObj().getJSONArray("name_servers");
    }

    public void deleteZone() {
        Request request = cf.request("/zones/" + getId(), ClientThread.RequestMethod.DELETE, null);
        System.out.println(request.getResponse());
    }

    public List<DnsRecord> listRecords() {
        Request request = cf.request("/zones/" + getId() + "/dns_records", ClientThread.RequestMethod.GET, "");
        List<DnsRecord> records = new ArrayList<>();
        for (int i = 0; i < request.getResponseObject().getJSONArray("result").length(); i++)
            records.add(new DnsRecord(this, request.getResponseObject().getJSONArray("result").getJSONObject(i)));
        return records;
    }

    public void checkActivation() {
        cf.request("/zones/" + getId() + "/activation_check", ClientThread.RequestMethod.PUT, "");
    }

    public Analytics getAnalytics(long from, long to) {
        JSONObject obj = new JSONObject("{\"variables\":{\"zoneTag\":\"" + getId() + "\",\"since\":\"" + new Date(from * 1000L).toInstant().toString() + "\"" + (to > 0 ? ",\"until\":\"" + new Date(to * 1000L).toInstant().toString() + "\"" : "") + "},\"query\":\"query GetZoneAnalytics($zoneTag: string, $since: string, $until: string) {\\n  viewer {\\n    zones(filter: {zoneTag: $zoneTag}) {\\n      totals: httpRequests1hGroups(limit: 10000, filter: {datetime_geq: $since, datetime_lt: $until}) {\\n        uniq {\\n          uniques\\n          __typename\\n        }\\n        __typename\\n      }\\n      zones: httpRequests1hGroups(orderBy: [datetime_ASC], limit: 10000, filter: {datetime_geq: $since, datetime_lt: $until}) {\\n        dimensions {\\n          timeslot: datetime\\n          __typename\\n        }\\n        uniq {\\n          uniques\\n          __typename\\n        }\\n        sum {\\n          browserMap {\\n            pageViews\\n            key: uaBrowserFamily\\n            __typename\\n          }\\n          bytes\\n          cachedBytes\\n          cachedRequests\\n          contentTypeMap {\\n            bytes\\n            requests\\n            key: edgeResponseContentTypeName\\n            __typename\\n          }\\n          clientSSLMap {\\n            requests\\n            key: clientSSLProtocol\\n            __typename\\n          }\\n          countryMap {\\n            bytes\\n            requests\\n            threats\\n            key: clientCountryName\\n            __typename\\n          }\\n          encryptedBytes\\n          encryptedRequests\\n          ipClassMap {\\n            requests\\n            key: ipType\\n            __typename\\n          }\\n          pageViews\\n          requests\\n          responseStatusMap {\\n            requests\\n            key: edgeResponseStatus\\n            __typename\\n          }\\n          threats\\n          threatPathingMap {\\n            requests\\n            key: threatPathingName\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\",\"operationName\":\"GetZoneAnalytics\"}");
        Request request = cf.request("/graphql", ClientThread.RequestMethod.POST, obj.toString());
        System.out.println(request.getResponse());
        return new Analytics(request.getResponseObject().getJSONObject("data").getJSONObject("viewer").getJSONArray("zones"));
    }

    public Request createRecord(String type, String name, String content, int ttl, boolean proxied) {
        JSONObject body = new JSONObject();
        body.put("type", type);
        body.put("name", name);
        body.put("content", content);
        body.put("ttl", ttl);
        body.put("proxied", proxied);
        return cf.request("/zones/" + getId() + "/dns_records", ClientThread.RequestMethod.POST, body.toString());
    }

}
