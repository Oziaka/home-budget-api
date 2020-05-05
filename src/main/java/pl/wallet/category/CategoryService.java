package pl.wallet.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.exception.EntityNotFoundException;
import pl.exception.ThereIsNoYourPropertyException;
import pl.user.User;

import java.util.List;

@Service
public class CategoryService {

  private CategoryRepository categoryRepository;

  @Autowired
  public CategoryService (CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category isUserCategory (User user, Long categoryId) {
    if(getCategories(user).stream().anyMatch(category -> category.getId().equals(categoryId)))
      return getCategory(categoryId);
    else throw new ThereIsNoYourPropertyException();
  }

  private List<Category> getCategories (User user) {
    return categoryRepository.findAllByUsers(user);
  }

  private Category getCategory (Long categoryId) {
    return categoryRepository.getById(categoryId).orElseThrow(() -> new EntityNotFoundException(categoryId, categoryId.getClass()));
  }

  void removeCategory (Long categoryId) {
    categoryRepository.deleteById(categoryId);
  }

  public Category saveCategory (Category category) {
    return categoryRepository.save(category);
  }

  List<Category> getCategoriesByUser (User user) {
    return categoryRepository.findAllByUsers(user);
  }

}
