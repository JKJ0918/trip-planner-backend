package com.tripPlanner.project.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "view")
@Data
public class ViewProperties {
    private int cooldownHours = 24;

}
