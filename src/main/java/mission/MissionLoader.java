package mission;

import tools.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MissionLoader {

    private final Map<String, MissionDefinition> missions = new LinkedHashMap<>();

    public MissionLoader(ObjectMapper objectMapper) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:missions/*.json");

        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                MissionDefinition mission = objectMapper.readValue(is, MissionDefinition.class);
                missions.put(mission.id(), mission);
            }
        }
    }

    public List<MissionDefinition> findAll() {
        return List.copyOf(missions.values());
    }

    public Optional<MissionDefinition> findById(String id) {
        return Optional.ofNullable(missions.get(id));
    }
}