package io.github.wpik.extramdc.spring;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"extra-mdc.enabled=true"})
public class EnabledWithPropertyTest extends EnabledTestBase {
}
