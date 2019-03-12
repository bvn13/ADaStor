package ru.bvn13.adastor.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bvn13.adastor.entities.dtos.StortionDto;
import ru.bvn13.adastor.web.services.StortionService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author boykovn at 11.03.2019
 */
@Controller
public class UploadController {

    private StortionService stortionService;

    @Autowired
    public void setStortionService(StortionService stortionService) {
        this.stortionService = stortionService;
    }

    @PostMapping(value="/a", produces = {"application/json"})
    public @ResponseBody
    StortionDto uploadData(HttpServletRequest request) throws IOException {
        return stortionService.createStortion(request.getInputStream());
    }

}
