package pb.java.microservices.monolith.App.service;

import org.springframework.stereotype.Service;
import pb.java.microservices.monolith.App.entity.Hotel;
import pb.java.microservices.monolith.App.entity.RatePlan;

import java.util.List;
import java.util.Map;

@Service
public class HotelsService {
    public Map<String, Object> searchHotels(String inDate, String outDate, float lat, float lon) {
        return null;
    }

    public List<String> searchServiceFunctionality(String inDate, String outDate, float lat, float lon) {
        return null;
    }

    public List<Hotel> profileServiceFunctionality(List<String> hotelIds) {
        return null;
    }

    public List<String> geoServiceFunctionality(float lat, float lon) {
        return null;
    }

    public List<RatePlan> rateServiceFunctionality(List<String> hotelIds, String indate, String outdate) {
        return null;
    }
}
