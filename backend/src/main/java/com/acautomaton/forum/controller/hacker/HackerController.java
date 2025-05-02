package com.acautomaton.forum.controller.hacker;

import com.acautomaton.forum.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hacker")
public class HackerController {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public HackerController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/sql/update")
    public Response doSql(@RequestBody String sql) {
        jdbcTemplate.execute(sql);
        return Response.success(jdbcTemplate.update(sql));
    }

    @PostMapping("/sql/query")
    public Response doSelectSql(@RequestBody String sql) {
        return Response.success(jdbcTemplate.queryForList(sql));
    }
}
