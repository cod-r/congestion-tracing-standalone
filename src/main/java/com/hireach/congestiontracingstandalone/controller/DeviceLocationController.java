//    Congestion API - a REST API built to track congestion spots and
//    crowded areas using real-time location data from mobile devices.
//
//    Copyright (C) 2020, University Politehnica of Bucharest, member
//    of the HiReach Project consortium <https://hireach-project.eu/>
//    <andrei[dot]gheorghiu[at]upb[dot]ro. This project has received
//    funding from the European Union’s Horizon 2020 research and
//    innovation programme under grant agreement no. 769819.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
package com.hireach.congestiontracingstandalone.controller;

import com.hireach.congestiontracingstandalone.component.CompanyWrapper;
import com.hireach.congestiontracingstandalone.service.DeviceLocationHistoryService;
import com.hireach.congestiontracingstandalone.service.DeviceLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.time.Instant;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class DeviceLocationController {

    private final DeviceLocationService deviceLocationService;
    private final DeviceLocationHistoryService deviceLocationHistoryService;
    private final CompanyWrapper companyWrapper;

    @PostMapping(value = "/location")
    @ResponseStatus(HttpStatus.OK)
    public void saveDeviceLocation(@RequestParam(value = "lat") @DecimalMin("-90.0") @DecimalMax("90.0") @Digits(integer = 2, fraction = 8) double lat,
                                   @RequestParam(value = "lon") @DecimalMin("-180.0") @DecimalMax("180.0") @Digits(integer = 3, fraction = 8) double lon,
                                   @RequestParam(value = "device_id") String deviceId) {
        Instant instant = Instant.now();
        deviceLocationService.saveOrUpdateDeviceLocation(lat, lon, deviceId, companyWrapper.getCompany(), instant);
        deviceLocationHistoryService.saveDeviceLocationHistory(lat, lon, deviceId, companyWrapper.getCompany(), instant);
    }

    @GetMapping(value = "/congestion")
    @ResponseStatus(HttpStatus.OK)
    public int getCongestion(@RequestParam("lat") @DecimalMin("-90.0") @DecimalMax("90.0") @Digits(integer = 2, fraction = 8) double lat,
                             @RequestParam("lon") @DecimalMin("-180.0") @DecimalMax("180.0") @Digits(integer = 3, fraction = 8) double lon,
                             @RequestParam("radius") @DecimalMin(value = "0.0", inclusive = false) @DecimalMax("6378000") double radius,
                             @RequestParam(value = "seconds_ago", required = false) Integer secondsAgo) {
        return deviceLocationService.getCongestion(lat, lon, radius, secondsAgo);
    }

}
