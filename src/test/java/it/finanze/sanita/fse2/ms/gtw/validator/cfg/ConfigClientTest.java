package it.finanze.sanita.fse2.ms.gtw.validator.cfg;

import it.finanze.sanita.fse2.ms.gtw.validator.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.validator.client.routes.ConfigClientRoutes;
import it.finanze.sanita.fse2.ms.gtw.validator.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.validator.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.validator.service.impl.ConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.validator.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static it.finanze.sanita.fse2.ms.gtw.validator.client.routes.base.ClientRoutes.Config.PROPS_NAME_AUDIT_ENABLED;
import static it.finanze.sanita.fse2.ms.gtw.validator.client.routes.base.ClientRoutes.Config.PROPS_NAME_CONTROL_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.gtw.validator.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.validator.enums.ConfigItemTypeEnum.GENERIC;
import static it.finanze.sanita.fse2.ms.gtw.validator.enums.ConfigItemTypeEnum.VALIDATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST)
public class ConfigClientTest{

    private static final ConfigItemTypeEnum specific = VALIDATOR;

    @Autowired
    private IConfigClient config;

    @MockBean
    private ConfigSRV service;

    @MockBean
    private RestTemplate client;

    @Autowired
    private ConfigClientRoutes routes;

    @MockBean
    private ProfileUtility profiles;

    private static final List<Pair<String, String>> DEFAULT_PROPS = Arrays.asList(
            Pair.of(PROPS_NAME_AUDIT_ENABLED, "false"),
            Pair.of(PROPS_NAME_CONTROL_LOG_ENABLED, "false")
    );

    @BeforeEach
    void init(){
        when(service.isAuditEnable()).thenReturn(true);
        when(service.isControlLogPersistenceEnable()).thenReturn(true);
    }

    @Test
    @DisplayName("Get prop test with prop value different from previous")
    void getPropTest(){
        // Mock the it-gtw-config status
        when(client.getForEntity(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(ResponseEntity.ok().build());

        String prop_name = "prop_name";
        String expected = "true";
        when(client.getForObject(routes.getConfigItem(specific, prop_name), String.class)).thenReturn(expected);

        String actual = config.getProps(prop_name, "false", specific);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get prop name with SPECIFIC prop not found")
    void getPropTestWithSpecificPropNotFound(){
        // Mock the it-gtw-config status
        when(client.getForEntity(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(ResponseEntity.ok().build());

        String prop_name = "prop_name";
        String expected = "true";
        when(client.getForObject(routes.getConfigItem(specific, prop_name), String.class)).thenReturn(null);
        when(client.getForObject(routes.getConfigItem(GENERIC, prop_name), String.class)).thenReturn(expected);


        String actual = config.getProps(prop_name, "false", specific);
        assertEquals(expected, actual);
    }

    ConfigItemDTO createItem(){
        ConfigItemDTO.ConfigDataItemDTO dataItem = new ConfigItemDTO.ConfigDataItemDTO();
        ConfigItemDTO expected = new ConfigItemDTO();
        // Valorizzo le props di tipo FHIR-MAPPING-ENGINE
        Map<String, String> map = new HashMap<String, String>();
        map.put("cfg-items-retention-day", "true");
        dataItem.setKey(GENERIC.name());
        dataItem.setItems(map);
        // Creo la variabile di ritorno
        List<ConfigItemDTO.ConfigDataItemDTO> props = new ArrayList<>();
        props.add(dataItem);
        expected.setTraceId("traceID");
        expected.setSpanId("spanId");
        expected.setConfigurationItems(props);

        return expected;
    }

    public List<Pair<String, String>> defaults() {
        return DEFAULT_PROPS;
    }
}
