package pb.java.microservices.monolith.App.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pb.java.microservices.monolith.App.entity.RatePlan;
import pb.java.microservices.monolith.App.entity.Stay;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class RateService {
    private static final Logger LOGGER = Logger.getLogger(RateService.class.getName());
    private final ResourceLoader resourceLoader;
    private Map<Stay, RatePlan> rateTable = new HashMap<>();

    @Autowired
    public RateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        System.out.println("Updated Vers 2");

        try {
            System.out.println("STARTED LOADING DATA");
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
        System.out.println("1");
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        System.out.println("2");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        System.out.println("3");
        try (InputStream is = resource.getInputStream()) {
            List<RatePlan> ratePlans = mapper.readValue(is, new TypeReference<List<RatePlan>>() {});
            for (RatePlan ratePlan : ratePlans) {
                Stay stay = new Stay(ratePlan.getHotelId(), ratePlan.getInDate(), ratePlan.getOutDate());
                this.rateTable.put(stay, ratePlan);
            }
            System.out.println("FINISHED LOADING DATA");
        } catch (Exception e) {
            System.out.println("ERROR LOADING DATA");
            e.printStackTrace(); // Important for debugging
        }
    }

}
