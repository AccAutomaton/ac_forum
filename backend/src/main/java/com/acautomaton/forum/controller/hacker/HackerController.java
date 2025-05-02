package com.acautomaton.forum.controller.hacker;

import cn.hutool.json.JSONUtil;
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
    public Response doUpdateSql(@RequestBody String sql) {
        try {
            return Response.success(JSONUtil.toJsonStr(jdbcTemplate.update(sql.replaceAll("^\"|\"$", ""))));
        } catch (Exception e) {
            return Response.success(e.getMessage());
        }
    }

    @PostMapping("/sql/select")
    public Response doSelectSql(@RequestBody String sql) {
        try {
            return Response.success(JSONUtil.toJsonStr(jdbcTemplate.queryForList(sql.replaceAll("^\"|\"$", ""))));
        } catch (Exception e) {
            return Response.success(e.getMessage());
        }
    }
}
