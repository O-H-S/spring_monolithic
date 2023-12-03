package com.ohs.monolithic.board.controller;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(BoardApiController.class)
@EnableMethodSecurity(prePostEnabled = true)
@Tag("base")
@Tag("unit")
class BoardApiControllerTest {

}