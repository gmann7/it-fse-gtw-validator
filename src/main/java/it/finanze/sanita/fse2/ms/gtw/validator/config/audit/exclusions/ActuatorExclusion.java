/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.validator.config.audit.exclusions;

import it.finanze.sanita.fse2.ms.gtw.validator.config.audit.AuditExclusion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class ActuatorExclusion implements AuditExclusion {

    @Autowired
    private WebEndpointProperties endpoints;

    @Override
    public boolean verify(String uri, HttpServletRequest req) {
        boolean skip = false;
        // Skip check if uri is null
        if(uri != null) {
            // Retrieve actuator exposed endpoints
            Set<String> ep = endpoints.getExposure().getInclude();
            // Retrieve actuator base path
            String base = endpoints.getBasePath();
            // Retrieve mapping
            Map<String, String> mapping = endpoints.getPathMapping();
            // Iterate
            Iterator<String> iterator = ep.iterator();
            // Until we find match
            while (iterator.hasNext() && !skip) {
                // Get value
                String endpoint = iterator.next();
                // Retrieve associated mapping
                // because it may have been re-defined (e.g live -> status ...)
                // If it wasn't overwritten, it will return null therefore we are using the default mapping value
                String mapper = mapping.getOrDefault(endpoint, endpoint);
                // Get actuator path
                String path = UriComponentsBuilder.newInstance().pathSegment(base, mapper).toUriString();
                // If path match, exit loop
                if (uri.startsWith(path)) skip = true;
            }
        }
        return skip;
    }
}
