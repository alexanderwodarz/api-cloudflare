package de.alexanderwodarz.code.cloudflare.zone.dns;

import de.alexanderwodarz.code.cloudflare.zone.Zone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

@AllArgsConstructor
@Getter
public class DnsRecord {

    private Zone zone;
    private JSONObject obj;

    public String getId() {
        return getObj().getString("id");
    }

    public String getZoneID() {
        return getObj().getString("zone_id");
    }

    public String getZoneName() {
        return getObj().getString("zone_name");
    }

    public String getName() {
        return getObj().getString("name");
    }

    public String getType() {
        return getObj().getString("type");
    }

    public String getContent() {
        return getObj().getString("content");
    }

    public boolean isProxiable() {
        return getObj().getBoolean("proxiable");
    }

    public boolean isProxied() {
        return getObj().getBoolean("proxied");
    }

    public int getTtl() {
        return getObj().getInt("ttl");
    }

    public boolean isLocked() {
        return getObj().getBoolean("locked");
    }

    public boolean isAutoAdded() {
        return getObj().getJSONObject("meta").getBoolean("auto_added");
    }

    public boolean isManagedByApps() {
        return getObj().getJSONObject("meta").getBoolean("managed_by_apps");
    }

    public boolean isManagedByArgoTunnel() {
        return getObj().getJSONObject("meta").getBoolean("managed_by_argo_tunnel");
    }

    public String getSource() {
        return getObj().getJSONObject("meta").getString("soure");
    }

    public String getCreated() {
        return getObj().getString("created_on");
    }

    public String getModified() {
        return getObj().getString("modified_on");
    }

    public void delete(){
        zone.deleteRecord(getId());
    }

}
