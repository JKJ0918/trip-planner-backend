package com.tripPlanner.project.flights;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;


@Component
public class FlightApiClient {
    // 서비스키 업로드 금지
    public List<FlightItem> fetchFlights(String dep, String arr, String date) {
        try {
            String serviceKey = "시크릿키 입력";
            String urlStr = "http://openapi.airport.co.kr/service/rest/FlightScheduleList/getIflightScheduleList?"
                    + "ServiceKey=" + serviceKey
                    + "&schDeptCityCode=" + dep
                    + "&schArrvCityCode=" + arr
                    + "&schDate=" + date;

            URL url = new URL(urlStr);

            JAXBContext context = JAXBContext.newInstance(FlightResponseDTO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FlightResponseDTO response = (FlightResponseDTO) unmarshaller.unmarshal(url);

            return response.getItems();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 빈 리스트 반환
        }
    }

}
