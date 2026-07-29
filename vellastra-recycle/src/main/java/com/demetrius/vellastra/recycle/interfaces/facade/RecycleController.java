package com.demetrius.vellastra.recycle.interfaces.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.recycle.application.RecycleService;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle")
public class RecycleController {
    private final RecycleService recycleService;
    public RecycleController(RecycleService recycleService) { this.recycleService = recycleService; }

    @GetMapping
    public Result<IPage<RecycleItemPO>> list(@RequestParam(defaultValue = "1") int current,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String type) {
        return Result.success(recycleService.list(current, size, type));
    }

    @PostMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) { recycleService.restore(id); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<Void> deletePermanently(@PathVariable Long id) { recycleService.deletePermanently(id); return Result.success(); }
}
