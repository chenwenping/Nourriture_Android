package team_10.nourriture_android.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ping on 2014/12/21.
 */
public class DishBean implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    private String _id;
    private String name;
    private String description;
    private Date date;
    private String picture;
    private RecipeBean recipeBean; /*  default: 'No recipeBean' */
    private IngredientBean ingredientBean;
    private ProblemBean problemBean;
    private UserBean userBean;

    public String get_id() { return _id; }

    public void set_id(String _id) { this._id = _id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public RecipeBean getRecipeBean() {
        return recipeBean;
    }

    public void setRecipeBean(RecipeBean recipeBean) {
        this.recipeBean = recipeBean;
    }

    public IngredientBean getIngredientBean() {
        return ingredientBean;
    }

    public void setIngredientBean(IngredientBean ingredientBean) {
        this.ingredientBean = ingredientBean;
    }

    public ProblemBean getProblemBean() {
        return problemBean;
    }

    public void setProblemBean(ProblemBean problemBean) {
        this.problemBean = problemBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
