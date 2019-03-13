package ru.bvn13.adastor.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bvn13.adastor.entities.dtos.StortionDto;
import ru.bvn13.adastor.exceptions.AdastorException;
import ru.bvn13.adastor.exceptions.InternalServerError;
import ru.bvn13.adastor.exceptions.StortionExistByHash;
import ru.bvn13.adastor.exceptions.UploadNotAvailable;
import ru.bvn13.adastor.web.services.StortionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    StortionDto uploadData(HttpServletRequest request, HttpServletResponse response) throws IOException, AdastorException {
        try {
            return stortionService.createStortion(request.getContentLengthLong(), request.getInputStream());
        } catch (InternalServerError internalServerError) {
            internalServerError.printStackTrace();
            response.sendError(500, "Internal server error, Sorry");
            return null;
        } catch (StortionExistByHash stortionExistByHash) {
            stortionExistByHash.printStackTrace();
            return stortionExistByHash.getStortion();
        } catch (UploadNotAvailable uploadNotAvailable) {
            response.sendError(406, uploadNotAvailable.getMessage());
            return null;
        }
    }

}
