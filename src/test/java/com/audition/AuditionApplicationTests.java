package com.audition;

import static org.assertj.core.api.Assertions.assertThat;

import com.audition.web.AuditionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuditionApplicationTests {

    // TODO implement unit test. Note that an applicant should create additional unit tests as required.

    @Autowired
    private AuditionController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
