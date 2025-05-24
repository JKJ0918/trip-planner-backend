package com.tripPlanner.project.flights;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Component
public class FlightApiClient {
    // 서비스키 업로드 금지
    public List<FlightItem> fetchFlights(String dep, String arr, String date) {

            List<FlightItem> allFlights = new ArrayList<>();

            for (int pageNo = 1; pageNo <=3; pageNo++){
                try {

                String serviceKey = "";
                String urlStr = "http://openapi.airport.co.kr/service/rest/FlightScheduleList/getIflightScheduleList?"
                        + "ServiceKey=" + serviceKey
                        + "&schDeptCityCode=" + dep
                        + "&schArrvCityCode=" + arr
                        + "&schDate=" + date
                        + "&pageNo=" + pageNo;

                URL url = new URL(urlStr);
                JAXBContext context = JAXBContext.newInstance(FlightResponseDTO.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                FlightResponseDTO response = (FlightResponseDTO) unmarshaller.unmarshal(url);

                List<FlightItem> currentPageItems = response.getItems();

                // 📌 null 체크 + 빈 데이터 체크
                if (currentPageItems == null || currentPageItems.isEmpty()) {
                    break; // 더 이상 데이터가 없으므로 반복 종료
                }

                allFlights.addAll(currentPageItems);

                } catch (Exception e){
                    e.printStackTrace();
                    return List.of(); // 빈리스트 반환
                }

            }

            return allFlights;

    }

}
