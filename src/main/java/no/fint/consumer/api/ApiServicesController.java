package no.fint.consumer.api;

import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@Api(tags = {"API Service Discovery"})
@RestController
@CrossOrigin
@RequestMapping(name = "API Service Discovery", value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApiServicesController {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private ApiServiceMapper apiServiceMapper;

    @GetMapping
    public Map<String, ApiService> getApiServices() {
        Set<RequestMappingInfo> infoList = requestMappingHandlerMapping.getHandlerMethods().keySet();

        Map<String, List<RequestMappingInfo>> serviceList = infoList.stream()
                .filter(requestMappingInfo -> requestMappingInfo.getName() != null)
                .collect(groupingBy(RequestMappingInfo::getName));

        Map<String, ApiService> apiServiceMap = Maps.newTreeMap();

        serviceList.forEach((s, requestMappingInfos) -> {
            Optional<ApiService> apiService = apiServiceMapper.getApiService(requestMappingInfos);
            apiService.ifPresent(a -> apiServiceMap.put(s.toLowerCase(), a));
        });

        return apiServiceMap;
    }
}
