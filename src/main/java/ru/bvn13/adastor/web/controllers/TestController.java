package ru.bvn13.adastor.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bvn13.adastor.entities.dtos.StortionDto;
import ru.bvn13.adastor.web.services.StortionService;

import java.util.stream.Stream;

/**
 * @author boykovn at 12.03.2019
 */
//@Controller
public class TestController {

    private StortionService stortionService;

    @Autowired
    public void setStortionRepository(StortionService stortionService) {
        this.stortionService = stortionService;
    }

    @GetMapping("/t")
    public @ResponseBody String test() {

        Stream<StortionDto> stortions = stortionService.findAllSortedByRetention();
        stortions.forEach(st -> System.out.println(String.format("%s - %s - %s - %s", st.getUuid(), st.getStoreDate(), st.getSize(), st.getRetention())));

        return "done";
    }

}
