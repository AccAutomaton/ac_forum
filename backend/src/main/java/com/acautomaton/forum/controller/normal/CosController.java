package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.util.CosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cos")
public class CosController {
    CosService cosService;

    @Autowired
    public CosController(CosService cosService) {
        this.cosService = cosService;
    }

    @GetMapping("/authorization/read/public")
    public Response getPublicResourcesAuthorization() {
        return Response.success(cosService.getPublicResourcesReadAuthorization());
    }
}
