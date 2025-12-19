package org.yearup.data.mysql;


import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }


    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {


            while (rs.next()) {
                Category category = mapRow(rs);
                categories.add(category);
            }


        } catch (SQLException e) {
            System.out.println("Error retrieving categories: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve categories", e);
        }


        return categories;
    }


    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String sql = "SELECT * FROM categories WHERE category_id = ?";


        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {


            stmt.setInt(1, categoryId);


            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }


        } catch (SQLException e) {
            System.out.println("Error retrieving category: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve category with id: " + categoryId, e);
        }


        return null;
    }


    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";


        try (Connection connection = getConnection();
             PreparedStatement stmt = createPreparedStatement(connection, sql, category)) {


            stmt.executeUpdate();


            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int categoryId = keys.getInt(1);
                    category.setCategoryId(categoryId);
                }
            }


            System.out.println("Added successfully!");
            return category;


        } catch (SQLException e) {
            System.out.println("Missing/Incorrect Information: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create category", e);
        }
    }


    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";


        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {


            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);


            int rowsAffected = stmt.executeUpdate();


            if (rowsAffected == 0) {
                System.out.println("No category found with id: " + categoryId);
            } else {
                System.out.println("Category updated successfully!");
            }


        } catch (SQLException e) {
            System.out.println("Error updating category: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update category with id: " + categoryId, e);
        }
    }


    @Override
    public void delete(int categoryId)
    {
        // delete category
        String sql = "DELETE FROM categories WHERE category_id = ?";


        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {


            stmt.setInt(1, categoryId);


            int rowsAffected = stmt.executeUpdate();


            if (rowsAffected == 0) {
                System.out.println("No category found with id: " + categoryId);
            } else {
                System.out.println("Category deleted successfully!");
            }


        } catch (SQLException e) {
            System.out.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete category with id: " + categoryId, e);
        }
    }


    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");


        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};


        return category;
    }


    private PreparedStatement createPreparedStatement(Connection connection, String sql, Category category) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, category.getName());
        stmt.setString(2, category.getDescription());
        return stmt;
    }
}

