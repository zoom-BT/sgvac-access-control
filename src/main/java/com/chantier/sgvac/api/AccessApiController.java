package com.chantier.sgvac.api;

import com.chantier.sgvac.access.AccessControlService;
import com.chantier.sgvac.access.AccessDecision;
import com.chantier.sgvac.access.AccessEvent;
import com.chantier.sgvac.access.AccessEventRepository;
import com.chantier.sgvac.api.dto.AccessEvaluateRequest;
import com.chantier.sgvac.api.dto.AccessEventResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/access")
public class AccessApiController {

    private final AccessControlService accessControlService;
    private final AccessEventRepository eventRepository;

    public AccessApiController(AccessControlService accessControlService,
                               AccessEventRepository eventRepository) {
        this.accessControlService = accessControlService;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/evaluate")
    public AccessEventResponse evaluate(@Valid @RequestBody AccessEvaluateRequest request,
                                       Authentication authentication) {
        String checkpoint = request.checkpoint() != null && !request.checkpoint().isBlank()
                ? request.checkpoint().trim()
                : "Portail principal";
        AccessEvent event = accessControlService.evaluate(
                request.badgeCode().trim(),
                checkpoint,
                authentication.getName(),
                LocalDateTime.now());
        return AccessEventResponse.from(event);
    }

    @GetMapping("/events")
    public List<AccessEventResponse> events(
            @RequestParam(required = false) String badgeCode,
            @RequestParam(required = false) String decision) {
        List<AccessEvent> events;
        if (badgeCode != null && !badgeCode.isBlank()) {
            events = eventRepository.findByBadgeCodeOrderByEventTimeDesc(badgeCode.trim());
        } else if (decision != null && !decision.isBlank()) {
            events = eventRepository.findByDecisionOrderByEventTimeDesc(
                    AccessDecision.valueOf(decision));
        } else {
            events = eventRepository.findAllByOrderByEventTimeDesc();
        }
        return events.stream().map(AccessEventResponse::from).toList();
    }
}
