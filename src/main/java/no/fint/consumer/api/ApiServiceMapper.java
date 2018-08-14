package no.fint.consumer.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApiServiceMapper {

    private static final String CACHE_SIZE_PATTERN = "/cache/size";
    private static final String LAST_UPDATE_PATTERN = "/last-updated";
    private static final String ONE_PATTERN = "/.*/\\{id:\\.\\+\\}";
    private static final String COLLECTION_PATTERN = "";

    @Value("${fint.relations.default-base-url}")
    private String defaultBaseUrl;

    @Value("${server.context-path}")
    private String serverContextPath;

    public Optional<ApiService> getApiService(List<RequestMappingInfo> requestMappingInfos) {

        ApiService apiService = new ApiService();

        Optional<String> cacheSizeMapping = getCacheSizeMapping(requestMappingInfos);
        Optional<String> lastUpdatedMapping = getLastUpdatedMapping(requestMappingInfos);
        Optional<List<String>> oneMappings = getOneMapping(requestMappingInfos);
        Optional<String> collectionMapping = getCollectionMapping(requestMappingInfos);

        if (cacheSizeMapping.isPresent() && lastUpdatedMapping.isPresent()
                && oneMappings.isPresent() && collectionMapping.isPresent()) {

            apiService.setCacheSizeUrl(getUrl(cacheSizeMapping.get()));
            apiService.setLastUpdatedUrl(getUrl(lastUpdatedMapping.get()));
            apiService.setOneUrl(getUrls(oneMappings.get()));
            apiService.setCollectionUrl(getUrl(collectionMapping.get()));

            return Optional.of(apiService);
        }

        return Optional.empty();
    }

    private Optional<String> getCacheSizeMapping(List<RequestMappingInfo> requestMappingInfos) {
        return Optional.ofNullable(getMapping(requestMappingInfos, CACHE_SIZE_PATTERN));
    }

    private Optional<String> getLastUpdatedMapping(List<RequestMappingInfo> requestMappingInfos) {
        return Optional.ofNullable(getMapping(requestMappingInfos, LAST_UPDATE_PATTERN));
    }

    private Optional<List<String>> getOneMapping(List<RequestMappingInfo> requestMappingInfos) {
        return Optional.ofNullable(getMappings(requestMappingInfos, ONE_PATTERN));
    }

    private Optional<String> getCollectionMapping(List<RequestMappingInfo> requestMappingInfos) {
        return Optional.ofNullable(getMapping(requestMappingInfos, COLLECTION_PATTERN));
    }

    private List<String> getMappings(List<RequestMappingInfo> requestMappingInfos, String pattern) {
        List<String> allMappings = new ArrayList<>();
        requestMappingInfos.forEach(requestMappingInfo -> {
            List<String> mappings = requestMappingInfo.getPatternsCondition().getPatterns().stream().filter(s ->
                    s.matches(String.format("/%s%s", requestMappingInfo.getName().toLowerCase(), pattern))).collect(Collectors.toList());
            allMappings.addAll(mappings);
        });

        if (allMappings.size() > 0) {
            return allMappings;
        }
        return null;
    }

    private String getMapping(List<RequestMappingInfo> requestMappingInfos, String pattern) {
        List<String> mappings = getMappings(requestMappingInfos, pattern);

        if (mappings != null && mappings.size() > 0) {
            return mappings.get(0);
        }
        return null;
    }


    private String getUrl(String serviceUrl) {
        return String.format("%s%s%s", defaultBaseUrl, serverContextPath, serviceUrl);
    }

    private List<String> getUrls(List<String> serviceUrls) {
        List<String> urls = new ArrayList<>();
        serviceUrls.forEach(url -> urls.add(getUrl(url)));

        return urls;
    }
}
