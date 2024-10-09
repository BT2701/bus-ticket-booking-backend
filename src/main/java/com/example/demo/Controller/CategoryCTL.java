package com.example.demo.Controller;

import com.example.demo.Model.Category;
import com.example.demo.Service.CategorySV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class CategoryCTL {
    @Autowired
    private CategorySV categorySV;
    @GetMapping("api/categories")
    public List<Category> getCategories() {
        return categorySV.getAllCategory();
    }
}
