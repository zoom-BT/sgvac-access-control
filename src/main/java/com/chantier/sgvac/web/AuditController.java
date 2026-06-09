package com.chantier.sgvac.web;

import com.chantier.sgvac.access.AccessDecision;
import com.chantier.sgvac.access.AccessEvent;
import com.chantier.sgvac.access.AccessEventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuditController {

    private final AccessEventRepository eventRepository;

    public AuditController(AccessEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/logs")
    public String logs(@RequestParam(required = false) String badgeCode,
                       @RequestParam(required = false) String decision,
                       Model model) {
        List<AccessEvent> events;
        if (badgeCode != null && !badgeCode.isBlank()) {
            events = eventRepository.findByBadgeCodeOrderByEventTimeDesc(badgeCode.trim());
        } else if (decision != null && !decision.isBlank()) {
            events = eventRepository.findByDecisionOrderByEventTimeDesc(
                    AccessDecision.valueOf(decision));
        } else {
            events = eventRepository.findAllByOrderByEventTimeDesc();
        }
        model.addAttribute("events", events);
        return "logs";
    }
}
