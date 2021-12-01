package top.codexvn.server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@MapperScan("top.codexvn.server.mapper")
@Scope
public class MybatisConfig {
}
