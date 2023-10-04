package pb.java.microservices.monolith.App.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pb.java.microservices.monolith.App.entity.GeoPoint;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeoService {
    private static final double MAX_SEARCH_RADIUS = 10;
    private static final int MAX_SEARCH_RESULTS = 1000000000;

    private final Map<String, GeoPoint> geoIndex = new HashMap<>();

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public GeoService(ResourceLoader resourceLoader) throws IOException {
        this.resourceLoader = resourceLoader;
        String jsonData = readJsonFile("data/geo.json");

        List<GeoPoint> geoPoints = objectMapper.readValue(jsonData, new TypeReference<List<GeoPoint>>() {
        });
        for (GeoPoint geoPoint : geoPoints) {
            geoIndex.put(geoPoint.getHotelId(), geoPoint);
        }
    }

    public List<String> getNearbyHotels(float lat, float lon) {
        GeoPoint center = new GeoPoint();
        center.setLat(lat);
        center.setLon(lon);

        return geoIndex.values().stream()
                .filter(point -> distance(point, center) <= MAX_SEARCH_RADIUS)
                .sorted(Comparator.comparingDouble(p -> distance(p, center)))
                .limit(MAX_SEARCH_RESULTS)
                .map(GeoPoint::getHotelId)
                .collect(Collectors.toList());
    }

    private String readJsonFile(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return org.apache.commons.io.IOUtils.toString(reader);
    }

    private double distance(GeoPoint p1, GeoPoint p2) {
        double dx = p1.getLon() - p2.getLon();
        double dy = p1.getLat() - p2.getLat();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
