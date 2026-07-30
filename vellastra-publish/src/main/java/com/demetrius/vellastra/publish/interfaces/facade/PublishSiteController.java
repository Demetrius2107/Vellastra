package com.demetrius.vellastra.publish.interfaces.facade;

import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.publish.application.PublishSiteService;
import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/publish/sites")
public class PublishSiteController {

    private final PublishSiteService siteService;

    public PublishSiteController(PublishSiteService siteService) { this.siteService = siteService; }

    @GetMapping
    public Result<List<PublishSite>> list() { return Result.success(siteService.listAll()); }

    @GetMapping("/{id}")
    public Result<PublishSite> getById(@PathVariable Long id) { return Result.success(siteService.getById(id)); }

    @PostMapping
    public Result<Long> create(@RequestBody PublishSite request) {
        return Result.success(siteService.create(
                request.getName(), request.getSlug(), request.getRepoUrl(),
                request.getBuildCommand(), request.getOutputDir(),
                request.getDomain(), request.getNotifyEmail()));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PublishSite request) {
        siteService.update(id, request.getName(), request.getDescription(),
                request.getRepoUrl(), request.getBuildCommand(), request.getOutputDir(),
                request.getDomain(), request.getNotifyEmail());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { siteService.delete(id); return Result.success(); }
}
