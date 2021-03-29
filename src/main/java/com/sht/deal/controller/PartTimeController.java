package com.sht.deal.controller;

import com.sht.deal.domain.PartTime;
import com.sht.deal.service.PartTimeService;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Aaron
 * @date 2021/3/28 12:42
 */
@RestController
@RequestMapping("api/part-time")
@Api(tags = "兼职信息")
public class PartTimeController {

    @Autowired
    private PartTimeService partTimeService;

    @PostMapping()
    public ResponseEntity add(@RequestBody PartTime partTime) throws Exception {
        return ResponseEntity.ok(this.partTimeService.add(partTime));
    }

    @GetMapping()
    public ResponseEntity<PageResult<PartTime>> findByPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "10") Integer rows
    ) {
        return ResponseEntity.ok(partTimeService.findByPage(name, userId, page, rows));
    }


    @DeleteMapping()
    public ResponseEntity delete(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(partTimeService.delete(id));
    }


    @PutMapping()
    public ResponseEntity update(@RequestBody PartTime partTime){
        return ResponseEntity.ok(this.partTimeService.update(partTime));
    }

    @GetMapping("findById")
    public ResponseEntity<JsonData> findById(@RequestParam(name = "id")Integer id) {
        return ResponseEntity.ok(partTimeService.findById(id));
    }
}
