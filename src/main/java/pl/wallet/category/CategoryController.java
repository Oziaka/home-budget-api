package pl.wallet.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.user.User;
import pl.user.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CategoryController {
  private CategoryService categoryService;
  private UserService userService;

  @Autowired
  public CategoryController (CategoryService categoryService, UserService userService) {
    this.categoryService = categoryService;
    this.userService = userService;
  }

  CategoryDto addCategory (Principal principal, CategoryDto categoryDto) {
    User user = userService.getUserByEmail(principal.getName());
    Category category = CategoryMapper.toEntity(categoryDto);
    category.addUser(user);
    category = categoryService.saveCategory(category);
    return CategoryMapper.toDto(category);
  }

  void removeCategory (Principal principal, Long categoryId) {
    User user = userService.getUserByEmail(principal.getName());
    categoryService.isUserCategory(user, categoryId);
    categoryService.removeCategory(categoryId);
  }

  List<CategoryDto> getCategories (Principal principal) {
    User user = userService.getUserByEmail(principal.getName());
    List<Category> categories = categoryService.getCategoriesByUser(user);
    return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
  }
}