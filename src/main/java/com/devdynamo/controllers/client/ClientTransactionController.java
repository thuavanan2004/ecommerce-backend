package com.devdynamo.controllers.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client/transactions")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Client transactions")
public class ClientTransactionController {
}
