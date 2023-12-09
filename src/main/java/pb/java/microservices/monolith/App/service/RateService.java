package pb.java.microservices.monolith.App.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import pb.java.microservices.monolith.App.entity.RatePlan;
import pb.java.microservices.monolith.App.entity.Stay;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RateService {
    private final ResourceLoader resourceLoader;
    private Map<Stay, RatePlan> rateTable = new HashMap<>();

    @Autowired
    public RateService(ResourceLoader resourceLoader) throws IOException {
        this.resourceLoader = resourceLoader;
        loadRateTableFromJsonFile("data/inventory.json");
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
        String jsonData = readJsonFile(filename);
        List<RatePlan> ratePlanList = parseJsonToRatePlanList(jsonData);
        this.rateTable = ratePlanList.stream()
                .collect(Collectors.toMap(
                        rp -> new Stay(rp.getHotelId(), rp.getInDate(), rp.getOutDate()),
                        Function.identity(),
                        (existing, replacement) -> existing)); // in case of duplicates in file
    }

    private List<RatePlan> parseJsonToRatePlanList(String jsonData) {
        List<RatePlan> ratePlanList = new ArrayList<>();
        JsonArray jsonArray = new JsonParser().parse(jsonData).getAsJsonArray();

        for (JsonElement jsonElement : jsonArray) {
            RatePlan ratePlan = new RatePlan();
            ratePlan.setHotelId(jsonElement.getAsJsonObject().get("hotelId").getAsString());
            ratePlan.setInDate(jsonElement.getAsJsonObject().get("inDate").getAsString());
            ratePlan.setOutDate(jsonElement.getAsJsonObject().get("outDate").getAsString());
            ratePlanList.add(ratePlan);
        }

        return ratePlanList;
    }

    private String readJsonFile(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
