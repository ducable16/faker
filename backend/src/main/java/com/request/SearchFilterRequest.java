package com.request;

import lombok.Data;

import java.util.List;

@Data
public class SearchFilterRequest {
    private String type;

    private String brandName;

    private Integer brandId = null;

    private Long lowerBound;

    private Long upperBound;

    private List<String> storages;

    private List<String> cpus;

    private List<String> memories;

//    private String displaySize;

    private String displayResolution;

//    private String battery;

//    private String chargingCapacity;

    private List<String> refreshRates;

//    private String graphicsCard;

//    private String operatingSystem;

//    private String weight;
}
