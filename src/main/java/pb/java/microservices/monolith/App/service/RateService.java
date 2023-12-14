package pb.java.microservices.monolith.App.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pb.java.microservices.monolith.App.entity.RatePlan;
import pb.java.microservices.monolith.App.entity.Stay;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RateService {
    private static final Logger LOGGER = Logger.getLogger(RateService.class.getName());
    private final ResourceLoader resourceLoader;
    private Map<Stay, RatePlan> rateTable = new HashMap<>();

    @Autowired
    public RateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        System.out.println("Updated Vers");
    }

    @PostConstruct
    public void init() {
        try {
            loadRateTableFromJsonFile("data/inventory.json");
            LOGGER.info("Inventory loaded successfully");
        } catch (IOException e) {
            LOGGER.severe("Failed to load inventory: " + e.getMessage());
        }
    }

    public List<RatePlan> getRates(List<String> hotelIds, String inDate, String outDate) {
        List<RatePlan> results = new ArrayList<>();
        for (String hotelId : hotelIds) {
            Stay stay = new Stay(hotelId, inDate, outDate);
            RatePlan ratePlan = rateTable.get(stay);
            if (ratePlan != null) {
                results.add(ratePlan);
            }
        }
        return results;
    }

    private void loadRateTableFromJsonFile(String filename) throws IOException {
        String path = resourceLoader.getResource("classpath:" + filename).getFile().getPath();

        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            Gson gson = new Gson();
            reader.beginArray();
            while (reader.hasNext()) {
                RatePlan ratePlan = gson.fromJson(reader, RatePlan.class);
                Stay stay = new Stay(ratePlan.getHotelId(), ratePlan.getInDate(), ratePlan.getOutDate());
                this.rateTable.put(stay, ratePlan);
            }
            reader.endArray();
        }
    }
}
