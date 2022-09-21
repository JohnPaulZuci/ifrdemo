package com.demo.application.service;

import com.demo.application.Util.CoproHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainFileServiceImpl {

    private final CoproHandler coproHandler;

    public CoproHandler.DedupeResponse  callDedupe(String filePath) throws Exception {


        final CoproHandler.DedupeResponse dedupeRespList = coproHandler
                .toDedupe(filePath);

        System.out.println("List ========= "+ dedupeRespList);
        return dedupeRespList;
    }
}
