package de.alexanderwodarz.code.cloudflare;

import de.alexanderwodarz.code.cloudflare.zone.Zone;

public class Main {

    public static void main(String[] args) {
        CloudFlare cf = new CloudFlare("7g1GRgcv-DrAii1-OpQxig1GWI-Yc8J1PmAlJU3R");
        for (Zone listZone : cf.listZones()) {
            System.out.println(listZone.getName()+ " => "+listZone.getStatus());
        }
    }

}
