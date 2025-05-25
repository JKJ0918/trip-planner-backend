package com.tripPlanner.project.flights;

import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

import java.net.URL;


@Component
public class FlightApiClient {
    // 서비스키 업로드 금지
    public FlightResponseDTO fetchFlights(String dep, String arr, String date, int page) {
        try {
            String serviceKey = "";
            String urlStr = "http://openapi.airport.co.kr/service/rest/FlightScheduleList/getIflightScheduleList?"
                    + "ServiceKey=" + serviceKey
                    + "&schDeptCityCode=" + dep
                    + "&schArrvCityCode=" + arr
                    + "&schDate=" + date
                    + "&pageNo" + page;


            URL url = new URL(urlStr);

            JAXBContext context = JAXBContext.newInstance(FlightResponseDTO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FlightResponseDTO response = (FlightResponseDTO) unmarshaller.unmarshal(url);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 빈 리스트 반환
        }
    }

}
