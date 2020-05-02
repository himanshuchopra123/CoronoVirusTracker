package io.javabrains.coronavirustracker.contollers;


import io.javabrains.coronavirustracker.model.LocationStats;
import io.javabrains.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String Home(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
       int totalReportedCases =  allStats.stream().mapToInt(stat -> stat.getLatestTotal()).sum();
        model.addAttribute("locationStats", coronaVirusDataService.getAllStats());
        model.addAttribute("totalReportedCases", totalReportedCases);
        return  "home";
    }
}
