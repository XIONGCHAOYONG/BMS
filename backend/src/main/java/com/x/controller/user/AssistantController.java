package com.x.controller.user;

import com.x.ai.api.Assistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
class AssistantController {

    @Autowired
    private Assistant assistant;
    @GetMapping(value = "/chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(@RequestParam("message") String message) {

//        return assistant.chat(BaseContext.getCurUserInfo().getId(),message);
        return assistant.chat(1L,message);
    }
}