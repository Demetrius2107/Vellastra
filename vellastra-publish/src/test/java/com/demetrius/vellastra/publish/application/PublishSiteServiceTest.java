package com.demetrius.vellastra.publish.application;

import com.demetrius.vellastra.publish.domain.site.entity.PublishSite;
import com.demetrius.vellastra.publish.domain.site.repository.PublishSiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishSiteServiceTest {

    @Mock
    private PublishSiteRepository siteRepository;

    private PublishSiteService siteService;

    @BeforeEach
    void setUp() {
        siteService = new PublishSiteService(siteRepository);
    }

    @Test
    @DisplayName("listAll 应返回所有站点")
    void listAll_shouldReturnSites() {
        when(siteRepository.findAll()).thenReturn(List.of(
                PublishSite.builder().id(1L).name("博客站").build(),
                PublishSite.builder().id(2L).name("文档站").build()));

        List<PublishSite> sites = siteService.listAll();

        assertEquals(2, sites.size());
        assertEquals("博客站", sites.get(0).getName());
    }

    @Test
    @DisplayName("getById 应返回站点")
    void getById_shouldReturnSite() {
        when(siteRepository.findById(1L)).thenReturn(PublishSite.builder().id(1L).name("博客站").build());

        PublishSite site = siteService.getById(1L);

        assertEquals("博客站", site.getName());
    }

    @Test
    @DisplayName("create 应保存并返回新站点ID")
    void create_shouldSaveAndReturnId() {
        doAnswer(inv -> {
            PublishSite site = inv.getArgument(0);
            site.setId(1L);
            return null;
        }).when(siteRepository).save(any());

        Long id = siteService.create("博客站", "blog", "https://github.com/user/blog",
                "npm run build", "dist", "blog.example.com", "admin@example.com");

        assertEquals(1L, id);
        verify(siteRepository).save(any());
    }

    @Test
    @DisplayName("delete 应删除站点")
    void delete_shouldCallRepository() {
        siteService.delete(1L);
        verify(siteRepository).delete(1L);
    }
}
