package controller.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.scene.control.Label;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class BookUtil {

    private JFXTextField isbn, title, authors, publisher, price, threshold, publicationYear;
    private JFXComboBox<Label> category;
    private Label failLabel;

    public BookUtil(JFXTextField isbn, JFXTextField title, JFXTextField authors, JFXTextField publisher,
                    JFXTextField price, JFXTextField threshold, JFXTextField publicationYear,
                    JFXComboBox<Label> category, Label failLabel) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.price = price;
        this.threshold = threshold;
        this.publicationYear = publicationYear;
        this.category = category;
        this.failLabel = failLabel;
    }

    public void initFieldsVaildators() {
        initCategory();
        initISBN();
        initTitle();
        initAuthors();
        initPublisher();
        initPrice();
        initThreshold();
        initPublicationYear();
    }
    public void clearFields() {
        isbn.clear();
        title.clear();
        authors.clear();
        publisher.clear();
        price.clear();
        publicationYear.clear();
        threshold.clear();
    }

    private void initPublicationYear() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Publication Year Required");
        publicationYear.getValidators().add(validator);
        publicationYear.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) publicationYear.validate();
        });
    }

    private void initThreshold() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Threshold Required");
        threshold.getValidators().add(validator);
        threshold.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) threshold.validate();
        });
    }

    private void initPrice() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Price Required");
        price.getValidators().add(validator);
        price.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) price.validate();
        });
    }

    private void initPublisher() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Publisher Required");
        publisher.getValidators().add(validator);
        publisher.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) publisher.validate();
        });
    }

    private void initAuthors() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Author(s) Required");
        authors.getValidators().add(validator);
        authors.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) authors.validate();
        });
    }

    private void initTitle() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Title Required");
        title.getValidators().add(validator);
        title.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) title.validate();
        });
    }

    private void initCategory() {
        category.getItems().add(new Label("Science"));
        category.getItems().add(new Label("Art"));
        category.getItems().add(new Label("Religion"));
        category.getItems().add(new Label("History"));
        category.getItems().add(new Label("Geography"));
    }

    private void initISBN() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("ISBN Required");
        isbn.getValidators().add(validator);
        isbn.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) isbn.validate();
        });
    }

    public boolean validateFields() {
        return validateIsbn() && validateTitle() && validateAuthors() && validatePublisher() && validatePrice()
                && validateThreshold() && validatePublYear() && validateCategory();
    }

    private boolean validateCategory() {
        if (category.getValue() == null) {
            failLabel.setText("Invalid Category");
            return false;
        }
        return true;
    }

    private boolean validatePublYear() {
        if (publicationYear.getText().isEmpty()) {
            failLabel.setText("Invalid publication year");
            return false;
        }
        try {
            Integer.parseInt(publicationYear.getText());
            return true;
        } catch (NumberFormatException e) {
            failLabel.setText("Invalid publication year");
            return false;
        }
    }

    private boolean validateThreshold() {
        if (threshold.getText().isEmpty()) {
            failLabel.setText("Invalid threshold");
            return false;
        }
        try {
            Integer.parseInt(threshold.getText());
            return true;
        } catch (NumberFormatException e) {
            failLabel.setText("Invalid threshold");
            return false;
        }
    }

    private boolean validatePrice() {
        if (price.getText().isEmpty()) {
            failLabel.setText("Invalid price");
            return false;
        }
        try {
            Float.parseFloat(price.getText());
            return true;
        }
        catch (NumberFormatException e) {
            failLabel.setText("Invalid price");
            return false;
        }
    }

    private boolean validatePublisher() {
        if (publisher.getText().isEmpty()) {
            failLabel.setText("Invalid Publisher");
            return false;
        }
        return true;
    }

    private boolean validateAuthors() {
        if (authors.getText().isEmpty()) {
            failLabel.setText("Invalid Authors");
            return false;
        }
        return true;
    }

    private boolean validateTitle() {
        if (title.getText().isEmpty()) {
            failLabel.setText("Invalid Title");
            return false;
        }
        return true;
    }

    private boolean validateIsbn() {
        if (isbn.getText().length() != 13 || !isbn.getText().matches("[0-9]+")) {
            failLabel.setText("Invalid ISBN");
            return false;
        }
        return true;
    }

    public Date getDate(String pubYearString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = null;
        try {
            date = sdf.parse(pubYearString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Collection<String> getAuthorsList(String authorNames) {
        Collection<String> authorsList = new ArrayList<>(Arrays.asList(authorNames.split(",")));
        ((ArrayList<String>) authorsList).replaceAll(String::trim);
        return authorsList;
    }
}
