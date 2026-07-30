package com.demetrius.vellastra.publish.domain.site.repository;

import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import com.demetrius.vellastra.publish.infrastructure.persistence.converter.PublishSiteConverter;
import com.demetrius.vellastra.publish.infrastructure.persistence.mapper.PublishSiteMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishSitePO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PublishSiteRepository {
    private final PublishSiteMapper mapper;
    private final PublishSiteConverter converter;
    public PublishSiteRepository(PublishSiteMapper mapper, PublishSiteConverter converter) {
        this.mapper = mapper; this.converter = converter;
    }
    public PublishSite findById(Long id) {
        PublishSitePO po = mapper.selectById(id);
        return po != null ? converter.toDomain(po) : null;
    }
    public List<PublishSite> findAll() {
        return mapper.selectList(null).stream().map(converter::toDomain).toList();
    }
    public void save(PublishSite site) {
        PublishSitePO po = converter.toPO(site);
        if (po.getId() == null) { mapper.insert(po); site.setId(po.getId()); }
        else { mapper.updateById(po); }
    }
    public void delete(Long id) { mapper.deleteById(id); }
}
