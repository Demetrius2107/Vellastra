package com.demetrius.vellastra.recycle.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.recycle.application.RecycleService;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recycle")
public class RecycleController {

    private final RecycleService recycleService;

    public RecycleController(RecycleService recycleService) {
        this.recycleService = recycleService;
    }

    @GetMapping
    public Result<IPage<RecycleItemPO>> list(@RequestParam(defaultValue = "1") int current,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String type) {
        return Result.success(recycleService.list(current, size, type));
    }

    @GetMapping("/{id}")
    public Result<RecycleItemPO> getById(@PathVariable Long id) {
        return Result.success(recycleService.getById(id));
    }

    @PostMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        recycleService.restore(id);
        return Result.success();
    }

    @PostMapping("/restore/batch")
    public Result<Void> batchRestore(@RequestBody List<Long> ids) {
        recycleService.batchRestore(ids);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePermanently(@PathVariable Long id) {
        recycleService.deletePermanently(id);
        return Result.success();
    }

    @PostMapping("/delete/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        recycleService.batchDeletePermanently(ids);
        return Result.success();
    }
}
