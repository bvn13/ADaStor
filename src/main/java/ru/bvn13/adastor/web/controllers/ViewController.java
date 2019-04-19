package ru.bvn13.adastor.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.bvn13.adastor.entities.dtos.StortionDto;
import ru.bvn13.adastor.web.services.StortionService;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author boykovn at 11.03.2019
 */
@RestController
public class ViewController {

    private StortionService stortionService;

    @Autowired
    public void setStortionService(StortionService stortionService) {
        this.stortionService = stortionService;
    }

    @GetMapping("/v/{uuid}")
    public void getStortion(@PathVariable("uuid") String uuid, HttpServletResponse response) throws IOException {
        Optional<StortionDto> stortion = stortionService.findStortion(uuid);
        if (stortion.isPresent()) {
            try(BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
                InputStream is = stortionService.getInputStream(stortion.get());
                is.transferTo(bos);
            } catch (FileNotFoundException e) {
                response.sendError(404, "not found");
            }
        } else {
            response.sendRedirect("/notfound");
        }
    }

}
